package personal.kcm3394.points.service;

import org.springframework.stereotype.Service;
import personal.kcm3394.points.model.Transaction;

import java.time.LocalDateTime;
import java.util.*;

/*
* Accesses user transaction data for payer balances. Allows adding, removing, and updating a payer's points for the user.
*/
@Service
public class TransactionServiceImpl implements TransactionService {

    private int userTotalBalance;
    private final Map<String, PayerTransactions> transactionsByPayer = new HashMap<>();
    private final Timeline transactionTimeline = new Timeline();

    @Override
    public void addPointsToSinglePayer(Transaction transaction) {
        String payerName = transaction.getPayerName();
        int payerPoints = transaction.getPoints();

        userTotalBalance += payerPoints;

        transactionsByPayer.putIfAbsent(payerName, new PayerTransactions());
        PayerTransactions entry = transactionsByPayer.get(payerName);
        entry.setTotalPoints(entry.getTotalPoints() + payerPoints);

        TransactionNode node = new TransactionNode(payerName, payerPoints, transaction.getTransactionDate());
        transactionTimeline.appendNode(node);
        entry.getPayerTransactionHistory().add(node);
    }

    /*
    * Adds negative transaction to TransactionStore by reducing oldest points of payer
    */
    @Override
    public void deductPointsFromSinglePayer(Transaction transaction) {
        String payerName = transaction.getPayerName();
        int payerPoints = transaction.getPoints();

        if (!transactionsByPayer.containsKey(payerName) ||
                transactionsByPayer.get(payerName).getTotalPoints() + payerPoints < 0) {
            throw new IllegalArgumentException("Payer balance cannot be negative");
        }

        userTotalBalance += payerPoints; // points are negative

        PayerTransactions entry = transactionsByPayer.get(payerName);
        entry.setTotalPoints(entry.getTotalPoints() + payerPoints);
        updatePayerTransactionHistory(-payerPoints, entry.getPayerTransactionHistory()); // takes a positive integer
    }

    /*
    * Deducts a set number of points given as a positive integer from the payer's transaction history, starting with the oldest.
    * If transaction's balance becomes zero, remove from payerTransactionHistory and from doubly linked list. The HashMap
    * transactionsByPayer will still have a key/value pair where the value's total points is 0 and there are no positive balance
    * transactions left.
    */
    private void updatePayerTransactionHistory(int pointsToDeduct, PriorityQueue<TransactionNode> payerTransactionHistory) {
        while (pointsToDeduct > 0) {
            TransactionNode oldestTransaction = payerTransactionHistory.peek();
            int currPoints = oldestTransaction.getPoints();
            if (pointsToDeduct < currPoints) {
                oldestTransaction.setPoints(currPoints - pointsToDeduct);
            } else {
                payerTransactionHistory.poll();
                transactionTimeline.removeNode(oldestTransaction);
            }
            pointsToDeduct -= currPoints;
        }
    }

    /*
    * Returns a list of the deducted points by transaction, starting with the oldest points.
    */
    @Override
    public List<Transaction> deductTotalPoints(int points) {
        List<Transaction> deductions = new LinkedList<>();
        if (points > userTotalBalance) {
            throw new IllegalArgumentException("User balance cannot be negative");
        }

        userTotalBalance -= points;

        int pointsToDeduct = points;
        TransactionNode currNode = transactionTimeline.getOldestTransactionNode();
        while (pointsToDeduct > 0) {
            PayerTransactions currPayerTransactions = transactionsByPayer.get(currNode.getPayerName());
            int currPoints = currNode.getPoints();

            if (pointsToDeduct < currPoints) {
                currNode.setPoints(currPoints - pointsToDeduct);
            } else {
                currPayerTransactions.getPayerTransactionHistory().poll();
                transactionTimeline.removeNode(currNode);
            }

            int pointsDeductedFromPayer = -Math.min(pointsToDeduct, currPoints);
            currPayerTransactions.setTotalPoints(currPayerTransactions.getTotalPoints() + pointsDeductedFromPayer); // update total in Map
            Transaction deductDetail = new Transaction(currNode.getPayerName(), pointsDeductedFromPayer, LocalDateTime.now());
            deductions.add(deductDetail);

            pointsToDeduct -= currPoints;
            currNode = currNode.getNext();
        }

        return deductions;
    }

    @Override
    public List<PayerBalance> getAllPayerBalances() {
        List<PayerBalance> balances = new LinkedList<>();
        transactionsByPayer.forEach((payerName, payerTransaction) -> {
            balances.add(new PayerBalance(payerName, payerTransaction.getTotalPoints()));
        });
        return balances;
    }

    public int getUserTotalBalance() {
        return userTotalBalance;
    }

    public int getTotalPointsByPayer(String payerName) {
        return transactionsByPayer.get(payerName).getTotalPoints();
    }

}
