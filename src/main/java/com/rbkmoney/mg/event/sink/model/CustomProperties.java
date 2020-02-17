package com.rbkmoney.mg.event.sink.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CustomProperties {

    private final boolean cleanInstall;
    private final boolean throttlingEnabled;

    //max.poll.records * throttlingTimeoutMs should always be less session.timeout
    private final int throttlingTimeoutMs;

    private final String initialEventSink;
    private final String aggregatedSinkTopic;
}
