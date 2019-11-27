package com.rbkmoney.mg.event.sink.handler;

import com.rbkmoney.damsel.payment_processing.EventPayload;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class MgEventSinkHandler<T> {

    private final SourceEventParser eventParser;
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
        return list;
    }

}
