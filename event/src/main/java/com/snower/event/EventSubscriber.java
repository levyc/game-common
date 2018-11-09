package com.snower.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
/**
 * 事件订阅者
 * @author levy
 *
 */
public class EventSubscriber {
	
	/**处理方法对应的实例*/
	private Object target;
	/**事件处理器*/
	private Method eventHandlemethod;
	
	public EventSubscriber(Object exector, Method eventHandlemethod) {
		super();
		this.target = exector;
		this.eventHandlemethod = eventHandlemethod;
	}

	public Object doInvoke(Object...args) throws Exception{
		try {
			return eventHandlemethod.invoke(target, args);
		} catch (IllegalAccessException e) {
			throw new Exception("cannot access method invoke ");
		} catch (IllegalArgumentException e) {
			throw new Exception("method invoke exception whih params:"+Arrays.toString(args));
		} catch (InvocationTargetException e) {
			throw new Exception("InvocationTarget error");
		}
	}

	public Object getTarget() {
		return target;
	}

	public Method getEventHandlemethod() {
		return eventHandlemethod;
	}
	
	
	
	
}
