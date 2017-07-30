package com.agent;

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
    ServerSocket pongSocket;
    @Override
    public void service() {
        try {
            //open socket
            pongSocket = new ServerSocket(PONG_PORT);
            //register with consul
            register(PONG_PORT, "Pong");
            //actual implementaion
            while(true){
                System.out.println("PongAgent[id=] waiting for pings ");
                Socket server = pongSocket.accept();

                DataInputStream in = new DataInputStream(server.getInputStream());
                System.out.println("Received "+in.readUTF()+" from PingAgent[id='']");

                System.out.println("Sending pong to PingAgent[id=]");
                DataOutputStream out = new DataOutputStream(server.getOutputStream());
                out.writeUTF("pong ");

                server.close();
                System.out.println(" ....... ...... ...... ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
