package de.lgohlke.codedemo;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.lgohlke.codedemo.service.*;
import okhttp3.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = StressIT.TestConfig.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"spring.main.banner-mode=off"})
public class StressIT {

    private ObjectMapper objectMapper = new ObjectMapper();

    @LocalServerPort
    private int port;

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private StatisticService statisticService;

    private static long initialTime = 1478192204000L;

    @Test
    public void shouldAddStressOnConcurrencyLevelOverHttp() throws Exception {

        BigDecimal sum = BigDecimal.ZERO;
        Double min = Double.MAX_VALUE;
        Double max = Double.MIN_VALUE;

        SecureRandom secureRandom = new SecureRandom();
        int rounds = 1_000;

        List<Runnable> jobs = new ArrayList<>(rounds);
        for (int i = 0; i < rounds; i++) {

            double amount = secureRandom.nextDouble();
            long delta = secureRandom.nextInt(59_000);
            sum = sum.add(BigDecimal.valueOf(amount));
            min = Math.min(min, amount);
            max = Math.max(max, amount);

            jobs.add(() -> {
                try {
                    postTransaction(new Transaction(amount, initialTime - delta));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        ExecutorService executorService = Executors.newFixedThreadPool(100);

        jobs.forEach(executorService::submit);

        executorService.shutdown();
        executorService.awaitTermination(100, TimeUnit.SECONDS);
        TimeUnit.SECONDS.sleep(5);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://localhost:" + port + "/statistics")
                .get()
                .build();
        Response response = client.newCall(request).execute();

        BigDecimal average = BigDecimal.ZERO.compareTo(sum) != 0 ? sum.divide(BigDecimal.valueOf(rounds),
                                                                              BigDecimal.ROUND_HALF_DOWN) : BigDecimal.ZERO;
        Statistics expectedStats = new Statistics(sum.doubleValue(), average.doubleValue(), max, min, rounds);
        String json = objectMapper.writeValueAsString(expectedStats);

        assertThat(json).isEqualTo(response.body().string());
    }

    @Test
    public void shouldAddStressOnConcurrencyLevelOverRaw() throws Exception {

        BigDecimal sum = BigDecimal.ZERO;
        Double min = Double.MAX_VALUE;
        Double max = Double.MIN_VALUE;

        SecureRandom secureRandom = new SecureRandom();
        int rounds = 1_000_000;

        ExecutorService executorService = Executors.newFixedThreadPool(100);
        List<Runnable> jobs = new ArrayList<>(rounds);
        for (int i = 0; i < rounds; i++) {
            double amount = secureRandom.nextDouble();
            long delta = secureRandom.nextInt(59_000);
            sum = sum.add(BigDecimal.valueOf(amount));
            min = Math.min(min, amount);
            max = Math.max(max, amount);

            jobs.add(() -> {
                Transaction transaction = new Transaction(amount, initialTime - delta);
                transactionService.addTransaction(transaction);
            });
        }

        jobs.forEach(executorService::submit);

        executorService.shutdown();
        executorService.awaitTermination(100, TimeUnit.SECONDS);

        BigDecimal average = BigDecimal.ZERO.compareTo(sum) != 0 ? sum.divide(BigDecimal.valueOf(rounds),
                                                                              BigDecimal.ROUND_HALF_DOWN) : BigDecimal.ZERO;
        Statistics expectedStats = new Statistics(sum.doubleValue(), average.doubleValue(), max, min, rounds);
        Statistics actualStats = statisticService.computeStats();

        assertThat(expectedStats).isEqualTo(actualStats);
    }

    private void postTransaction(Transaction transaction) throws Exception {
        String payload = objectMapper.writeValueAsString(transaction);

        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, payload);
        Request request = new Request.Builder()
                .url("http://localhost:" + port + "/transactions")
                .post(body)
                .build();
        client.newCall(request).execute();
    }

    @TestConfiguration
    public static class TestConfig {
        @Bean
        TimeService timeService() {
            return TimeServiceFactory.fixed(initialTime);
        }

        @Bean
        StatisticService statisticService(TimeService timeService) {
            return new StatisticService(timeService);
        }

        @Bean
        TransactionService transactionService(StatisticService statisticService, TimeService timeService) {
            return new TransactionService(statisticService, timeService);
        }

        @Bean
        Controller controller(StatisticService statisticService, TransactionService transactionService) {
            return new Controller(statisticService, transactionService);
        }
    }
}
