package personal.kcm3394.points.service;

import lombok.Getter;
import lombok.Setter;

import java.util.PriorityQueue;

@Getter
@Setter
public class PayerTransactions {

    private int totalPoints;

    /*
    * Ordered queue of payer transactions with a current balance
    */
    private final PriorityQueue<TransactionNode> payerTransactionHistory = new PriorityQueue<>((t1, t2) -> t1.getTransactionDate().compareTo(t2.getTransactionDate()));
}
