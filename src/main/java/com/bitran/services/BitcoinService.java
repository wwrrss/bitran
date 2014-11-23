/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bitran.services;

import com.bitran.sockets.TransactionSocket;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ListenableFuture;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.bitcoinj.core.AbstractPeerEventListener;
import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.Coin;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Peer;
import org.bitcoinj.core.PeerAddress;
import org.bitcoinj.core.PeerEventListener;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionOutput;

import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.MemoryBlockStore;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 *
 * @author Willian
 */
@Service
public class BitcoinService {

    private final NetworkParameters netParams = MainNetParams.get();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private PeerGroup peerGroup;
    private Peer peer;
    private PeerEventListener listener;

    @PostConstruct
    public void ejecutar() {

        fetchTransactions();

    }

    private void fetchTransactions() {
        try {
            System.out.println("post");

            BlockStore blockStore = new MemoryBlockStore(netParams);
            BlockChain blockChain = new BlockChain(netParams, blockStore);
            peerGroup = new PeerGroup(netParams, blockChain);
            listener = new AbstractPeerEventListener() {
                @Override
                public void onTransaction(Peer p, Transaction t) {
                    
                    broadCastTransaction(t);
                    p = null;
                    t = null;
                }

            };
            PeerEventListener listenConnected = new AbstractPeerEventListener() {
                @Override
                public void onPeerConnected(Peer p, int peerCount) {

                    System.out.println("new peer, peers: " + peerCount);
                    p.addEventListener(listener);
                }
            };
            peerGroup.addEventListener(listenConnected);
            com.google.common.util.concurrent.Service service = peerGroup.startAsync();
            peerGroup.addAddress(new PeerAddress(InetAddress.getLocalHost()));
            Thread.sleep(10000);

            System.out.println("fin");
        } catch (BlockStoreException | InterruptedException | UnknownHostException ex) {
            Logger.getLogger(BitcoinService.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Async
    public void broadCastTransaction(Transaction t) {
        try {
            com.bitran.models.Transaction tx = new com.bitran.models.Transaction();
            tx.setTxid(t.getHashAsString());
            Long total = 0L;
            for (TransactionOutput to : t.getOutputs()) {
                Coin value = to.getValue();
                total += value.value;
            }
            tx.setAmount(total);
            String message = objectMapper.writeValueAsString(tx);

            TransactionSocket.sendTransaction(message);
            t = null;
            total = null;
            tx = null;
            message = null;

        } catch (IOException ex) {

        }
    }

}
