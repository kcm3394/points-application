package personal.kcm3394.points.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import personal.kcm3394.points.model.Transaction;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class TransactionServiceTest {

    private TransactionServiceImpl mockService;

    @BeforeEach
    void setUp() {
        mockService = new TransactionServiceImpl();
    }

    @Test
    void should_add_new_payer_and_update_user_balance() {
        Transaction transaction = new Transaction("ADIDAS", 200, LocalDateTime.now());
        mockService.addPointsToSinglePayer(transaction);

        assertEquals(200, mockService.getUserTotalBalance());
        assertEquals(200, mockService.getTotalPointsByPayer(transaction.getPayerName()));
    }

    @Test
    void should_add_transaction_to_payer_and_update_payer_balance_and_user_balance() {
        Transaction transaction1 = new Transaction("ADIDAS", 200, LocalDateTime.now());
        mockService.addPointsToSinglePayer(transaction1);

        Transaction transaction2 = new Transaction("NIKE", 500, LocalDateTime.now());
        mockService.addPointsToSinglePayer(transaction2);

        Transaction transaction3 = new Transaction("ADIDAS", 1000, LocalDateTime.now());
        mockService.addPointsToSinglePayer(transaction3);

        assertEquals(1700, mockService.getUserTotalBalance());
        assertEquals(1200, mockService.getTotalPointsByPayer("ADIDAS"));
        assertEquals(500, mockService.getTotalPointsByPayer("NIKE")); //NIKE is unaffected
    }

    @Test
    void should_reduce_points_from_single_payer_and_update_or_delete_transaction_balances() {
        Transaction transaction1 = new Transaction("ADIDAS", 200, LocalDateTime.now());
        mockService.addPointsToSinglePayer(transaction1);

        Transaction transaction2 = new Transaction("ADIDAS", 1000, LocalDateTime.now());
        mockService.addPointsToSinglePayer(transaction2);

        Transaction transaction3 = new Transaction("ADIDAS", -400, LocalDateTime.now());
        mockService.deductPointsFromSinglePayer(transaction3);

        assertEquals(800, mockService.getUserTotalBalance());
        assertEquals(800, mockService.getTotalPointsByPayer("ADIDAS"));
    }

    @Test
    void should_reduce_points_oldest_first_and_return_list_of_deductions() {
        Transaction transaction1 = new Transaction("ADIDAS", 200, LocalDateTime.now());
        mockService.addPointsToSinglePayer(transaction1);

        Transaction transaction2 = new Transaction("NIKE", 500, LocalDateTime.now());
        mockService.addPointsToSinglePayer(transaction2);

        Transaction transaction3 = new Transaction("ADIDAS", 1000, LocalDateTime.now());
        mockService.addPointsToSinglePayer(transaction3);

        List<Transaction> deductions = mockService.deductTotalPoints(400);

        assertEquals(1300, mockService.getUserTotalBalance());
        assertEquals(1000, mockService.getTotalPointsByPayer("ADIDAS"));
        assertEquals(300, mockService.getTotalPointsByPayer("NIKE"));
        assertEquals(2, deductions.size());

        int totalDeducted = deductions.stream()
                .map(Transaction::getPoints)
                .reduce(0, Integer::sum);

        assertEquals(totalDeducted, -400);
    }

    @Test
    void should_return_list_of_user_balances_per_payer() {
        Transaction transaction1 = new Transaction("ADIDAS", 200, LocalDateTime.now());
        mockService.addPointsToSinglePayer(transaction1);

        Transaction transaction2 = new Transaction("NIKE", 500, LocalDateTime.now());
        mockService.addPointsToSinglePayer(transaction2);

        Transaction transaction3 = new Transaction("PUMA", 1000, LocalDateTime.now());
        mockService.addPointsToSinglePayer(transaction3);

        List<PayerBalance> balances = mockService.getAllPayerBalances();

        assertEquals(3, balances.size());

        int totalBalance = balances.stream()
                .map(PayerBalance::getPointsBalance)
                .reduce(0, Integer::sum);

        assertEquals(totalBalance, mockService.getUserTotalBalance());
    }
}
