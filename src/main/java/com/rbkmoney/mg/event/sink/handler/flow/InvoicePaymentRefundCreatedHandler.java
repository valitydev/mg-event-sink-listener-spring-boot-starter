package com.rbkmoney.mg.event.sink.handler.flow;

import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TBase;

@Slf4j
public abstract class InvoicePaymentRefundCreatedHandler<T> implements EventHandler<T> {

    @Getter
    private final Filter<TBase> filter = new PathConditionFilter(
            new PathConditionRule(
                    "invoice_payment_change.payload.invoice_payment_refund_change.payload.invoice_payment_refund_created",
                    new IsNullCondition().not())
    );

}
