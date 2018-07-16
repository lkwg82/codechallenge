package de.lgohlke.codedemo.service;

import de.lgohlke.codedemo.service.*;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TransactionServiceTest {

    private StatisticService statisticService = mock(StatisticService.class);
    private TimeService timeService = TimeServiceFactory.fixed(60_000);

    private TransactionService service = new TransactionService(statisticService, timeService);

    @Test
    public void shouldNotAddTransactionWhenOlderThan60Secs() {
        assertThat(addTransaction(0)).isFalse();

        verifyZeroInteractions(statisticService);
    }

    @Test
    public void shouldAddTransactionWhenExactInTheRange() {
        assertThat(addTransaction(1)).isTrue();
        assertThat(addTransaction(60_000)).isTrue();

        verify(statisticService, times(2)).addTransaction(any());
    }

    @Test
    public void shouldAddTransactionWhenInTheFuture() {
        // spec is unclear about that
        assertThat(addTransaction(60_001)).isTrue();

        verify(statisticService, times(1)).addTransaction(any());
    }

    private boolean addTransaction(long timestamp) {
        Transaction t = new Transaction(12.3, timestamp);
        return service.addTransaction(t);
    }
}