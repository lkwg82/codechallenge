package de.lgohlke.codedemo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.AtomicDouble;
import de.lgohlke.codedemo.service.*;
import okhttp3.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = StressTest.TestConfig.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"spring.main.banner-mode=off"})
public class StressTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @LocalServerPort
    private int port;

    private static long initialTime = 1478192204000L;

    @Test
    public void shouldAddStressOnConcurrencyLevel() throws Exception {


        AtomicDouble sum = new AtomicDouble();
        AtomicDouble min = new AtomicDouble(Double.MAX_VALUE);
        AtomicDouble max = new AtomicDouble(Double.MIN_VALUE);

        SecureRandom secureRandom = new SecureRandom();
        int rounds = 1_000;

        List<Runnable> jobs = new ArrayList<>(rounds);
        IntStream.range(0, rounds).forEach(i -> {

            double amount = secureRandom.nextDouble();
            long delta = secureRandom.nextInt(59_000);
            sum.set(sum.get() + amount);
            min.set(Math.min(min.get(), amount));
            max.set(Math.max(max.get(), amount));

            jobs.add(() -> {
                try {
                    postTransaction(new Transaction(amount, initialTime - delta));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });

        Double average = sum.doubleValue() / rounds;

        System.out.println("count:    " + rounds);
        System.out.println("sum:      " + sum);
        System.out.println("min:      " + min);
        System.out.println("max:      " + max);
        System.out.println("avg:      " + average);


        ExecutorService executorService = Executors.newFixedThreadPool(100);

        jobs.forEach(executorService::submit);

        executorService.shutdown();
        executorService.awaitTermination(100, TimeUnit.SECONDS);
        TimeUnit.SECONDS.sleep(2);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://localhost:" + port + "/statistics")
                .get()
                .build();
        Response response = client.newCall(request).execute();
        System.out.println(response.body().string());

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
        Response response = client.newCall(request).execute();

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
