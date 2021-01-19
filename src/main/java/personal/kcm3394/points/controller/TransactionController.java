package personal.kcm3394.points.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import personal.kcm3394.points.model.Transaction;
import personal.kcm3394.points.service.HistoryService;
import personal.kcm3394.points.service.PayerBalance;
import personal.kcm3394.points.service.TransactionService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/points")
public class TransactionController {

    private final TransactionService transactionService;
    private final HistoryService historyService;

    @GetMapping
    public ResponseEntity<List<PayerBalance>> listAllPayerBalancesForUser() {
        List<PayerBalance> balances = transactionService.getAllPayerBalances();
        return ResponseEntity.ok(balances);
    }

    @PostMapping("/add-points")
    public ResponseEntity<Void> addPointsToUserAccount(@RequestBody @Valid Transaction transaction) {
        if (transaction.getPoints() > 0) {
            transactionService.addPointsToSinglePayer(transaction);
        } else {
            transactionService.deductPointsFromSinglePayer(transaction);
        }

        historyService.addTransactionToHistory(transaction);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/spend-points")
    public ResponseEntity<List<Transaction>> deductTotalPointsFromUserAccount(@RequestParam int points) {
        List<Transaction> deductions = transactionService.deductTotalPoints(points);
        deductions.forEach(historyService::addTransactionToHistory);
        return ResponseEntity.ok(deductions);
    }

}
