package sample;

public class WidgetBuffer {
	private Widget[] widgets;
	private int readIndex;
	private int writeIndex;
	private int occupied;
	private boolean closed = true;

	
	public WidgetBuffer(int initialSize) {
		super();
		widgets = new Widget[initialSize];
		
	}

	
	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	public boolean bufferEmpty() {
		
		return occupied == 0;
	}

	public Widget getWidget(String id) {
		synchronized(widgets) {
			Widget widget = null;
			int tries = 0;
			while(widget == null) {
				System.out.println("[CONSUMER-"+id+"][TRIAL:"+tries+"] widgets   read: "+readIndex+" write: "+writeIndex+" occupied: "+occupied+"");
				if(readIndex != writeIndex || occupied != 0) {
					System.out.println("[CONSUMER-"+id+"][TRIAL:"+tries+"] filling widget consumer request");
					widget = widgets[readIndex];
					readIndex =  (readIndex + 1) % widgets.length;
					occupied--;
					widgets.notifyAll();
				}else {
					System.out.println("[CONSUMER-"+id+"][TRIAL:"+tries+"] Widget Consumer waiting");
					try {
						widgets.wait(1000);
						tries++;
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
					System.out.println("[CONSUMER-"+id+"][TRIAL:"+tries+"] consumer awoke");
				}
				if(tries == 3) {
					break;
				}
			}
			return widget;
		}
		
	}
	
	public void putWidget(Widget widget) {
		synchronized(widgets) {
			boolean produced = false;
			while(!produced) {
				System.out.println("[PRODUCER] widgets   read: "+readIndex+" write: "+writeIndex+"occupied: "+occupied+"");
				if(occupied == widgets.length) {
					System.out.println("[PRODUCER] all occupied");
					try {
						widgets.wait();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
					System.out.println("[PRODUCER] producer awoke");
				}else {
					System.out.println("[PRODUCER] producing widget");
					widgets[writeIndex] = widget;
					writeIndex = (writeIndex + 1) % widgets.length;;
					occupied++;
					produced = true;
					widgets.notifyAll();
				}
			}
		}
	}

	public boolean isClosed() {
		return closed;
	}
}
