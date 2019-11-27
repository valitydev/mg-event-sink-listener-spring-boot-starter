package com.rbkmoney.mg.event.sink;

import com.rbkmoney.machinegun.eventsink.SinkEvent;
import com.rbkmoney.mg.event.sink.handler.MgEventSinkHandler;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MgEventSinkRowMapper<T> implements KeyValueMapper<String, SinkEvent, KeyValue<String, List<T>>> {

    private final MgEventSinkHandler<T> mgEventSinkHandler;

    @Override
    public KeyValue<String, List<T>> apply(String key, SinkEvent value) {
        return new KeyValue<>(key, mgEventSinkHandler.handle(value));
    }

}
