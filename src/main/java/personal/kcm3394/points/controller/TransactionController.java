package personal.kcm3394.points.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import personal.kcm3394.points.model.*;
import personal.kcm3394.points.service.TransactionService;

import javax.validation.Valid;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/points")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<List<PayerBalanceDto>> listAllPayerBalancesForUser() {
        List<PayerBalance> balances = transactionService.getAllPayerBalances();
        return ResponseEntity.ok(convertEntitiesToPayerBalanceDtos(balances));
    }

    @PostMapping("/add-points")
    public ResponseEntity<Void> addPointsToUserAccount(@RequestBody @Valid TransactionDto transactionDto) {
        if (transactionDto.getPoints() > 0) {
            transactionService.addPointsToSinglePayer(convertTransactionDtoToEntity(transactionDto));
        } else {
            transactionService.deductPointsFromSinglePayer(convertTransactionDtoToEntity(transactionDto));
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/spend-points")
    public ResponseEntity<List<TransactionDto>> deductTotalPointsFromUserAccount(@RequestParam int points) {
        List<TransactionBalance> deductions = transactionService.deductTotalPoints(points);
        return ResponseEntity.ok(convertEntitiesToTransactionDtos(deductions));
    }

    private static List<PayerBalanceDto> convertEntitiesToPayerBalanceDtos(List<PayerBalance> balances) {
        List<PayerBalanceDto> dtos = new LinkedList<>();
        balances.forEach(balance -> dtos.add(convertEntityToPayerBalanceDto(balance)));
        return dtos;
    }

    private static PayerBalanceDto convertEntityToPayerBalanceDto(PayerBalance balance) {
        return new PayerBalanceDto(balance.getPayerName(), balance.getPointsBalance());
    }

    private static List<TransactionDto> convertEntitiesToTransactionDtos(List<TransactionBalance> transactions) {
        List<TransactionDto> dtos = new LinkedList<>();
        transactions.forEach(transaction -> dtos.add(convertEntityToTransactionDto(transaction)));
        return dtos;
    }

    private static TransactionBalance convertTransactionDtoToEntity(TransactionDto dto) {
        TransactionBalance transaction = new TransactionBalance();
        BeanUtils.copyProperties(dto, transaction);
        return transaction;
    }

    private static TransactionDto convertEntityToTransactionDto(TransactionBalance transaction) {
        return new TransactionDto(transaction.getPayerName(), transaction.getPoints(), transaction.getTransactionDate());
    }

}
