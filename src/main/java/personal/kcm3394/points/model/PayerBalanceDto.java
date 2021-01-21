package personal.kcm3394.points.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PayerBalanceDto {

    private final String payerName;
    private final int pointsBalance;
}
