package com.rbkmoney.mg.event.sink.handler;

import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import lombok.Getter;
import org.apache.thrift.TBase;

public abstract class InvoicePaymentStatusChangedHandler<T> implements EventHandler<T> {

    @Getter
    private Filter<TBase> filter = new PathConditionFilter(
            new PathConditionRule(
                    "invoice_payment_change.payload.invoice_payment_status_changed",
                    new IsNullCondition().not())
    );

}
