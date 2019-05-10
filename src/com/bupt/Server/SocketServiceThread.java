package com.bupt.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServiceThread implements Runnable{

    static int RoomCnt = 0;
    static Object lock = new Object();

    private Socket RoomSocket = null;
    //private ServerSocket server;
    private int port=8888;

    private boolean exit = false;
    private boolean start = false;

    private BufferedReader in = null; //输入，from 客户端
    private PrintWriter out = null; //输出，to 客户端

    //Room Info
    private String Room = "";


    
    
    public SocketServiceThread(Socket RoomSocket){
        Room = "Unset"+RoomSocket.toString();
        this.init(RoomSocket);
    }
    private void init(Socket RoomSocket){
        try {
            this.RoomSocket = RoomSocket;
            in = new BufferedReader(new InputStreamReader(
                    RoomSocket.getInputStream())); //输入，from 客户端
            out = new PrintWriter(RoomSocket.getOutputStream()); //输出，to 客户端
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    //Server端等待Client告知房间号
    private boolean CheckingReady(){
        try{
            if(in.readLine().equals("r"))
            {
                Room = in.readLine();
                System.out.println("set Room:"+Room);
                return true;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    private void process(){

    }
    @Override
    public void run(){
        try {
            System.out.println(Room+"Server Thread start!");


            while(!start){
                synchronized(lock){

                    if(CheckingReady())
                        RoomCnt +=1;

                    System.out.println("Room:"+Room+" RoomCnt:"+RoomCnt);

                    if(RoomCnt == 4)
                        lock.notifyAll();

                    System.out.println("Room:"+Room+" wait");
                    lock.wait();
                }
                break;
            }

            System.out.println("------------Room:"+Room+"is Ready! Test Begin.");

            while(!exit){

                process();

                out.println(Room+"^^^^^^^^^^^^^cnt");
                out.flush(); // to 客户端，输出
                Thread.sleep(1000);
            }

            RoomSocket.close();
            System.out.println(Room+"Server end!");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
