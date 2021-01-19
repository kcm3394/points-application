package personal.kcm3394.points.service;

import personal.kcm3394.points.model.Transaction;

import java.util.List;

public interface TransactionService {

    void addPointsToSinglePayer(Transaction transaction);

    void deductPointsFromSinglePayer(Transaction transaction);

    List<Transaction> deductTotalPoints(int points);

    List<PayerBalance> getAllPayerBalances();
}
