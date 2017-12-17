package sample;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(value = Parameterized.class)
public class LockConditionProducerConsumerApplicationTest {
	Boolean isAsync;
	Integer amountToProduce;
	
	public LockConditionProducerConsumerApplicationTest(Boolean isAsync, Integer amountToProduce) {
		this.isAsync = isAsync;
		this.amountToProduce = amountToProduce;
	}
	
	 @Parameters(name = "{index}: x({0}, {1})")
	    public static Collection<Object[]> data() {
	        return Arrays.asList(new Object[][]{
	                {true, 20},
	                {false, 30}
	        });
	    }

	    
	@Test public void test() {
		
		IntStream.range(0,10).forEach(c->{
			System.out.println("running test" + c);
			runTrial(amountToProduce);
		});
	}
	
	public void runTrial(int amountToProduce) {
		LockConditionProducerConsumerApplication applicationInstance = new LockConditionProducerConsumerApplication();
		int amountConsumed = applicationInstance.run(false, 10, TimeUnit.SECONDS, amountToProduce);
		Assert.assertEquals(amountToProduce, amountConsumed);
	}
}
