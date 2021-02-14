package ds.node;

import ds.communication.request.SearchRequest;
import ds.credential.Credential;
import ds.history.StatRecord;

import java.net.DatagramSocket;
import java.util.*;

public class Node implements Observer {

    private Credential credential;
    private List<String> fileList;
    private List<Credential> routingTable;
    private List<StatRecord> statTable;
    private Hashtable<String, SearchRequest> queryDetailsTable ;
    private Map<String, Credential> queryRoutingTable;
    private DatagramSocket socket;

    public Node(DatagramSocket socket,Credential credential,List<String> fileList){
        this.socket = socket;
        this.credential = credential;
        this.fileList = fileList;
        this.statTable = new ArrayList();
        this.queryDetailsTable = new Hashtable<>();
        this.routingTable = new ArrayList();
        this.queryRoutingTable = new HashMap<String, Credential>();
    }

    public Credential getCredential() {
        return credential;
    }

    public DatagramSocket getSocket(){
        return this.socket;
    }

    public Map<String, Credential> getQueryRoutingTable() {
        return queryRoutingTable;
    }

    public void setQueryRoutingTable(Map<String, Credential> queryRoutingTable) {
        this.queryRoutingTable = queryRoutingTable;
    }

    public void addQueryRecordToRouting(String queryId, Credential from) {
        this.queryRoutingTable.put(queryId, from);
    }

    public Credential removeQueryRecordFromRouting(String queryId) {
        return this.queryRoutingTable.remove(queryId);
    }

    public Credential getQueryRoutingRecord(String queryId){
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

    public Hashtable<String,SearchRequest> getQueryDetailsTable(){
        return this.queryDetailsTable;
    }

    public void addSearchQuery(SearchRequest searchQuery){
        this.queryDetailsTable.put(searchQuery.getSearchQueryID(),searchQuery);
    }

    public void removeSearchQuery(String searchQueryID){
        synchronized (this.queryDetailsTable){
            this.queryDetailsTable.remove(searchQueryID);
        }
        System.out.println("Search Query "+searchQueryID+" is removed from Query Detasils table !");
    }

    public SearchRequest getSearchQueryByID(String ID){
        return this.queryDetailsTable.get(ID);
    }

    @Override
    public void update(Observable o, Object arg) {
        System.out.println("Search query "+(String) arg+" is expired !");
        removeSearchQuery((String) arg);
    }
}
