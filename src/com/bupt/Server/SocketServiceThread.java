package com.bupt.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServiceThread implements Runnable{
    private String ServiceName ;
    private ServerSocket server;
    private int port=8888;
    public SocketServiceThread(String ServiceName){
        this.ServiceName = "ServiceThread---"+ServiceName+"---:";
        this.init(ServiceName);
    }
    public void init(String portOffset){
        try {
            server = new ServerSocket(port+Integer.parseInt(portOffset));
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    @Override
    public void run(){
        try {
            System.out.println(ServiceName+"Server start!");
            Socket socket = server.accept(); //阻塞等待, 直到一个客户端 socket过来
            System.out.println(ServiceName+"There comes a socket!");
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream())); //输入，from 客户端
            PrintWriter out = new PrintWriter(socket.getOutputStream()); //输出，to 客户端
            int cnt=10;
            while(cnt-->0){
                System.out.println(ServiceName+in.readLine());
                Thread.sleep(2000);
                out.println(ServiceName+"^^^^^^^^^^^^^cnt"+cnt);
                out.flush(); // to 客户端，输出
            }
            socket.close();
            server.close();
            System.out.println(ServiceName+"Server end!");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
