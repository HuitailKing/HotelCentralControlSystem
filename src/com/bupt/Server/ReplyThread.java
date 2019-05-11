package com.bupt.Server;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import com.google.gson.*;

import com.bupt.InfoBean;
import java.util.*;
import java.util.concurrent.*;
import java.net.*;
import com.google.gson.*;

import javax.sound.sampled.Port;
import java.util.*;


public class ReplyThread extends BaseThread implements Runnable{
    private Object Lock = null;

    private Socket TestSocket = null;
    private int port = 8891;
    private int TestPort = 9999;

    //Info
    PrintWriter TestWriter = null;
    BufferedReader TestGetter = null;
    private InfoBean Info = new InfoBean();
    static private Gson gson = new Gson();

    public  ReplyThread(Object lock){
        try{

            Lock = lock;

            //连接test
            TestSocket = new Socket();
            TestSocket.bind(new InetSocketAddress(port));
            TestSocket.connect(new InetSocketAddress("127.0.0.1", TestPort));
            System.out.println("Connect"+port+" to "+TestPort);
            TestGetter = new BufferedReader(new InputStreamReader(
                    TestSocket.getInputStream())); //输入，from 客户端
            TestWriter = new PrintWriter(TestSocket.getOutputStream()); //输出，to 客户端
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void ReplyStart(){

    }
    @Override
    public void run(){
        LockTools.lock.lock();
        try{
            //告知Test节点自己的房间号
            Info.clear();
            Info.ActionType = "r";
            Info.Room = "s";
            TestWriter.println(gson.toJson(Info));
            TestWriter.flush();

            //等待验收开始
            while(!super.Start) LockTools.StartCondition.await();

            //发送i给Test Node
            Info.clear();
            Info.ActionType = "i";
            TestWriter.println(gson.toJson(Info));
            TestWriter.flush();

            //一边检查socket有没有信息，一边等待报告新状态信息
            while(!super.Exit){

                int res = 0;
                //Test节点要求看账单
                if((res=TestGetter.read())!=0){
                    LockTools.lock.lock();
                    try{
                        System.out.println(res);
                        Info.clear();
                        String str = (char)res+TestGetter.readLine();
                        System.out.println(str);
                        Info = gson.fromJson(str,InfoBean.class);
                        assert Info.ActionType =="b";
                        String bill = super.GetBill(Integer.valueOf(Info.Room));
                        Info.clear();
                        Info.Bill = bill;
                        Info.Room = super.Room + "";
                        TestWriter.println(gson.toJson(Info));
                        TestWriter.flush();

                    }finally {
                        LockTools.lock.unlock();
                    }
                }

                if(super.ShouldReply){
                    LockTools.lock.lock();
                    try{
                        Info.clear();
                        Info.ActionType = "state";
                        Info.Room = super.Room + "";
                        Info.Value = super.StateStr;
                        TestWriter.println(gson.toJson(Info));
                        TestWriter.flush();
                    }finally {
                        LockTools.lock.unlock();
                    }
                }
                Thread.sleep(100);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        finally {
            LockTools.lock.unlock();
        }

    }

}
