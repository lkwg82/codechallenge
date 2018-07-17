package de.lgohlke.codedemo.service;

import lombok.Getter;

@Getter
class StatisticBucketPerSecond {
    private long created = 0L;

    private long count = 0L;
    private double sum = 0d;
    private double min = Double.MAX_VALUE;
    private double max = Double.MIN_VALUE;

    void resetWhenExpired(long now, long oldestInRange) {
        if (created < oldestInRange) {
            created = now;

            count = 0L;
            sum = 0d;
            min = Double.MAX_VALUE;
            max = Double.MIN_VALUE;
        }
    }

    void addAmount(double amount) {
        sum += amount;
        count++;
        min = Math.min(min, amount);
        max = Math.max(max, amount);
    }
}
