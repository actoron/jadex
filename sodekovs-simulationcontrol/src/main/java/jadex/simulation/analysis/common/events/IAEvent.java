package jadex.simulation.analysis.common.events;

public interface IAEvent
{

	public abstract void setCommand(String eventCommand);

	public abstract String getCommand();

	public abstract String getEventType();

	public abstract Object getMutex();

}