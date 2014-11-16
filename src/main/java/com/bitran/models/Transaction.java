/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bitran.models;

import java.math.BigInteger;

/**
 *
 * @author Willian
 */
public class Transaction {
    private String txid;
    private Long amount;

    public Transaction() {
        this.txid ="";
        this.amount =0L;
    }
    

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
    
    
}
