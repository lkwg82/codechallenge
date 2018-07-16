package de.lgohlke.codedemo.service;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StatisticServiceTest {

    /*
      time based evictions
     */

    private ManualTimeService timeService = new ManualTimeService();
    private StatisticService service = new StatisticService(1, timeService);

    @Test
    public void shouldHave2TransactionsAtTheSameTime() {
        service.addTransaction(new Transaction(12.3, 60_000));
        service.addTransaction(new Transaction(12.3, 60_000));

        Statistics statistics = service.computeStats();
        assertThat(statistics.getCount()).isEqualTo(2);
    }

    @Test
    public void shouldHave3SubsequentTransactions() {
        service.addTransaction(new Transaction(12.3, 60_000));

        timeService.tick(1_000);
        service.addTransaction(new Transaction(12.3, 61_000));
        service.addTransaction(new Transaction(12.3, 61_001));

        Statistics statistics = service.computeStats();
        assertThat(statistics.getCount()).isEqualTo(2);
    }

    @Test
    public void shouldHave3SubsequentTransactionsWithAWindowSizePause() {
        service.addTransaction(new Transaction(12.3, 60_000));

        timeService.tick(1_000);
        service.addTransaction(new Transaction(12.3, 61_000));

        timeService.tick(1_000);
        service.addTransaction(new Transaction(12.3, 62_000));

        Statistics statistics = service.computeStats();
        assertThat(statistics.getCount()).isEqualTo(1);
    }

    static class ManualTimeService implements TimeService {
        private long now = 60_000;

        @Override
        public long now() {
            return now;
        }

        void tick(int step) {
            now += step;
        }
    }

    /*
      timestamp based range checks
     */

    @Test
    public void shouldSkipTheTransactionFromTheFuture() {
        service.addTransaction(new Transaction(12.3, 10));

        Statistics statistics = service.computeStats();
        assertThat(statistics.getCount()).isEqualTo(0);
    }

    @Test
    public void shouldSkipTooOldTransaction() {
        service.addTransaction(new Transaction(12.3, -10));

        Statistics statistics = service.computeStats();
        assertThat(statistics.getCount()).isEqualTo(0);
    }

    /*
     math checks
     */

    @Test
    public void shouldSumUp() {
        TimeService fixedTimeService = () -> 20_000;
        StatisticService service = new StatisticService(10, fixedTimeService);

        service.addTransaction(new Transaction(1.5,9_000)); // out of range

        service.addTransaction(new Transaction(3,10_000));
        service.addTransaction(new Transaction(4,11_000));
        service.addTransaction(new Transaction(5,12_000));
        service.addTransaction(new Transaction(6,13_000));
        service.addTransaction(new Transaction(7,14_000));
        service.addTransaction(new Transaction(8,15_000));
        service.addTransaction(new Transaction(9,19_999));

        service.addTransaction(new Transaction(1.5,21_000)); // out of range

        Statistics statistics = service.computeStats();
        assertThat(statistics.getCount()).isEqualTo(7);
        assertThat(statistics.getSum()).isEqualTo(42);
        assertThat(statistics.getMin()).isEqualTo(3);
        assertThat(statistics.getMax()).isEqualTo(9);

        assertThat(statistics.getAvg()).isEqualTo(6);
    }
}