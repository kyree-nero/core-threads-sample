package sample;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockConditionWidgetBuffer implements WidgetBuffer{
	private Widget[] widgets;
	private int readIndex;
	private int writeIndex;
	private int occupied;
	private boolean closed = true;

	private Lock widgetsLock = new ReentrantLock();
	private Condition canRead = widgetsLock.newCondition();
	private Condition canWrite = widgetsLock.newCondition();
	
	public LockConditionWidgetBuffer(int initialSize) {
		super();
		widgets = new Widget[initialSize];
		
	}

	
	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	public boolean bufferEmpty() {
		
		return occupied == 0;
	}

	public Widget getWidget(String id) {
		widgetsLock.lock();
		Widget widget = null;
		int tries = 0;
		while(widget == null) {
			System.out.println("[CONSUMER-"+id+"][TRIAL:"+tries+"] widgets   read: "+readIndex+" write: "+writeIndex+" occupied: "+occupied+"");
			if(readIndex != writeIndex || occupied != 0) {
				System.out.println("[CONSUMER-"+id+"][TRIAL:"+tries+"] filling widget consumer request");
				widget = widgets[readIndex];
				readIndex =  (readIndex + 1) % widgets.length;
				occupied--;
				canWrite.signalAll();
			}else {
				System.out.println("[CONSUMER-"+id+"][TRIAL:"+tries+"] Widget Consumer waiting");
				try {
					canRead.await(1000, TimeUnit.MILLISECONDS);
					tries++;
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
				System.out.println("[CONSUMER-"+id+"][TRIAL:"+tries+"] consumer awoke");
			}
			if(tries == 3) {
				break;
			}
		}
		widgetsLock.unlock();
		return widget;
			
		
	}
	
	public void putWidget(Widget widget) {
		widgetsLock.lock();
			boolean produced = false;
			while(!produced) {
				System.out.println("[PRODUCER] widgets   read: "+readIndex+" write: "+writeIndex+"occupied: "+occupied+"");
				if(occupied == widgets.length) {
					System.out.println("[PRODUCER] all occupied");
					try {
						canWrite.await();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
					System.out.println("[PRODUCER] producer awoke");
				}else {
					System.out.println("[PRODUCER] producing widget");
					widgets[writeIndex] = widget;
					writeIndex = (writeIndex + 1) % widgets.length;;
					occupied++;
					produced = true;
					canRead.signalAll();
				}
			}
		
		widgetsLock.unlock();
	}

	public boolean isClosed() {
		return closed;
	}
}
