package dev.vality.handlers;

import dev.vality.damsel.payment_processing.InvoiceChange;
import dev.vality.machinegun.eventsink.SinkEvent;
import dev.vality.mg.event.sink.handler.flow.InvoicePaymentStatusChangedHandler;

public class InvoicePaymentStatusChangedHandlerTestImpl extends InvoicePaymentStatusChangedHandler<String> {

    @Override
    public String handle(InvoiceChange change, SinkEvent event) {
        return change.toString();
    }

}
