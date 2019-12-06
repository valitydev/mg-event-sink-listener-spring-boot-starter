package com.rbkmoney;

import com.rbkmoney.app.TestApplication;
import com.rbkmoney.handlers.InvoicedCreateHandlerTestImpl;
import com.rbkmoney.handlers.InvoicePaymentStartedHandlerTestImpl;
import com.rbkmoney.handlers.InvoicePaymentStatusChangedHandlerTestImpl;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import com.rbkmoney.mg.event.sink.EventSinkAggregationStreamFactoryImpl;
import com.rbkmoney.mg.event.sink.MgEventSinkRowMapper;
import com.rbkmoney.mg.event.sink.converter.BinaryConverterImpl;
import com.rbkmoney.mg.event.sink.converter.SinkEventToEventPayloadConverter;
import com.rbkmoney.mg.event.sink.handler.MgEventSinkHandlerExecutor;
import com.rbkmoney.mg.event.sink.handler.flow.EventHandler;
import com.rbkmoney.mg.event.sink.serde.SinkEventSerde;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
public class EventSinkListenerTest extends KafkaAbstractTest {

    private static final String DIR_PATH = "tmp/state-store/";
    public static final String RESULT = "<InvoiceChange invoice_created:InvoiceCreated(invoice:Invoice(id:sourceID, owner_id:owner_id, shop_id:SHOP_ID, created_at:2016-08-10T16:07:18Z, status:<InvoiceStatus unpaid:InvoiceUnpaid()>, details:InvoiceDetails(product:product), due:2016-08-10T16:07:23Z, cost:Cash(amount:12, currency:CurrencyRef(symbolic_code:RUB)), context:Content(type:contentType, data:74 65 73 74)))><InvoiceChange invoice_payment_change:InvoicePaymentChange(id:1, payload:<InvoicePaymentChangePayload invoice_payment_started:InvoicePaymentStarted(payment:InvoicePayment(id:1, created_at:2016-08-10T16:07:18Z, domain_revision:0, status:<InvoicePaymentStatus processed:InvoicePaymentProcessed()>, payer:<Payer payment_resource:PaymentResourcePayer(resource:DisposablePaymentResource(payment_tool:<PaymentTool bank_card:BankCard(token:477bba133c182267fe5f086924abdc5db71f77bfc27f01f2843f2cdc69d89f05, payment_system:mastercard, bin:666, masked_pan:4242, issuer_country:RUS)>, client_info:ClientInfo(ip_address:123.123.123.123, fingerprint:finger)), contact_info:ContactInfo(email:test@mail.ru))>, cost:Cash(amount:123, currency:CurrencyRef(symbolic_code:RUB)), flow:<InvoicePaymentFlow hold:InvoicePaymentFlowHold(on_hold_expiration:capture, held_until:werwer)>))>)>\n";
    private SinkEventToEventPayloadConverter eventParser = new SinkEventToEventPayloadConverter(new BinaryConverterImpl());

    private List<EventHandler<String>> eventHandlers = new ArrayList<>();

    @Before
    public void init() {
        eventHandlers.add(new InvoicedCreateHandlerTestImpl());
        eventHandlers.add(new InvoicePaymentStartedHandlerTestImpl());
        eventHandlers.add(new InvoicePaymentStatusChangedHandlerTestImpl());
    }

    @Test
    public void testEventSink() throws InterruptedException {

        List<SinkEvent> sinkEvents = MgEventSinkFlowGenerator.generateSuccessFlow("sourceID");
        sinkEvents.forEach(this::produceMessageToEventSink);

        KafkaStreams kafkaStreams = new EventSinkAggregationStreamFactoryImpl<>(EVENT_SINK, AGGREGATED_EVENT_SINK, new SinkEventSerde(),
                Serdes.String(), Serdes.String(),
                () -> "",
                (key, value, aggregate) -> {
                    System.out.println(aggregate);
                    return aggregate + value;
                }, new MgEventSinkRowMapper<>(new MgEventSinkHandlerExecutor<>(eventParser, eventHandlers)),
                r -> RESULT.equals(r),
                (key, value) -> key)
                .create(eventSinkStreamProperties());

        Thread.sleep(4_000l);

        kafkaStreams.close(Duration.ofSeconds(10));

        Consumer<String, Object> consumer = createConsumer(StringDeserializer.class);
        consumer.subscribe(Arrays.asList(AGGREGATED_EVENT_SINK));

        Thread.sleep(2_000l);

        ConsumerRecords<String, Object> poll = consumer.poll(Duration.ofSeconds(10));

        Iterable<ConsumerRecord<String, Object>> records = poll.records(AGGREGATED_EVENT_SINK);

        for (ConsumerRecord<String, Object> record : records) {
            Assert.assertEquals(RESULT, record);
        }
    }

    @Bean
    public Properties eventSinkStreamProperties() {
        final Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "event-sink-fraud");
        props.put(StreamsConfig.CLIENT_ID_CONFIG, "test");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        props.put(StreamsConfig.STATE_DIR_CONFIG, DIR_PATH);
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 1);
        return props;
    }

    @AfterClass
    public static void clean() {
        File dir = new File("tmp/state-store/event-sink-fraud/1_0/rocksdb/KSTREAM-AGGREGATE-STATE-STORE-0000000005");
        for (File file : dir.listFiles()) {
            file.delete();
        }
        dir.delete();
    }
}
