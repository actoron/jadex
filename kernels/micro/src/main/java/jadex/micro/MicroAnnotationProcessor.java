package jadex.micro;

import java.io.PrintStream;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import jadex.commons.Boolean3;
import jadex.commons.SUtil;
import jadex.micro.annotation.Agent;

/**
 *  Add auto-start entries for "@Agent"s.
 */
public class MicroAnnotationProcessor	extends AbstractProcessor
{
	/**
	 *  Handles @Agent annotations.
	 */
	@Override
	public Set<String> getSupportedAnnotationTypes()
	{
		return Collections.singleton(Agent.class.getName());
	}
	
	/**
	 *  Use this for whatever reason.
	 */
	@Override
	public SourceVersion getSupportedSourceVersion()
	{
		return SourceVersion.latestSupported();
	}

	/**
	 *  Do the work.
	 */
	@Override
	public boolean process(Set< ? extends TypeElement> annotations, RoundEnvironment roundEnv)
	{
		for(TypeElement ann: annotations)
		{
			if(ann.getQualifiedName().toString().equals(Agent.class.getName()))
			{
				try
				{
					FileObject	fo	= processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", "META-INF/jadex_autostart.json");
					try(PrintStream	ps	= new PrintStream(fo.openOutputStream()))
					{
						ps.println("{");
						ps.println("    \"_generated\": \""+new Date()+"\",");
						ps.println("    \"autostart_agents\": {");
					    for(Element elm : roundEnv.getElementsAnnotatedWith(Agent.class))
					    {
					    	if(elm.getKind()==ElementKind.CLASS)
					    	{
					    		Agent	agent	= elm.getAnnotation(Agent.class);
					    		if(agent.autostart().value()!=Boolean3.NULL)
					    		{
					    			ps.println("        \""+elm+"\": "+agent.autostart().value().toString().toLowerCase()+",");
					    		}
					    	}
					    }
						ps.println("    }");
						ps.println("}");
					}
				}
				catch(Exception e)
				{
					processingEnv.getMessager().printMessage(Kind.ERROR, SUtil.getExceptionStacktrace(e));
					return true;
				}
			}
		}
		return false;
	}
}
