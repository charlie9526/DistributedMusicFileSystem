package ds.constant;

import java.util.HashMap;

public class Constant {

    public final static HashMap<String,String> IPConstants = new HashMap<String, String>();
    public final static HashMap<String,String> usernameConstants = new HashMap<String, String>();
    public final static HashMap<String,Integer> serverPortConstants = new HashMap<String, Integer>();
    public final static HashMap<String,String> protocolConstants = new HashMap<String, String>();
    public final static HashMap<String,Integer> errorCodeConstants = new HashMap<String, Integer>();
    public  final static HashMap<String, String> logColorConstants = new HashMap<>();
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

        logColorConstants.put( "ANSI_BLACK", "\u001B[30m");
        logColorConstants.put( "BOLD_PURPLE", "\033[1;35m");
        logColorConstants.put( "ANSI_PURPLE", "\u001B[35m");
        logColorConstants.put( "ANSI_GREEN", "\u001B[32m");
        logColorConstants.put( "ANSI_BLUE", "\u001B[34m");
        logColorConstants.put( "ANSI_YELLOW", "\u001B[33m");
        logColorConstants.put( "ANSI_RED", "\u001B[31m");
        logColorConstants.put( "ANSI_WHITE", "\u001B[37m");
        logColorConstants.put( "ANSI_RESET", "\u001B[0m");
        logColorConstants.put("BOLD_BLACK", "\033[1;30m");
        logColorConstants.put("BOLD_RED", "\033[1;31m");


    }
}