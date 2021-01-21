package personal.kcm3394.points.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import personal.kcm3394.points.model.PayerBalance;
import personal.kcm3394.points.model.Transaction;
import personal.kcm3394.points.model.TransactionBalance;
import personal.kcm3394.points.model.TransactionHistory;
import personal.kcm3394.points.repository.PayerBalanceRepository;
import personal.kcm3394.points.repository.TransactionBalanceRepository;
import personal.kcm3394.points.repository.TransactionHistoryRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class TransactionServiceTest {

    @Mock
    private PayerBalanceRepository payerRepository;

    @Mock
    private TransactionBalanceRepository transactionRepository;

    @Mock
    private TransactionHistoryRepository historyRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    void should_add_new_payer_balance_and_add_to_history() {
        TransactionBalance transaction = new TransactionBalance("ADIDAS", 200, LocalDateTime.now());

        when(payerRepository.findByPayerNameEquals(anyString())).thenReturn(Optional.empty());

        transactionService.addPointsToSinglePayer(transaction);

        verify(historyRepository, times(1)).save(any(TransactionHistory.class));
        verify(transactionRepository, times(1)).save(any(TransactionBalance.class));

        ArgumentCaptor<PayerBalance> payerCaptor = ArgumentCaptor.forClass(PayerBalance.class);
        verify(payerRepository, times(1)).save(payerCaptor.capture());
        assertEquals(200, payerCaptor.getValue().getPointsBalance());
    }

    @Test
    void should_update_existing_balance_of_payer_and_add_to_history() {
        TransactionBalance transaction = new TransactionBalance("ADIDAS", 200, LocalDateTime.now());
        PayerBalance payerBalance = new PayerBalance("ADIDAS", 500);

        when(payerRepository.findByPayerNameEquals(anyString())).thenReturn(Optional.of(payerBalance));

        transactionService.addPointsToSinglePayer(transaction);

        verify(historyRepository, times(1)).save(any(TransactionHistory.class));
        verify(transactionRepository, times(1)).save(any(TransactionBalance.class));

        ArgumentCaptor<PayerBalance> payerCaptor = ArgumentCaptor.forClass(PayerBalance.class);
        verify(payerRepository, times(1)).save(payerCaptor.capture());
        assertEquals(700, payerCaptor.getValue().getPointsBalance());
    }

    @Test
    void should_reduce_points_from_single_payer_and_update_or_delete_transaction_balances() {
        TransactionBalance transaction = new TransactionBalance("ADIDAS", -400, LocalDateTime.now());
        PayerBalance payerBalance = new PayerBalance("ADIDAS", 700);

        when(payerRepository.findByPayerNameEquals(anyString())).thenReturn(Optional.of(payerBalance));
        when(transactionRepository.findByPayerNameEqualsOrderByTransactionDate(anyString())).thenReturn(getAdidasTransactionList());

        transactionService.deductPointsFromSinglePayer(transaction);

        verify(historyRepository, times(1)).save(any(TransactionHistory.class));

        // Verify that first transaction was deleted
        ArgumentCaptor<Long> deleteTransactionCaptor = ArgumentCaptor.forClass(Long.class);
        verify(transactionRepository, times(1)).deleteById(deleteTransactionCaptor.capture());
        assertEquals(1L, deleteTransactionCaptor.getValue());

        // Verify that second transaction has 300 pts remaining
        ArgumentCaptor<TransactionBalance> updateTransactionCaptor = ArgumentCaptor.forClass(TransactionBalance.class);
        verify(transactionRepository, times(1)).save(updateTransactionCaptor.capture());
        assertEquals(300, updateTransactionCaptor.getValue().getPoints());

        // Verify that payerTotal is updated to reflect loss of points
        ArgumentCaptor<PayerBalance> payerCaptor = ArgumentCaptor.forClass(PayerBalance.class);
        verify(payerRepository, times(1)).save(payerCaptor.capture());
        assertEquals(300, payerCaptor.getValue().getPointsBalance());
    }

    @Test
    void should_deduct_points_oldest_first_and_return_list_of_deductions() {
        when(payerRepository.findByPayerNameEquals("ADIDAS")).thenReturn(Optional.of(getAdidasPayerBalance()));
        when(payerRepository.findByPayerNameEquals("NIKE")).thenReturn(Optional.of(getNikePayerBalance()));
        when(transactionRepository.findAllByOrderByTransactionDate()).thenReturn(getTransactionList());
        when(payerRepository.findAll()).thenReturn(getAllPayerBalances());

        List<TransactionBalance> deductions = transactionService.deductTotalPoints(400);

        // Verify points balance for ADIDAS and NIKE updated
        ArgumentCaptor<PayerBalance> payerCaptor = ArgumentCaptor.forClass(PayerBalance.class);
        verify(payerRepository, times(2)).save(payerCaptor.capture());
        List<PayerBalance> captured = payerCaptor.getAllValues();
        assertEquals(1000, captured.get(0).getPointsBalance());
        assertEquals(300, captured.get(1).getPointsBalance());

        assertEquals(2, deductions.size());
        int totalDeducted = deductions.stream()
                .map(Transaction::getPoints)
                .reduce(0, Integer::sum);
        assertEquals(totalDeducted, -400);
    }

    @Test
    void should_return_list_of_user_balances_per_payer() {
        when(payerRepository.findAll()).thenReturn(getAllPayerBalances());

        List<PayerBalance> balances = transactionService.getAllPayerBalances();

        assertEquals(2, balances.size());
    }

    private List<TransactionBalance> getTransactionList() {
        TransactionBalance transaction1 = new TransactionBalance(1L, "ADIDAS", 200, LocalDateTime.now());
        TransactionBalance transaction2 = new TransactionBalance(2L, "NIKE", 500, LocalDateTime.now());
        TransactionBalance transaction3 = new TransactionBalance(3L, "ADIDAS", 1000, LocalDateTime.now());

        return List.of(transaction1, transaction2, transaction3);
    }

    private List<TransactionBalance> getAdidasTransactionList() {
        TransactionBalance transaction1 = new TransactionBalance(1L, "ADIDAS", 200, LocalDateTime.now());
        TransactionBalance transaction2 = new TransactionBalance(2L, "ADIDAS", 500, LocalDateTime.now());

        return List.of(transaction1, transaction2);
    }

    private PayerBalance getAdidasPayerBalance() {
        return new PayerBalance("ADIDAS", 1200);
    }

    private PayerBalance getNikePayerBalance() {
        return new PayerBalance("NIKE", 500);
    }

    private List<PayerBalance> getAllPayerBalances() {
        return List.of(getAdidasPayerBalance(), getNikePayerBalance());
    }
}
