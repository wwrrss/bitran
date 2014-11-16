/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bitran.services;

import com.bitran.sockets.TransactionSocket;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
import org.bitcoinj.params.TestNet2Params;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.MemoryBlockStore;
import org.springframework.stereotype.Component;

/**
 *
 * @author Willian
 */
@Component
public class BitcoinService {
    private final NetworkParameters netParams = TestNet2Params.get();
    private ObjectMapper objectMapper = new ObjectMapper();
    @PostConstruct
    private void fetchTransactions(){
        try {
            BlockStore blockStore = new MemoryBlockStore(netParams);
            BlockChain blockChain = new BlockChain(netParams, blockStore);
            PeerGroup peerGroup = new PeerGroup(netParams,blockChain);
            peerGroup.startAsync();
            peerGroup.addAddress(new PeerAddress(InetAddress.getLocalHost()));
            Thread.sleep(10000);
            
            Peer peer = peerGroup.getConnectedPeers().get(0);
            PeerEventListener listener = new AbstractPeerEventListener(){
              @Override
              public void onTransaction(Peer p,Transaction t){
                  try {
                      com.bitran.models.Transaction tx = new com.bitran.models.Transaction();
                      tx.setTxid(t.getHashAsString());
                      Long total=0L;
                      for(TransactionOutput to:t.getOutputs()){
                          Coin value = to.getValue();
                          total+=value.value;
                      }
                      tx.setAmount(total);
                      String message = objectMapper.writeValueAsString(tx);
                      
                      TransactionSocket.sendTransaction(message);
                  } catch (IOException ex) {
                      Logger.getLogger(BitcoinService.class.getName()).log(Level.SEVERE, null, ex);
                  }
                  
              }
              
            };
            peer.addEventListener(listener);
        } catch (BlockStoreException | InterruptedException ex) {
            Logger.getLogger(BitcoinService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(BitcoinService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
}
