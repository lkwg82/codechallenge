package de.lgohlke.codedemo;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TransactionServiceTest {

    private TimeService timeService = TimeServiceFactory.fixed(60_000);
    private TransactionService service = new TransactionService(timeService);

    @Test
    public void shouldNotAddTransactionWhenOlderThan60Secs() {
        assertThat(addTransaction(0)).isFalse();
    }

    @Test
    public void shouldAddTransactionWhenExactInTheRange() {
        assertThat(addTransaction(1)).isTrue();
        assertThat(addTransaction(60_000)).isTrue();
    }

    @Test
    public void shouldAddTransactionWhenInTheFuture() {
        // spec is unclear about that
        assertThat(addTransaction(60_001)).isTrue();
    }

    private boolean addTransaction(long timestamp) {
        Transaction t = new Transaction(12.3, timestamp);
        return service.addTransaction(t);
    }
}