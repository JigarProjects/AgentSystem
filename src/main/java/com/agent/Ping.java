package com.agent;

import com.ecwid.consul.v1.catalog.model.CatalogService;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;

import static com.agent.Agent.readFromStream;

/**
 * Created by Jigar on 7/29/2017.
 */
public class Ping extends Agent{
    final int PING_PORT = 5001;
    String pingAgentID = null;

    Integer pongAgentPort = null;
    String pongAgentID = null;
    String pongAgentAddress = null;

    @Override
    public void service() {

        pingAgentID = register(PING_PORT,"Ping");
        CatalogService pongService = null;

        while(pongService == null) {
            //Step 1: Lookup PongAgent
            System.out.println("Looking for PongAgents");
            pongService = findService("Pong");

            if(pongService != null){
                try {
                    pongAgentAddress = pongService.getServiceAddress();
                    pongAgentPort = pongService.getServicePort();
                    pongAgentID = pongService.getServiceId();
                    //Step 2 : check connection; if the connection is stale then deregister it using IOException
                    Socket pongClient = new Socket(pongAgentAddress, pongAgentPort);
                    System.out.println("Found PongAgent{id="+pongAgentID+"} at " + pongAgentAddress + " on port " + pongAgentPort);

                    //Step 3 : prepare message and send it
                    String pingMessage = "ping";
                    System.out.println("Sending "+ pingMessage +" to PongAgent[id="+pongAgentID+" ]  " + pongClient.getRemoteSocketAddress());
                    DataOutputStream dataOutputStream= new DataOutputStream(pongClient.getOutputStream());
                    Agent.writeToStream(dataOutputStream, pingAgentID, pingMessage);

                    //Step 4: print the response
                    DataInputStream dataInputStream = new DataInputStream(pongClient.getInputStream());
                    JSONObject incomingJSON = Agent.readFromStream(dataInputStream);
                    System.out.println("PingAgent[id="+pingAgentID+"]: Received " + incomingJSON.get("message")+" from PongAgent[id=]"+pongAgentID);
                    pongClient.close();

                    break;
                } catch (IOException e) {
                    //service is not available; deregister it
                    System.out.println("deregistrering stale service");
                    consulClient.agentServiceDeregister(pongService.getServiceId());
                    pongService = null;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }else{
                //sleep for 30s; if there are no pongAgent
                try {
                    //Thread.sleep(30000);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


    }
}
