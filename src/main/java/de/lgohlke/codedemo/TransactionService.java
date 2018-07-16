package de.lgohlke.codedemo;

import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    private final TimeService timeservice;

    public TransactionService() {
        this(TimeServiceFactory.system());
    }

    public TransactionService(TimeService timeService) {
        this.timeservice = timeService;
    }

    // TODO check null
    public boolean addTransaction(Transaction transaction) {
        // TODO check overflow attack
        long difference = timeservice.now() - transaction.getTimestamp();
        return difference < 60_000;
    }
}
