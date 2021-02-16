package ds.node;

import com.sun.net.httpserver.HttpServer;
import ds.communication.request.SearchRequest;
import ds.credential.Credential;
import ds.downloadAPI.HttpResthandler;
import ds.history.StatRecord;

import java.net.DatagramSocket;
import java.util.*;

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
        return this.queryRoutingTable.remove(queryId);
    }

    public Credential getQueryRoutingRecord(String queryId) {
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
        System.out.println("Search Query " + searchQueryID + " is removed from Query Detasils table !\n");
    }

    public SearchRequest getSearchQueryByID(String ID) {
        return this.queryDetailsTable.get(ID);
    }

}
