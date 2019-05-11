package com.bupt.Server;


import java.util.concurrent.*;
import java.net.Socket;
import java.net.ServerSocket;

public class SocketServer implements Runnable {
    private int ServiceThreadNum = 10;
    private int ServerPort = 8888;
    private ServerSocket server = null;
    private ExecutorService pool = Executors.newFixedThreadPool(ServiceThreadNum);
    public  SocketServer(){};

    @Override
    public void run() {

        try{
            server = new ServerSocket(ServerPort);
            Object lock = new Object();

            pool.submit(new ReplyThread(lock));
            //Client Thread
            while(true){
                Socket socket = server.accept();
                pool.submit(new SocketServiceThread(socket,lock));
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}