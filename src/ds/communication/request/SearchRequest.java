package ds.communication.request;

import ds.communication.Message;
import ds.credential.Credential;

public class SearchRequest extends Message {

    private String searchQueryID;
    private Credential triggeredCredential;
    private String fileName;
    private int hops;
    private Credential senderCredentials;

    public SearchRequest(String searchQueryID, Credential triggeredCredential, String fileName, int hops,Credential senderCredentials) {
        this.searchQueryID = searchQueryID;
        this.triggeredCredential = triggeredCredential;
        this.fileName = fileName;
        this.hops = hops;
        this.senderCredentials=senderCredentials;
    }

    public Credential getSenderCredentials() {
        return senderCredentials;
    }

    public void setSenderCredentials(Credential senderCredentials) {
        this.senderCredentials = senderCredentials;
    }

    public Credential getTriggeredCredentials() {
        return triggeredCredential;
    }

    public void setTriggeredCredentials(Credential credential) {
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
        message += " " + searchQueryID + " " + this.getTriggeredCredentials().getIp() + " " + this.getTriggeredCredentials().getPort() + " " + this.getFileName() + " " + this.getHops();
        return super.getMessageAsString(message);
    }

    public int incHops() {
        return ++hops;
    }
}