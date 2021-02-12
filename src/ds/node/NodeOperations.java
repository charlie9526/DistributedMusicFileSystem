package ds.node;

import ds.communication.Message;
import ds.communication.request.SearchRequest;
import ds.communication.response.SearchResponse;
import ds.credential.Credential;

import java.util.List;

public interface NodeOperations {

    void start();

    void register();

    void unRegister();

    void join(Credential neighbourCredential);

    void joinOk(Credential senderCredential);

    void leave(Credential neighbourCredential);

    void leaveOk(Credential senderCredential);

    void search(SearchRequest searchRequest, Credential sendCredential);

    void searchOk(SearchResponse searchResponse);

    List<String> createFileList();

    void processResponse(Message response);

    void error(Credential senderCredential);

    boolean isRegOk();

    List<String> checkForFiles(String fileName, List<String> fileList);

    void triggerSearchRequest(SearchRequest searchRequest);

    void printRoutingTable(List<Credential> routingTable);

}
