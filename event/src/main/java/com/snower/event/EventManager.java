package com.snower.event;

import com.alibaba.fastjson.JSON;
import com.snower.event.annotation.EventHandler;
import com.snower.utils.thread.NamedThreadFactory;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 事件管理器
 *
 * @author levy
 */
public class EventManager {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private BlockingQueue<Event<?, ?>> eventQueue = new LinkedBlockingQueue<Event<?, ?>>();
  /**
   * 各种事件订阅者集合K:事件类型  V:订阅者集合
   */
  private Map<Integer, Set<EventSubscriber>> subscribers = new HashMap<Integer, Set<EventSubscriber>>();

  public EventManager() {
    Thread consumerThread = new NamedThreadFactory("event").newThread(new EventConsumer());
    consumerThread.setDaemon(true);
    consumerThread.start();
  }


  /**
   * 注册事件订阅者
   */
  public void registerSubscriber(Object target) {
    //将有事件处理器注解的方法包装成事件订阅者存储起来
    Class<? extends Object> class1 = target.getClass();
    Method[] methods = class1.getMethods();

    for (Method method : methods) {
      method.setAccessible(true);
      EventHandler annotation = method.getAnnotation(EventHandler.class);
      if (annotation == null) {
        continue;
      }

      EventSubscriber subscriber = new EventSubscriber(target, method);
      int eventType = annotation.value();
      Set<EventSubscriber> set = subscribers.get(eventType);
      if (set == null) {
        set = new HashSet<EventSubscriber>();
        subscribers.put(eventType, set);
      }
      set.add(subscriber);
    }

  }

  public void publishEvent(Event<?, ?> event) {
    fireEvent(event);
  }

  private void fireEvent(Event<?, ?> event) {
    boolean asynchronous = event.isAsynchronous();
    if (asynchronous) {//异步则放到事件队列内等待处理
      if (!eventQueue.add(event)) {
        logger.error("event:[{}] add to queue fail", JSON.toJSON(event));
      }
    } else {//同步执行
      doFireEvent(event);
    }
  }


  private void doFireEvent(Event<?, ?> event) {
    Set<EventSubscriber> eventSubscribers = this.subscribers.get(event.getType());

    if (eventSubscribers == null || eventSubscribers.isEmpty()) {
      logger.error("event:[{}] no subscriber");
      return;
    }

    for (EventSubscriber subscriber : eventSubscribers) {
      try {
        subscriber.doInvoke(event);
      } catch (Exception e) {
        logger.error("execute event fail,type:[{}],target:[{}],method:[{}],reason:[{}]",
            event.getType(), subscriber.getTarget().getClass().toString(),
            subscriber.getEventHandlemethod().toString(), e);
      }
    }
  }

  /**
   * 事件消费者
   *
   * @author levy
   */
  private class EventConsumer implements Runnable {

    public void run() {
      while (true) {
        try {
          Event<?, ?> event = eventQueue.take();
          doFireEvent(event);
        } catch (InterruptedException e) {
          logger.error("event queue execute error,reason:[{}]", e);
        }
      }
    }

  }

}
