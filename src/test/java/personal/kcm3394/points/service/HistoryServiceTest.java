package personal.kcm3394.points.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import personal.kcm3394.points.model.Transaction;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class HistoryServiceTest {

    private HistoryServiceImpl mockService;

    @BeforeEach
    void setUp() {
        mockService = new HistoryServiceImpl();
    }

    @Test
    void should_add_transactions_to_history() {
        Transaction transaction1 = new Transaction("ADIDAS", 200, LocalDateTime.now());
        mockService.addTransactionToHistory(transaction1);

        Transaction transaction2 = new Transaction("NIKE", 500, LocalDateTime.now());
        mockService.addTransactionToHistory(transaction2);

        Transaction transaction3 = new Transaction("ADIDAS", 1000, LocalDateTime.now());
        mockService.addTransactionToHistory(transaction3);

        assertEquals(3, mockService.getUserHistory().size());
    }
}
