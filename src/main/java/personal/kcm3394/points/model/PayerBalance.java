package personal.kcm3394.points.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class PayerBalance {

    public PayerBalance() {
    }

    public PayerBalance(String payerName, Integer pointsBalance) {
        this.payerName = payerName;
        this.pointsBalance = pointsBalance;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String payerName;
    private Integer pointsBalance;
}
