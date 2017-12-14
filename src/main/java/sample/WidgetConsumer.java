package sample;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class WidgetConsumer implements Runnable {
	private ArrayBlockingQueue<Widget> widgetQueue;
	private int threshold = 50;
	private String id;
	private AtomicBoolean widgetProducerOpen;
	private int consumed= 0;
	
	public WidgetConsumer(AtomicBoolean widgetProducerOpen, ArrayBlockingQueue<Widget> widgetQueue, String id) {
		this.id = id;
		this.widgetQueue = widgetQueue;
		this.widgetProducerOpen = widgetProducerOpen;
	}
	
	
	@Override
	public void run() {
		try {
			
			while(widgetProducerOpen.get() == true && consumed < threshold ) {
					System.out.println("WidgetConsumer "+id+" looking for widgets");
					
					Widget widget = widgetQueue.poll(3, TimeUnit.SECONDS);
					if(widget != null) {
						
						System.out.println("...consuming");
						consumed ++;
						
							Thread.sleep(new Double(1000 * Math.random()).longValue());
						
						System.out.println("...consumed");
					}else {
						System.out.println("waiting for widgets");
						try {
							Thread.sleep(new Double(1000 * Math.random()).longValue());
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
						}
						System.out.println("done waiting for widgets");
					}
				
			}
			System.out.println("end WidgetConsumer "+id + " ...consumed " + consumed);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
		
	}


	public int getConsumed() {
		return consumed;
	}
	
}
