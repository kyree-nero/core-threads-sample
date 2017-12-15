package sample;

public interface WidgetBuffer {

	void setClosed(boolean closed);

	boolean bufferEmpty();

	Widget getWidget(String id);

	void putWidget(Widget widget);

	boolean isClosed();

}