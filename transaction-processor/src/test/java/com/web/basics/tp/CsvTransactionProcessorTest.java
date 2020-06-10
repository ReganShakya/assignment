/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.web.basics.tp;

import com.web.basics.tp.impl.CsvTransactionProcessor;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author regan
 */
public class CsvTransactionProcessorTest {
    
    private TransactionProcessor csvTransactionProcessor;
    
    private InputStream inputStream(String str) {
        return new ByteArrayInputStream(str.getBytes());
    }
    
    private Transaction newTransaction(String type, BigDecimal amount, String narration) {
        return new Transaction(type, amount, narration);
    }
    
    @Before
    public void init(){
        csvTransactionProcessor = new CsvTransactionProcessor();
    }
       
    @Test
    public void returnExpectedTransactions(){ //when importing given Csv Stream, retrun expected transactions
        InputStream is = inputStream("D,6100,electricity\nC,1920,salary\nD,150,rental");
        csvTransactionProcessor.importTransactions(is);
        List<Transaction> transactions = csvTransactionProcessor.getImportedTransactions();
        assertThat(transactions, containsInAnyOrder(
                newTransaction("C", new BigDecimal(1920), "salary"),
                newTransaction("D", new BigDecimal(6100), "electricity"),
                newTransaction("D", new BigDecimal(150), "rental")
        ));
    }
    
    @Test
    public void balanceReturnTrue() throws Exception { //when importing given csv stream, check if dr and cr are balanced and return true
        InputStream is = inputStream("D,1250,electricity\nC,1920, salary\nD,670,rental");
        csvTransactionProcessor.importTransactions(is);        
        assertEquals(true,csvTransactionProcessor.isBalanced());
    }
    
    @Test
    public void balanceReturnFalse() throws Exception{ //when importing given csv stream, check if dr and cr are unbalanced and return false
        InputStream is = inputStream("D,1250,electricity\nC,1900, salary\nD,670,rental");
        csvTransactionProcessor.importTransactions(is);        
        assertEquals(false,csvTransactionProcessor.isBalanced());
    }
    
    @Test
    public void invalidTransaction() throws Exception{ //when calling validate, if csvStream have invalid transaction then report the violations
        InputStream is = inputStream("D,1250,electricity\nS,1920, salary\nD,670,rental");
        csvTransactionProcessor.importTransactions(is);
        List<Violation> violations = csvTransactionProcessor.validate();        
        assertThat(violations, containsInAnyOrder(new Violation(2, "type", "Violation in type of transaction")));
    }
    
    @Test 
    public void multipleInvalidTransactions() throws Exception{ //when calling validate, if csvStream have multiple invalid transaction then report the violations
        InputStream is = inputStream("D,1250,electricity\nS,1900, salary\nD,six,rental");
        csvTransactionProcessor.importTransactions(is);
        List<Violation> violations = csvTransactionProcessor.validate();        
        assertThat(violations, containsInAnyOrder(new Violation(2,"type","Violation in type of transaction"),
                new Violation(3,"amount","Violation in amount of Transaction")));
    }
    
    @Test 
    public void multipleErrorsInSameTransaction() throws Exception{ //when calling validate, if csvStream have multiple errors in same transaction then report the violations
        InputStream is = inputStream("D,1250,electricity\nS,hundred, salary\nD,six,rental");
        csvTransactionProcessor.importTransactions(is);
        List<Violation> violations = csvTransactionProcessor.validate();        
        assertThat(violations, containsInAnyOrder(new Violation(2,"type","Violation in type of transaction"),
                new Violation(3,"amount","Violation in amount of Transaction"),
                 new Violation(2,"amount","Violation in amount of Transaction")));
    }
}
