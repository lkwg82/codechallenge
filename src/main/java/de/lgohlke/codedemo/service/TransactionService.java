package de.lgohlke.codedemo.service;

import lombok.NonNull;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    private final StatisticService statisticService;
    private final TimeService timeservice;

    private TransactionService() { // just for DI
        this(null);
    }

    public TransactionService(StatisticService statisticService) {
        this(statisticService, TimeServiceFactory.system());
    }

    public TransactionService(StatisticService statisticService, TimeService timeService) {
        this.statisticService = statisticService;
        this.timeservice = timeService;
    }

    public boolean addTransaction(@NonNull Transaction transaction) {
        // TODO check overflow attack, needs to check what is the purpose of these statistics
        // is it for monitoring operational or do full some financial service
        long difference = timeservice.now() - transaction.getTimestamp();
        boolean validToAdd = difference < 60_000;

        if (validToAdd) {
            statisticService.addTransaction(transaction);
        }
        return validToAdd;
    }
}
