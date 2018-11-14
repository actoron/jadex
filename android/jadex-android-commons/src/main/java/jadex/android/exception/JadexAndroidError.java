package jadex.android.exception;

/**
 * General Jadex Android Runtime Error
 * @author Julian Kalinowski
 */
public class JadexAndroidError extends Error {
	private static final long serialVersionUID = -6629474526395117954L;
	private String msg;
	
	public JadexAndroidError() {
		this.msg = "";
	}
	
	public JadexAndroidError(Throwable t) {
		super (t);
	}
	
	public JadexAndroidError(String msg) {
		this.msg = msg;
	}
	
	@Override
	public String getMessage() {
		return this.msg;
	}

}
