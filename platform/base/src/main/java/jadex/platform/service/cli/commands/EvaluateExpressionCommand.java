package jadex.platform.service.cli.commands;

import java.util.Map;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.transformation.IObjectStringConverter;
import jadex.javaparser.SJavaParser;
import jadex.platform.service.cli.ACliCommand;
import jadex.platform.service.cli.ArgumentInfo;
import jadex.platform.service.cli.CliContext;
import jadex.platform.service.cli.ResultInfo;

/**
 *  Command for evaluating expressions.
 */
public class EvaluateExpressionCommand extends ACliCommand
{
	/**
	 *  Get the command names (name including alias').
	 *  @return A string array of the command name and optional further alias names.
	 */
	public String[] getNames()
	{
		return new String[]{"ee", "evaluate"};
	}
	
	/**
	 *  Get the command description.
	 *  @return The command description.
	 */
	public String getDescription()
	{
		return "Evaluate an expression.";
	}
	
	/**
	 *  Get example usage(s).
	 *  @return Example usages.
	 */
	public String getExampleUsage()
	{
		return "ee $args.superpeer";
	}
	
	/**
	 *  Invoke the command.
	 *  @param context The context.
	 *  @param args The arguments.
	 */
	public Object invokeCommand(final CliContext context, final Map<String, Object> args)
	{
		final Future<Object> ret = new Future<Object>();
		
		final String exp = (String)args.get(null);
		
		final IExternalAccess comp = (IExternalAccess)context.getUserContext();
		
		comp.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
//				IExternalAccess ea = SServiceProvider.getExternalAccessProxy(ia, comp.getId().getRoot());
				IExternalAccess ea = ia.getExternalAccess(comp.getId().getRoot());
				return ea.scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						Object res = SJavaParser.evaluateExpression(exp, ia.getModel().getAllImports(), ia.getFetcher(), ia.getClassLoader());
						ret.setResult(res);
						return IFuture.DONE;
					}
				});
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
		ArgumentInfo dir = new ArgumentInfo(null, String.class, null, "The expression.", null);
		return new ArgumentInfo[]{dir};
	}
	
	/**
	 *  Get the result info.
	 *  @param context The context.
	 *  @return The result info.
	 */
	public ResultInfo getResultInfo(CliContext context, final Map<String, Object> args)
	{
		return new ResultInfo(Object.class, "The expression result.", new IObjectStringConverter()
		{
			public String convertObject(Object val, Object context)
			{
				StringBuffer buf = new StringBuffer();
				
				buf.append("expression result is: ").append(val).append(SUtil.LF);
				
				return buf.toString();
			}
		});
	}
}
