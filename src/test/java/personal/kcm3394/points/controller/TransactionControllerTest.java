package personal.kcm3394.points.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import personal.kcm3394.points.model.Transaction;
import personal.kcm3394.points.service.HistoryService;
import personal.kcm3394.points.service.PayerBalance;
import personal.kcm3394.points.service.TransactionService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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

    @MockBean
    private HistoryService historyService;

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
    void should_call_addPointsToSinglePayer_when_transaction_is_positive_and_add_to_history() throws Exception {
        Transaction transaction = new Transaction("ADIDAS", 200, LocalDateTime.now());

        mockMvc.perform(post("/points/add-points")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isOk());

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionService, times(1)).addPointsToSinglePayer(transactionCaptor.capture());
        verify(historyService, times(1)).addTransactionToHistory(transactionCaptor.capture());
        assertEquals("ADIDAS", transactionCaptor.getValue().getPayerName());
    }

    @Test
    void should_call_deductPointsFromSinglePayer_when_transaction_is_negative_and_add_to_history() throws Exception {
        Transaction transaction = new Transaction("ADIDAS", -200, LocalDateTime.now());

        mockMvc.perform(post("/points/add-points")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isOk());

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionService, times(1)).deductPointsFromSinglePayer(transactionCaptor.capture());
        verify(historyService, times(1)).addTransactionToHistory(transactionCaptor.capture());
        assertEquals("ADIDAS", transactionCaptor.getValue().getPayerName());
    }

    @Test
    void should_return_list_of_deductions_and_add_each_to_history() throws Exception {
        Transaction transaction1 = new Transaction("ADIDAS", -200, LocalDateTime.now());
        Transaction transaction2 = new Transaction("NIKE", -500, LocalDateTime.now());
        Transaction transaction3 = new Transaction("PUMA", -100, LocalDateTime.now());
        List<Transaction> deductions = new LinkedList<>(List.of(transaction1, transaction2, transaction3));

        when(transactionService.deductTotalPoints(anyInt())).thenReturn(deductions);

        mockMvc.perform(post("/points/spend-points")
                .param("points", "700")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].payerName").value("ADIDAS"))
                .andExpect(jsonPath("$[0].points").value(-200));

        verify(historyService, times(3)).addTransactionToHistory(any(Transaction.class));
    }
}
