package dev.vality.mg.event.sink;

import dev.vality.machinegun.eventsink.SinkEvent;
import dev.vality.mg.event.sink.exception.StreamInitializationException;
import dev.vality.mg.event.sink.model.CustomProperties;
import dev.vality.mg.event.sink.serde.SinkEventSerde;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;

import java.util.List;
import java.util.Properties;
import java.util.function.Predicate;

@Slf4j
@RequiredArgsConstructor
public class EventSinkAggregationStreamFactoryImpl<K, T, R> implements EventStreamFactory {

    private final CustomProperties customProperties;

    private final SinkEventSerde sinkEventSerde;
    private final Serde<K> kafkaSerde;
    private final Serde<R> resultSerde;
    private final Initializer<R> initializer;
    private final Aggregator<K, T, R> aggregator;
    private final KeyValueMapper<String, SinkEvent, KeyValue<K, List<T>>> keyValueMapper;
    private final Predicate<R> filter;
    private final KeyValueMapper<K, T, K> selector;

    @Override
    public KafkaStreams create(final Properties streamsConfiguration) {
        KafkaStreams kafkaStreams = null;
        try {
            log.info("Create stream aggregation!");

            StreamsBuilder builder = new StreamsBuilder();
            builder.stream(customProperties.getInitialEventSink(), Consumed.with(Serdes.String(), sinkEventSerde))
                    .peek((key, value) -> sleepIfThrottle())
                    .peek((key, value) -> log.debug("Aggregate key={} value={}", key, value))
                    .map(keyValueMapper)
                    .flatMapValues(value -> value)
                    .groupBy(selector)
                    .aggregate(initializer, aggregator, Materialized.with(kafkaSerde, resultSerde))
                    .toStream()
                    .filter((key, value) -> filter.test(value))
                    .peek((key, value) -> log.debug("Filtered key={} value={}", key, value))
                    .to(customProperties.getAggregatedSinkTopic(), Produced.with(kafkaSerde, resultSerde));

            kafkaStreams = new KafkaStreams(builder.build(), streamsConfiguration);

            kafkaStreams.setUncaughtExceptionHandler((t, e) -> {
                log.error("Caught unhandled Kafka Streams Exception:", e);
                throw new StreamInitializationException(e);
            });

            if (customProperties.isCleanInstall()) {
                kafkaStreams.cleanUp();
            }

            kafkaStreams.start();
            log.info("Stream aggregation is started!");
            return kafkaStreams;
        } catch (Exception e) {
            log.error("Error when EventSinkAggregationStreamFactoryImpl insert e: ", e);
            if (kafkaStreams != null) {
                kafkaStreams.close();
            }
            throw new StreamInitializationException(e);
        }
    }

    private void sleepIfThrottle() {
        if (customProperties.isThrottlingEnabled()) {
            try {
                Thread.sleep(customProperties.getThrottlingTimeoutMs());
            } catch (InterruptedException e) {
                log.error("Exception when throttle e: ", e);
                Thread.currentThread().interrupt();
            }
        }
    }

}
