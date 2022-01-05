package jadex.microservice;

import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.commons.IFilter;
import jadex.commons.SClassReader;
import jadex.commons.SClassReader.AnnotationInfo;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.microservice.annotation.Microservice;

/**
 *  Microservice kernel.
 */
@ProvidedServices(
{
	@ProvidedService(type=IComponentFactory.class, scope=ServiceScope.PLATFORM, implementation=@Implementation(expression="new jadex.microservice.MicroserviceFactory($component, null)"))
})
@Agent
@Properties(
{
	@NameValue(name="system", value="true"), 
	@NameValue(name="kernel.types", value="new String[]{\".class\"}"),
	@NameValue(name="kernel.filter", value="jadex.microservice.KernelMicroserviceAgent.AGENTFILTER"),
	@NameValue(name="kernel.componenttypes", value="new String[]{\""+MicroserviceFactory.FILETYPE_MICROSERVICE+"\"}")
})
public class KernelMicroserviceAgent
{
	public static final IFilter<Object> AGENTFILTER = new IFilter<Object>()
	{
		public boolean filter(Object obj)
		{
			boolean ret = false;
			if(obj instanceof SClassReader.ClassFileInfo)
			{
				SClassReader.ClassFileInfo ci = (SClassReader.ClassFileInfo)obj;
				AnnotationInfo ai = ci.getClassInfo().getAnnotation(Microservice.class.getName());
				if(ai!=null)
					ret = true;
			}
			return ret;
		}
	};
}
	
