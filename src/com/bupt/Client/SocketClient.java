package com.bupt.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.*;

public class SocketClient implements  Runnable {

    private String Room;
    private int CurTemperature;
    private int TargetTemperature;
    private int gear;
    private int time;

    //Port Info
    private int port=6666,portOffset = 0;
    private int ServerPort= 8888;

    //for Communications
    PrintWriter out = null;
    BufferedReader in = null;

    public SocketClient(String Room){
        this.Room = "Room---"+Room+"---";
        this.portOffset = Integer.parseInt(Room);
    }

    @Override
    public  void run() {
        try {
            System.out.println(Room+"Client instance,Room:"+Room);

            Socket socket = new Socket();
            socket.bind(new InetSocketAddress(port+portOffset));
            socket.connect(new InetSocketAddress("127.0.0.1", ServerPort));

            System.out.println(Room+"Client start!");
            out = new PrintWriter(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //告知Server，本Room Ready
            out.println("r");
            out.println(Room);
            out.flush();


            int cnt=10;
            while (cnt-->0){
                out.println(Room+"Client CCCCCCCC"+"************cnt"+cnt);
                out.flush(); // 刷缓冲输出，to 服务器
                Thread.sleep(1000);
                System.out.println(Room+in.readLine()); // 打印服务器发过来的字符串
            }
            System.out.println(Room+"Client end!");
            socket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e){
            e.printStackTrace();
        }

    }

}