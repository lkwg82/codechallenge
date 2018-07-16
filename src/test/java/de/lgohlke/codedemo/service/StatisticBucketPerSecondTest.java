package de.lgohlke.codedemo.service;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StatisticBucketPerSecondTest {
    private StatisticBucketPerSecond bucket = new StatisticBucketPerSecond();

    @Test
    public void shouldResetOnExpiration() {
        bucket.addAmount(100d);
        bucket.resetWhenExpired(10, 10);

        assertThat(bucket.getCount()).isEqualTo(0);
    }

    @Test
    public void shouldNotResetWithinLifetime() {
        bucket.resetWhenExpired(10, 10);

        bucket.addAmount(10d);

        bucket.resetWhenExpired(11, 10);

        assertThat(bucket.getCount()).isEqualTo(1);
        assertThat(bucket.getSum()).isEqualTo(10);
    }
}