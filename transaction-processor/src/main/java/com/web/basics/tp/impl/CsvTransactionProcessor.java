/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.web.basics.tp.impl;

import com.web.basics.tp.Transaction;
import com.web.basics.tp.TransactionProcessor;
import com.web.basics.tp.Violation;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author regan
 */
public class CsvTransactionProcessor implements TransactionProcessor {

    private List<Transaction> transactions;

    @Override
    public void importTransactions(InputStream is) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is));) {
            transactions = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                String strAmount = tokens[1];
                BigDecimal amount = getAmount(strAmount);
                transactions.add(new Transaction(tokens[0], amount, tokens[2]));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Error reading CSV", e);
        }
    }

    @Override
    public List<Transaction> getImportedTransactions() {
        return transactions;
    }

    @Override
    public boolean isBalanced() {
        BigDecimal balance = BigDecimal.ZERO;
        for (Transaction t:transactions) {
            balance = balance.add("C".equals(t.getType()) ? t.getAmount() : t.getAmount().negate());
        }
        return balance.compareTo(BigDecimal.ZERO) == 0;
    }

    @Override
    public List<Violation> validate() {
        List<Violation> violations = new ArrayList<>();
        for (int i = 0; i < transactions.size(); i++) {
            validateType(transactions.get(i), violations, i+1);
            validateAmount(transactions.get(i), violations, i+1);
        }
        return violations;
    }

    private BigDecimal getAmount(String strAmount) {
        try{
            return new BigDecimal(strAmount);
        }catch (NumberFormatException e){
            return BigDecimal.ZERO;
        }
    }

    private void validateType(Transaction transaction, List<Violation> violations, int index) {
        if (transaction.getType() == null || !Arrays.asList("C", "D").contains(transaction.getType())) {
            violations.add(new Violation(index, "type", "Violation in type of Transaction"));
        }
    }

    private void validateAmount(Transaction transaction, List<Violation> violations, int index) {
        if(transaction.getAmount() == null || transaction.getAmount().compareTo(BigDecimal.ZERO) == 0){
            violations.add(new Violation(index, "amount", "Violation in amount of Transaction"));
        }
    }
}
