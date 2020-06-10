/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.web.basics.tp;

import com.web.basics.tp.impl.XmlTransactionProcessor;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
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
public class XmlTransactionProcessorTest{
    private TransactionProcessor xmlTransactionProcessor;
    
    private Transaction newTransaction(String type, BigDecimal amount, String narration) {
        return new Transaction(type, amount, narration);
    }
    
    @Before
    public void init(){
        xmlTransactionProcessor = new XmlTransactionProcessor();
    }
    
    @Test
    public void returnExpectedTransactions(){ //when importing given Csv Stream, retrun expected transactions
        InputStream is = new ByteArrayInputStream(("<TransactionList>\n" +
            "    <Transaction type=\"D\" amount=\"6100\" narration=\"electricity\" />\n" +
            "    <Transaction type=\"C\" amount=\"1920\" narration=\"salary\"/>\n" +
            "    <Transaction type=\"D\" amount=\"150\" narration=\"rental\"/>\n" +
            "</TransactionList>").getBytes(StandardCharsets.UTF_8));
        xmlTransactionProcessor.importTransactions(is);
        List<Transaction> transactions = xmlTransactionProcessor.getImportedTransactions();        
        assertThat(transactions, containsInAnyOrder(
                newTransaction("D", new BigDecimal(6100), "electricity"),
                newTransaction("C", new BigDecimal(1920), "salary"),
                newTransaction("D", new BigDecimal(150), "rental")
        ));
    }
    
    @Test
    public void balanceReturnTrue() throws Exception { //when importing given csv stream, check if dr and cr are balanced and return true
        InputStream is = new ByteArrayInputStream(("<TransactionList>\n" +
            "    <Transaction type=\"D\" amount=\"1250\" narration=\"electricity\" />\n" +
            "    <Transaction type=\"C\" amount=\"1900\" narration=\"salary\"/>\n" +
            "    <Transaction type=\"D\" amount=\"650\" narration=\"rental\"/>\n" +
            "</TransactionList>").getBytes(StandardCharsets.UTF_8));
        xmlTransactionProcessor.importTransactions(is);        
        assertEquals(true,xmlTransactionProcessor.isBalanced());
    }
    
    @Test
    public void balanceReturnFalse() throws Exception{ //when importing given csv stream, check if dr and cr are unbalanced and return false
        InputStream is = new ByteArrayInputStream(("<TransactionList>\n" +
            "    <Transaction type=\"D\" amount=\"1250\" narration=\"electricity\" />\n" +
            "    <Transaction type=\"C\" amount=\"1900\" narration=\"salary\"/>\n" +
            "    <Transaction type=\"D\" amount=\"750\" narration=\"rental\"/>\n" +
            "</TransactionList>").getBytes(StandardCharsets.UTF_8));
        xmlTransactionProcessor.importTransactions(is);        
        assertEquals(false,xmlTransactionProcessor.isBalanced());
    }
    
    @Test
    public void invalidTransaction() throws Exception{ //when calling validate, if csvStream have invalid transaction then report the violations
        InputStream is = new ByteArrayInputStream(("<TransactionList>\n" +
            "    <Transaction type=\"D\" amount=\"1250\" narration=\"electricity\" />\n" +
            "    <Transaction type=\"S\" amount=\"1900\" narration=\"salary\"/>\n" +
            "    <Transaction type=\"D\" amount=\"650\" narration=\"rental\"/>\n" +
            "</TransactionList>").getBytes(StandardCharsets.UTF_8));
        xmlTransactionProcessor.importTransactions(is);
        List<Violation> violations = xmlTransactionProcessor.validate();        
        assertThat(violations, containsInAnyOrder(new Violation(2, "type", "Violation in type of transaction")));
    }
    
    @Test 
    public void multipleInvalidTransactions() throws Exception{ //when calling validate, if csvStream have multiple invalid transaction then report the violations
        InputStream is = new ByteArrayInputStream(("<TransactionList>\n" +
            "    <Transaction type=\"D\" amount=\"1250\" narration=\"electricity\" />\n" +
            "    <Transaction type=\"S\" amount=\"1900\" narration=\"salary\"/>\n" +
            "    <Transaction type=\"D\" amount=\"six\" narration=\"rental\"/>\n" +
            "</TransactionList>").getBytes(StandardCharsets.UTF_8));
        xmlTransactionProcessor.importTransactions(is);
        List<Violation> violations = xmlTransactionProcessor.validate();        
        assertThat(violations, containsInAnyOrder(new Violation(2,"type","Violation in type of transaction"),
                new Violation(3,"amount","Violation in amount of Transaction")));
    }
    
    @Test 
    public void multipleErrorsInSameTransaction() throws Exception{ //when calling validate, if csvStream have multiple errors in same transaction then report the violations
        InputStream is = new ByteArrayInputStream(("<TransactionList>\n" +
            "    <Transaction type=\"D\" amount=\"1250\" narration=\"electricity\" />\n" +
            "    <Transaction type=\"S\" amount=\"hundred\" narration=\"salary\"/>\n" +
            "    <Transaction type=\"D\" amount=\"six\" narration=\"rental\"/>\n" +
            "</TransactionList>").getBytes(StandardCharsets.UTF_8));
        xmlTransactionProcessor.importTransactions(is);
        List<Violation> violations = xmlTransactionProcessor.validate();        
        assertThat(violations, containsInAnyOrder(new Violation(2,"type","Violation in type of transaction"),
                new Violation(3,"amount","Violation in amount of Transaction"),
                 new Violation(2,"amount","Violation in amount of Transaction")));
    }
}
