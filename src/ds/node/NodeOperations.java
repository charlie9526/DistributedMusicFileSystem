package ds.node;

import ds.communication.Message;
import ds.communication.request.SearchRequest;
import ds.communication.response.SearchResponse;
import ds.credential.Credential;

import java.util.List;

public interface NodeOperations {

    void join(Credential neighbourCredential);

    void joinOk(Credential senderCredential);

    void leave(Credential neighbourCredential);

    void leaveOk(Credential senderCredential);

    void search(SearchRequest searchRequest, Credential sendCredential);

    void searchOk(SearchResponse searchResponse,Credential receiverCredentials);

    void processResponse(Message response);

    void error(Credential senderCredential);

    List<String> checkForFiles(String fileName, List<String> fileList);

    void triggerSearchRequest(SearchRequest searchRequest);

    void printRoutingTable(List<Credential> routingTable);

}
