package ds.communication.request;

import ds.communication.Message;
import ds.credential.Credential;

public class SearchRequest extends Message {

    private String searchQueryID;
    private Credential triggeredCredential;
    private String fileName;
    private int hops;

    public SearchRequest(String searchQueryID, Credential triggeredCredential, String fileName, int hops) {
        this.searchQueryID = searchQueryID;
        this.triggeredCredential = triggeredCredential;
        this.fileName = fileName;
        this.hops = hops;
    }

    public Credential getCredential() {
        return triggeredCredential;
    }

    public void setCredential(Credential credential) {
        this.triggeredCredential = credential;
    }

    public String getSearchQueryID() {
        return searchQueryID;
    }

    public void setSearchQueryID(String searchQueryID) {
        this.searchQueryID = this.searchQueryID;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getHops() {
        return hops;
    }

    public void setHops(int hops) {
        this.hops = hops;
    }

    @Override
    public String getMessageAsString(String message) {
        message += " " + searchQueryID + " " + this.getCredential().getIp() + " " + this.getCredential().getPort() + " " + this.getFileName() + " " + this.getHops();
        return super.getMessageAsString(message);
    }

    public int incHops() {
        return ++hops;
    }
}