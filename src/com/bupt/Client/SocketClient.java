package com.bupt.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketClient implements  Runnable {
    private String room_name;
    private int port=8888,portOffset=0;
    public SocketClient(String room_name){
        this.room_name = "ROOM---"+room_name+"---";
        this.portOffset = Integer.parseInt(room_name);
    }
    @Override
    public  void run() {
        try {
            System.out.println(room_name+"Client tring!");
            Socket socket = new Socket("127.0.0.1", port+portOffset);
            System.out.println(room_name+"Client start!");
            PrintWriter out = new PrintWriter(socket.getOutputStream()); // 输出，to 服务器 socket
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream())); // 输入， from 服务器 socket
            out.println(room_name+"Client request! :-) ");
            out.println(room_name+"Client request! :-) -------");
            out.flush(); // 刷缓冲输出，to 服务器
            System.out.println(room_name+"print:"+in.readLine()); // 打印服务器发过来的字符串
            System.out.println(room_name+"Client end!");
            socket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}