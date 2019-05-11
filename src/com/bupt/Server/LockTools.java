package com.bupt.Server;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockTools {
    static public Lock lock = new ReentrantLock();
    static public Condition StartCondition = lock.newCondition();
    static public Condition StateEmptyCondition = lock.newCondition();
    static public Condition StateFullCondition = lock.newCondition();
  //  static public Condition
}
