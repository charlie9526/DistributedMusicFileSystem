package ds.programme;

import ds.communication.request.SearchRequest;
import ds.constant.Constant;
import ds.controller.NodeOperator;
import ds.credential.Credential;

import java.util.*;

public class DSProgramme {

    public static void main(String[] args) {

        HashMap<String, String> paramMap = new HashMap<>();

        for (int i = 0; i < args.length; i = i + 2) {
            paramMap.put(args[i], args[i + 1]);
            System.out.println(args[i] + " : " + args[i + 1]);
        }

        System.out.println();

        String bootstrapIp = paramMap.get("-b") != null ? paramMap.get("-b") : Constant.IP_BOOTSTRAP_SERVER;
        String nodeIp = paramMap.get("-i") != null ? paramMap.get("-i") : Constant.IP_BOOTSTRAP_SERVER;
        int nodePort = paramMap.get("-p") != null ? Integer.parseInt(paramMap.get("-p")) : new Random().nextInt(Constant.MAX_PORT_NODE - Constant.MIN_PORT_NODE) + Constant.MIN_PORT_NODE;
        String nodeUsername = paramMap.get("-u") != null ? paramMap.get("-u") : UUID.randomUUID().toString();

        Credential bootstrapServerCredential = new Credential(bootstrapIp, Constant.PORT_BOOTSTRAP_SERVER, Constant.USERNAME_BOOTSTRAP_SERVER);
        Map<Integer, String> searchQueryTable = new HashMap<>();
        List<String> searchQueries = Arrays.asList("Twilight", "Jack", "American_Idol", "Happy_Feet", "Twilight_saga", "Happy_Feet", "Feet");
        Collections.shuffle(searchQueries);

        // Generate self credentials
        Credential nodeCredential = new Credential(nodeIp, nodePort, nodeUsername);

        // Initiate the thread for UDP connection
        NodeOperator nodeOperator = new NodeOperator(bootstrapServerCredential, nodeCredential);

        // Register in network
        nodeOperator.register();
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (nodeOperator.isRegOk()) {
                for (int i = 0; i < searchQueries.size(); i++) {
                    searchQueryTable.put(i, searchQueries.get(i));
                    SearchRequest searchRequest = new SearchRequest(1, nodeOperator.getNode().getCredential(), searchQueryTable.get(i), 0);
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
