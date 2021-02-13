package ds.node;

import ds.communication.SearchQuery;
import ds.credential.Credential;
import ds.history.StatRecord;

import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {

    private Credential credential;
    private List<String> fileList;
    private List<Credential> routingTable;
    private List<StatRecord> statTable;
    private HashMap<String,SearchQuery> queryDetailsTable ;
    private Map<String, Credential> queryTable;
    private DatagramSocket socket;

    public Node(DatagramSocket socket,Credential credential,List<String> fileList){
        this.socket = socket;
        this.credential = credential;
        this.fileList = fileList;
        this.statTable = new ArrayList();
        this.queryDetailsTable = new HashMap<>();
        this.routingTable = new ArrayList();
        this.queryTable = new HashMap<String, Credential>();
    }

    public Credential getCredential() {
        return credential;
    }

    public DatagramSocket getSocket(){
        return this.socket;
    }

    public Map<String, Credential> getQueryTable() {
        return queryTable;
    }

    public void setQueryTable(Map<String, Credential> queryTable) {
        this.queryTable = queryTable;
    }

    public void addQueryRecord(String queryId, Credential from) {
        this.queryTable.put(queryId, from);
    }

    public void removeQueryRecord(String queryId, Credential from) {
        this.queryTable.remove(queryId);
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

    public HashMap<String,SearchQuery> getQueryDetailsTable(){
        return this.queryDetailsTable;
    }

    public void addSearchQuery(SearchQuery searchQuery){
        this.queryDetailsTable.put(searchQuery.getID(),searchQuery);
    }

    public void removeSearchQuery(String searchQueryID){
        System.out.println("Search Query "+searchQueryID+" is removed from Query Detasils table after successfull search !");
        this.queryDetailsTable.remove(searchQueryID);
    }

    public SearchQuery getSearchQueryByID(String ID){
        return this.queryDetailsTable.get(ID);
    }
}
