package com.snower.utils.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 可命名线程工厂
 *
 * @author levy
 */
public class NamedThreadFactory implements ThreadFactory {

  private String name;
  private AtomicInteger sn = new AtomicInteger(1);
  private ThreadGroup threadGroup;


  public NamedThreadFactory(String groupName, String name) {
    super();
    this.name = name;
    this.threadGroup = new ThreadGroup(groupName);
  }


  public Thread newThread(Runnable runnable) {
    return new Thread(threadGroup, runnable, getNextThreadName());
  }

  private String getNextThreadName() {
    return this.name + "-thread-" + this.sn.getAndIncrement();
  }

}
