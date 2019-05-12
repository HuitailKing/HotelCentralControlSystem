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
import com.bupt.InfoBean;

public class SocketServiceThread extends BaseThread implements Runnable{

    static int RoomCnt = 0;
    private Object Lock = null;

    private Socket RoomSocket = null;
    //private ServerSocket server;
    private int port=8888;

    private String Room = "";

    //Info
    private BufferedReader ClientGetter = null; //输入，from 客户端
    private PrintWriter ClientWriter = null; //输出，to 客户端
    static private Gson gson = new Gson();
    private InfoBean Info = new InfoBean();
    

    public SocketServiceThread(Socket RoomSocket,Object lock){
        Lock = lock;
        Room = "Unset"+RoomSocket.toString();
        this.init(RoomSocket);
    }
    private void init(Socket RoomSocket){
        try {
            this.RoomSocket = RoomSocket;
            ClientGetter = new BufferedReader(new InputStreamReader(
                    RoomSocket.getInputStream())); //输入，from 客户端
            ClientWriter = new PrintWriter(RoomSocket.getOutputStream()); //输出，to 客户端
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    //Info process



    //Server端等待Client告知房间号
    private boolean CheckingReady(){
        try{
            Info.clear();
            Info = gson.fromJson(ClientGetter.readLine(),InfoBean.class);
            if(Info.ActionType.equals("r"))
            {
                Room = Info.Room;
                System.out.println("set Room:"+Room);
                return true;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("Checking faill"+gson.toJson(Info));
        return false;
    }


    //接受Client的状态信心并唤醒ReplyThread
    private void process(){
        try {
            Info = gson.fromJson(ClientGetter.readLine(), InfoBean.class);
            System.out.println("【Server】【Received】Room "+Room+gson.toJson(Info));
            //账单信息(添加即可)和状态信息（需要立即打印）
            if(Info.ActionType.equals("b")||Info.ActionType.equals("state")){

                //多线程处理父类的静态变量
                LockTools.ReplyLock.lock();
                try{

                    //添加账单
                    if(Info.ActionType.equals("b")) super.NewBill(Info);

                    //需要打印
                    //等待上个状态信息打印完毕
                    while(!super.StateStr.equals("")) {
                        //System.out.println("【Server】Room"+Room+" await");
                        LockTools.StateEmptyCondition.await();
                    }
                    super.Room = Integer.valueOf(Room);
                    super.StateStr = Info.Value;
                    super.ShouldReply = true;

                }finally {
                    LockTools.ReplyLock.unlock();
                }
            }else{
                System.out.println("【ERROR】Server Get:"+Info.ActionType);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void run(){
        try {

            System.out.println(Room+"Server Thread is Waiting...");
            //等待Client全部ready
            LockTools.StartLock.lock();
            try{

                if(CheckingReady()) RoomCnt +=1;

                System.out.println("Room:"+Room+" RoomCnt:"+RoomCnt);

                while(RoomCnt !=4){
                    System.out.println("Room:"+Room+" wait");
                    LockTools.StartCondition.await();
                }
                System.out.println("Room:"+Room+" out of wait");
            } finally {
                if(super.Start != true){
                    super.Start = true;
                    LockTools.StartCondition.signalAll();
                }
                LockTools.StartLock.unlock();
                System.out.println("###########################Room:"+Room+"is Ready! Test Begin##########################################");
            }


            while(!super.Exit){

                process();

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
