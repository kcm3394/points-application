package personal.kcm3394.points.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/*
 * Entity for keeping non-modifiable records of payer transactions.
 */
@Getter
@Setter
@Entity
@Table(name = "transaction_history")
public class TransactionHistory extends Transaction {

    public TransactionHistory() {
    }

    public TransactionHistory(@NotNull String payerName, @NotNull Integer points, @NotNull LocalDateTime transactionDate) {
        super(payerName, points, transactionDate);
    }
}
