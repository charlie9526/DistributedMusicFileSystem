package ds.controller;

import ds.communication.Message;
import ds.communication.request.*;
import ds.communication.response.*;
import ds.constant.Constant;
import ds.credential.Credential;
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

    public NodeOperator(Credential nodeCredential, NodeRegistrar nodeRegistrar) {
        this.nodeRegistrar = nodeRegistrar;
        this.node = nodeRegistrar.getNode();
        this.start();
    }

    public Node getNode() {
        return node;
    }

    public NodeRegistrar getNodeRegistrar() {
        return this.nodeRegistrar;
    }

    @Override
    public void run() {
        System.out.println("Server " + this.node.getCredential().getUsername() + " created at "
                + this.node.getCredential().getPort() + ". Waiting for incoming data...");
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
        String msg = joinRequest.getMessageAsString(Constant.commandConstants.get("JOIN"));
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
        String msg = joinResponse.getMessageAsString(Constant.commandConstants.get("JOINOK"));
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
        String msg = leaveRequest.getMessageAsString(Constant.commandConstants.get("LEAVE"));
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
        String msg = leaveResponse.getMessageAsString(Constant.commandConstants.get("LEAVEOK"));
        try {
            socket.send(new DatagramPacket(msg.getBytes(), msg.getBytes().length,
                    InetAddress.getByName(senderCredentials.getIp()), senderCredentials.getPort()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void search(SearchRequest searchRequest, Credential sendCredentials) {
        String msg = searchRequest.getMessageAsString(Constant.commandConstants.get("SEARCH"));
        if ((this.getNode().getCredential().getIp() != searchRequest.getTriggeredCredentials().getIp())
                && this.getNode().getCredential().getPort() != searchRequest.getTriggeredCredentials().getPort()) {
            this.getNode().addQueryRecordToRouting(searchRequest.getSearchQueryID(),
                    searchRequest.getSenderCredentials());
            System.out.println("Query Record Added=======>");
        }
        try {
            synchronized (node.getQueryDetailsTable()) {
                System.out.println(
                        "Send SER request message to " + sendCredentials.getIp() + " : " + sendCredentials.getPort());
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
        String msg = searchResponse.getMessageAsString(Constant.commandConstants.get("SEARCHOK"));
        try {
            System.out.println("Search OK response is send to -" + receiverCredentials.getIp() + " - "
                    + receiverCredentials.getPort());
            socket.send(new DatagramPacket(msg.getBytes(), msg.getBytes().length,
                    InetAddress.getByName(receiverCredentials.getIp()), receiverCredentials.getPort()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void error(Credential senderCredential) {
        ErrorResponse errorResponse = new ErrorResponse();
        String msg = errorResponse.getMessageAsString(Constant.commandConstants.get("ERROR"));
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
            if (registerResponse.getNoOfNodes() == Constant.codeConstants.get("ERROR_ALREADY_REGISTERED")) {
                System.out.println("Already registered at Bootstrap with same username");
                Credential credential = node.getCredential();
                credential.setUsername(UUID.randomUUID().toString());
                node.setCredential(credential);
                nodeRegistrar.register();
            } else if (registerResponse.getNoOfNodes() == Constant.codeConstants.get("ERROR_DUPLICATE_IP")) {
                System.out.println("Already registered at Bootstrap with same port");
                Credential credential = node.getCredential();
                credential.setPort(credential.getPort() + 1);
                node.setCredential(credential);
                nodeRegistrar.register();
            } else if (registerResponse.getNoOfNodes() == Constant.codeConstants.get("ERROR_CANNOT_REGISTER")) {
                System.out.printf("Can’t register. Bootstrap server full. Try again later");
            } else if (registerResponse.getNoOfNodes() == Constant.codeConstants.get("ERROR_COMMAND")) {
                System.out.println("Error in command");
            } else {
                List<Credential> credentialList = registerResponse.getCredentials();
                ArrayList<Credential> routingTable = new ArrayList();
                for (Credential credential : credentialList) {
                    routingTable.add(credential);
                }
                printRoutingTable(routingTable);
                // TODO: check whether the received nodes are alive before adding to routing
                // table
                this.node.setRoutingTable(routingTable);
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

        } else if (response instanceof SearchResponse) {
            SearchResponse searchResponse = (SearchResponse) response;
            if (searchResponse.getNoOfFiles() == Constant.codeConstants.get("ERROR_NODE_UNREACHABLE")) {
                System.out.println("Failure due to node unreachable");
            } else if (searchResponse.getNoOfFiles() == Constant.codeConstants.get("ERROR_OTHER")) {
                System.out.println("Some other error");
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
                    System.out.println("Query routing table record is deleted========>");
                    searchOk(searchResponse, queryFrom);
                } else if (node.getSearchQueryByID(searchResponse.getSequenceNo()) != null) {
                    synchronized (node.getSuccessQueryIDs()) {
                        node.addSuccessQuery(searchResponse.getSequenceNo());
                    }
                    synchronized (node.getQueryDetailsTable()) {
                        node.removeSearchQuery(searchResponse.getSequenceNo());
                    }
                    System.out.println("--------------------------------------------------------");
                    System.out.println(searchResponse.toString());
                    System.out.println("--------------------------------------------------------");
                } else {
                    System.out.println("File already recieved or time is expired.");
                }
            }

        } else if (response instanceof JoinRequest) {
            joinOk(node.getCredential());

        } else if (response instanceof JoinResponse) {
            JoinResponse joinResponse = (JoinResponse) response;
            List<Credential> routingTable = node.getRoutingTable();
            routingTable.add(joinResponse.getSenderCredential());
            node.setRoutingTable(routingTable);

        } else if (response instanceof LeaveRequest) {
            LeaveRequest leaveRequest = (LeaveRequest) response;
            List<Credential> routingTable = node.getRoutingTable();
            routingTable.remove(leaveRequest.getCredential());
            node.setRoutingTable(routingTable);

        } else if (response instanceof LeaveResponse) {
            // Nothing to do here

        } else if (response instanceof ErrorResponse) {
            ErrorResponse errorResponse = (ErrorResponse) response;
            System.out.println(errorResponse.toString());
        }
    }

    @Override
    public void sendPongMessage(PingRequest request) {
        PongResponse pong = new PongResponse();
        String msg = pong.getMessageAsString(Constant.commandConstants.get("PONG"));
        try {
            socket.send(new DatagramPacket(msg.getBytes(), msg.getBytes().length,
                    InetAddress.getByName(request.getSenderCredentials().getIp()),
                    request.getSenderCredentials().getPort()));
            System.out.println("Pong message sent");

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
        System.out.println("Routing table updated as :");
        System.out.println("--------------------------------------------------------");
        System.out.println("IP \t \t \t PORT");
        for (Credential credential : routingTable) {
            System.out.println(credential.getIp() + "\t" + credential.getPort());
        }
        System.out.println("--------------------------------------------------------");
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
        System.out.println("Cache table updated as :");
        System.out.println("--------------------------------------------------------");
        System.out.println("IP \t \t \t PORT");
        Enumeration keys = cacheTable.keys();
        while ((keys.hasMoreElements())) {
            Credential node = (Credential) keys.nextElement();
            System.out.println(node.getIp() + "\t \t \t" + node.getPort() + " ===> "
                    + Arrays.toString(cacheTable.get(node).toArray()));
        }
        System.out.println("--------------------------------------------------------");
    }

    @Override
    public void triggerSearchRequest(SearchRequest searchRequest) {
        System.out.println("Triggered search request for " + searchRequest.getFileName());
        List<String> searchResult = checkForFiles(searchRequest.getFileName(), node.getFileList());
        if (!searchResult.isEmpty()) {
            System.out.println("File " + searchRequest.getFileName() + " is available at "
                    + node.getCredential().getIp() + " : " + node.getCredential().getPort());
            SearchResponse searchResponse = new SearchResponse(searchRequest.getSearchQueryID(), searchResult.size(),
                    node.getCredential(), searchRequest.getHops(), searchResult, node.getCredential());
            if (searchRequest.getTriggeredCredentials().getIp() == node.getCredential().getIp()
                    && searchRequest.getTriggeredCredentials().getPort() == node.getCredential().getPort()) {
                System.out.println(searchResponse.toString());
            } else {
                searchOk(searchResponse, searchRequest.getSenderCredentials());
            }
        } else {
            System.out.println("File is not available in the node itself" + node.getCredential().getIp() + " : "
                    + node.getCredential().getPort());
            Hashtable<Credential, List<String>> cacheResult = checkFilesInCache(searchRequest.getFileName(),
                    this.node.getCacheTable());
            if (!cacheResult.isEmpty()) {
                System.out.println("File is available in the cache" + node.getCredential().getIp() + " : "
                        + node.getCredential().getPort());
                Credential fileOwner = cacheResult.keys().nextElement();
                List<String> fileList = cacheResult.get(fileOwner);
                SearchResponse searchResponse = new SearchResponse(searchRequest.getSearchQueryID(), fileList.size(),
                        fileOwner, searchRequest.getHops(), fileList, node.getCredential());
                searchOk(searchResponse, searchRequest.getSenderCredentials());
            } else {
                System.out.println("File is not available in the cache" + node.getCredential().getIp() + " : "
                        + node.getCredential().getPort());
                searchRequest.setHops(searchRequest.incHops());
                if (node.getRoutingTable().size() > 0) {
                    for (Credential credential : node.getRoutingTable()) {
                        if (searchRequest.getTriggeredCredentials().getIp() != credential.getIp()
                                && searchRequest.getTriggeredCredentials().getPort() != credential.getPort()) {
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
        System.out.println("\nSearch query " + sr.getSearchQueryID() + " - " + sr.getFileName() + " is expired !");
        node.removeSearchQuery(sr.getSearchQueryID());
        if (sr.getRetriedCount() < 2) {
            sr.incrementExpiredTime();
            sr.incrementRetriedCount();
            triggerSearchRequest(sr);
        }
    }
}
