import com.agent.Agent;
import com.agent.Ping;
import com.agent.Pong;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        Agent agent = null;
        if(args.length != 0){
            switch ( args[0] ) {
                case "Pong":
                    agent = new Pong();
                    break;
                case "Ping":
                    agent = new Ping();
                    break;
                default:
                    throw new IllegalArgumentException("Invalid day agent Name " + args[0]);
            }
            agent.service();
        }else{
            throw new IllegalArgumentException("Provide agent Name " );
        }
    }
}
