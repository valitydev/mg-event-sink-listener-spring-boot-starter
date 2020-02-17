package com.rbkmoney.mg.event.sink.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomProperties {

    private boolean cleanInstall;
    private boolean throttlingEnabled;

    //max.poll.records * throttlingTimeoutMs should always be less session.timeout
    private int throttlingTimeoutMs;

    private String initialEventSink;
    private String aggregatedSinkTopic;

}
