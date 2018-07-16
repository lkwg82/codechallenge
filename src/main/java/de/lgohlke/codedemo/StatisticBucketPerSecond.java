package de.lgohlke.codedemo;

import lombok.Getter;

@Getter
class StatisticBucketPerSecond {
    private Long created = 0L;

    private Long count = 0L;
    private Double sum = 0d;
    private Double min = Double.MAX_VALUE;
    private Double max = Double.MIN_VALUE;

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
    // TODO consistency about usign Long/long wrapped types

}
