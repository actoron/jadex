package jadex.platform.service.cli;

import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.transformation.BasicTypeConverter;
import jadex.commons.transformation.IObjectStringConverter;
import jadex.commons.transformation.IStringObjectConverter;

/**
 * 
 */
public class ACliCommand implements ICliCommand
{
	/**
	 *  Get the argument types.
	 */
	public ArgumentInfo[] getArgumentInfos()
	{
		return null;
	}
	
	/**
	 *  Get the result info.
	 */
	public ResultInfo getResultInfo()
	{
		return null;
	}

	/**
	 *  Invoke the command.
	 */
	public IFuture<String> invokeCommand(final Object context, String[] strargs)
	{
		final Future<String> ret = new Future<String>();
		
		// Convert arguments
		ArgumentInfo[] arginfos = getArgumentInfos();
				
		Object[] args = null;
		if(strargs!=null && strargs.length>0)
		{
			args = new Object[strargs.length];
			for(int i=0; i<strargs.length; i++)
			{
				IStringObjectConverter conv = arginfos[i].getConverter();
				Class<?> target = arginfos[i].getType();
				if(conv!=null)
				{
					try
					{
						args[i] = conv.convertString(strargs[i], context);
					}
					catch(Exception e)
					{
						ret.setException(e);
						return ret;
					}
				}
				else if(!String.class.equals(target))
				{
					conv = BasicTypeConverter.getBasicStringConverter(arginfos[i].getType());
					if(conv==null)
					{
						ret.setException(new RuntimeException("No converter for conversion from string -> "+target.getName()));
						return ret;
					}
					else
					{
						try
						{
							args[i] = conv.convertString(strargs[i], context);
						}
						catch(Exception e)
						{
							ret.setException(e);
							return ret;
						}
					}
				}
				else
				{
					args[i] = strargs[i];
				}
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
					IObjectStringConverter conv = getResultInfo()==null? null: getResultInfo().getConverter();
					if(conv!=null)
					{
						result = conv.convertObject(result, context);
					}
					else if(!(result instanceof String))
					{
						conv = BasicTypeConverter.getBasicObjectConverter(getResultInfo().getType());
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
			});
		}
		
		return ret;
	}

	
	/**
	 *  Invoke the command.
	 */
	public Object invokeCommand(Object context, Object[] args)
	{
		return null;
	}

}
