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
    private Timestamp manufacturedTime;
    private Timestamp expiredTime;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");

    public SearchRequest(String searchQueryID, Credential triggeredCredential, String fileName, int hops) {
        this.searchQueryID = searchQueryID;
        this.triggeredCredential = triggeredCredential;
        this.fileName = fileName;
        this.hops = hops;
        this.manufacturedTime = new Timestamp(System.currentTimeMillis());
        System.out.println(sdf.format(this.manufacturedTime));
        this.expiredTime = new Timestamp(this.manufacturedTime.getTime()+30);
        System.out.println(sdf.format(this.expiredTime));
    }

    public Credential getCredential() {
        return triggeredCredential;
    }

    public Timestamp getExpiredTime() {
        return expiredTime;
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