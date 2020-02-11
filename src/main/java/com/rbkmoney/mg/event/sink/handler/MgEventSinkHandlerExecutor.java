package com.rbkmoney.mg.event.sink.handler;

import com.rbkmoney.damsel.payment_processing.EventPayload;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import com.rbkmoney.mg.event.sink.converter.SinkEventToEventPayloadConverter;
import com.rbkmoney.mg.event.sink.handler.flow.EventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@Slf4j
@RequiredArgsConstructor
public class MgEventSinkHandlerExecutor<T> {

    private final SinkEventToEventPayloadConverter eventParser;
    private final List<EventHandler<T>> eventHandlers;

    public List<T> handle(SinkEvent sinkEvent) {
        List<T> list = new ArrayList<>();
        EventPayload eventPayload = eventParser.parseEvent(sinkEvent);
        if (eventPayload.isSetInvoiceChanges()) {
            for (InvoiceChange change : eventPayload.getInvoiceChanges()) {
                eventHandlers.stream()
                        .filter(invoiceChangeHandler -> invoiceChangeHandler.filter(change))
                        .findFirst()
                        .ifPresent(tEventHandler -> list.add(tEventHandler.handle(change, sinkEvent)));
            }
        }
        return list.stream()
                .filter(Objects::nonNull)
                .collect(toList());
    }

}
