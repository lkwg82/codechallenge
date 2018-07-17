package de.lgohlke.codedemo.service;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class Statistics {
    private final double sum;
    private final double avg;
    private final double max;
    private final double min;
    private final long count;
}
