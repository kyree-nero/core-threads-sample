package sample;

public class BufferedWidgetProducer implements Runnable{
	public BufferedWidgetProducer(WidgetBuffer buffer, int amountToProduce) {
		this.buffer = buffer; 
		this.amountToProduce = amountToProduce;
	}
	private WidgetBuffer buffer;
	private Integer amountToProduce;
	
	@Override
	public void run() {
		try {
		System.out.println("[PRODUCER] starting shop ");
		buffer.setClosed(false);
		int currentCount = 0;
		while(!buffer.isClosed() && currentCount < amountToProduce) {
			System.out.println("[PRODUCER] WidgetProducer looking for widgets");
			buffer.putWidget(new Widget("x"));	
			currentCount++;
			System.out.println("[PRODUCER] widget produced");
		}
		while(!buffer.bufferEmpty()) {
			System.out.println("[PRODUCER] finished production...waiting for buffer to clear");
			Thread.sleep(500);
		}
		System.out.println("closing shop");
		buffer.setClosed(true);
		}catch(InterruptedException i) {
			Thread.currentThread().interrupt();
		}
	}

}
