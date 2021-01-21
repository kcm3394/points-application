package personal.kcm3394.points.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import personal.kcm3394.points.model.TransactionBalance;

import java.util.List;

@Repository
public interface TransactionBalanceRepository extends JpaRepository<TransactionBalance, Long> {

    List<TransactionBalance> findByPayerNameEqualsOrderByTransactionDate(String payerName);

    List<TransactionBalance> findAllByOrderByTransactionDate();
}
