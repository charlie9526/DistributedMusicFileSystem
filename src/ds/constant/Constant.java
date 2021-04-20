package ds.constant;

import java.util.HashMap;

public class Constant {

    public final static HashMap<String,String> IPConstants = new HashMap<String, String>();
    public final static HashMap<String,String> usernameConstants = new HashMap<String, String>();
    public final static HashMap<String,Integer> serverPortConstants = new HashMap<String, Integer>();
    public final static HashMap<String,String> protocolConstants = new HashMap<String, String>();
    public final static HashMap<String,Integer> errorCodeConstants = new HashMap<String, Integer>();

    static {
        usernameConstants.put("USERNAME_BOOTSTRAP_SERVER","Bootstrap");
        IPConstants.put("IP_BOOTSTRAP_SERVER","127.0.0.1");

        serverPortConstants.put("PORT_BOOTSTRAP_SERVER",55555);
        serverPortConstants.put("MIN_PORT_NODE",4000);
        serverPortConstants.put("MAX_PORT_NODE",5000);
        serverPortConstants.put("MIN_REST_PORT_NODE",8000);
        serverPortConstants.put("MAX_REST_PORT_NODE",9000);
        serverPortConstants.put("PING_TIMEOUT",5000);


        errorCodeConstants.put("ERROR_CANNOT_REGISTER",9996);
        errorCodeConstants.put("ERROR_DUPLICATE_IP",9997);
        errorCodeConstants.put("ERROR_ALREADY_REGISTERED",9998);
        errorCodeConstants.put("ERROR_COMMAND",9999);
        errorCodeConstants.put("ERROR_NODE_UNREACHABLE",9999);
        errorCodeConstants.put("ERROR_OTHER",9998);
        errorCodeConstants.put("MAX_HOP", 10);

        protocolConstants.put("REG","REG");
        protocolConstants.put("REGOK","REGOK");
        protocolConstants.put("UNREG","UNREG");
        protocolConstants.put("UNREGOK","UNROK");
        protocolConstants.put("JOIN","JOIN");
        protocolConstants.put("JOINOK","JOINOK");
        protocolConstants.put("LEAVE","LEAVE");
        protocolConstants.put("LEAVEOK","LEAVEOK");
        protocolConstants.put("SEARCH","SER");
        protocolConstants.put("SEARCHOK","SEROK");
        protocolConstants.put("ERROR","ERROR");
        protocolConstants.put("PING","PING");
        protocolConstants.put("PONG","PONG");
    }
}