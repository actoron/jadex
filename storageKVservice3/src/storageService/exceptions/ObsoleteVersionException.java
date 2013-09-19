package storageService.exceptions;

public class ObsoleteVersionException extends Exception {

	/**
	 * ObsoleteVersionException is thrown upon an attempt to write a versioned
	 * value to a database that contains a more recent version.
	 */
	private static final long serialVersionUID = 1L;

	public ObsoleteVersionException() {
		// TODO Auto-generated constructor stub
	}

	public ObsoleteVersionException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public ObsoleteVersionException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public ObsoleteVersionException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public ObsoleteVersionException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);
		// TODO Auto-generated constructor stub
	}

}
