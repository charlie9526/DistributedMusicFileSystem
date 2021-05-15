package ds.controller;

import ds.communication.Message;
import ds.communication.request.*;
import ds.communication.response.*;
import ds.constant.Constant;
import ds.credential.Credential;
import ds.downloadAPI.FileDownloader;
import ds.node.Node;
import ds.node.NodeOperations;
import ds.parser.Parser;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class NodeOperator implements NodeOperations, Runnable, Observer {

    private Node node;
    private DatagramSocket socket;
    private NodeRegistrar nodeRegistrar;
    private Hashtable<String, Long> timeRecorder;
    private int received;
    private int forwarded;
    private int answered;
    private int backward;
    private int successCount;
    private int downloadErrorCount;

    public NodeOperator(Credential nodeCredential, NodeRegistrar nodeRegistrar) {
        this.nodeRegistrar = nodeRegistrar;
        this.node = nodeRegistrar.getNode();
        this.timeRecorder = new Hashtable<>();
        this.start();
    }

    public void resetCounter(){
        this.received = 0;
        this.forwarded = 0;
        this.answered = 0;
        this.backward = 0;
        this.successCount = 0;
        downloadErrorCount = 0;
    }

    public Node getNode() {
        return node;
    }

    public NodeRegistrar getNodeRegistrar() {
        return this.nodeRegistrar;
    }

    @Override
    public void run() {
        Node.logMessage("Server " + this.node.getCredential().getUsername() + " created at "
                + this.node.getCredential().getPort() + ". Waiting for incoming data...", "ANSI_WHITE");
        byte buffer[];
        DatagramPacket datagramPacket;
        while (true) {
            buffer = new byte[65536];
            datagramPacket = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(datagramPacket);
                String message = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
                Credential senderCredential = new Credential(datagramPacket.getAddress().getHostAddress(),
                        datagramPacket.getPort(), null);
                Message response = Parser.parse(message, senderCredential);
                processResponse(response);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void start() {
        socket = nodeRegistrar.getSocket();
        if (socket != null) {
            new Thread(this).start();
        }
    }

    @Override
    public void join(Credential neighbourCredential) {
        JoinRequest joinRequest = new JoinRequest(node.getCredential());
        String msg = joinRequest.getMessageAsString(Constant.protocolConstants.get("JOIN"));
        try {
            socket.send(new DatagramPacket(msg.getBytes(), msg.getBytes().length,
                    InetAddress.getByName(neighbourCredential.getIp()), neighbourCredential.getPort()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void joinOk(Credential senderCredential) {
        JoinResponse joinResponse = new JoinResponse(0, node.getCredential());
        String msg = joinResponse.getMessageAsString(Constant.protocolConstants.get("JOINOK"));

        List<Credential> routingTable = this.node.getRoutingTable();
        if(routingTable.size() == 2){
            routingTable.remove(1);
        }
        routingTable.add(senderCredential);

        this.node.setRoutingTable(routingTable);
        printRoutingTable(routingTable);

        try {
            socket.send(new DatagramPacket(msg.getBytes(), msg.getBytes().length,
                    InetAddress.getByName(senderCredential.getIp()), senderCredential.getPort()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void leave(Credential neighbourCredential) {
        LeaveRequest leaveRequest = new LeaveRequest(node.getCredential());
        String msg = leaveRequest.getMessageAsString(Constant.protocolConstants.get("LEAVE"));
        try {
            socket.send(new DatagramPacket(msg.getBytes(), msg.getBytes().length,
                    InetAddress.getByName(neighbourCredential.getIp()), neighbourCredential.getPort()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void leaveOk(Credential senderCredentials) {
        LeaveResponse leaveResponse = new LeaveResponse(0);
        String msg = leaveResponse.getMessageAsString(Constant.protocolConstants.get("LEAVEOK"));
        try {
            socket.send(new DatagramPacket(msg.getBytes(), msg.getBytes().length,
                    InetAddress.getByName(senderCredentials.getIp()), senderCredentials.getPort()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void search(SearchRequest searchRequest, Credential sendCredentials) {
        String msg = searchRequest.getMessageAsString(Constant.protocolConstants.get("SEARCH"));
        if ((this.getNode().getCredential().getIp() != searchRequest.getTriggeredCredentials().getIp())
                && this.getNode().getCredential().getPort() != searchRequest.getTriggeredCredentials().getPort()
                    && this.getNode().getQueryRoutingRecord(searchRequest.getSearchQueryID()) == null) {
            this.getNode().addQueryRecordToRouting(searchRequest.getSearchQueryID(),
                    searchRequest.getSenderCredentials());

            //Node.logMessage("Query Record Added=======>");
        }

        try {
            synchronized (node.getQueryDetailsTable()) {
                Node.logMessage(
                        "Send SER request message to " + sendCredentials.getIp() + " : " + sendCredentials.getPort(), "ANSI_YELLOW");
                socket.send(new DatagramPacket(msg.getBytes(), msg.getBytes().length,
                        InetAddress.getByName(sendCredentials.getIp()), sendCredentials.getPort()));
                if ((!node.checkSuccessQuery(searchRequest.getSearchQueryID()))
                        && (node.getCredential().getIp() == searchRequest.getTriggeredCredentials().getIp())
                        && node.getCredential().getPort() == searchRequest.getTriggeredCredentials().getPort()) {
                    node.addSearchQuery(searchRequest);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void searchOk(SearchResponse searchResponse, Credential receiverCredentials) {
        String msg = searchResponse.getMessageAsString(Constant.protocolConstants.get("SEARCHOK"));
        try {
            Node.logMessage("Search OK response is send to -" + receiverCredentials.getIp() + " - "
                    + receiverCredentials.getPort(), "ANSI_BLUE");
            socket.send(new DatagramPacket(msg.getBytes(), msg.getBytes().length,
                    InetAddress.getByName(receiverCredentials.getIp()), receiverCredentials.getPort()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void error(Credential senderCredential) {
        ErrorResponse errorResponse = new ErrorResponse();
        String msg = errorResponse.getMessageAsString(Constant.protocolConstants.get("ERROR"));
        try {
            socket.send(new DatagramPacket(msg.getBytes(), msg.getBytes().length,
                    InetAddress.getByName(senderCredential.getIp()), senderCredential.getPort()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processResponse(Message response) {
        if (response instanceof PingRequest) {
            sendPongMessage((PingRequest) response);
        } else if (response instanceof RegisterResponse) {
            RegisterResponse registerResponse = (RegisterResponse) response;
            if (registerResponse.getNoOfNodes() == Constant.errorCodeConstants.get("ERROR_ALREADY_REGISTERED")) {
                Node.logMessage("Already registered at Bootstrap with same username", "ANSI_WHITE");
                Credential credential = node.getCredential();
                credential.setUsername(UUID.randomUUID().toString());
                node.setCredential(credential);
                nodeRegistrar.register();
            } else if (registerResponse.getNoOfNodes() == Constant.errorCodeConstants.get("ERROR_DUPLICATE_IP")) {
                Node.logMessage("Already registered at Bootstrap with same port", "ANSI_WHITE");
                Credential credential = node.getCredential();
                credential.setPort(credential.getPort() + 1);
                node.setCredential(credential);
                nodeRegistrar.register();
            } else if (registerResponse.getNoOfNodes() == Constant.errorCodeConstants.get("ERROR_CANNOT_REGISTER")) {
                System.out.printf("Can’t register. Bootstrap server full. Try again later");
            } else if (registerResponse.getNoOfNodes() == Constant.errorCodeConstants.get("ERROR_COMMAND")) {
                Node.logMessage("Error in command", "ANSI_RED");
            } else {
                List<Credential> credentialList = registerResponse.getCredentials();
                ArrayList<Credential> routingTable = new ArrayList();
                for (Credential credential : credentialList) {
                    //routingTable.add(credential);
                    this.join(credential);
                }

                // TODO: check whether the received nodes are alive before adding to routing
                // table
                //this.node.setRoutingTable(routingTable);
                this.nodeRegistrar.setRegOK(true);

                this.node.setCacheTable(new Hashtable<>());
            }

        } else if (response instanceof UnregisterResponse) {
            // TODO: set leave request for all of the nodes at routing table
            node.setRoutingTable(new ArrayList<>());
            node.setFileList(new ArrayList<>());
            node.setStatTable(new ArrayList<>());
            this.nodeRegistrar.setRegOK(false);

        } else if (response instanceof SearchRequest) {
            SearchRequest searchRequest = (SearchRequest) response;
            triggerSearchRequest(searchRequest);
            received++;

        } else if (response instanceof SearchResponse) {
            SearchResponse searchResponse = (SearchResponse) response;
            received++;
            if (searchResponse.getNoOfFiles() == Constant.errorCodeConstants.get("ERROR_NODE_UNREACHABLE")) {
                Node.logMessage("Failure due to node unreachable", "ANSI_RED");
            } else if (searchResponse.getNoOfFiles() == Constant.errorCodeConstants.get("ERROR_OTHER")) {
                Node.logMessage("Some other error", "ANSI_RED");
            } else {
                Hashtable<Credential, HashSet<String>> cacheTable = this.node.getCacheTable();
                Credential owner = searchResponse.getCredential();
                HashSet<String> fileList;

                if (cacheTable.containsKey(owner)) {
                    fileList = cacheTable.get(owner);
                    fileList.addAll(searchResponse.getFileList());
                } else {
                    fileList = new HashSet<>(searchResponse.getFileList());
                    cacheTable.put(owner, fileList);
                }

                printCacheTable(cacheTable);

                if ((boolean) (this.getNode().getQueryRoutingRecord(searchResponse.getSequenceNo()) != null)) {
                    Credential queryFrom = this.getNode().removeQueryRecordFromRouting(searchResponse.getSequenceNo());
                    searchResponse.setSenderCredentials(node.getCredential());
                    //Node.logMessage("Query routing table record is deleted========>");
                    searchOk(searchResponse, queryFrom);
                    backward++;
                } else if (node.getSearchQueryByID(searchResponse.getSequenceNo()) != null) {
                    long finish = System.currentTimeMillis();
                    long timeElapsed = finish - this.timeRecorder.get(searchResponse.getSequenceNo());
                    Node.logMessage("Query latency : " + node.getSearchQueryByID(searchResponse.getSequenceNo()).getFileName()
                            + " : " + timeElapsed + " ms" + " | Hop count :" + searchResponse.getHops(), "ANSI_WHITE");
                    synchronized (node.getSuccessQueryIDs()) {
                        node.addSuccessQuery(searchResponse.getSequenceNo());
                    }
                    synchronized (node.getQueryDetailsTable()) {
                        node.removeSearchQuery(searchResponse.getSequenceNo());
                    }
                    Node.logMessage("--------------------------------------------------------", "ANSI_BLUE");
                    Node.logMessage(searchResponse.toString(), "ANSI_BLUE");

                    Node.logMessage("--------------------------------------------------------", "ANSI_BLUE");
                    successCount++;

                    download(searchResponse);
                } else {
                    Node.logMessage("File already received or time is expired.", "ANSI_GREEN");
                }
            }

        } else if (response instanceof JoinRequest) {
            joinOk(((JoinRequest) response).getCredential());

        } else if (response instanceof JoinResponse) {
            JoinResponse joinResponse = (JoinResponse) response;
            List<Credential> routingTable = node.getRoutingTable();
            routingTable.add(joinResponse.getSenderCredential());
            node.setRoutingTable(routingTable);
            printRoutingTable(routingTable);

        } else if (response instanceof LeaveRequest) {
            LeaveRequest leaveRequest = (LeaveRequest) response;
            List<Credential> routingTable = node.getRoutingTable();
            routingTable.remove(leaveRequest.getCredential());
            node.setRoutingTable(routingTable);
            printRoutingTable(node.getRoutingTable());

            //Remove the leave node from cache table
            //Remove cache table entry
            //Hashtable<Credential, HashSet<String>> cacheTable = this.node.getCacheTable();
            //cacheTable.remove(leaveRequest.getCredential());
            //this.node.setCacheTable(cacheTable);

        } else if (response instanceof LeaveResponse) {
            // Nothing to do here

        } else if (response instanceof ErrorResponse) {
            ErrorResponse errorResponse = (ErrorResponse) response;
            Node.logMessage(errorResponse.toString(), "ANSI_RED");
        }
    }

    public void download(SearchResponse response){
        FileDownloader fD = new FileDownloader();
        try {
            fD.downloadFile(response.getFileList().get(0),response.getCredential());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendPongMessage(PingRequest request) {
        PongResponse pong = new PongResponse();
        String msg = pong.getMessageAsString(Constant.protocolConstants.get("PONG"));
        try {
            socket.send(new DatagramPacket(msg.getBytes(), msg.getBytes().length,
                    InetAddress.getByName(request.getSenderCredentials().getIp()),
                    request.getSenderCredentials().getPort()));
            //Node.logMessage("Pong message sent");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> checkForFiles(String fileName, List<String> fileList) {
        Pattern pattern = Pattern.compile(fileName);
        return fileList.stream().filter(pattern.asPredicate()).collect(Collectors.toList());
    }

    @Override
    public void printRoutingTable(List<Credential> routingTable) {

        Node.logMessage("----------------Routing Table--------------------------", "ANSI_WHITE");
        Node.logMessage("IP \t \t \t PORT", "ANSI_WHITE");
        for (Credential credential : routingTable) {
            Node.logMessage(credential.getIp() + "\t" + credential.getPort(), "ANSI_WHITE");
        }
        Node.logMessage("--------------------------------------------------------", "ANSI_WHITE");
    }

    @Override
    public Hashtable<Credential, List<String>> checkFilesInCache(String fileName,
            Hashtable<Credential, HashSet<String>> cacheTable) {
        // TODO:check cache table to check whether record is there for the searching
        // file
        Enumeration credentialsList = cacheTable.keys();
        Hashtable<Credential, List<String>> searchResult = new Hashtable<>();
        while (credentialsList.hasMoreElements()) {
            Credential searchNode = (Credential) credentialsList.nextElement();
            List<String> fileList = new ArrayList<>(cacheTable.get(searchNode));
            Pattern pattern = Pattern.compile(fileName);
            fileList = fileList.stream().filter(pattern.asPredicate()).collect(Collectors.toList());
            if (!fileList.isEmpty()) {
                searchResult.put(searchNode, fileList);
                return searchResult;
            }
        }
        return searchResult;
    }

    public void printCacheTable(Hashtable<Credential, HashSet<String>> cacheTable) {
        Node.logMessage("Cache table updated as :", "ANSI_WHITE");
        Node.logMessage("--------------------------------------------------------", "ANSI_WHITE");
        Node.logMessage("IP \t \t \t PORT", "ANSI_WHITE");
        Enumeration keys = cacheTable.keys();
        while ((keys.hasMoreElements())) {
            Credential node = (Credential) keys.nextElement();
            Node.logMessage(node.getIp() + "\t \t \t" + node.getPort() + " ===> "
                    + Arrays.toString(cacheTable.get(node).toArray()), "ANSI_WHITE");
        }
        Node.logMessage("--------------------------------------------------------", "ANSI_WHITE");
    }

    @Override
    public void triggerSearchRequest(SearchRequest searchRequest) {

        if (this.node.getSearchQueryByID(searchRequest.getSearchQueryID()) != null ||
                this.node.getQueryRoutingRecord(searchRequest.getSearchQueryID()) != null) {
            Node.logMessage("Same search query is received earlier | Query ID : " +
                    searchRequest.getSearchQueryID(), "ANSI_RED");
            return;
        }

        if (searchRequest.getTriggeredCredentials().getIp() == this.node.getCredential().getIp()
                && searchRequest.getTriggeredCredentials().getPort() == this.node.getCredential().getPort() && searchRequest.getHops() == 0) {
            this.timeRecorder.put(searchRequest.getSearchQueryID(), System.currentTimeMillis());
            Node.logMessage("Triggered my search request for " + searchRequest.getFileName(), "BOLD_PURPLE");
        }else {
            Node.logMessage("Triggered search request for " + searchRequest.getFileName(), "ANSI_PURPLE");
        }


        List<String> searchResult = checkForFiles(searchRequest.getFileName(), node.getFileList());
        if (!searchResult.isEmpty()) {
            Node.logMessage("File " + searchRequest.getFileName() + " is available at "
                    + node.getCredential().getIp() + " : " + node.getCredential().getPort(), "ANSI_BLUE");

            SearchResponse searchResponse = new SearchResponse(searchRequest.getSearchQueryID(), searchResult.size(),
                    node.getCredential(), searchRequest.getHops(), searchResult, node.getCredential());
            if (searchRequest.getTriggeredCredentials().getIp() == node.getCredential().getIp()
                    && searchRequest.getTriggeredCredentials().getPort() == node.getCredential().getPort()) {
                Node.logMessage("File " + searchRequest.getFileName() + " is available on me.", "ANSI_BLUE");
                successCount++;
            } else {
                answered++;
                searchOk(searchResponse, searchRequest.getSenderCredentials());
            }
        } else {
            Node.logMessage("File is not available in the node itself " + node.getCredential().getIp() + " : "
                    + node.getCredential().getPort(), "ANSI_RED");
            Hashtable<Credential, List<String>> cacheResult = checkFilesInCache(searchRequest.getFileName(),
                    this.node.getCacheTable());
            if (!cacheResult.isEmpty()) {
                Node.logMessage("File is available in the cache " + node.getCredential().getIp() + " : "
                        + node.getCredential().getPort(), "ANSI_GREEN");

                Credential fileOwner = cacheResult.keys().nextElement();
                List<String> fileList = cacheResult.get(fileOwner);
                SearchResponse searchResponse = new SearchResponse(searchRequest.getSearchQueryID(), fileList.size(),
                        fileOwner, searchRequest.getHops(), fileList, node.getCredential());

                if(searchRequest.getTriggeredCredentials().getIp() == this.node.getCredential().getIp() &&
                        searchRequest.getTriggeredCredentials().getPort() == this.node.getCredential().getPort()){
                    successCount++;
                    download(searchResponse);
                }else {
                    searchOk(searchResponse, searchRequest.getSenderCredentials());
                }
                answered++;
            } else {
                Node.logMessage("File is not available in the cache" + node.getCredential().getIp() + " : "
                        + node.getCredential().getPort(), "ANSI_RED");
                searchRequest.setHops(searchRequest.incHops());
                //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>"+searchRequest.getSearchQueryID());

                if (node.getRoutingTable().size() > 0 &&
                        searchRequest.getHops() < Constant.errorCodeConstants.get("MAX_HOP")) {

                    for (Credential credential : node.getRoutingTable()) {
                        if(searchRequest.getHops() <= 1){
                            forwarded++;
                        }
                        if (searchRequest.getTriggeredCredentials().getIp() != credential.getIp()
                                && searchRequest.getTriggeredCredentials().getPort() != credential.getPort()) {
                            //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>"+searchRequest.getSearchQueryID());
                            search(searchRequest, credential);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        SearchRequest sr = (SearchRequest) arg;

        String uuid = UUID.randomUUID().toString() + "-" + this.getNode().getCredential().getUsername();
        SearchRequest searchRequest = new SearchRequest(uuid, this.getNode().getCredential(), sr.getFileName(), 0,
                this.getNode().getCredential());

        Node.logMessage("\nSearch query " + sr.getSearchQueryID() + " - " + sr.getFileName() + " is expired !",
                "ANSI_RED");

        node.removeSearchQuery(sr.getSearchQueryID());
        if (sr.getRetriedCount() < 2) {
            sr.incrementExpiredTime();
            sr.incrementRetriedCount();
            sr.setHops(0);
            triggerSearchRequest(searchRequest);

        }
    }

    public void printMessageStats(){
        Node.logMessage("Received : " + received, "ANSI_WHITE");
        Node.logMessage("Forward : " + forwarded, "ANSI_WHITE");
        Node.logMessage("Backward : " + backward, "ANSI_WHITE");
        Node.logMessage("Answered : " + answered, "ANSI_WHITE");
        Node.logMessage("Success count : " + successCount, "ANSI_WHITE");
    }
}
