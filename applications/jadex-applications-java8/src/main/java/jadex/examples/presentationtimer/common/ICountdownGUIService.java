package jadex.examples.presentationtimer.common;


public interface ICountdownGUIService extends ICountdownService {

	void informTimeUpdated(String timeString);
	
	void informStateUpdated(State state);
	
	void setController(ICountdownController controller);
}
