package personal.kcm3394.points.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@ToString
@AllArgsConstructor
public class Transaction {

    @NotNull
    private final String payerName;

    @NotNull
    private final int points;

    @NotNull
    private final LocalDateTime transactionDate;
}
