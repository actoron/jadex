package storageService.exceptions;

public class StorageException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public StorageException() {
	}

	public StorageException(String string) {
		super(string);
	}

}
