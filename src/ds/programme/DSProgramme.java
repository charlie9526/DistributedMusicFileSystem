package ds.programme;

import ds.communication.SearchQuery;
import ds.communication.request.SearchRequest;
import ds.constant.Constant;
import ds.controller.NodeOperator;
import ds.controller.NodeRegistrar;
import ds.credential.Credential;

import java.util.*;

public class DSProgramme {

    public static void main(String[] args) {

        String [] paramNames = {"boostrap IP","boostrap Port","boostrap Username","node Ip","node Port","node Username"};
        HashMap<String, String> paramsMap = new HashMap<>();
        for (int i = 0; i < args.length; i = i + 2) {
            paramsMap.put(args[i], args[i + 1]);
            System.out.println(paramNames[i/2] + " : " + args[i + 1]);
        }

        String bootstrapIp = paramsMap.get("-bi") != null ? paramsMap.get("-bi") : Constant.IPConstants.get("IP_BOOTSTRAP_SERVER");
        String nodeIp = paramsMap.get("-ni") != null ? paramsMap.get("-ni") : Constant.IPConstants.get("IP_BOOTSTRAP_SERVER");
        int nodePort = paramsMap.get("-np") != null ? Integer.parseInt(paramsMap.get("-np")) : new Random().nextInt(Constant.portConstants.get("MAX_PORT_NODE") - Constant.portConstants.get("MIN_PORT_NODE")) + Constant.portConstants.get("MIN_PORT_NODE");
        String nodeUsername = paramsMap.get("-nu") != null ? paramsMap.get("-nu") : UUID.randomUUID().toString();

        Credential bootstrapServerCredential = new Credential(bootstrapIp, Constant.portConstants.get("PORT_BOOTSTRAP_SERVER"), Constant.usernameConstants.get("USERNAME_BOOTSTRAP_SERVER"));
        Map<Integer, String> searchQueryTable = new HashMap<>();

        List<String> searchQueries = Arrays.asList("Adventures_of_Tintin", "Harry_Potter");
        Collections.shuffle(searchQueries);

//        Generate self credentials
        Credential nodeCredential = new Credential(nodeIp, nodePort, nodeUsername);

//        Register the node with bootstrap
        NodeRegistrar nodeRegistrar = new NodeRegistrar(bootstrapServerCredential,nodeCredential);

//        Initiate the thread for UDP connection
        NodeOperator nodeOperator = new NodeOperator(bootstrapServerCredential, nodeRegistrar);

//        Register in network
        nodeRegistrar.register();

        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (nodeOperator.getNodeRegistrar().isRegOK()) {
                for (int i = 0; i < searchQueries.size(); i++) {
                    System.out.println(searchQueries.get(i));
                    String uuid = UUID.randomUUID().toString();
                    nodeOperator.getNode().addSearchQuery(new SearchQuery(uuid,searchQueries.get(i)));
                    SearchRequest searchRequest = new SearchRequest(uuid, nodeOperator.getNode().getCredential(), nodeOperator.getNode().getSearchQueryByID(uuid).getQueryFileNameString(), 0,nodeOperator.getNode().getCredential());
                    nodeOperator.triggerSearchRequest(searchRequest);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
        }

        while (true) ;
    }
}
