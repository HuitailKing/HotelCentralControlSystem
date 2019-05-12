package com.bupt.Test;

import java.util.concurrent.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TestNode implements Runnable {
    private int ServiceThreadNum = 10;
    private int TestPort = 9999;
    private ServerSocket server = null;
    private ExecutorService pool = Executors.newFixedThreadPool(ServiceThreadNum);
    public  TestNode(){};

    @Override
    public void run() {


        try{
            server = new ServerSocket(TestPort);
            System.out.println("Test Node Listening...");
            while(true){
                Socket ForClient = server.accept();
                pool.submit(new TestThread(ForClient));
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}