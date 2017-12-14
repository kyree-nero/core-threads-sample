package sample;

public class BufferedWidgetConsumer implements Runnable {
	private WidgetBuffer buffer;
	
	private String id;
	private int threshold = 50;
	private int consumed= 0;
	
	public BufferedWidgetConsumer(WidgetBuffer buffer, String id) {
		this.id = id;
		this.buffer = buffer;
	}
	
	
	@Override
	public void run() {
		try {
			
			while(!buffer.isClosed() && consumed < threshold ) {
					System.out.println("[CONSUMER-"+id+"]  looking for widgets");
					
					Widget widget = buffer.getWidget(id);
					if(widget != null) {
						
						System.out.println("[CONSUMER-"+id+"] ...consuming");
						consumed ++;
						
							Thread.sleep(new Double(500 * Math.random()).longValue());
						
						System.out.println("[CONSUMER-"+id+"] ...consumed");
					}else {
					
						System.out.println("[CONSUMER-"+id+"] did not receive any widgets");
						break;
					}
				
			}
			System.out.println("[CONSUMER-"+id+"] end  ... total consumed " + consumed);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		
	}


	public int getConsumed() {
		return consumed;
	}
	
	
}
