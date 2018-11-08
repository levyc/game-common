package com.snower.event;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.snower.event.annotation.EventHandler;
/**
 * 事件管理器
 * @author levy
 *
 */
public class EventManager {
	
	/**各种事件订阅者集合K:事件类型  V:订阅者集合*/
	private Map<Integer,Set<EventSubscriber>> subscribers = new HashMap<Integer, Set<EventSubscriber>>();
	
	/**
	 * 注册事件订阅者
	 * @param target
	 */
	public void registerSubscriber(Object target){
		
		Class<? extends Object> class1 = target.getClass();
		Method[] methods = class1.getMethods();
		for(Method method:methods){
			method.setAccessible(true);
			EventHandler annotation = method.getAnnotation(EventHandler.class);
			if(annotation==null){
				continue;
			}
			EventSubscriber subscriber = new EventSubscriber(target,method);
			int eventType = annotation.value();
			Set<EventSubscriber> set = subscribers.get(eventType);
		}
		
	}
	
}
