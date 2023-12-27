package com.moim.backend.global.filter;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

public class TimeCheckFilter extends Filter<ILoggingEvent> {
    @Override
    public FilterReply decide(ILoggingEvent event) {
        return (event.getLoggerName().equals("com.moim.backend.global.aspect.PerformanceAspect"))
                ? FilterReply.ACCEPT
                : FilterReply.DENY;
    }
}
