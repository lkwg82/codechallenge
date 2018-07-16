package de.lgohlke.codedemo;

import org.springframework.stereotype.Service;

@Service
public class StatisticService {
    public Statistics computeStats() {
        return new Statistics(1000d, 100d, 200d, 50d, 10);
    }
}
