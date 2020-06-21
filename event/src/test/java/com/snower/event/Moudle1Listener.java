package com.snower.event;

import com.snower.event.annotation.EventHandler;
import com.snower.event.annotation.Listener;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 模块1下定义事件处理器的类
 *
 * @author levy
 */
@Listener
public class Moudle1Listener {


  @EventHandler(EventType.LOGIN_EVENT)
  public void handleLogin(Event<Long, AtomicInteger> event) {
    Long source = event.getSource();
    System.err.println("模块1下收到登录事件--玩家:" + source + "登录了,数据模块+1");
    event.getData().getAndIncrement();
  }

}
