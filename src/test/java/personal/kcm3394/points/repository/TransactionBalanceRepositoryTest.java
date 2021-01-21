package personal.kcm3394.points.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import personal.kcm3394.points.model.TransactionBalance;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class TransactionBalanceRepositoryTest {

    @Autowired
    private TransactionBalanceRepository transactionRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private TransactionBalance testTransaction1;
    private TransactionBalance testTransaction2;
    private TransactionBalance testTransaction3;

    @BeforeEach
    void setUp() {
        testTransaction1 = new TransactionBalance("ADIDAS", 200, LocalDateTime.of(2021, 1, 1, 12, 0));
        testEntityManager.persist(testTransaction1);

        testTransaction2 = new TransactionBalance("NIKE", 500, LocalDateTime.of(2021, 1, 3, 12, 0));
        testEntityManager.persist(testTransaction2);

        testTransaction3 = new TransactionBalance("ADIDAS", 1000, LocalDateTime.of(2020, 12, 1, 12, 0));
        testEntityManager.persistAndFlush(testTransaction3);
    }

    @Test
    void should_return_saved_transaction() {
        TransactionBalance transaction = new TransactionBalance("ADIDAS", 200, LocalDateTime.now());

        TransactionBalance saved = transactionRepository.save(transaction);

        assertTrue(transactionRepository.findById(saved.getId()).isPresent());
        assertThat(transactionRepository.findById(saved.getId()).get(), equalTo(saved));
    }

    @Test
    void should_update_transaction() {
        testTransaction1.setPoints(testTransaction1.getPoints() - 100);

        TransactionBalance changed = transactionRepository.save(testTransaction1);

        assertEquals(testTransaction1.getId(), changed.getId());
        assertEquals(100, transactionRepository.findById(testTransaction1.getId()).get().getPoints());
    }

    @Test
    void should_delete_transaction() {
        transactionRepository.deleteById(testTransaction1.getId());

        assertFalse(transactionRepository.findById(testTransaction1.getId()).isPresent());
    }

    @Test
    void should_return_list_of_transactions_for_single_payer_oldest_first() {
        List<TransactionBalance> transactions = transactionRepository.findByPayerNameEqualsOrderByTransactionDate("ADIDAS");

        assertEquals(2, transactions.size());
        assertEquals(testTransaction3, transactions.get(0));
    }

    @Test
    void should_return_list_of_all_transactions_oldest_first() {
        List<TransactionBalance> transactions = transactionRepository.findAllByOrderByTransactionDate();

        assertEquals(3, transactions.size());
        assertEquals(testTransaction3, transactions.get(0));
        assertEquals(testTransaction1, transactions.get(1));
        assertEquals(testTransaction2, transactions.get(2));
    }
}
