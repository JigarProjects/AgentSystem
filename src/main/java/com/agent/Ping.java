package com.agent;
import com.ecwid.consul.v1.catalog.model.CatalogService;

import java.io.*;
import java.net.Socket;



/**
 * Created by Jigar on 7/29/2017.
 */
public class Ping extends Agent{
    final int PING_PORT = 5001;
    Integer pongServicePort = null;
    String pongServiceID = null;
    String pongServiceAddress = null;
    @Override
    public void service() {

        register(PING_PORT,"Ping");
        CatalogService pongService = null;

        while(pongService == null) {
            System.out.println("Looking for PongAgents");
            pongService = findService("Pong");

            //if agent found
            //  check if it's active; if not then deregister it and get another agent
            //else sleep for 30 s
            if(pongService != null){
                try {
                    pongServiceAddress = pongService.getServiceAddress();
                    pongServicePort = pongService.getServicePort();
                    pongServiceID = pongService.getServiceId();
                    Socket client = new Socket(pongServiceAddress, pongServicePort);
                    System.out.println("Found PongAgent{id="+pongServiceID+"} at " + pongServiceAddress + " on port " + pongServicePort);

                    System.out.println("Sending ping to PongAgent[id="+pongServiceID+" ]" + client.getRemoteSocketAddress());
                    OutputStream outToServer = client.getOutputStream();
                    DataOutputStream out = new DataOutputStream(outToServer);
                    out.writeUTF("ping ");

                    InputStream inFromServer = client.getInputStream();
                    DataInputStream in = new DataInputStream(inFromServer);
                    System.out.println("PingAgent[id=]: Received " + in.readUTF()+" from PongAgent[id=]"+pongServiceID);
                    client.close();

                } catch (IOException e) {
                    //service is not available; deregister it
                    System.out.println("deregistrering stale service");
                    client.agentServiceDeregister(pongService.getServiceId());
                    pongService = null;
                    break;
                }
                //perform task

                break;
            }else{
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


    }
}
