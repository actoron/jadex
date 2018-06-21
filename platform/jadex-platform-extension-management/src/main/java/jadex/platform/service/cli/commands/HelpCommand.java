package jadex.platform.service.cli.commands;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.transformation.IObjectStringConverter;
import jadex.platform.service.cli.ACliCommand;
import jadex.platform.service.cli.ArgumentInfo;
import jadex.platform.service.cli.CliContext;
import jadex.platform.service.cli.ICliCommand;
import jadex.platform.service.cli.ResultInfo;

/**
 *  The help command can be used to display all available or
 *  a specific command.
 */
public class HelpCommand extends ACliCommand
{
	/**
	 *  Get the command names (name including alias').
	 *  @return A string array of the command name and optional further alias names.
	 */
	public String[] getNames()
	{
		return new String[]{"h", "help"};
	}
	
	/**
	 *  Get the command description.
	 *  @return The command description.
	 */
	public String getDescription()
	{
		return "Get help about all available commands.";
	}
	
	/**
	 *  Get example usage(s).
	 *  @return Example usages.
	 */
	public String getExampleUsage()
	{
		return "h cc";
	}
	
	/**
	 *  Get the argument types.
	 */
	public ArgumentInfo[] getArgumentInfos(CliContext context)
	{
		ArgumentInfo ai = new ArgumentInfo(null, String.class, null, "The component name.", null);
		return new ArgumentInfo[]{ai};
	}
	
	/**
	 *  Invoke the command.
	 *  @param context The context.
	 *  @param args The arguments.
	 */
	public Object invokeCommand(CliContext context, Map<String, Object> args)
	{
		Future<Collection<ICliCommand>> ret = new Future<Collection<ICliCommand>>();
		
		String arg = (String)args.get(null);
		if(arg!=null)
		{
			ICliCommand cmd = context.getShell().getCommands().get(arg);
			Set<ICliCommand> res = new HashSet<ICliCommand>();
			res.add(cmd);
			ret.setResult(res);
		}
		else
		{
			ret.setResult(new LinkedHashSet<ICliCommand>(context.getShell().getCommands().values()));
		}
		
		return ret;
	}
	
	/**
	 *  Get the result info.
	 *  @param context The context.
	 *  @return The result info.
	 */
	public ResultInfo getResultInfo(CliContext context, Map<String, Object> args)
	{
		return new ResultInfo(Map.class, "List of running components", new IObjectStringConverter()
		{
			public String convertObject(Object val, Object context)
			{
				StringBuffer buf = new StringBuffer();
				
				if(val!=null)
				{
					Collection<ICliCommand> col = (Collection<ICliCommand>)val;
					
					if(col.size()==1)
					{
						ICliCommand cmd = col.iterator().next();
						buf.append(getShortCommandInfo(cmd)).append(SUtil.LF);
						ArgumentInfo[] ais = cmd.getArgumentInfos((CliContext)context);
						if(ais!=null)
						{
							for(int i=0; i<ais.length; i++)
							{
								buf.append(ais[i].getUsageText());
								if(i+1<ais.length)
									buf.append(SUtil.LF);
							}
						}
						if(cmd.getExampleUsage()!=null)
							buf.append(SUtil.LF).append("examples: ").append(SUtil.LF).append(cmd.getExampleUsage());
					}
					else
					{
						buf.append("available commands: ").append(SUtil.LF);
						for(ICliCommand cmd: col)
						{
							buf.append(getShortCommandInfo(cmd)).append(SUtil.LF);
						}
					}
				}
				
				return buf.toString();
			}
		});
	}
	
	/**
	 *  Get a short command info.
	 *  @param cmd The command.
	 */
	protected String getShortCommandInfo(ICliCommand cmd)
	{
		StringBuffer buf = new StringBuffer();
		
		String[] names =cmd.getNames();
		buf.append(names[0]);
		if(names.length>1)
		{
			buf.append(" (");
			for(int i=1; i<names.length; i++)
			{
				buf.append(names[i]);
				if(i+1<names.length)
				{
					buf.append(", ");
				}
			}
			buf.append(")");
		}
		
		if(cmd.getDescription()!=null)
			buf.append(": ").append(cmd.getDescription());
		
		return buf.toString();
	}
	
	
}
