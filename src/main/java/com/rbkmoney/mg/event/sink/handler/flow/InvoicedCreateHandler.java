package com.rbkmoney.mg.event.sink.handler.flow;

import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import lombok.Getter;
import org.apache.thrift.TBase;

public abstract class InvoicedCreateHandler<R> implements EventHandler<R> {

    @Getter
    private Filter<TBase> filter = new PathConditionFilter(
            new PathConditionRule(
                    "invoice_created",
                    new IsNullCondition().not()
            )
    );

}
