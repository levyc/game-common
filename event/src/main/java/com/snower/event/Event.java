package com.snower.event;
/**
 * 事件抽象
 * @author Levy
 *
 */
public class Event<S,D> {
	
	/**事件类型*/
	private int type;
	/**事件源*/
	private S source;
	/**事件数据*/
	private D data;
	/**事件间异步执行*/
	private boolean asynchronous;
	
	public Event(int type,S source,D data){
		this.type = type;
		this.source = source;
		this.data = data;
	}
	
	
	public Event(int type, S source) {
		this(type, source, null);
	}


	public int getType() {
		return type;
	}


	public S getSource() {
		return source;
	}


	public D getData() {
		return data;
	}


	public boolean isAsynchronous() {
		return asynchronous;
	}


	public void setAsynchronous(boolean asynchronous) {
		this.asynchronous = asynchronous;
	}
	
}
