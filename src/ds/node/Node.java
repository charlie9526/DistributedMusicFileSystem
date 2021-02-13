package ds.node;

import ds.communication.SearchQuery;
import ds.credential.Credential;
import ds.history.StatRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {

    private Credential credential;
    private List<String> fileList;
    private List<Credential> routingTable;
    private List<StatRecord> statTable;
    private HashMap<String,SearchQuery> queryDetailsTable = new HashMap<>();
    private Map<String, Credential> queryRoutingTable;


    public Credential getCredential() {
        return credential;
    }

    public Map<String, Credential> getQueryRoutingTable() {
        return queryRoutingTable;
    }

    public void setQueryRoutingTable(Map<String, Credential> queryRoutingTable) {
        this.queryRoutingTable = queryRoutingTable;
    }

    public void addQueryRecord(String queryId, Credential from) {
        this.queryRoutingTable.put(queryId, from);
    }

    public void removeQueryRecord(String queryId, Credential from) {
        this.queryRoutingTable.remove(queryId);
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

    public void setStatTable(List<StatRecord> statTable) {this.statTable = statTable;}

    public HashMap<String,SearchQuery> getQueryDetailsTable(){return this.queryDetailsTable;}

    public void addSearchQuery(SearchQuery searchQuery){ this.queryDetailsTable.put(searchQuery.getID(),searchQuery); }

    public void removeSearchQuery(int searchQueryID){ queryDetailsTable.remove(searchQueryID); }

    public SearchQuery getSearchQueryByID(String ID){ return this.queryDetailsTable.get(ID);}
}
