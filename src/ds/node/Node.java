package ds.node;

import ds.communication.SearchQuery;
import ds.credential.Credential;
import ds.history.StatRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Node {

    private Credential credential;
    private List<String> fileList;
    private List<Credential> routingTable;
    private List<StatRecord> statTable;
    private Map<String, Credential> queryTable;
    private ArrayList<SearchQuery> queryDetailsTable;

    public Credential getCredential() {
        return credential;
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

    public void setStatTable(List<StatRecord> statTable) {this.statTable = statTable;}

    public ArrayList<SearchQuery> getQueryDetailsTable(){return this.queryDetailsTable;}

    public void putSearchQuery(SearchQuery searchQuery){ this.queryDetailsTable.add(searchQuery); }

    public void removeSearchQuery(SearchQuery searchQuery){ queryDetailsTable.remove(searchQuery); }
}
