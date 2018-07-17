package de.lgohlke.codedemo.service;

import lombok.NonNull;
import org.springframework.stereotype.Service;

@Service
public class StatisticService {
    private final int windowSize;
    private final TimeService timeService;

    private final StatisticBucketPerSecond[] buckets;

    private StatisticService() {
        this(TimeServiceFactory.system());
    }

    public StatisticService(TimeService timeService) {
        this(60, timeService);
    }

    StatisticService(int windowSize, TimeService timeService) {
        this.windowSize = windowSize;
        this.timeService = timeService;

        buckets = new StatisticBucketPerSecond[windowSize];
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = new StatisticBucketPerSecond();
        }
    }

    public Statistics computeStats() {
        long now = timeService.now();

        long count = 0;
        double sum = 0d;
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        for (int i = 0; i < windowSize; i++) {
            StatisticBucketPerSecond currentBucket = buckets[i];
            synchronized (buckets) {
                currentBucket.resetWhenExpired(now, now - windowSize * 1000);
                count += currentBucket.getCount();
                sum += currentBucket.getSum();

                min = Math.min(min, currentBucket.getMin());
                max = Math.max(max, currentBucket.getMax());
            }
        }

        double avg = Double.compare(0,sum) != 0 ? sum / count:0;

        return new Statistics(sum, avg, max, min, count);
    }

    public void addTransaction(@NonNull Transaction transaction) {
        long now = timeService.now();
        long timestamp = transaction.getTimestamp();

        if (now < timestamp) {
            // TODO what about safety space to accept transactions in the future 5 seconds ahead
            // skip future transactions
            return;
        }

        if ((now - windowSize * 1000) > timestamp) {
            // skip too old transactions
            // second safety net after controller
            return;
        }

        int bucketIndex = Math.toIntExact(now % (long) windowSize);
        StatisticBucketPerSecond currentBucket = buckets[bucketIndex];
        synchronized (buckets) {
            currentBucket.resetWhenExpired(now, now - windowSize * 1000);
            // valid range of amount is unspecified, could break here
            currentBucket.addAmount(transaction.getAmount());
        }
    }
}
