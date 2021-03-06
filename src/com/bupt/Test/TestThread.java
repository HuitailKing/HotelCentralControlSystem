package com.bupt.Test;

import com.bupt.InfoBean;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.bupt.Server.LockTools;

public class TestThread implements Runnable {
    static boolean TestStart = false;
    static Object lock = new Object();

    private Socket TestSocket = null;

    //Info
    PrintWriter Writer = null;
    BufferedReader Getter = null;
    private InfoBean Info = new InfoBean();
    static private Gson gson = new Gson();
    //Room Info
    private String Room = "";


    public TestThread(Socket TestSocket){
        Room = "Unset"+TestSocket.toString();
        this.init(TestSocket);
    }
    private void init(Socket TestSocket){
        try {
            this.TestSocket = TestSocket;
            Getter = new BufferedReader(new InputStreamReader(
                    TestSocket.getInputStream())); //输入，from 客户端
            Writer = new PrintWriter(TestSocket.getOutputStream()); //输出，to 客户端
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void process(){

    }
    
    
    @Override
    public void run(){

       try {

           //等待告知房间号
           Info.clear();
           Info = gson.fromJson(Getter.readLine(),InfoBean.class);
           Room = Info.Room;
           System.out.println("【Test Receive】【Room】"+Room);

           int Cnt = 0;
            if(Room.equals("s")){
                //如果是Server则只接受并打印信息，20秒后发送b
                while(true){
                    int res;
                    if((res = Getter.read())!=0) {
                        Info.clear();
                        String str = (char)res + Getter.readLine();
                        Info =  gson.fromJson(str,InfoBean.class);
                        System.out.println("【Info】【Test Received from Server】"+gson.toJson(Info));
                        if(Info.ActionType.equals("i")){
                            TestStart = true;
                            LockTools.Testlock.lock();
                            LockTools.TestStartCondition.signalAll();
                            System.out.println("【Test】【sigal all】");
                            LockTools.Testlock.unlock();
                        }
                    } else{
                        Thread.sleep(1000);
                        Cnt += 1;
                    }
//                    if(Cnt>=20){
//                        Info.clear();
//                        Info.ActionType = "b";
//                        Info.Room = "2";
//                        Writer.println(gson.toJson(Info));
//                        Writer.flush();
//                    }
                    //Info.clear();
                    //Info = gson.fromJson(Getter.readLine(),InfoBean.class);
                    //System.out.println("【【【【【【【Receive】】】】】】】"+gson.toJson(Info));
                    Thread.sleep(10);
                }
            } else{
                //如果是Client则设置初始温度，风速和目标温度，关闭风机

                LockTools.Testlock.lock();
                try {
                    while (!TestStart) LockTools.TestStartCondition.await();
                    System.out.println("【Test】【Room "+ Room + "】sync");
                }finally {
                    LockTools.Testlock.unlock();
                }

                Info.clear();
                Info.ActionType = "it";
                Info.it = "50";
                Writer.println(gson.toJson(Info));
                Writer.flush();
                System.out.println("【Test】【Send to Client"+ Room+"】"+gson.toJson(Info));

                Info.clear();
                Info.ActionType = "tt";
                Info.tt = "20";
                Info.w = "1";
                Writer.println(gson.toJson(Info));
                Writer.flush();
                System.out.println("【Test】【Send to Client"+ Room+"】"+gson.toJson(Info));


                Thread.sleep(60000);
                Info.clear();
                Info.ActionType = "w";
                Info.w = "0";
                Writer.println(gson.toJson(Info));
                Writer.flush();
                System.out.println("【Test】【Send to Client"+ Room+"】"+gson.toJson(Info));

            }
       }catch (Exception e){
           e.printStackTrace();
       }

    }
}
