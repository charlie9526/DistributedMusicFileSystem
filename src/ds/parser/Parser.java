package ds.parser;


import ds.communication.Message;
import ds.communication.request.*;
import ds.communication.response.*;
import ds.constant.Constant;
import ds.credential.Credential;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Parser {

    public static Message parse(String message, Credential senderCredential) {

        System.out.println("Message received : " + message);
        StringTokenizer st = new StringTokenizer(message, " ");

        String length = st.nextToken();
        String command = st.nextToken();
        System.out.println(length+"===length");
        System.out.println(message);
        System.out.println(command+"====comamnd");

        if (command.equals(Constant.commandConstants.get("REG"))) {
            String ip = st.nextToken();
            int port = Integer.parseInt(st.nextToken());
            String username = st.nextToken();
            Credential userCredentials = new Credential(ip, port, username);
            return new RegisterRequest(userCredentials);

        } else if (command.equals(Constant.commandConstants.get("REGOK"))) {
            int numOfNodes = Integer.parseInt(st.nextToken());
            String ip;
            int port;
            List<Credential> nodes = new ArrayList<>();
            if (!(numOfNodes == Constant.codeConstants.get("ERROR_CANNOT_REGISTER") || numOfNodes == Constant.codeConstants.get("ERROR_DUPLICATE_IP") || numOfNodes == Constant.codeConstants.get("ERROR_ALREADY_REGISTERED") || numOfNodes == Constant.codeConstants.get("ERROR_COMMAND"))) {
                for (int i = 0; i < numOfNodes; i++) {
                    ip = st.nextToken();
                    port = Integer.parseInt(st.nextToken());
                    nodes.add(new Credential(ip, port, null));
                }
            }
            RegisterResponse registerResponse = new RegisterResponse(numOfNodes, nodes);
            return registerResponse;

        } else if (command.equals(Constant.commandConstants.get("UNREG"))) {
            String ip = st.nextToken();
            int port = Integer.parseInt(st.nextToken());
            String username = st.nextToken();
            Credential unregUserCredentials = new Credential(ip, port, username);
            return new UnregisterRequest(unregUserCredentials);

        } else if (command.equals(Constant.commandConstants.get("UNREGOK"))) {
            int value = Integer.parseInt(st.nextToken());
            return new UnregisterResponse(value);

        } else if (command.equals(Constant.commandConstants.get("LEAVE"))) {
            String ip = st.nextToken();
            int port = Integer.parseInt(st.nextToken());
            Credential crd = new Credential(ip, port, null);
            return new LeaveRequest(crd);

        } else if (command.equals(Constant.commandConstants.get("JOIN"))) {
            String ip = st.nextToken();
            int port = Integer.parseInt(st.nextToken());
            Credential joinerCredentials = new Credential(ip, port, null);
            return new JoinRequest(joinerCredentials);

        } else if (command.equals(Constant.commandConstants.get("JOINOK"))) {
            int value = Integer.parseInt(st.nextToken());
            return new JoinResponse(value, senderCredential);

        } else if (command.equals(Constant.commandConstants.get("LEAVEOK"))) {
            int value = Integer.parseInt(st.nextToken());
            return new LeaveResponse(value);

        } else if (command.equals(Constant.commandConstants.get("SEARCH"))) {
            String seqNum = st.nextToken();
            String ip = st.nextToken();//file request triggered node's ip
            int port = Integer.parseInt(st.nextToken());
            String fileName = st.nextToken();
            int hops = Integer.parseInt(st.nextToken());
            Credential crd = new Credential(ip, port, null);
            return new SearchRequest(seqNum, crd, fileName, hops,senderCredential);

        } else if (command.equals(Constant.commandConstants.get("SEARCHOK"))) {
            String sequenceNo = st.nextToken();
            int numOfFiles = Integer.parseInt(st.nextToken());
            String ip = st.nextToken();
            int port = Integer.parseInt(st.nextToken());
            int hops = Integer.parseInt(st.nextToken());
            List<String> fileList = new ArrayList<>();
            if (numOfFiles > 0 && !(numOfFiles == Constant.codeConstants.get("ERROR_OTHER") || numOfFiles == Constant.codeConstants.get("ERROR_NODE_UNREACHABLE"))) {
                for (int i = 0; i < numOfFiles; i++) {
                    fileList.add(st.nextToken());
                }
            }
            Credential endNodeCredentials = new Credential(ip, port, null);
            return new SearchResponse(sequenceNo, numOfFiles, endNodeCredentials, hops, fileList,senderCredential,);

        } else if (command.equals(Constant.codeConstants.get("ERROR"))) {
            return new ErrorResponse();
        }

        return null;
    }
}
