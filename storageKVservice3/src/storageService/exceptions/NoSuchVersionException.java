package storageService.exceptions;

public class NoSuchVersionException extends Exception {

	/**
	 * NoSuchVersionException is thrown upon an attempt to update a specific
	 * version that doesn't exist in the database.
	 */
	private static final long serialVersionUID = 1L;

	public NoSuchVersionException() {
		// TODO Auto-generated constructor stub
	}

	public NoSuchVersionException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public NoSuchVersionException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public NoSuchVersionException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public NoSuchVersionException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);
		// TODO Auto-generated constructor stub
	}

}
