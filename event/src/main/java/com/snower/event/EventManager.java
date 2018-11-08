package com.snower.event;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EventManager {
	
	/**各种事件订阅者集合K:事件类型  V:订阅者集合*/
	private Map<Integer,Set<EventSubscriber>> subscribers = new HashMap<Integer, Set<EventSubscriber>>();
	
}
