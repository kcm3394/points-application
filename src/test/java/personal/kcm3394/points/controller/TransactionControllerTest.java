package personal.kcm3394.points.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import personal.kcm3394.points.model.PayerBalance;
import personal.kcm3394.points.model.TransactionBalance;
import personal.kcm3394.points.model.TransactionDto;
import personal.kcm3394.points.service.TransactionService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;

    @Test
    void should_return_list_of_balances() throws Exception {
        PayerBalance balance1 = new PayerBalance("ADIDAS", 500);
        PayerBalance balance2 = new PayerBalance("NIKE", 1200);

        List<PayerBalance> balances = new ArrayList<>(List.of(balance1, balance2));
        when(transactionService.getAllPayerBalances()).thenReturn(balances);

        mockMvc.perform(get("/points")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].payerName").value("ADIDAS"))
                .andExpect(jsonPath("$[0].pointsBalance").value(500));
    }

    @Test
    void should_call_addPointsToSinglePayer_when_transaction_is_positive() throws Exception {
        TransactionDto transactionDto = new TransactionDto("ADIDAS", 200, LocalDateTime.now());

        mockMvc.perform(post("/points/add-points")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().isOk());

        ArgumentCaptor<TransactionBalance> transactionCaptor = ArgumentCaptor.forClass(TransactionBalance.class);
        verify(transactionService, times(1)).addPointsToSinglePayer(transactionCaptor.capture());
        assertEquals("ADIDAS", transactionCaptor.getValue().getPayerName());
    }

    @Test
    void should_call_deductPointsFromSinglePayer_when_transaction_is_negative() throws Exception {
        TransactionDto transactionDto = new TransactionDto("ADIDAS", -200, LocalDateTime.now());

        mockMvc.perform(post("/points/add-points")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().isOk());

        ArgumentCaptor<TransactionBalance> transactionCaptor = ArgumentCaptor.forClass(TransactionBalance.class);
        verify(transactionService, times(1)).deductPointsFromSinglePayer(transactionCaptor.capture());
        assertEquals("ADIDAS", transactionCaptor.getValue().getPayerName());
    }

    @Test
    void should_return_list_of_deductions() throws Exception {
        TransactionBalance transaction1 = new TransactionBalance("ADIDAS", -200, LocalDateTime.now());
        TransactionBalance transaction2 = new TransactionBalance("NIKE", -500, LocalDateTime.now());
        TransactionBalance transaction3 = new TransactionBalance("PUMA", -100, LocalDateTime.now());

        List<TransactionBalance> deductions = List.of(transaction1, transaction2, transaction3);

        when(transactionService.deductTotalPoints(anyInt())).thenReturn(deductions);

        mockMvc.perform(post("/points/spend-points")
                .param("points", "700")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].payerName").value("ADIDAS"))
                .andExpect(jsonPath("$[0].points").value(-200));

        verify(transactionService, times(1)).deductTotalPoints(anyInt());
    }
}
