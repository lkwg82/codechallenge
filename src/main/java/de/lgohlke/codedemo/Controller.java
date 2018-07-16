package de.lgohlke.codedemo;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@RestController
public class Controller {
    private final TransactionService service;

    @PostMapping("/transactions")
    public void addTransaction(@RequestBody Transaction transaction,
                               HttpServletResponse response) {
        if (service.addTransaction(transaction)) {
            response.setStatus(201);
        } else {
            response.setStatus(204);
        }
    }

    @GetMapping("/statistics")
    public void showStatistics() {

    }
}
