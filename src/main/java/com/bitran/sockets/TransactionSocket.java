/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bitran.sockets;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 *
 * @author Willian
 */
@ServerEndpoint("/transactionSocks")
public class TransactionSocket {
    public static Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());
    
    @OnOpen
    public void onOpen(Session s) {
        peers.add(s);
    }

    @OnClose
    public void onClose(Session s) {
        peers.remove(s);
    }
    public static void sendTransaction(String message){
        for(Session s: peers){
            try {
                s.getBasicRemote().sendText(message);
            } catch (IOException ex) {
                Logger.getLogger(TransactionSocket.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
