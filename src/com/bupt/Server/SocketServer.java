package com.bupt.Server;

import com.bupt.Client.SocketClient;

import java.util.concurrent.*;
import java.net.Socket;
import java.net.ServerSocket;

public class SocketServer implements Runnable {
    private int ServiceThreadNum = 4;
    private int ServerPort = 8888;
    private ServerSocket server = null;
    private ExecutorService pool = Executors.newFixedThreadPool(ServiceThreadNum);
    public  SocketServer(){};

    @Override
    public void run() {
//        for(int i=1;i<=ServiceThreadNum;i++){
//            Runnable serviceTask = new SocketServiceThread(i+"");
//            pool.submit(serviceTask);
//        }
        try{
            server = new ServerSocket(ServerPort);
            while(true){
                Socket socket = server.accept();
                pool.submit(new SocketServiceThread(socket));
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}