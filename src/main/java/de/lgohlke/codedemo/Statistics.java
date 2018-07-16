package de.lgohlke.codedemo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
class Statistics {
    private final double sum;
    private final double avg;
    private final double max;
    private final double min;
    private final long count;
}
