package com.bupt.Server;

import java.util.*;
import com.bupt.InfoBean;
public abstract class BaseThread {
    static public boolean Exit = false;
    static public boolean Start = false;

    static public String StateStr = "";
    static public boolean ShouldReply = false;
    static public int Room = 0;

    //仅仅用于存放各个房间的账单
    static public Map<Integer,InfoBean> RoomRes = new HashMap<>();
    public void NewBill(InfoBean Info){
        int roomNum = Integer.valueOf(Info.Room);
        String state = Info.Value;
        String bill = Info.Bill;
        if(RoomRes.get(roomNum)==null){
            RoomRes.put(roomNum,Info);
        }else {
            InfoBean ExistInfo = RoomRes.get(roomNum);
            ExistInfo.Bill += ";";
            ExistInfo.Bill += Info.Bill;
        }
    }
    public String GetBill(int room){
        InfoBean TargetInfo = RoomRes.get(room);
        String res = "";
        res +="r="+TargetInfo.Room+" b="+TargetInfo.Bill;
        return res;
    }
}
