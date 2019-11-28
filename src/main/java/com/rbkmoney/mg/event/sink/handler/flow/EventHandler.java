package com.rbkmoney.mg.event.sink.handler.flow;

import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import org.apache.thrift.TBase;

public interface EventHandler<R> {

    default boolean filter(TBase invoiceChange) {
        return getFilter().match(invoiceChange);
    }

    R handle(InvoiceChange change, SinkEvent event);

    Filter<TBase> getFilter();

}
