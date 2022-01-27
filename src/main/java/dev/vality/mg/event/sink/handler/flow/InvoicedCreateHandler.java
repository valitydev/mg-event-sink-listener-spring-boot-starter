package dev.vality.mg.event.sink.handler.flow;

import dev.vality.geck.filter.Filter;
import dev.vality.geck.filter.PathConditionFilter;
import dev.vality.geck.filter.condition.IsNullCondition;
import dev.vality.geck.filter.rule.PathConditionRule;
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
