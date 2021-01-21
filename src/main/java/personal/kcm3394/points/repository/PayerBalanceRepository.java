package personal.kcm3394.points.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import personal.kcm3394.points.model.PayerBalance;

import java.util.Optional;

@Repository
public interface PayerBalanceRepository extends JpaRepository<PayerBalance, Long> {

    Optional<PayerBalance> findByPayerNameEquals(String payerName);
}
