package de.lgohlke.codedemo;

import org.hamcrest.core.Is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(Controller.class)
public class ControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TransactionService transactionService;
    @MockBean
    private StatisticService statisticService;

    @Test
    public void shouldAddTransaction() throws Exception {

        when(transactionService.addTransaction(any())).thenReturn(true);

        String payload = "{" +
                         "\"amount\": 12.3," +
                         "\"timestamp\": " + System.currentTimeMillis() + "" +
                         "}";

        mvc.perform(post("/transactions")
                            .contentType(MediaType.APPLICATION_JSON_UTF8)
                            .content(payload))
           .andExpect(status().is(201));
    }

    @Test
    public void shouldNotAddTransaction() throws Exception {

        when(transactionService.addTransaction(any())).thenReturn(false);

        String payload = "{" +
                         "\"amount\": 12.3," +
                         "\"timestamp\": " + System.currentTimeMillis() + "" +
                         "}";

        mvc.perform(post("/transactions")
                            .contentType(MediaType.APPLICATION_JSON_UTF8)
                            .content(payload))
           .andExpect(status().is(204));
    }

    @Test
    public void showStatistics() throws Exception {
        Statistics stats=new Statistics(1000d, 100d, 200d, 50d, 10);
        when(statisticService.computeStats()).thenReturn(stats);

        mvc.perform(get("/statistics"))
           .andExpect(status().is(200))
           .andExpect(jsonPath("sum", Is.is(1000d)));
    }
}
