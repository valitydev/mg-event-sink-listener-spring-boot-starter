package dev.vality.mg.event.sink.converter;

import dev.vality.damsel.payment_processing.EventPayload;
import dev.vality.machinegun.eventsink.SinkEvent;
import dev.vality.mg.event.sink.exception.ParseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SinkEventToEventPayloadConverter {

    private final BinaryConverter<EventPayload> converter;

    public EventPayload parseEvent(SinkEvent message) {
        try {
            byte[] bin = message.getEvent().getData().getBin();
            return converter.convert(bin, EventPayload.class);
        } catch (Exception e) {
            log.error("Exception when parse message e: ", e);
            throw new ParseException();
        }
    }
}
