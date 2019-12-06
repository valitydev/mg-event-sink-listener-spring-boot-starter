package com.rbkmoney.handlers;

import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import com.rbkmoney.mg.event.sink.handler.flow.InvoicePaymentStatusChangedHandler;

public class InvoicePaymentStatusChangedHandlerTestImpl extends InvoicePaymentStatusChangedHandler<String> {

    @Override
    public String handle(InvoiceChange change, SinkEvent event) {
        return change.toString();
    }

}
