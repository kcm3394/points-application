package personal.kcm3394.points.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import personal.kcm3394.points.model.PayerBalance;
import personal.kcm3394.points.model.Transaction;
import personal.kcm3394.points.model.TransactionBalance;
import personal.kcm3394.points.model.TransactionHistory;
import personal.kcm3394.points.repository.PayerBalanceRepository;
import personal.kcm3394.points.repository.TransactionBalanceRepository;
import personal.kcm3394.points.repository.TransactionHistoryRepository;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final TransactionBalanceRepository transactionRepository;
    private final TransactionHistoryRepository historyRepository;
    private final PayerBalanceRepository payerRepository;

    @Override
    public void addPointsToSinglePayer(TransactionBalance transaction) {
        String payerName = transaction.getPayerName();
        int payerPoints = transaction.getPoints();

        historyRepository.save(convertTransactionToTransactionHistory(transaction));
        transactionRepository.save(transaction);

        Optional<PayerBalance> optionalPayer = payerRepository.findByPayerNameEquals(payerName);
        if (optionalPayer.isPresent()) {
            PayerBalance found = optionalPayer.get();
            found.setPointsBalance(found.getPointsBalance() + payerPoints);
            payerRepository.save(found);
        } else {
            PayerBalance newPayer = new PayerBalance(payerName, payerPoints);
            payerRepository.save(newPayer);
        }
    }

    /*
     * Adds negative transaction by reducing oldest points of payer
     */
    @Override
    public void deductPointsFromSinglePayer(TransactionBalance transaction) {
        String payerName = transaction.getPayerName();
        int payerPoints = transaction.getPoints(); // negative

        Optional<PayerBalance> optionalPayer = payerRepository.findByPayerNameEquals(payerName);
        if (optionalPayer.isEmpty() || optionalPayer.get().getPointsBalance() + payerPoints < 0) {
            throw new IllegalArgumentException("Payer balance cannot be negative");
        }

        historyRepository.save(convertTransactionToTransactionHistory(transaction));
        deductPointsFromOldestTransactionsOfSinglePayer(-payerPoints, payerName);

        PayerBalance found = optionalPayer.get();
        found.setPointsBalance(found.getPointsBalance() + payerPoints);
        payerRepository.save(found);
    }

    /*
     * Deducts a set number of points given as a positive integer from the specified payer's transaction balances, starting with
     * the oldest. If the transaction's balance becomes zero, remove transaction balance. The specified payer will still have a
     * PayerTotal record where the total points are 0 and there are no positive transaction balances left.
     */
    private void deductPointsFromOldestTransactionsOfSinglePayer(int pointsToDeduct, String payerName) {
        Iterator<TransactionBalance> payerBalances = transactionRepository.findByPayerNameEqualsOrderByTransactionDate(payerName).iterator();
        while (pointsToDeduct > 0) {
            TransactionBalance oldestTransaction = payerBalances.next();
            deductPointsFromSingleTransaction(oldestTransaction, pointsToDeduct);
            pointsToDeduct -= oldestTransaction.getPoints();
        }
    }

    private void deductPointsFromSingleTransaction(TransactionBalance transaction, int pointsToDeduct) {
        int currPoints = transaction.getPoints();
        if (pointsToDeduct < currPoints) {
            transaction.setPoints(currPoints - pointsToDeduct);
            transactionRepository.save(transaction);
        } else {
            transactionRepository.deleteById(transaction.getId());
        }
    }

    /*
     * Returns a list of the deducted points by transaction, starting with the oldest points.
     */
    @Override
    public List<TransactionBalance> deductTotalPoints(int points) {
        if (points > getUserTotalBalance()) {
            throw new IllegalArgumentException("User balance cannot be negative");
        }

        List<TransactionBalance> deductions = new LinkedList<>();
        Iterator<TransactionBalance> allTransactions = transactionRepository.findAllByOrderByTransactionDate().iterator();
        int pointsToDeduct = points;

        while (pointsToDeduct > 0) {
            TransactionBalance oldestTransaction = allTransactions.next();
            deductPointsFromSingleTransaction(oldestTransaction, pointsToDeduct);

            // Update payer's points balance
            int pointsDeductedFromPayer = -Math.min(pointsToDeduct, oldestTransaction.getPoints());
            // Payer's TransactionBalance will have a corresponding PayerBalance
            @SuppressWarnings("OptionalGetWithoutIsPresent") PayerBalance payerToDeductFrom = payerRepository.findByPayerNameEquals(oldestTransaction.getPayerName()).get();
            payerToDeductFrom.setPointsBalance(payerToDeductFrom.getPointsBalance() + pointsDeductedFromPayer);
            payerRepository.save(payerToDeductFrom);

            // Add deduction to transactionHistory and deductions list
            TransactionBalance deductDetail = new TransactionBalance(oldestTransaction.getPayerName(), pointsDeductedFromPayer, LocalDateTime.now());
            historyRepository.save(convertTransactionToTransactionHistory(deductDetail));
            deductions.add(deductDetail);

            pointsToDeduct -= oldestTransaction.getPoints();
        }

        return deductions;
    }

    private int getUserTotalBalance() {
        List<PayerBalance> allBalances = payerRepository.findAll();
        return allBalances.stream()
                .map(PayerBalance::getPointsBalance)
                .reduce(0, Integer::sum);
    }

    @Override
    public List<PayerBalance> getAllPayerBalances() {
        return payerRepository.findAll();
    }

    private TransactionHistory convertTransactionToTransactionHistory(Transaction transaction) {
        TransactionHistory history = new TransactionHistory();
        BeanUtils.copyProperties(transaction, history);
        return history;
    }
}
