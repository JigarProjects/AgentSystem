package com.agent;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Jigar on 7/28/2017.
 */

public class Pong extends Agent {
    final int PONG_PORT = 5000;
    ServerSocket pongListenerSocket;
    String pongAgentID;
    @Override
    public void service() {
        try {
            //open serversocket which listens to incoming request and responds
            pongListenerSocket = new ServerSocket(PONG_PORT);
            //register with consul
            pongAgentID = register(PONG_PORT, "Pong");
            //actual implementaion
            while(true){
                System.out.println("PongAgent[id="+ pongAgentID +"] waiting for pings ");
                //Step 1 : Listen to incoming request and parse it
                Socket pongServer = pongListenerSocket.accept();

                DataInputStream inputStream = new DataInputStream(pongServer.getInputStream());
                JSONObject incomingRequest = Agent.readFromStream(inputStream);
                String requestorID = (String) incomingRequest.get("requestor_id");
                System.out.println("Received "+ incomingRequest.get("message") +" from PingAgent[id='"+ requestorID +" ']");

                //Step 2: Send request
                String pongMesssage ="pong";
                System.out.println("Sending "+pongMesssage+" to PingAgent[id="+requestorID+"]");
                DataOutputStream objectStream = new DataOutputStream(pongServer.getOutputStream());
                Agent.writeToStream(objectStream, pongAgentID, pongMesssage);

                pongServer.close();
                System.out.println(" ....... ...... ...... ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
