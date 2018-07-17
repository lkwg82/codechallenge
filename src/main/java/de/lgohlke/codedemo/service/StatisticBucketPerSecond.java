package de.lgohlke.codedemo.service;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
class StatisticBucketPerSecond {
    private long created = 0L;

    private long count = 0L;
    private BigDecimal sum = BigDecimal.ZERO;
    private double min = Double.MAX_VALUE;
    private double max = Double.MIN_VALUE;

    void resetWhenExpired(long now, long oldestInRange) {
        if (created < oldestInRange) {
            created = now;

            count = 0L;
            sum = BigDecimal.ZERO;
            min = Double.MAX_VALUE;
            max = Double.MIN_VALUE;
        }
    }

    void addAmount(double amount) {
        sum = sum.add(BigDecimal.valueOf(amount));
        count++;
        min = Math.min(min, amount);
        max = Math.max(max, amount);
    }
}
