package ds.controller;

import ds.communication.request.SearchRequest;
import ds.node.Node;

import java.sql.Timestamp;
import java.util.*;

public class TimeKeeperSingleton extends Observable implements Runnable{
    private ArrayList<Node> nodeList =null;
    private Thread timeKeeperThread =null;
    private static TimeKeeperSingleton singleton = null;

    private TimeKeeperSingleton(){
        nodeList = new ArrayList<Node>();
        timeKeeperThread = new Thread(this);
    }

    public void addNodeToList(Node node){
        this.nodeList.add(node);
    }

    public static TimeKeeperSingleton getTimeKeeper(){
        if (singleton==null){
            singleton = new TimeKeeperSingleton();
        }
        return singleton;
    }

    @Override
    public void run(){
        while (true){
            for (Node node : nodeList){
                HashMap <String, SearchRequest> queryDetailsTable = node.getQueryDetailsTable();
//                System.out.println(">>>>>>time keepr>>>>>>>>>>"+queryDetailsTable.size());
                for(Map.Entry<String,SearchRequest> entry : queryDetailsTable.entrySet()){
                    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
                    if (currentTime.getTime()>entry.getValue().getExpiredTime().getTime()){
                        setChanged();
                        notifyObservers(entry.getKey());
                    }
                }
            }
        }
    }
    public int getNodeListSize(){
        return this.nodeList.get(0).getQueryDetailsTable().size();
    }

    public void start(){
        this.timeKeeperThread.start();
    }
}