package sample;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class WaitNotifyProducerConsumerApplicationTest {
	
	@Test public void test() {
		int amountToProduce = 50; 
		IntStream.range(0,10).forEach(c->{
			System.out.println("running [WAITNOTIFY] test" + c);
			runTrial(amountToProduce);
		});
	}
	
	public void runTrial(int amountToProduce) {
		WaitNotifyProducerConsumerApplication applicationInstance = new WaitNotifyProducerConsumerApplication();
		int amountConsumed = applicationInstance.run(10, TimeUnit.SECONDS, amountToProduce);
		Assert.assertEquals(amountToProduce, amountConsumed);
	}
}
