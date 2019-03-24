package com.bupt.Server;

import java.util.concurrent.*;

public class SocketServer implements Runnable {
    private int ServiceThreadNum = 4;
    private ExecutorService pool = Executors.newFixedThreadPool(ServiceThreadNum);
    public  SocketServer(){};

    @Override
    public void run() {
        for(int i=1;i<=ServiceThreadNum;i++){
            Runnable serviceTask = new SocketServiceThread(i+"");
            pool.submit(serviceTask);
        }
    }
}