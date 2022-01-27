package dev.vality.mg.event.sink.handler;

import dev.vality.damsel.payment_processing.EventPayload;
import dev.vality.damsel.payment_processing.InvoiceChange;
import dev.vality.machinegun.eventsink.SinkEvent;
import dev.vality.mg.event.sink.converter.SinkEventToEventPayloadConverter;
import dev.vality.mg.event.sink.handler.flow.EventHandler;
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
                        .ifPresent(eventHandler -> list.add(eventHandler.handle(change, sinkEvent)));
            }
        }
        return list.stream()
                .filter(Objects::nonNull)
                .collect(toList());
    }

}
