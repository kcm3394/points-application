package personal.kcm3394.points.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import personal.kcm3394.points.model.PayerBalance;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class PayerBalanceRepositoryTest {

    @Autowired
    private PayerBalanceRepository payerRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private PayerBalance adidasBalance;

    @BeforeEach
    void setUp() {
        adidasBalance = new PayerBalance("ADIDAS", 200);
        testEntityManager.persist(adidasBalance);

        PayerBalance nikeBalance = new PayerBalance("NIKE", 500);
        testEntityManager.persist(nikeBalance);

        PayerBalance pumaBalance = new PayerBalance("PUMA", 100);
        testEntityManager.persistAndFlush(pumaBalance);
    }

    @Test
    void should_return_saved_payer_balance() {
        PayerBalance balance = new PayerBalance("ADIDAS", 200);

        PayerBalance saved = payerRepository.save(balance);

        assertTrue(payerRepository.findById(saved.getId()).isPresent());
        assertThat(payerRepository.findById(saved.getId()).get(), equalTo(saved));
    }

    @Test
    void should_update_payer_balance() {
        adidasBalance.setPointsBalance(adidasBalance.getPointsBalance() - 100);
        PayerBalance changed = payerRepository.save(adidasBalance);

        assertEquals(adidasBalance.getId(), changed.getId());
        assertEquals(100, payerRepository.findById(adidasBalance.getId()).get().getPointsBalance());
    }

    @Test
    void should_return_all_payer_balances() {
        List<PayerBalance> balances = payerRepository.findAll();

        assertEquals(3, balances.size());
    }

    @Test
    void should_return_payer_balance_by_payer_name() {
        Optional<PayerBalance> balance = payerRepository.findByPayerNameEquals("ADIDAS");

        assertTrue(balance.isPresent());
        assertEquals(200, balance.get().getPointsBalance());
    }
}
