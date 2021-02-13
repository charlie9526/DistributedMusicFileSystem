package ds.node;

import ds.communication.SearchQuery;
import ds.credential.Credential;
import ds.history.StatRecord;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private Credential credential;
    private List<String> fileList;
    private List<Credential> routingTable;
    private List<StatRecord> statTable;
    private ArrayList<SearchQuery> queryDetailsTable;

    public Credential getCredential() {
        return credential;
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
