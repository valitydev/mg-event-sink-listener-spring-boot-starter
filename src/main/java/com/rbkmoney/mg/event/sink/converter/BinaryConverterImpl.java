package com.rbkmoney.mg.event.sink.converter;

import com.rbkmoney.damsel.payment_processing.EventPayload;
import com.rbkmoney.mg.event.sink.exception.ParseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BinaryConverterImpl implements BinaryConverter<EventPayload> {

    private final ThreadLocal<TDeserializer> thriftDeserializerThreadLocal = //NOSONAR we don't reset this value
            ThreadLocal.withInitial(() -> new TDeserializer(new TBinaryProtocol.Factory()));

    @Override
    public EventPayload convert(byte[] bin, Class<EventPayload> clazz) {
        EventPayload event = new EventPayload();
        try {
            thriftDeserializerThreadLocal.get().deserialize(event, bin);
        } catch (TException e) {
            log.error("BinaryConverterImpl e: ", e);
            throw new ParseException(e);
        }
        return event;
    }
}
