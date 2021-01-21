package personal.kcm3394.points.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TransactionDto {

    @NotNull
    private final String payerName;

    @NotNull
    private final int points;

    @NotNull
    private final LocalDateTime transactionDate;
}
