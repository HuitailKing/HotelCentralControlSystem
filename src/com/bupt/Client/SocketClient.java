package com.bupt.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.*;
import com.google.gson.*;
import java.util.*;
import com.bupt.InfoBean;

public class SocketClient implements  Runnable {

    //Port
    private int portOffset = 0;
    private int port = 20050;
    private int port_for_server = 30050;
    private int ServerPort= 8888;
    private int TestPort = 9999;

    //for Communications
    private InfoBean Info = new InfoBean();
    PrintWriter ServerWriter = null;
    BufferedReader ServerGetter = null;
    PrintWriter TestWriter = null;
    BufferedReader TestGetter = null;
    static private Gson gson = new Gson();
    private Map<String,String> InfoMap = new HashMap<>();


    //Info
    private String Room;
    private int fare = 0;
    //defined by teacher
    private int time = 0;
    private int t = 0; //当前温度
    private int it = 0;
    private int tt = 0; //目标温度
    private int w = 0;


    public SocketClient(String Room){
        this.Room = "Room---"+Room+"---";
        this.portOffset = Integer.parseInt(Room);
    }

    private void Process(int w){
        try{
            Info.clear();
            //到预定温度且风机没关
            //这个费用目前不知道什么时候传出去
            if(t ==tt && w!=0){
                fare+=5*(it-tt);
                Thread.sleep(1000);
                return;
            }


            if(w == 0 && t< it){
                t+=1;
                time+=2;
                Info.Value = "r="+Room+" t="+t+" w="+w;
                Info.ActionType = "State";
                Thread.sleep(2000);
            } else if(w == 1){
                t-=1;
                time +=6;
                Info.Value = "r="+Room+" t="+t+" w="+w;
                Info.ActionType = "b";
                Info.Bill = (time-6)+","+time+","+t+","+(t-1)+","+ 5;
                Thread.sleep(6000);
            } else if(w == 2){
                t-=1;
                time += 4;
                Info.Value = "r="+Room+" t="+t+" w="+w;
                Info.ActionType = "b";
                Info.Bill = (time-4)+","+time+","+t+","+(t-1)+","+ 5;
                Thread.sleep(4000);
            } else if(w == 3){
                t-=1;
                time +=2;
                Info.Value = "r="+Room+" t="+t+" w="+w;
                Info.ActionType = "b";
                Info.Bill = (time-2)+","+time+","+t+","+(t-1)+","+ 5;
                Thread.sleep(2000);
            }
            ServerWriter.println(gson.toJson(Info));
            ServerWriter.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public  void run() {
        try {
            System.out.println(Room+"Client instance,Room:"+Room);

            //连接test
            Socket testsoket = new Socket();
            testsoket.bind(new InetSocketAddress(port+portOffset));
            testsoket.connect(new InetSocketAddress("127.0.0.1", TestPort));
            System.out.println("Connect Test:"+(port+portOffset)+" to "+TestPort);

            //连接server
            Socket socket = new Socket();
            socket.bind(new InetSocketAddress(port_for_server+portOffset));
            socket.connect(new InetSocketAddress("127.0.0.1", ServerPort));
            System.out.println("Connect Server:"+(port_for_server+portOffset)+" to "+ServerPort);


            System.out.println(Room+"Client start!");
            ServerWriter = new PrintWriter(socket.getOutputStream());
            ServerGetter = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            TestWriter = new PrintWriter(socket.getOutputStream());
            TestGetter = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //告知Server，本Room Ready
            Info.clear();
            Info.ActionType = "r";
            Info.Room = Room;
            ServerWriter.println(gson.toJson(Info));
            ServerWriter.flush();
            System.out.println(Room+"Client ready");

            //得到Test节点的初始化
            Info = gson.fromJson(TestGetter.readLine(),InfoBean.class);
            assert Info.ActionType.equals("it");
            t = it = Integer.valueOf(Info.it);
            System.out.println("【Client】"+Room+"is initilized");

            //被设置风速，目标温度
            Info = gson.fromJson(TestGetter.readLine(),InfoBean.class);
            assert Info.ActionType.equals("tt");
            tt = Integer.valueOf(Info.tt);
            w = Integer.valueOf(Info.w);
            System.out.println("【Client】"+Room+"is setted");

            while(true){
                if(TestGetter.read()==0)
                    Process(w);
                else{
                    Info = gson.fromJson(TestGetter.readLine(),InfoBean.class);
                    assert Info.ActionType.equals("w");
                    w = Integer.valueOf(Info.w);
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}