package sample;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LockConditionProducerConsumerApplication {
	public static void main(String[] args) {
		
		new LockConditionProducerConsumerApplication().run(false, 30, TimeUnit.SECONDS, 50);
		
	}
	public Integer run(boolean runAsyncConsumers, long timeout, TimeUnit unit, Integer amountToProduce) {
		
		System.out.println("amountToProduce:"+amountToProduce+"");
		LockConditionWidgetBuffer buffer = new LockConditionWidgetBuffer(10);
		
		
		ExecutorService executor = Executors.newFixedThreadPool(5);
		
		//run the producer
		executor.execute(new BufferedWidgetProducer(buffer, amountToProduce ));
	
		//run the consumers
		List<Future<Integer>> consumed = null;
		
		if(runAsyncConsumers) {
			consumed = startAsyncConsumers(buffer, executor);
		}else {
			consumed = startConsumers(buffer, executor);
		}
		
		
		//wait for the consumers to end
		Function<Future<Integer>, Integer> waitForReturn = new Function<Future<Integer>, Integer>(){

			@Override
			public Integer apply(Future<Integer> t) {
				try {
					System.out.println("...got result " + t.get());
					return t.get();
				} catch (Exception e) {
					throw new RuntimeException(e);
				} 
			}
			
		};
		
		//collect results
		Integer amountConsumed = 
				consumed.stream().
					map(waitForReturn).
					collect(Collectors.summingInt(Integer::intValue));
		
		//wait for producer if applicable
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
		
		System.out.println(("amountConsumed:"+amountConsumed+""));
		
			return amountConsumed;
		
	}
	
	private List<Future<Integer>> startConsumers(LockConditionWidgetBuffer buffer, ExecutorService executor ){
		List<BufferedWidgetConsumerTask> consumers = 
				Arrays.asList("A", "B", "C", "D").stream().
				map(s -> new BufferedWidgetConsumerTask(buffer, s)).
				collect(Collectors.toList());
		
		
		return  consumers.stream().
				map(e -> executor.submit(e)).
				collect(Collectors.toList());
		
		
	}
	
	private List<Future<Integer>> startAsyncConsumers(LockConditionWidgetBuffer buffer, ExecutorService executor ){
		List<BufferedWidgetConsumerAsyncTask> consumers = 
				Arrays.asList("A", "B", "C", "D").stream().
				map(s -> new BufferedWidgetConsumerAsyncTask(buffer, s)).
				collect(Collectors.toList());
		
		
		return consumers.stream().
					map(
							c ->
							CompletableFuture.supplyAsync(() -> c.run())
							).collect(Collectors.toList());
						
		
		
					
		
	}
}
