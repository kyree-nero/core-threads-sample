package sample;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Test;

public class SimpleProducerConsumerApplicationTest {
	@Test public void test() {
		int amountToProduce = 20; 
		IntStream.range(0,10).forEach(c->{
			System.out.println("running test" + c);
			runTrial(amountToProduce);
		});
	}
	
	public void runTrial(int amountToProduce) {
		SimpleProducerConsumerApplication applicationInstance = new SimpleProducerConsumerApplication();
		int amountConsumed = applicationInstance.run(10, TimeUnit.SECONDS, 2, amountToProduce/2);
		Assert.assertEquals(amountToProduce, amountConsumed);
	}
}
