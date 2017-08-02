import com.agent.Agent;
import com.agent.Ping;
import com.agent.Pong;

import java.io.IOException;

public class AgentSystem {

    public static void main(String[] args) throws IOException {
        Agent agent = null;
        if(args.length == 2){
            Agent.setConfigurationPath(args[0]);
            switch ( args[1] ) {
                case "PongAgent":
                    agent = new Pong();
                    break;
                case "PingAgent":
                    agent = new Ping();
                    break;
                default:
                    throw new IllegalArgumentException("Invalid agent Name " + args[0]);
            }
            agent.service();
        }else{
            throw new IllegalArgumentException("Provide agent Name or given more than 1 argument" );
        }
    }
}
