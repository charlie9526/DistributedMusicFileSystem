package ds.communication.response;

import ds.communication.Message;
import ds.credential.Credential;

public class JoinResponse extends Message {

    private int value;

    private Credential senderCredential;

    public JoinResponse(int value, Credential senderCredential) {
        this.value = value;
        this.senderCredential = senderCredential;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Credential getSenderCredential() {
        return senderCredential;
    }

    public void setSenderCredential(Credential senderCredential) {
        this.senderCredential = senderCredential;
    }

    @Override
    public String getMessageAsString(String message) {
        message += " " + this.getValue();
        return super.getMessageAsString(message);
    }
}
