package personal.kcm3394.points.service;

import personal.kcm3394.points.model.Transaction;

import java.util.List;

public interface HistoryService {

    void addTransactionToHistory(Transaction transaction);

    List<Transaction> getUserHistory();
}
