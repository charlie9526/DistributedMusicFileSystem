package ds.communication.request;

import ds.communication.Message;
import ds.credential.Credential;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class SearchRequest extends Message {

    private String searchQueryID;
    private Credential triggeredCredential;
    private String fileName;
    private int hops;
    private Credential senderCredentials;
    private Timestamp manufacturedTime;
    private Timestamp expiredTime;
    private int retriedCount;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");

    public SearchRequest(String searchQueryID, Credential triggeredCredential, String fileName, int hops, Credential senderCredentials) {
        this.searchQueryID = searchQueryID;
        this.triggeredCredential = triggeredCredential;
        this.fileName = fileName;
        this.hops = hops;
        this.senderCredentials = senderCredentials;
        this.manufacturedTime = new Timestamp(System.currentTimeMillis());
//        System.out.println(sdf.format(this.manufacturedTime));
        this.expiredTime = new Timestamp(this.manufacturedTime.getTime() + 30000);
//        System.out.println(sdf.format(this.expiredTime));
        this.retriedCount = 0;
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

    public Timestamp getExpiredTime() {
        return expiredTime;
    }

    public void incrementExpiredTime() {
        this.expiredTime = new Timestamp(this.expiredTime.getTime() + 10000);
    }

    public void setTriggeredCredentials(Credential credential) {
        this.triggeredCredential = credential;
    }

    public String getSearchQueryID() {
        return searchQueryID;
    }

    public int getRetriedCount() {
        return retriedCount;
    }

    public void incrementRetriedCount() {
        this.retriedCount = this.retriedCount + 1;
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