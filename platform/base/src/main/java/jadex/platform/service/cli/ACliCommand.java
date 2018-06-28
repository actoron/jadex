package jadex.platform.service.cli;

import java.util.HashMap;
import java.util.Map;

import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.transformation.BasicTypeConverter;
import jadex.commons.transformation.IObjectStringConverter;
import jadex.commons.transformation.IStringObjectConverter;

/**
 *  The abstract command implementation implements the logic for:
 *  
 *  invokeCommand(final CliContext context, String[] strargs)
 *  
 *  by converting the arguments to objects using the argument infos (and converts)
 *  and converting back the result to a string using the result info.
 */
public abstract class ACliCommand implements ICliCommand
{
	/**
	 *  Get the command names (name including alias').
	 *  @return A string array of the command name and optional further alias names.
	 */
	public abstract String[] getNames();
	
	
	/**
	 *  Get the command description.
	 *  @return The command description.
	 */
	public String getDescription()
	{
		return null;
	}
	
	/**
	 *  Get example usage(s).
	 *  @return Example usages.
	 */
	public String getExampleUsage()
	{
		return null;
	}
	
	/**
	 *  Invoke the command.
	 *  @param context The context.
	 *  @param args The arguments.
	 */
	public abstract Object invokeCommand(CliContext context, Map<String, Object> args);
	
	/**
	 *  Get the argument infos.
	 *  @param context The context.
	 *  @return The argument infos.
	 */
	public ArgumentInfo[] getArgumentInfos(CliContext context)
	{
		return null;
	}
	
	/**
	 *  Get the result info.
	 *  @param context The context.
	 *  @return The result info.
	 */
	public ResultInfo getResultInfo(CliContext context, Map<String, Object> args)
	{
		return null;
	}

	/**
	 *  Invoke the command.
	 *  @param context The context.
	 *  @param args The arguments.
	 */
	public IFuture<String> invokeCommand(final CliContext context, String[] strargs)
	{
		final Future<String> ret = new Future<String>();
		
		// Convert arguments
		Map<String, ArgumentInfo> argimap = new HashMap<String, ArgumentInfo>();
		ArgumentInfo[] tmp = getArgumentInfos(context);
		if(tmp!=null)
		{
			for(ArgumentInfo ai: tmp)
			{
				argimap.put(ai.getName(), ai);
			}
		}
				
		final Map<String, Object> args = new HashMap<String, Object>();
		
		if(strargs!=null && strargs.length>0)
		{
			for(int i=0; i<strargs.length; i++)
			{
				String name = strargs[i].startsWith("-")? strargs[i++]: null;
				Object val = null;
				if(i<strargs.length)
				{
					ArgumentInfo cai = argimap.get(name);
					if(cai==null)
					{
						ret.setException(new RuntimeException("Unknown argument: "+name));
						return ret;
					}
					val = strargs[i];
					
					IStringObjectConverter conv = cai.getConverter();
					Class<?> target = cai.getType();
					if(conv!=null)
					{
						try
						{
							val = conv.convertString(strargs[i], context);
						}
						catch(Exception e)
						{
							ret.setException(e);
							return ret;
						}
					}
					else if(!String.class.equals(target))
					{
						conv = BasicTypeConverter.getBasicStringConverter(cai.getType());
						if(conv==null)
						{
							ret.setException(new RuntimeException("No converter for conversion from string -> "+target.getName()));
							return ret;
						}
						else
						{
							try
							{
								val = conv.convertString(strargs[i], context);
							}
							catch(Exception e)
							{
								ret.setException(e);
								return ret;
							}
						}
					}
				}
				args.put(name, val);
			}
		}

		// Invoke command
		Object res = invokeCommand(context, args);
	
		// Result conversion
		if(res instanceof IFuture)
		{
			IFuture fut = (IFuture)res;
			fut.addResultListener(new DelegationResultListener(ret)
			{
				public void customResultAvailable(Object result)
				{
					ResultInfo ri = getResultInfo(context, args);
					IObjectStringConverter conv = ri==null? null: ri.getConverter();
					if(conv!=null)
					{
						result = conv.convertObject(result, context);
					}
					else if(!(result instanceof String))
					{
						conv = BasicTypeConverter.getBasicObjectConverter(ri.getType());
						if(conv==null)
						{
							exceptionOccurred(new RuntimeException("No converter for conversion from "+result.getClass().getSimpleName()+" -> String"));
							return;
						}
						else
						{
							try
							{
								result = conv.convertObject(result, context);
							}
							catch(Exception e)
							{
								exceptionOccurred(e);
								return;
							}
						}
					}
					
					super.customResultAvailable(result);
				}
				
//				public void exceptionOccurred(Exception exception)
//				{
//					super.customResultAvailable("Invocation error: "+exception.getMessage());
//				}
			});
		}
		
		return ret;
	}

}
