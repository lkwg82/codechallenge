package de.lgohlke.codedemo;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.lgohlke.codedemo.service.StatisticService;
import de.lgohlke.codedemo.service.Statistics;
import de.lgohlke.codedemo.service.Transaction;
import de.lgohlke.codedemo.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@RestController
public class Controller {
    private final StatisticService statisticService;
    private final TransactionService transactionService;

    // TODO add null checks
    @PostMapping("/transactions")
    public void addTransaction(@RequestBody Transaction transaction,
                               HttpServletResponse response) {
        if (transactionService.addTransaction(transaction)) {
            response.setStatus(201);
        } else {
            response.setStatus(204);
        }
    }

    // TODO is there something weird with the example?? missing the '.0' after the numbers, but talking about double
    @GetMapping(value = "/statistics")
    public String showStatistics() throws JsonProcessingException {
        Statistics statistics = statisticService.computeStats();

        // TODO refine, use spring tools
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return objectMapper.writeValueAsString(statistics);
    }
}
