package ds.node;

import com.sun.net.httpserver.HttpServer;
import ds.communication.request.SearchRequest;
import ds.credential.Credential;
import ds.downloadAPI.HttpResthandler;
import ds.history.StatRecord;

import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramSocket;
import java.util.*;
import java.util.logging.*;
import java.util.logging.Formatter;

public class Node {

    private Credential credential;
    private List<String> fileList;
    private List<Credential> routingTable;
    private List<StatRecord> statTable;
    private Hashtable<String, SearchRequest> queryDetailsTable;
    private Map<String, Credential> queryRoutingTable;
    private DatagramSocket socket;
    private ArrayList<String> successQueryIDs;
    private Hashtable<Credential, HashSet<String>> cacheTable;
    private DatagramSocket pingSocket;
    private HttpResthandler restAPI ;
    private static  Logger logger;

    public Node(DatagramSocket socket, Credential credential, List<String> fileList,DatagramSocket pingSocket) {
        this.socket = socket;
        this.pingSocket=pingSocket;
        this.credential = credential;
        this.fileList = fileList;
        this.statTable = new ArrayList();
        this.queryDetailsTable = new Hashtable<>();
        this.routingTable = new ArrayList();
        this.queryRoutingTable = new HashMap<String, Credential>();
        this.successQueryIDs = new ArrayList<String>();
    }


    public Boolean checkSuccessQuery(String query) {
        if (this.successQueryIDs.contains(query)) {
            return true;
        }
        return false;
    }

    public static void createFile(int port)  {
        FileHandler handler = null;
        try {

            handler = new FileHandler("./"+new Integer(port).toString()+".log", true);
            logger = Logger.getLogger("ds.hunky.log");

            Handler[] handlers = logger.getParent().getHandlers();
            for(int i=0; i < handlers.length;i++){
                handlers[i].setFormatter(new MyCustomFormatter());
            }

            logger.addHandler(handler);

            handler.setFormatter(new MyCustomFormatter());
            logger.warning("custom formatter - info message");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void logMessage(String message){
        Node.logger.info(message);
    }

    public HttpResthandler getRestAPI() {
        return restAPI;
    }

    public void setRestAPI(HttpResthandler restAPI) {
        this.restAPI = restAPI;
    }

    public Hashtable<Credential, HashSet<String>> getCacheTable() {
        return cacheTable;
    }

    public void setCacheTable(Hashtable<Credential, HashSet<String>> cacheTable) {
        this.cacheTable = cacheTable;
    }

    public ArrayList<String> getSuccessQueryIDs() {
        return successQueryIDs;
    }

    public void addSuccessQuery(String query) {
        this.successQueryIDs.add(query);
    }

    public Credential getCredential() {
        return credential;
    }

    public DatagramSocket getSocket() {
        return this.socket;
    }

    public DatagramSocket getPingSocket(){
        return this.pingSocket;
    }

    public Map<String, Credential> getQueryRoutingTable() {
        return queryRoutingTable;
    }

    public void addQueryRecordToRouting(String queryId, Credential from) {
        this.queryRoutingTable.put(queryId, from);
    }

    public Credential removeQueryRecordFromRouting(String queryId) {
        logMessage("Removed "+queryId+" from query roting table .");
        return this.queryRoutingTable.remove(queryId);
    }

    public Credential getQueryRoutingRecord(String queryId) {
        /*Set<String> strings = this.queryRoutingTable.keySet();
        /Node.logMessage("============== Printing Query Routing Table==========");
        for(String key: strings){
            Node.logMessage(key);
        }
        Node.logMessage("=====================================");
        */
        return this.queryRoutingTable.get(queryId);
    }

    public void setCredential(Credential credential) {
        this.credential = credential;
    }

    public List<String> getFileList() {
        return fileList;
    }

    public void setFileList(List<String> fileList) {
        this.fileList = fileList;
    }

    public List<Credential> getRoutingTable() {
        return routingTable;
    }

    public void setRoutingTable(List<Credential> routingTable) {
        this.routingTable = routingTable;
        printRoutingTable(this.routingTable);
    }

    public List<StatRecord> getStatTable() {
        return statTable;
    }

    public void setStatTable(List<StatRecord> statTable) {
        this.statTable = statTable;
    }

    public Hashtable<String, SearchRequest> getQueryDetailsTable() {
        return this.queryDetailsTable;
    }

    public void addSearchQuery(SearchRequest searchQuery) {
        this.queryDetailsTable.put(searchQuery.getSearchQueryID(), searchQuery);
    }

    public void removeSearchQuery(String searchQueryID) {
        this.queryDetailsTable.remove(searchQueryID);
        logMessage("Search Query " + searchQueryID + " is removed from Query Detasils table !\n");
    }

    public SearchRequest getSearchQueryByID(String ID) {
        /*
        Node.logMessage("============== Printing Query Details Table==========");
        Set<String> strings = this.queryDetailsTable.keySet();
        for(String key: strings){
            Node.logMessage(key);
        }
        Node.logMessage("============================");
        */
        return this.queryDetailsTable.get(ID);
    }

    private static class MyCustomFormatter extends Formatter {

        @Override
        public String format(LogRecord record) {
            StringBuffer sb = new StringBuffer();
            sb.append(record.getMessage());
            sb.append("\n");
            return sb.toString();
        }

    }

    public void printRoutingTable(List<Credential> routingTable) {

        Node.logMessage("----------------Routing Table--------------------------");
        Node.logMessage("IP \t \t \t PORT");
        for (Credential credential : routingTable) {
            Node.logMessage(credential.getIp() + "\t" + credential.getPort());
        }
        Node.logMessage("--------------------------------------------------------");
    }

}
