package ds.communication.request;

import ds.communication.Message;
import ds.credential.Credential;

public class PingRequest extends Message {
    private Credential credentials;

    public PingRequest(Credential credentials) {
        this.setSenderCredentials(credentials);
    }

    public PingRequest(){
        
    }

    public Credential getSenderCredentials() {
        return credentials;
    }

    public void setSenderCredentials(Credential credentials) {
        this.credentials = credentials;
    }
    
    
}
