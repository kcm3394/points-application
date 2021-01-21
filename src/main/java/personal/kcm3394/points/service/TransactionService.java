package personal.kcm3394.points.service;

import personal.kcm3394.points.model.PayerBalance;
import personal.kcm3394.points.model.TransactionBalance;

import java.util.List;

public interface TransactionService {

    void addPointsToSinglePayer(TransactionBalance transaction);

    void deductPointsFromSinglePayer(TransactionBalance transaction);

    List<TransactionBalance> deductTotalPoints(int points);

    List<PayerBalance> getAllPayerBalances();
}
