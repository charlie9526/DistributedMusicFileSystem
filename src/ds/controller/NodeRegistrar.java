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

public class NodeRegistrar {

    private Node node;
    private Credential bootstrapServerCredential;
    private DatagramSocket socket;

    public DatagramSocket getSocket() {
        return socket;
    }

    public Node getNode() {
        return node;
    }

    public NodeRegistrar(Credential bootstrapServerCredential,Credential nodeCredential) {
        this.node = new Node();
        node.setCredential(nodeCredential);
        node.setFileList(createFileList());
        node.setRoutingTable(new ArrayList());
        node.setStatTable(new ArrayList());
        this.bootstrapServerCredential = bootstrapServerCredential;
        try {
            this.socket = new DatagramSocket(this.node.getCredential().getPort());
        } catch (SocketException e) {
            this.socket = null;
            e.printStackTrace();
        }
    }

    public void register() {
        RegisterRequest registerRequest = new RegisterRequest(node.getCredential());
        String msg = registerRequest.getMessageAsString(Constant.Command.REG);
        try {
            socket.send(new DatagramPacket(msg.getBytes(), msg.getBytes().length, InetAddress.getByName(bootstrapServerCredential.getIp()), bootstrapServerCredential.getPort()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void unRegister() {
        UnregisterRequest unregisterRequest = new UnregisterRequest(node.getCredential());
        String msg = unregisterRequest.getMessageAsString(Constant.Command.UNREG);
        try {
            socket.send(new DatagramPacket(msg.getBytes(), msg.getBytes().length, InetAddress.getByName(bootstrapServerCredential.getIp()), bootstrapServerCredential.getPort()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> createFileList() {
        ArrayList<String> fileList = new ArrayList<>();
        fileList.add("Adventures_of_Tintin");
        fileList.add("Jack_and_Jill");
        fileList.add("Glee");
        fileList.add("The_Vampire Diarie");
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
        System.out.println("File List : " + Arrays.toString(subFileList.toArray()));
        return subFileList;
    }

}
