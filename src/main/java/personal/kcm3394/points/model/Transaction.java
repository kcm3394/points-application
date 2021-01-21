package personal.kcm3394.points.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/*
* Parent class for TransactionBalance and TransactionHistory to inherit shared properties
*/
@MappedSuperclass
@Getter
@Setter
public class Transaction {

    public Transaction(Long id, @NotNull String payerName, @NotNull Integer points, @NotNull LocalDateTime transactionDate) {
        this.id = id;
        this.payerName = payerName;
        this.points = points;
        this.transactionDate = transactionDate;
    }

    public Transaction() {
    }

    public Transaction(@NotNull String payerName, @NotNull Integer points, @NotNull LocalDateTime transactionDate) {
        this.payerName = payerName;
        this.points = points;
        this.transactionDate = transactionDate;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String payerName;

    @NotNull
    private Integer points;

    @NotNull
    private LocalDateTime transactionDate;
}
