package ds.communication.response;

import java.util.List;
import ds.communication.Message;
import ds.credential.Credential;

public class SearchResponse extends Message {

    private String sequenceNo;
    private int noOfFiles;
    private Credential credential;// credentials of file having node
    private int hops;
    private List<String> fileList;
    private Credential senderCredentials;

    public SearchResponse(String sequenceNo, int noOfFiles, Credential credential, int hops, List<String> fileList,
            Credential senderCredentials) {
        this.sequenceNo = sequenceNo;
        this.noOfFiles = noOfFiles;
        this.credential = credential;
        this.hops = hops;
        this.fileList = fileList;
        this.senderCredentials = senderCredentials;

    }

    public Credential getSenderCredentials() {
        return senderCredentials;
    }

    public void setSenderCredentials(Credential senderCredentials) {
        this.senderCredentials = senderCredentials;
    }

    public int getNoOfFiles() {
        return noOfFiles;
    }

    public void setNoOfFiles(int noOfFiles) {
        this.noOfFiles = noOfFiles;
    }

    public Credential getCredential() {
        return credential;
    }

    public void setCredential(Credential credential) {
        this.credential = credential;
    }

    public String getSequenceNo() {
        return sequenceNo;
    }

    public void setSequenceNo(String sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    public int getHops() {
        return hops;
    }

    public void setHops(int hops) {
        this.hops = hops;
    }

    public List<String> getFileList() {
        return fileList;
    }

    public void setFileList(List<String> fileList) {
        this.fileList = fileList;
    }

    @Override
    public String getMessageAsString(String message) {
        message += " " + sequenceNo + " " + this.getNoOfFiles() + " " + this.getCredential().getIp() + " " + this.getCredential().getPort() + " " + this.getHops();
        for (String file : fileList) {
            message += " " + file;
        }
        return super.getMessageAsString(message);
    }

    @Override
    public String toString() {
        String response = "Search Results:" +
                "\nSequence No: " + this.getSequenceNo() +
                "\nNo of files: " + fileList.size() +
                "\nIP: " + this.getCredential().getIp() +
                "\nPort: " + this.getCredential().getPort() +
                "\nHop count: " + this.getHops();
        for (int i = 1; i <= fileList.size(); i++) {
            response += "\nFile " + i + ": " + fileList.get(i - 1);
        }

        return response;
    }
}
