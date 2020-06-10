/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.web.basics.tp;

/**
 *
 * @author regan
 */
import java.io.InputStream;
import java.util.List;


public interface TransactionProcessor {

    void importTransactions(InputStream is);

    List<Transaction> getImportedTransactions();

    List<Violation> validate();

    boolean isBalanced();
}
