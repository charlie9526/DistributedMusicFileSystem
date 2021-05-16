package ds.controller;

import ds.communication.request.RegisterRequest;
import ds.communication.request.SearchRequest;
import ds.communication.request.UnregisterRequest;
import ds.constant.Constant;
import ds.credential.Credential;
import ds.node.Node;
import ds.node.NodeOperations;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.*;
import java.util.logging.Logger;

public class NodeRegistrar {

    private Node node;
    private Credential bootstrapServerCredential;
    private Boolean regOK = false;
    
    public NodeRegistrar(Credential bootstrapServerCredential, Credential nodeCredential,int pingPort) {
        DatagramSocket socket = null;
        DatagramSocket pingSocket=null;
        try {
            socket = new DatagramSocket(nodeCredential.getPort());
            pingSocket=new DatagramSocket(pingPort);
            pingSocket.setSoTimeout(Constant.serverPortConstants.get("PING_TIMEOUT"));
        } catch (SocketException e) {
            e.printStackTrace();
        }finally {
            this.node = new Node(socket,nodeCredential,createFileList(),pingSocket);
        }
        this.bootstrapServerCredential = bootstrapServerCredential;
    }

    public DatagramSocket getSocket() {
        return this.node.getSocket();
    }

    public Node getNode() {
        return node;
    }

    public void register() {
        RegisterRequest registerRequest = new RegisterRequest(node.getCredential());
        String msg = registerRequest.getMessageAsString(Constant.protocolConstants.get("REG"));
        try {
            this.getNode().getSocket().send(new DatagramPacket(msg.getBytes(), msg.getBytes().length,
                    InetAddress.getByName(bootstrapServerCredential.getIp()), bootstrapServerCredential.getPort()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void unRegister() {
        UnregisterRequest unregisterRequest = new UnregisterRequest(node.getCredential());
        String msg = unregisterRequest.getMessageAsString(Constant.protocolConstants.get("UNREG"));
        try {
            this.getNode().getSocket().send(new DatagramPacket(msg.getBytes(), msg.getBytes().length,
                    InetAddress.getByName(bootstrapServerCredential.getIp()), bootstrapServerCredential.getPort()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isRegOK() {
        return this.regOK;
    }

    public void setRegOK(Boolean bool){
        this.regOK = bool;
    }

    public List<String> createFileList() {
        ArrayList<String> fileList = new ArrayList<>();
        fileList.add("Adventures_of_Tintin");
        fileList.add("Jack_and_Jill");
        fileList.add("Glee");
        fileList.add("The_Vampire_Diarie");
        fileList.add("King_Arthur");
        fileList.add("Windows_XP");
        fileList.add("Harry_Potter");
        fileList.add("Kung_Fu_Panda");
        fileList.add("Lady_Gaga");
        fileList.add("Twilight");
        fileList.add("Windows_8");
        fileList.add("Mission_Impossible");
        fileList.add("Turn_Up_The_Music");
        fileList.add("Super_Mario");
        fileList.add("American_Pickers");
        fileList.add("Microsoft_Office_2010");
        fileList.add("Happy_Feet");
        fileList.add("Modern_Family");
        fileList.add("American_Idol");
        fileList.add("Hacking_for_Dummies");
        Collections.shuffle(fileList);
        List<String> subFileList = fileList.subList(0, 5);

        /*for(int i=0; i < subFileList.size(); i++){
            subFileList.set(i, subFileList.get(i).replace("_", " "));
        }*/

        Node.logMessage("File List : " + Arrays.toString(subFileList.toArray()), "ANSI_WHITE");
        return subFileList;
    }

}
