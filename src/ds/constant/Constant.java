package ds.constant;

import java.util.HashMap;

public class Constant {

    public final static HashMap<String,String> IPConstants = new HashMap<String, String>();
    public final static HashMap<String,String> usernameConstants = new HashMap<String, String>();
    public final static HashMap<String,Integer> portConstants = new HashMap<String, Integer>();
    public final static HashMap<String,String> commandConstants = new HashMap<String, String>();
    public final static HashMap<String,Integer> codeConstants = new HashMap<String, Integer>();

    static {
        usernameConstants.put("USERNAME_BOOTSTRAP_SERVER","Bootstrap");
        IPConstants.put("IP_BOOTSTRAP_SERVER","127.0.0.1");

        portConstants.put("PORT_BOOTSTRAP_SERVER",55555);
        portConstants.put("MIN_PORT_NODE",40000);
        portConstants.put("MAX_PORT_NODE",50000);

        codeConstants.put("ERROR_CANNOT_REGISTER",9996);
        codeConstants.put("ERROR_DUPLICATE_IP",9997);
        codeConstants.put("ERROR_ALREADY_REGISTERED",9998);
        codeConstants.put("ERROR_COMMAND",9999);
        codeConstants.put("ERROR_NODE_UNREACHABLE",9999);
        codeConstants.put("ERROR_OTHER",9998);

        commandConstants.put("REG","REG");
        commandConstants.put("REGOK","REGOK");
        commandConstants.put("UNREG","UNREG");
        commandConstants.put("UNREGOK","UNROK");
        commandConstants.put("JOIN","JOIN");
        commandConstants.put("JOINOK","JOINOK");
        commandConstants.put("LEAVE","LEAVE");
        commandConstants.put("LEAVEOK","LEAVEOK");
        commandConstants.put("SEARCH","SER");
        commandConstants.put("SEARCHOK","SEROK");
        commandConstants.put("ERROR","ERROR");
    }
}