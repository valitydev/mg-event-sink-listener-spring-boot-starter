package dev.vality.handlers;

import dev.vality.damsel.payment_processing.InvoiceChange;
import dev.vality.machinegun.eventsink.SinkEvent;
import dev.vality.mg.event.sink.handler.flow.InvoicePaymentStartedHandler;

public class InvoicePaymentStartedHandlerTestImpl extends InvoicePaymentStartedHandler<String> {

    @Override
    public String handle(InvoiceChange change, SinkEvent event) {
        return change.toString();
    }

}
