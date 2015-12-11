package jadex.base;

import java.io.File;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.filetransfer.FileData;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.transformation.annotations.Classname;

/**
 *  Helper methods for remote file system view.
 */
public class SRemoteFileChooser
{	
	/**
	 *  Initialize the remote file system view such that
	 *  home, default and current directory as well as roots
	 *  are available.
	 */
	public static IFuture<Object[]>	init(IExternalAccess access)
	{
		return access.scheduleStep(new IComponentStep<Object[]>()
		{
			@Classname("init")
			public IFuture<Object[]> execute(IInternalAccess ia)
			{
				try
				{
					Object[]	ret	= new Object[4];
					ret[0]	= FileData.convertToRemoteFiles(File.listRoots());
					ret[1]	= new FileData(SUtil.getHomeDirectory());
					ret[2]	= new FileData(SUtil.getDefaultDirectory());
					
					String	path	= new File(".").getAbsolutePath();
					if(path.endsWith("."))
					{
						path	= path.substring(0, path.length()-1);
					}
					ret[3]	= new FileData(new File(path));					
					
					return new Future<Object[]>(ret);
				}
				catch(Exception e)
				{
					// Protect remote platform from execution errors.
					Thread.dumpStack();
					e.printStackTrace();
					return new Future<Object[]>(e);
				}
			}
		});
	}
	
	/**
	 * Returns all root partitions on this system. For example, on Windows, this
	 * would be the "Desktop" folder, while on DOS this would be the A: through
	 * Z: drives.
	 */
	public static IFuture<FileData[]> getRoots(IExternalAccess access)
	{
		return access.scheduleStep(new IComponentStep<FileData[]>()
		{
			@Classname("getRoots")
			public IFuture<FileData[]> execute(IInternalAccess ia)
			{
				try
				{
					File[] roots = File.listRoots();
					return new Future<FileData[]>(FileData.convertToRemoteFiles(roots));
				}
				catch(Exception e)
				{
					// Protect remote platform from execution errors.
					Thread.dumpStack();
					e.printStackTrace();
					return new Future<FileData[]>(e);
				}					
			}
		});
	}


	// Providing default implementations for the remaining methods
	// because most OS file systems will likely be able to use this
	// code. If a given OS can't, override these methods in its
	// implementation.
	public static IFuture<FileData> getHomeDirectory(IExternalAccess access)
	{
		return access.scheduleStep(new IComponentStep<FileData>()
		{
			@Classname("getHomeDirectory")
			public IFuture<FileData> execute(IInternalAccess ia)
			{
				try
				{
					File ret = SUtil.getHomeDirectory();
					return new Future<FileData>(ret!=null? new FileData(ret): null);
				}
				catch(Exception e)
				{
					// Protect remote platform from execution errors.
					Thread.dumpStack();
					e.printStackTrace();
					return new Future<FileData>(e);
				}
			}
		});
	}

	/**
	 *  Get the current directory of the remote VM.
	 */
	public static IFuture<FileData> getCurrentDirectory(IExternalAccess access)
	{
		return access.scheduleStep(new IComponentStep<FileData>()
		{
			@Classname("getCurrentDirectory")
			public IFuture<FileData> execute(IInternalAccess ia)
			{
				try
				{
					String	path	= new File(".").getAbsolutePath();
					if(path.endsWith("."))
					{
						path	= path.substring(0, path.length()-1);
					}
					return new Future<FileData>(new FileData(new File(path)));
				}
				catch(Exception e)
				{
					// Protect remote platform from execution errors.
					Thread.dumpStack();
					e.printStackTrace();
					return new Future<FileData>(e);
				}				
			}
		});
	}
	
	/**
	 * Return the user's default starting directory for the file chooser.
	 * 
	 * @return a <code>File</code> object representing the default starting
	 *         folder
	 * @since 1.4
	 */
	public static IFuture<FileData> getDefaultDirectory(IExternalAccess access)
	{
		return access.scheduleStep(new IComponentStep<FileData>()
		{
			@Classname("getDefaultDirectory")
			public IFuture<FileData> execute(IInternalAccess ia)
			{
				try
				{
					File ret = SUtil.getDefaultDirectory();
					return new Future<FileData>(ret!=null? new FileData(ret): null);
				}
				catch(Exception e)
				{
					// Protect remote platform from execution errors.
					Thread.dumpStack();
					e.printStackTrace();
					return new Future<FileData>(e);
				}					
			}
		});
	}

	/**
	 * Gets the list of shown (i.e. not hidden) files.
	 */
	public static IFuture<FileData[]> getFiles(IExternalAccess access, final FileData mydir, final boolean useFileHiding)
	{
		return access.scheduleStep(new IComponentStep<FileData[]>()
		{
			@Classname("getFiles")
			public IFuture<FileData[]> execute(IInternalAccess ia)
			{
				try
				{
					File dir = new File(mydir.getPath());
					File[] files;
					if(dir.exists())
					{
						files = SUtil.getFiles(dir, useFileHiding);
	//						System.out.println("children: "+dir+" "+SUtil.arrayToString(files));
					}
					else
					{
	//						System.out.println("file does not exist: "+dir);
						files = new File[0];
					}
					return new Future<FileData[]>(FileData.convertToRemoteFiles(files));
				}
				catch(Exception e)
				{
					// Protect remote platform from execution errors.
					Thread.dumpStack();
					e.printStackTrace();
					return new Future<FileData[]>(e);
				}					
			}
		});
	}


	/**
	 * Returns the parent directory of <code>dir</code>.
	 * 
	 * @param dir the <code>File</code> being queried
	 * @return the parent directory of <code>dir</code>, or <code>null</code> if
	 *         <code>dir</code> is <code>null</code>
	 */
	public static IFuture<FileData> getParentDirectory(IExternalAccess access, final String path)
	{
		return access.scheduleStep(new IComponentStep<FileData>()
		{
			@Classname("getParentDirectory")
			public IFuture<FileData> execute(IInternalAccess ia)
			{
				try
				{
					File parent = SUtil.getParentDirectory(new File(path)); // todo: useFileHandling
					return new Future<FileData>(parent!=null? new FileData(parent): null);
				}
				catch(Exception e)
				{
					// Protect remote platform from execution errors.
					Thread.dumpStack();
					e.printStackTrace();
					return new Future<FileData>(e);
				}
			}
		});
	}
}
