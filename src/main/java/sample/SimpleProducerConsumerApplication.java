package sample;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class SimpleProducerConsumerApplication {

	
	
	public static void main(String[] args) {
		new SimpleProducerConsumerApplication().run(10, TimeUnit.SECONDS, 2, 10);
		
	}
	public Integer run(long timeout, TimeUnit unit,  int numberOfBatches, int batchSize) {
		AtomicBoolean widgetProducerOpen = new AtomicBoolean(true);
		ArrayBlockingQueue<Widget> widgetQueue = new ArrayBlockingQueue<Widget>(10);
		
		List<WidgetConsumer> consumers = 
				Arrays.asList("A", "B", "C", "D").stream().
				map(s -> new WidgetConsumer(widgetProducerOpen, widgetQueue, s)).
				collect(Collectors.toList());
		
		
		ExecutorService executor = Executors.newFixedThreadPool(5);
		
		WidgetProducer producer = new WidgetProducer(widgetProducerOpen, widgetQueue,   numberOfBatches,  batchSize);
		executor.execute(producer);
		
		consumers.stream().forEach(e -> executor.execute(e));
		
		executor.shutdown();
		boolean terminatedGracefully = false;
		try {
			terminatedGracefully = executor.awaitTermination(timeout, unit);
			//executor.shutdown();
			if(terminatedGracefully == false) {
				List<Runnable> runnables = executor.shutdownNow();
				System.out.println("---"+runnables.size()+" threads terminated ungracefully---");
				runnables.stream().forEach(r -> System.out.println(r));
				System.out.println("---");
			}else {
				System.out.println("---all threads terminated gracefully---");
			}
		} catch (InterruptedException e1) {
			e1.printStackTrace();
			
		}
		
		System.out.println(("amountProduced:"+producer.getAmountMade()+""));
		Integer amountConsumed = consumers
				.stream()
				.collect(Collectors.summingInt(WidgetConsumer::getConsumed));
		System.out.println(("amountConsumed:"+amountConsumed+""));
		return amountConsumed;
		
		
	}
	
	
	

}
