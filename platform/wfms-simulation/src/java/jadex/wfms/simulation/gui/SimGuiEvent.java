package jadex.wfms.simulation.gui;

public class SimGuiEvent
{
	public static final int OPEN_PROCESS = 0;
	
	private int type;
	
	public SimGuiEvent(int type)
	{
		this.type = type;
	}
	
	public int getEventType()
	{
		return type;
	}
}
