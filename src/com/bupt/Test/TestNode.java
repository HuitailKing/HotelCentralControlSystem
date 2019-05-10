package com.bupt.Test;

import java.util.concurrent.*;

public class TestNode implements Runnable {
    private int ServiceThreadNum = 4;
    private ExecutorService pool = Executors.newFixedThreadPool(ServiceThreadNum);
    public  TestNode(){};

    @Override
    public void run() {
        for(int i=1;i<=ServiceThreadNum;i++){
            Runnable serviceTask = new TestForClientThread(i+"");
            pool.submit(serviceTask);
        }
    }
}