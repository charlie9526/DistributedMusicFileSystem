package ds.controller;

import ds.communication.request.PingRequest;
import ds.communication.request.SearchRequest;
import ds.constant.Constant;
import ds.credential.Credential;
import ds.node.Node;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.*;

public class Watchman implements Runnable {
    private Thread watchmanThread = null;
    private static Watchman singleton = null;
    private Node node;

    private Watchman(Node node) {
        watchmanThread = new Thread(this);
        this.node = node;

    }

    public static Watchman getWatchman() {
        return singleton;
    }

    public static void createWatchman(Node node) {
        if (singleton == null) {
            singleton = new Watchman(node);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            synchronized (this.node.getRoutingTable()) {
                ArrayList<String[]> keyArray = new ArrayList<String[]>();
                for (Credential cred : this.node.getRoutingTable()) {
                    String[] x = { cred.getIp(), ((Integer) cred.getPort()).toString() };
                    keyArray.add(x);
                }
                for (String[] key : keyArray) {
                    String[] checkingNode = key;
                    sendPingRequest(checkingNode[0], checkingNode[1]);
                    byte[] buffer = new byte[65536];
                    DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                    try {
                        node.getPingSocket().receive(datagramPacket);
                        //System.out.println("Pong message recieved" + checkingNode[0] + checkingNode[1]);
                    } catch (IOException e) {
                        //System.out.println("Pong message not recieved" + checkingNode[0] + checkingNode[1]);
                    }
                }
            }

        }
    }

    public void sendPingRequest(String ip, String port) {
        PingRequest ping = new PingRequest();
        String msg = ping.getMessageAsString(Constant.protocolConstants.get("PING"));
        try {
            node.getPingSocket().send(new DatagramPacket(msg.getBytes(), msg.getBytes().length,
                    InetAddress.getByName(ip), Integer.parseInt(port)));
            //System.out.println("\nPing Message Sent");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        this.watchmanThread.start();
    }
}