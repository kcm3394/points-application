package personal.kcm3394.points.service;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionNode {

    public TransactionNode(String payerName, int points, LocalDateTime transactionDate) {
        this.payerName = payerName;
        this.points = points;
        this.transactionDate = transactionDate;
    }

    public TransactionNode() {}

    private String payerName;
    private int points;
    private LocalDateTime transactionDate;

    /* For use in doubly linked list. Keeps reference to previous transaction in date order. */
    private TransactionNode prev;

    /* For use in doubly linked list. Keeps reference to next transaction in date order. */
    private TransactionNode next;


}
