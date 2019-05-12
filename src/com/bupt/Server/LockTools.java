package com.bupt.Server;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockTools {

    //Test Node用锁
    static public Lock Testlock = new ReentrantLock();
    static public Condition TestStartCondition = Testlock.newCondition();

    //Server端锁 检查所有Client就位
    static public Lock StartLock = new ReentrantLock();
    static public Condition StartCondition = StartLock.newCondition();

    //Server端锁 检查是否有来自Client的新信息
    static public Lock ReplyLock =  new ReentrantLock();
    static public Condition StateEmptyCondition = ReplyLock.newCondition();
    //static public Condition StateFullCondition = lock.newCondition();


}
