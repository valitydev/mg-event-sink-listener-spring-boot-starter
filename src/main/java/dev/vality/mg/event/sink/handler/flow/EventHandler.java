package dev.vality.mg.event.sink.handler.flow;

import dev.vality.damsel.payment_processing.InvoiceChange;
import dev.vality.geck.filter.Filter;
import dev.vality.machinegun.eventsink.SinkEvent;
import org.apache.thrift.TBase;

public interface EventHandler<R> {

    default boolean filter(TBase invoiceChange) {
        return getFilter().match(invoiceChange);
    }

    R handle(InvoiceChange change, SinkEvent event);

    Filter<TBase> getFilter();

}
