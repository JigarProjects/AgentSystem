package com.agent;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.QueryParams;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.agent.model.NewService;
import com.ecwid.consul.v1.catalog.model.CatalogService;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.Properties;
import java.util.Random;

/**
 * Created by Jigar on 7/28/2017.
 */
public abstract class Agent {
    String agent_name;
    private static String consulServer;
    private static Integer consulPort;
    private static String serviceAddress;
    static ConsulClient client;
    static{
        try {
            setUp();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void setUp() throws IOException {
        try {
            Properties prop = new Properties();


            InputStream input = Agent.class.getClassLoader().getResourceAsStream("config.properties");
            prop.load(input);
            serviceAddress = InetAddress.getLocalHost().getHostAddress().toString();
            consulServer = prop.getProperty("consul_server");
            consulPort = Integer.parseInt(prop.getProperty("consul_port"));
            if (consulServer == null) {
                throw new IllegalArgumentException("Set parameters");
            }

            client = new ConsulClient(consulServer, consulPort);
        }
        catch(IOException e){
            System.out.println("Improper configuration ");
            throw e;
        }
    }

    public String getUniqueID(int port, String serviceName){
        String uniqueID= serviceAddress+":"+port+"/"+serviceName;
        return uniqueID;
    }
    public String register(int port, String serviceName){
        String uniqueid = getUniqueID(port,serviceName);

        NewService newService = new NewService();
        newService.setId(uniqueid);
        newService.setName(serviceName);
        newService.setAddress(serviceAddress);
        newService.setPort(port);
        Response<Void> response = client.agentServiceRegister(newService);
        System.out.println(response);

        return uniqueid;
    }
    public abstract void service();

    public CatalogService findService(String serviceToFind){
        CatalogService catalogService = null;
        Response<List<CatalogService>> lookupResponse = client.getCatalogService(serviceToFind, QueryParams.DEFAULT);
        int responseSize = lookupResponse.getValue().size();
        if(responseSize != 0) {
            //fetch service at random
            Random random = new Random();
            int number = random.nextInt() % lookupResponse.getValue().size();
            catalogService = lookupResponse.getValue().get(number);
        }
        return catalogService;
        /*for(CatalogService service: lookupResponse.getValue()){
            System.out.println(service.getServiceAddress()+" "+service.getServicePort());
            boolean isAvailable = service(service.getServiceAddress(),service.getServicePort(),service.getServiceId());
            if(isAvailable){
                System.out.println("");
                break;
            }else{
                System.out.println("deregistering : "+service);
                client.agentServiceDeregister(service.getServiceId());
            }
        }*/
    }
}
