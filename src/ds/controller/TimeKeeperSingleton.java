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
//                System.out.println(">>>>>>time keepr>>>>>>>>>>"+queryDetailsTable.size());
                synchronized (node.getQueryDetailsTable()){
                    for(String key : node.getQueryDetailsTable().keySet()){
                        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
                        if (currentTime.getTime()>node.getQueryDetailsTable().get(key).getExpiredTime().getTime()){
                            setChanged();
                            notifyObservers(key);
                        }
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