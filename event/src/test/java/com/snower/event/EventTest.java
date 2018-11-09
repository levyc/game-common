package com.snower.event;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

public class EventTest {
	
	@Test
	public void test(){
		EventManager eventManager = new EventManager();
		Moudle1Listener moudle1Listener = new Moudle1Listener();
		Moudle2Listener moudle2Listener = new Moudle2Listener();
		eventManager.registerSubscriber(moudle1Listener);
		eventManager.registerSubscriber(moudle2Listener);
		
		AtomicInteger counter = new AtomicInteger(0);
		
		eventManager.publishEvent(new Event<Long, Object>(EventType.LOGIN_EVENT,1000L,counter));
		
		Assert.assertEquals(2, counter.get());
	}
	
}
