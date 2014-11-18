/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bitran.services;

import com.bitran.sockets.TransactionSocket;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
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
import org.springframework.stereotype.Service;

/**
 *
 * @author Willian
 */
@Service
public class BitcoinService {

    private final NetworkParameters netParams = MainNetParams.get();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    private void fetchTransactions() {
        System.out.println("post");
        Thread t = new Thread("tListener") {
            @Override
            public void run() {
                try {
                    BlockStore blockStore = new MemoryBlockStore(netParams);
                    BlockChain blockChain = new BlockChain(netParams, blockStore);
                    PeerGroup peerGroup = new PeerGroup(netParams, blockChain);
                    com.google.common.util.concurrent.Service service = peerGroup.startAsync();
                    peerGroup.addAddress(new PeerAddress(InetAddress.getLocalHost()));
                    Thread.sleep(10000);
                    Peer peer = peerGroup.getConnectedPeers().get(0);
                    PeerEventListener listener = new AbstractPeerEventListener() {
                        @Override
                        public void onTransaction(Peer p, Transaction t) {
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
                                System.out.println(message);
                                TransactionSocket.sendTransaction(message);
                            } catch (IOException ex) {

                            }

                        }

                    };
                    peer.addEventListener(listener);
                    System.out.println("fin");
                    while(true){
                        
                    }

                } catch (BlockStoreException | UnknownHostException | InterruptedException ex) {
                    Logger.getLogger(BitcoinService.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        };
        t.setDaemon(true);
        t.start();
    }
}
