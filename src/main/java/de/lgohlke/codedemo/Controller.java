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

    @PostMapping("/transactions")
    public void addTransaction(@RequestBody Transaction transaction,
                               HttpServletResponse response) {

        if (isInvalid(transaction)){
            response.setStatus(400);
            return;
        }

        if (transactionService.addTransaction(transaction)) {
            response.setStatus(201);
        } else {
            response.setStatus(204);
        }
    }

    private boolean isInvalid(Transaction transaction) {
        // what about the amount? is it ok to have negative amount
        // I miss context, so I cant decide


        // here there could be strong rules to avoid bad data in the system
        // e.g. now - 1hr or so
        return transaction.getTimestamp() < 0;
    }

    // TODO is there something weird with the example?? missing the '.0' after the number, but talking about double
    @GetMapping(value = "/statistics")
    public String showStatistics() throws JsonProcessingException {
        Statistics statistics = statisticService.computeStats();

        // TODO refine, use spring tools
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return objectMapper.writeValueAsString(statistics);
    }
}
