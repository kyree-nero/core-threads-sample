package sample;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class SimpleWaitNotifyProducerConsumerApplication {
	public static void main(String[] args) {
		new SimpleWaitNotifyProducerConsumerApplication().run(30, TimeUnit.SECONDS, 50);
		
	}
	public Integer run(long timeout, TimeUnit unit, Integer amountToProduce) {
		
		System.out.println(("amountToProduce:"+amountToProduce+""));
		WidgetBuffer buffer = new WidgetBuffer(10);
		
		List<BufferedWidgetConsumer> consumers = 
				Arrays.asList("A", "B", "C", "D").stream().
				map(s -> new BufferedWidgetConsumer(buffer, s)).
				collect(Collectors.toList());
		
		
		ExecutorService executor = Executors.newFixedThreadPool(5);
		
		executor.execute(new BufferedWidgetProducer(buffer, amountToProduce ));
		
		consumers.stream().forEach(e -> executor.execute(e));
		executor.shutdown();
		boolean terminatedGracefully = false;
		try {
			terminatedGracefully = executor.awaitTermination(timeout, unit);
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
		Integer amountConsumed = consumers
				.stream()
				.collect(Collectors.summingInt(BufferedWidgetConsumer::getConsumed));
		System.out.println(("amountConsumed:"+amountConsumed+""));
		return amountConsumed;
	}
}
