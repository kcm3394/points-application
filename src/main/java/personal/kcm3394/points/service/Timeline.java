package personal.kcm3394.points.service;

import lombok.Getter;

/*
* Keeps the payer transactions in date order from oldest to newest by adding new transactions to the end and removing or
* updating oldest transactions from the start. Head and Tail TransactionNodes allow access to start and end of Timeline.
*/
@Getter
public class Timeline {

    private final TransactionNode head = new TransactionNode();
    private final TransactionNode tail = new TransactionNode();

    public Timeline() {
        head.setNext(tail);
        tail.setPrev(head);
    }

    public void removeNode(TransactionNode node) {
        TransactionNode prev = node.getPrev();
        TransactionNode next = node.getNext();

        prev.setNext(next);
        next.setPrev(prev);
    }

    public void appendNode(TransactionNode node) {
        node.setPrev(tail.getPrev());
        node.setNext(tail);

        tail.getPrev().setNext(node);
        tail.setPrev(node);
    }

    public TransactionNode getOldestTransactionNode() {
        return head.getNext();
    }

    public TransactionNode getNewestTransactionNode() {
        return tail.getPrev();
    }

}
