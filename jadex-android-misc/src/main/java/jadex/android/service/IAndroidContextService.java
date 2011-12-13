package jadex.android.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public interface IAndroidContextService {

	public FileOutputStream openFileOutputStream(String name)
			throws FileNotFoundException;

	public FileInputStream openFileInputStream(String name)
			throws FileNotFoundException;
	
	public File getFile(String name);

}