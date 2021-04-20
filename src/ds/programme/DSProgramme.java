package ds.programme;

import ds.communication.request.SearchRequest;
import ds.constant.Constant;
import ds.controller.NodeOperator;
import ds.controller.NodeRegistrar;
import ds.controller.TimeKeeperSingleton;
import ds.controller.Watchman;
import ds.credential.Credential;
import ds.downloadAPI.HttpResthandler;
import ds.node.Node;

import java.util.Scanner;
import java.io.IOException;
import java.util.*;

public class DSProgramme {

    public static void main(String[] args) {

        String[] parameterNames = {"boostrap IP", "boostrap Port", "boostrap Username", "node Ip", "node Port", "node Username"};
        HashMap<String, String> parametersMap = new HashMap<>();
        for (int i = 0; i < args.length; i = i + 2) {
            parametersMap.put(args[i], args[i + 1]);
            System.out.println(parameterNames[i / 2] + " : " + args[i + 1]);
        }

        String bootstrapServerIp = parametersMap.get("-bi") != null ? parametersMap.get("-bi") : Constant.IPConstants.get("IP_BOOTSTRAP_SERVER");
        String nodeIp = parametersMap.get("-ni") != null ? parametersMap.get("-ni") : Constant.IPConstants.get("IP_BOOTSTRAP_SERVER");
        int nodePort = parametersMap.get("-np") != null ? Integer.parseInt(parametersMap.get("-np")) : new Random().nextInt(Constant.serverPortConstants.get("MAX_PORT_NODE") - Constant.serverPortConstants.get("MIN_PORT_NODE")) + Constant.serverPortConstants.get("MIN_PORT_NODE");
        int pingPort = parametersMap.get("-pp") != null ? Integer.parseInt(parametersMap.get("-pp")) : new Random().nextInt(Constant.serverPortConstants.get("MAX_PORT_NODE") - Constant.serverPortConstants.get("MIN_PORT_NODE")) + Constant.serverPortConstants.get("MIN_PORT_NODE");
        String nodeUsername = parametersMap.get("-nu") != null ? parametersMap.get("-nu") : UUID.randomUUID().toString();
        int restAPIPort = parametersMap.get("-np") != null ? Integer.parseInt(parametersMap.get("-np")) : new Random().nextInt(Constant.serverPortConstants.get("MAX_REST_PORT_NODE") - Constant.serverPortConstants.get("MIN_REST_PORT_NODE")) + Constant.serverPortConstants.get("MIN_PORT_NODE");


        Node.createFile(nodePort);

        Credential bootstrapServerCredential = new Credential(bootstrapServerIp, Constant.serverPortConstants.get("PORT_BOOTSTRAP_SERVER"), Constant.usernameConstants.get("USERNAME_BOOTSTRAP_SERVER"));
        Map<Integer, String> searchQueryTable = new HashMap<>();

        List<String> searchQueries = Arrays.asList("Twilight", "Jack", "American_Idol", "Happy_Feet", "Twilight_saga", "Happy_Feet", "Happy_Feet", "Feet", "Happy_Feet", "Twilight", "Windows", "Happy_Feet", "Mission_Impossible", "Twilight", "Windows_8", "The", "Happy", "Windows_8", "Happy_Feet", "Super_Mario", "Jack_and_Jill", "Happy_Feet", "Impossible", "Happy_Feet", "Turn_Up_The_Music", "Adventures_of_Tintin", "Twilight_saga", "Happy_Feet", "Super_Mario", "American_Pickers", "Microsoft_Office_2010", "Twilight", "Modern_Family", "Jack_and_Jill", "Jill", "Glee", "The_Vampire_Diarie", "King_Arthur", "Jack_and_Jill", "King_Arthur", "Windows_XP", "Harry_Potter", "Feet", "Kung_Fu_Panda", "Lady_Gaga", "Gaga", "Happy_Feet", "Twilight", "Hacking", "King");
        Collections.shuffle(searchQueries);
        searchQueries = searchQueries.subList(0, 4);
        Collections.shuffle(searchQueries);

//        Generate node credentials
        Credential nodeCredential = new Credential(nodeIp, nodePort, nodeUsername);

//        Register the node with bootstrap server
        NodeRegistrar nodeRegistrar = new NodeRegistrar(bootstrapServerCredential, nodeCredential, pingPort);

//        Initiate the UDP connection thread
        NodeOperator nodeOperator = new NodeOperator(bootstrapServerCredential, nodeRegistrar);

//        Register the node
        nodeRegistrar.register();

        TimeKeeperSingleton timeKeeper = TimeKeeperSingleton.getTimeKeeper();
        timeKeeper.addObserver(nodeOperator);
        timeKeeper.addNodeToList(nodeRegistrar.getNode());
        timeKeeper.start();

        HttpResthandler restAPI = new HttpResthandler();
        try {
            restAPI.up(nodePort, nodeOperator.getNode().getCredential().getIp());
        } catch (IOException e) {
            e.printStackTrace();
        }


        nodeOperator.getNode().setRestAPI(restAPI);


        Watchman.createWatchman(nodeRegistrar.getNode());
        Watchman.getWatchman().start();



        Scanner sc = new Scanner(System.in);

        String input = sc.next();

        if (input.equals("start")) {


            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (nodeOperator.getNodeRegistrar().isRegOK()) {
                for (int i = 0; i < searchQueries.size(); i++) {
                    Node.logMessage("");
                    //                    System.out.println(searchQueries.get(i));
                    String uuid = UUID.randomUUID().toString() + "-" + nodeOperator.getNode().getCredential().getUsername();
                    SearchRequest searchRequest = new SearchRequest(uuid, nodeOperator.getNode().getCredential(), searchQueries.get(i), 0, nodeOperator.getNode().getCredential());


                    //long start = System.currentTimeMillis();
                    nodeOperator.triggerSearchRequest(searchRequest);
                    //long finish = System.currentTimeMillis();
                    //long timeElapsed = finish - start;
                    //System.out.println("Query latency : " + searchQueries.get(i) + " : " + timeElapsed + " ms");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        } else if (input.equals("leave")){
            List<Credential> routingTable = nodeOperator.getNode().getRoutingTable();
           for(int i=0; i < routingTable.size(); i++){
               nodeOperator.leave(routingTable.get(i));
           }
            System.exit(0);
        }

        while (true) {
            try {
                nodeOperator.printMessageStats();
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}


