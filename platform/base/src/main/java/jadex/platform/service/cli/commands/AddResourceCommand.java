package jadex.platform.service.cli.commands;

import java.net.URL;
import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.transformation.IObjectStringConverter;
import jadex.platform.service.cli.ACliCommand;
import jadex.platform.service.cli.ArgumentInfo;
import jadex.platform.service.cli.CliContext;
import jadex.platform.service.cli.ResultInfo;

/**
 *  Add a new resource to the library service.
 *  
 *  Currently only allows for adding urls (future versions will allow adding rids as well)
 */
public class AddResourceCommand extends ACliCommand
{
	/**
	 *  Get the command names (name including alias').
	 *  @return A string array of the command name and optional further alias names.
	 */
	public String[] getNames()
	{
		return new String[]{"ar", "addresource", "ap", "addpath"};
	}
	
	/**
	 *  Get the command description.
	 *  @return The command description.
	 */
	public String getDescription()
	{
		return "Add a resource to the platform.";
	}
	
	/**
	 *  Get example usage(s).
	 *  @return Example usages.
	 */
	public String getExampleUsage()
	{
		return "ar c:\\temp\\my.jar : add path c:\\temp\\my.jar as resource url";
	}
	
	/**
	 *  Invoke the command.
	 *  @param context The context.
	 *  @param args The arguments.
	 */
	public Object invokeCommand(final CliContext context, final Map<String, Object> args)
	{
		final Future<IResourceIdentifier> ret = new Future<IResourceIdentifier>();
		
		final String path = (String)args.get(null);
		
		final IExternalAccess comp = (IExternalAccess)context.getUserContext();
		
		comp.searchService( new ServiceQuery<>( ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM))
			.addResultListener(new ExceptionDelegationResultListener<ILibraryService, IResourceIdentifier>(ret)
		{
			public void customResultAvailable(final ILibraryService ls)
			{
				try
				{
					URL url = SUtil.toURL(path);
					ls.addURL(null, url).addResultListener(new DelegationResultListener<IResourceIdentifier>(ret));
				}
				catch(Exception e)
				{
					ret.setException(e);
				}
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
		ArgumentInfo path = new ArgumentInfo(null, String.class, null, "The url.", null);
		return new ArgumentInfo[]{path};
	}
	
	/**
	 *  Get the result info.
	 *  @param context The context.
	 *  @return The result info.
	 */
	public ResultInfo getResultInfo(CliContext context, Map<String, Object> args)
	{
		return new ResultInfo(IComponentIdentifier.class, "The creation result.", new IObjectStringConverter()
		{
			public String convertObject(Object val, Object context)
			{
				StringBuffer buf = new StringBuffer();
				
				buf.append("resource added: ").append(val).append(SUtil.LF);
				
				return buf.toString();
			}
		});
	}
}
