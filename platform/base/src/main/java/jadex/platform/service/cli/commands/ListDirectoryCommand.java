/**
 * 
 */
package jadex.platform.service.cli.commands;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.filetransfer.FileData;
import jadex.bridge.service.types.filetransfer.IFileTransferService;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.transformation.IObjectStringConverter;
import jadex.platform.service.cli.ACliCommand;
import jadex.platform.service.cli.ArgumentInfo;
import jadex.platform.service.cli.CliContext;
import jadex.platform.service.cli.ResultInfo;

/**
 *  List the file contents of a directory.
 */
public class ListDirectoryCommand extends ACliCommand
{
	/**
	 *  Get the command names (name including alias').
	 *  @return A string array of the command name and optional further alias names.
	 */
	public String[] getNames()
	{
		return new String[]{"ls", "dir"};
	}
	
	/**
	 *  Get the command description.
	 *  @return The command description.
	 */
	public String getDescription()
	{
		return "List current directory.";
	}
	
	/**
	 *  Get example usage(s).
	 *  @return Example usages.
	 */
	public String getExampleUsage()
	{
		return "ls c:\temp : list the content of directory c:\temp";
	}
	
	/**
	 *  Invoke the command.
	 *  @param context The context.
	 *  @param args The arguments.
	 */
	public Object invokeCommand(final CliContext context, final Map<String, Object> args)
	{
		final Future<FileData[]> ret = new Future<FileData[]>();
		
		final String dir = args.get(null)==null? context.getShell().getWorkingDir(): (String)args.get(null);
		
		final IExternalAccess comp = (IExternalAccess)context.getUserContext();
		
		comp.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(final IInternalAccess ia)
			{
				ia.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>( IFileTransferService.class, RequiredServiceInfo.SCOPE_PLATFORM))
					.addResultListener(ia.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<IFileTransferService, FileData[]>(ret)
				{
					public void customResultAvailable(final IFileTransferService ds)
					{
						ds.listDirectory(dir).addResultListener(new DelegationResultListener<FileData[]>(ret));
					}
				}));
				
				return IFuture.DONE;
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get the argument infos.
	 *  @param context The context.
	 *  @return The argument infos.
	 */
	public ArgumentInfo[] getArgumentInfos(CliContext context)
	{
		ArgumentInfo dir = new ArgumentInfo(null, String.class, null, "The directory.", null);
		return new ArgumentInfo[]{dir};
	}
	
	/**
	 *  Get the result info.
	 *  @param context The context.
	 *  @return The result info.
	 */
	public ResultInfo getResultInfo(final CliContext clicontext, final Map<String, Object> args)
	{
		return new ResultInfo(IComponentIdentifier.class, "The creation result.", new IObjectStringConverter()
		{
			public String convertObject(Object val, Object context)
			{
				StringBuffer buf = new StringBuffer();
				
				final String dir = (String)args.get(null);
				
				String wd = clicontext.getShell().getWorkingDir();
				buf.append((dir==null? wd: dir)+" content: ").append(SUtil.LF);
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				
				FileData[] fds = (FileData[])val;
				Arrays.sort(fds, FILE_COMPARATOR);
				for(FileData fd: fds)
				{
					buf.append(fd.isDirectory()? "* ": "  ");
					buf.append(fd.getDisplayName());
					buf.append(" ").append(sdf.format(new Date(fd.getLastModified()))).append(" ");
					if(!fd.isDirectory())
						buf.append(" [").append(SUtil.bytesToString(fd.getFileSize())).append("] ");
					buf.append(SUtil.LF);
				}
				
				return buf.toString();
			}
		});
	}
	
	/**
	 *  Comparator for filenodes.
	 */
	public static final Comparator<FileData> FILE_COMPARATOR = new Comparator<FileData>()
	{
		public int compare(FileData o1, FileData o2)
		{
			String	name1	= o1.getFilename();
			String	name2	= o2.getFilename();
			boolean	dir1	= o1.isDirectory();
			boolean	dir2	= o2.isDirectory();

			int	ret;
			if(dir1 && !dir2)
				ret	= -1;
			else if(!dir1 && dir2)
				ret	= 1;
			else
				ret	= name1.compareTo(name2);
			
//			System.out.println("comp: "+ret+" "+name1+" "+name2);
			return ret;
		}
	};
}
