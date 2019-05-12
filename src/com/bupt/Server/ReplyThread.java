package com.bupt.Server;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import com.google.gson.*;

import com.bupt.InfoBean;

import java.net.*;



public class ReplyThread extends BaseThread implements Runnable {
    private Object Lock = null;

    private Socket TestSocket = null;
    private int port = 8291;
    private int TestPort = 9999;

    //Info
    PrintWriter TestWriter = null;
    BufferedReader TestGetter = null;
    private InfoBean Info = new InfoBean();
    static private Gson gson = new Gson();

    public ReplyThread(Object lock) {
        try {

            Lock = lock;

            //连接test
            TestSocket = new Socket();
            TestSocket.bind(new InetSocketAddress(port));
            TestSocket.connect(new InetSocketAddress("127.0.0.1", TestPort));
            System.out.println("Connect" + port + " to " + TestPort);
            TestGetter = new BufferedReader(new InputStreamReader(
                    TestSocket.getInputStream())); //输入，from 客户端
            TestWriter = new PrintWriter(TestSocket.getOutputStream()); //输出，to 客户端
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ReplyStart() {

    }

    @Override
    public void run() {
        LockTools.StartLock.lock();
        try {
            //告知Test节点自己的房间号
            Info.clear();
            Info.ActionType = "r";
            Info.Room = "s";
            TestWriter.println(gson.toJson(Info));
            TestWriter.flush();

            //等待验收开始
            while (!super.Start) {
                System.out.println("【Reply】:" + Room + " is waiting");
                LockTools.StartCondition.await();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            LockTools.StartLock.unlock();
        }


        try {
            int PreparingTime = 10;
            while (PreparingTime-- > 0) {
                System.out.println("【Info】Test will start in " + PreparingTime + " seconds");
                Thread.sleep(1000);
            }
            //发送i给Test Node
            System.out.println("【Reply】【Send to Test: i】");
            Info.clear();
            Info.ActionType = "i";
            TestWriter.println(gson.toJson(Info));
            TestWriter.flush();

            //一边检查socket有没有信息，一边等待报告新状态信息
            while (!super.Exit) {

                //Test节点要求看账单
                if (TestGetter.ready()) {
                    System.out.println("【Server Received】【Test is checking bill】");
                    LockTools.ReplyLock.lock();
                    try {
                        Info.clear();
                        String str = TestGetter.readLine();
                        System.out.println(str);
                        Info = gson.fromJson(str, InfoBean.class);
                        assert Info.ActionType == "b";
                        String bill = super.GetBill(Integer.valueOf(Info.Room));
                        Info.clear();
                        Info.Bill = bill;
                        Info.Room = super.Room + "";
                        TestWriter.println(gson.toJson(Info));
                        TestWriter.flush();

                    } finally {
                        LockTools.ReplyLock.unlock();
                    }
                }

                if (super.ShouldReply) {
                    //System.out.println("【Reply】Should Reply");
                    LockTools.ReplyLock.lock();
                    try {
                        Info.clear();
                        Info.ActionType = "state";
                        Info.Room = super.Room + "";
                        Info.Value = super.StateStr;
                        super.StateStr = "";
                        super.ShouldReply = false;
                        TestWriter.println(gson.toJson(Info));
                        TestWriter.flush();
                    } finally {
                        LockTools.StateEmptyCondition.signalAll();
                        LockTools.ReplyLock.unlock();
                        System.out.println("【Reply】【to Test】" + gson.toJson(Info) + "");
                    }
                }
                Thread.sleep(100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}