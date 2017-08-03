package com.agent;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.QueryParams;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.agent.model.NewService;
import com.ecwid.consul.v1.catalog.model.CatalogService;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.InetAddress;
import java.util.List;
import java.util.Properties;
import java.util.Random;

/**
 * Created by Jigar on 7/28/2017.
 */
public abstract class Agent {
    private static String configurationFilePath= "config.properties";
    String agent_name;
    private static String consulServer;
    private static Integer consulPort;
    private static String serviceAddress;
    static ConsulClient consulClient;
    static{
        try {
            setUp();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setConfigurationPath(String filePath){
        configurationFilePath =  filePath;
    }

    private static void setUp() throws IOException {
        try {
            Properties prop = new Properties();

            InputStream input = new FileInputStream(configurationFilePath);
            prop.load(input);
            serviceAddress = InetAddress.getLocalHost().getHostAddress().toString();
            consulServer = prop.getProperty("consul_server");
            consulPort = Integer.parseInt(prop.getProperty("consul_port"));
            if (prop.getProperty("serviceAddress")!= null ){
                serviceAddress = prop.getProperty("serviceAddress");
            }
            if (consulServer == null) {
                throw new IllegalArgumentException("Set parameters");
            }

            consulClient = new ConsulClient(consulServer, consulPort);
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
        Response<Void> response = consulClient.agentServiceRegister(newService);
        System.out.println(response);

        return uniqueid;
    }
    public abstract void service();

    public CatalogService findService(String serviceToFind){
        CatalogService catalogService = null;
        Response<List<CatalogService>> lookupResponse = consulClient.getCatalogService(serviceToFind, QueryParams.DEFAULT);
        int responseSize = lookupResponse.getValue().size();
        if(responseSize != 0) {
            //fetch service at random
            Random random = new Random();
            int number = random.nextInt() % lookupResponse.getValue().size();
            catalogService = lookupResponse.getValue().get(number);
        }
        return catalogService;
    }

    //utility method to write to socket
    protected static void writeToStream(DataOutputStream dataOutputStream, String agent_id, String message) throws IOException {
        Model request = new Model(agent_id, message);
        dataOutputStream.writeUTF( request.toString() );
    }

    protected static JSONObject readFromStream(DataInputStream dataInputStream) throws IOException, ParseException {
        JSONObject incomingRequest = null;
        String requestString = dataInputStream.readUTF();
        incomingRequest = Model.convertToJSON(requestString);
        return incomingRequest;
    }
}
