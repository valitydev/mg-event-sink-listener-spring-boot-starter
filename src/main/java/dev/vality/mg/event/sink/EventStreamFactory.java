package dev.vality.mg.event.sink;

import org.apache.kafka.streams.KafkaStreams;

import java.util.Properties;

public interface EventStreamFactory {

    KafkaStreams create(final Properties streamsConfiguration);

}
