package personal.kcm3394.points.service;

import org.springframework.stereotype.Service;
import personal.kcm3394.points.model.Transaction;

import java.util.LinkedList;
import java.util.List;

@Service
public class HistoryServiceImpl implements HistoryService {

    private final List<Transaction> transactionHistory = new LinkedList<>();

    @Override
    public void addTransactionToHistory(Transaction transaction) {
        transactionHistory.add(transaction);
    }

    @Override
    public List<Transaction> getUserHistory() {
        return new LinkedList<>(transactionHistory);
    }
}
