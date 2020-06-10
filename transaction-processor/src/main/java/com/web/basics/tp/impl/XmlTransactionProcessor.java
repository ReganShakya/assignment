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
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

/**
 *
 * @author regan
 */
public class XmlTransactionProcessor implements TransactionProcessor{

    List<Transaction> transactions;
    List<Violation> violations;
    
    @Override
    public void importTransactions(InputStream is){        
        transactions = new ArrayList<>();
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String inline = "";
        try{
            while((inline = inputReader.readLine()) != null){
                sb.append(inline);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        SAXBuilder builder = new SAXBuilder();
        
        try{
            org.jdom2.Document document = builder.build(new ByteArrayInputStream(sb.toString().getBytes()));
            Element transactionsElement = document.getRootElement();
            
            List<Element> transactionList = transactionsElement.getChildren();
            
            for(Element transaction:transactionList){
                Transaction t = new Transaction();
                t.setType(transaction.getAttribute("type").getValue());
                t.setAmount(getAmount(transaction.getAttribute("amount").getValue()));
                t.setNarration(transaction.getAttribute("narration").getValue());
                transactions.add(t);
            }
        }catch(Exception e){
            e.printStackTrace();
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
