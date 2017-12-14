package sample;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

public class WidgetProducer implements Runnable{
	public WidgetProducer(AtomicBoolean widgetProducerOpen, ArrayBlockingQueue<Widget> widgetQueue, int numberOfBatches, int batchSize) {
		this.widgetQueue = widgetQueue;
		this.widgetProducerOpen = widgetProducerOpen;
		this.batchSize = batchSize;
		this.numberOfBatches = numberOfBatches;
	}
	private AtomicBoolean widgetProducerOpen;
	private ArrayBlockingQueue<Widget> widgetQueue;
	private int numberOfBatches = 0;
	private int batchSize = 0;
	int amountMade = 0;
	
	@Override
	public void run() {
		System.out.println("starting shop");
		int batchesMade = 0;
		while(batchesMade < numberOfBatches || !widgetQueue.isEmpty() ) {
			System.out.println("WidgetProducer looking for widgets");
			synchronized(widgetQueue) {
				System.out.println("WidgetProducer approaching widget container");
				if(widgetQueue.size() == 0) {
					int currentCount = 0;
					System.out.println("...making widgets ");
					final int iBatchesMade = batchesMade;
					IntStream.range(0, 10).forEach(i -> {
						widgetQueue.add(new Widget("batch-"+iBatchesMade+"__widget" + currentCount));
						amountMade++;
					});
					
					System.out.println("...done making widgets " + widgetQueue.size() + "  amountMade:" + amountMade);
					
					batchesMade++;
				}
			}
			
			System.out.println("tending shop");
			try {
				Thread.sleep(new Double(1000 * Math.random()).longValue());
			} catch (InterruptedException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
			
		}
		System.out.println("produced " + numberOfBatches + " batches ");
		System.out.println("closing shop");
		
		widgetProducerOpen.set(false);
	}

	public int getAmountMade() {
		return amountMade;
	}

	
}
