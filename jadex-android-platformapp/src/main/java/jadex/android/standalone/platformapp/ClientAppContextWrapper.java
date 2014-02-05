package jadex.android.standalone.platformapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.content.Context;
import android.content.ContextWrapper;

public class ClientAppContextWrapper extends ContextWrapper
{

	private Context original;
	private String packageName;
	private File baseDir;

	public ClientAppContextWrapper(Context base, Context original)
	{
		super(base);
		packageName = base.getApplicationInfo().packageName;
		this.original = original;
		
		baseDir = new File(original.getFilesDir(), packageName + File.pathSeparator);
		if (!baseDir.exists()) {
			baseDir.mkdir();
		}
	}

	@Override
	public FileInputStream openFileInput(String name) throws FileNotFoundException
	{
		File file = new File(baseDir, name);
		return new FileInputStream(file);
	}

	@Override
	public FileOutputStream openFileOutput(String name, int mode) throws FileNotFoundException
	{
		// Does not support world readable yet.
		File file = new File(baseDir, name);
		return new FileOutputStream(file, mode == MODE_APPEND);
	}

	@Override
	public boolean deleteFile(String name)
	{
		File file = new File(baseDir, name);
		return file.delete();
	}

	@Override
	public File getFileStreamPath(String name)
	{
		File file = new File(baseDir, name);
		return file;
	}

	@Override
	public String[] fileList()
	{
		return baseDir.list();
	}

	@Override
	public File getFilesDir()
	{
		return baseDir;
	}

	@Override
	public File getExternalFilesDir(String type)
	{
		return original.getExternalFilesDir(type);
	}

	@Override
	public File getCacheDir()
	{
		return original.getCacheDir();
	}

	@Override
	public File getExternalCacheDir()
	{
		return original.getExternalCacheDir();
	}

	@Override
	public File getDir(String name, int mode)
	{
		// modes not supported
		File file = new File(baseDir, name);
		if (!file.exists()) {
			file.mkdir();
		}
		return file;
	}

	// TODO: database access.
}
