package com.snower.event;

import com.snower.event.annotation.EventHandler;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 模块2下定义事件处理器的类
 *
 * @author levy
 */
public class Moudle2Listener {


  @EventHandler(EventType.LOGIN_EVENT)
  public void handleLogin(Event<Long, AtomicInteger> event) {
    Long source = event.getSource();
    System.err.println("模块2下收到登录事件--玩家:" + source + "登录了,数据模块+1");
    event.getData().getAndIncrement();
  }

}
