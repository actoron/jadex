package jadex.examples.presentationtimer.common;

public class State {

	public boolean isRunning;
	public boolean isInfo;
	public boolean isWarn;

	public State() {
	}
	
	public State(boolean isRunning, boolean isInfo, boolean isWarn) {
		this.isRunning = isRunning;
		this.isInfo = isInfo;
		this.isWarn = isWarn;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public boolean isInfo() {
		return isInfo;
	}

	public void setInfo(boolean isInfo) {
		this.isInfo = isInfo;
	}

	public boolean isWarn() {
		return isWarn;
	}

	public void setWarn(boolean isWarn) {
		this.isWarn = isWarn;
	}

	@Override
	public String toString() {
		return isRunning ? "Running" : "Not Running" 
				+ (isInfo ? " (INFO) " : "") 
				+ (isWarn ? " (WARN)" : "");
	}

}