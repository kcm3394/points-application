package personal.kcm3394.points.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/*
* Entity for keeping modifiable records of payer transactions so points can be deducted from oldest transactions.
*/
@Getter
@Setter
@Entity
@Table(name = "transaction_balances")
public class TransactionBalance extends Transaction {

    public TransactionBalance(Long id, @NotNull String payerName, @NotNull Integer points, @NotNull LocalDateTime transactionDate) {
        super(id, payerName, points, transactionDate);
    }

    public TransactionBalance() {
    }

    public TransactionBalance(@NotNull String payerName, @NotNull Integer points, @NotNull LocalDateTime transactionDate) {
        super(payerName, points, transactionDate);
    }
}
