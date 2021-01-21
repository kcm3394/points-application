package personal.kcm3394.points.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import personal.kcm3394.points.model.TransactionBalance;
import personal.kcm3394.points.model.TransactionHistory;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class TransactionHistoryRepositoryTest {

    @Autowired
    private TransactionHistoryRepository historyRepository;

    @Test
    void should_return_saved_transaction() {
        TransactionHistory transaction = new TransactionHistory("ADIDAS", 200, LocalDateTime.now());

        TransactionHistory saved = historyRepository.save(transaction);

        assertTrue(historyRepository.findById(saved.getId()).isPresent());
        assertThat(historyRepository.findById(saved.getId()).get(), equalTo(saved));
    }
}
