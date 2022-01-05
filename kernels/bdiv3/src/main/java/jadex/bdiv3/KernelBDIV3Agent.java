package jadex.bdiv3;


import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.commons.Boolean3;
import jadex.commons.IFilter;
import jadex.commons.SClassReader;
import jadex.commons.SReflect;
import jadex.commons.SClassReader.AnnotationInfo;
import jadex.micro.MicroAgentFactory;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Component kernel.
 */
//@Properties({@NameValue(name="system", value="true"), @NameValue(name="kernel.types", value="new String[]{\"BDI.class\"}")})
// multi factory only uses .class (and BDI.class as marker to know which kernels have been started)
@ProvidedServices({
	@ProvidedService(type=IComponentFactory.class, scope=ServiceScope.PLATFORM, implementation=@Implementation(
//	expression="new jadex.bdiv3.BDIAgentFactory($component)"))
	expression="new jadex.bdiv3.BDIAgentFactory($component, jadex.commons.SUtil.createHashMap(new String[]{\"debugger.panels\"},new Object[]{\"jadex.tools.debugger.bdiv3.BDIViewerDebuggerPanel\"}))"))
})
@Properties(
{
	@NameValue(name="system", value="true"), 
	@NameValue(name="kernel.types", value="new String[]{\".class\"}"),
	@NameValue(name="kernel.filter", value="jadex.bdiv3.KernelBDIV3Agent.AGENTFILTER"),
	@NameValue(name="kernel.componenttypes", value="new String[]{\""+BDIAgentFactory.FILETYPE_BDIAGENT+"\","+"\""+BDIAgentFactory.FILETYPE_BDICAPA+"\"}"),
	@NameValue(name="kernel.anntypes", value="new String[]{\""+BDIAgentFactory.TYPE+"\", null}") // must declare in same order as component types 1:1 mapping
})
@Agent(name="kernel_bdi",
	autostart=Boolean3.FALSE,
	predecessors="jadex.platform.service.security.SecurityAgent")
public class KernelBDIV3Agent 
{
	public static final IFilter<Object> AGENTFILTER = new IFilter<Object>()
	{
		public boolean filter(Object obj)
		{
			boolean ret = false;
			if(obj instanceof SClassReader.ClassFileInfo)
			{
				SClassReader.ClassFileInfo ci = (SClassReader.ClassFileInfo)obj;
				AnnotationInfo ai = ci.getClassInfo().getAnnotation(Agent.class.getName());
				String type = ai!=null? (String)ai.getValue("type"): null;
				if(type==null)
					type = SReflect.getAnnotationDefaultValue(Agent.class, "type");
				if(ai!=null && BDIAgentFactory.TYPE.equals(type))
					ret = true;
				//if(ci.getFilename().indexOf("Agent")!=-1)
				//	System.out.println("bdifilter: "+ret+" "+obj);
			}
			return ret;
		}
	};
}
