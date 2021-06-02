package jadex.config.processor;

import java.io.PrintWriter;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;

import jadex.commons.SUtil;

import javax.tools.StandardLocation;

/**
 *  Create config settings classes from agent arguments.
 */
@SupportedAnnotationTypes("jadex.micro.annotation.AgentArgument")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ConfigProcessor extends AbstractProcessor
{
    /**
     * @param processingEnv environment to access facilities the tool framework
     * provides to the processor
     */
	@Override
	public synchronized void init(ProcessingEnvironment env)
	{
		env.getMessager().printMessage(Kind.NOTE, "initing "+this+" "+getSupportedAnnotationTypes());
		try
		{
			FileObject	fo	= env.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "jadex.config", "ConfigProcessor.txt");
			try(PrintWriter	out	= new PrintWriter(fo.openWriter()))
			{
				out.println("Yo!");
			}
		}
		catch(Exception e)
		{
			env.getMessager().printMessage(Kind.ERROR, SUtil.getExceptionStacktrace(e));			
		}
		super.init(env);
	}

    /**
     * Processes a set of annotation types on type elements
     * originating from the prior round and returns whether or not
     * these annotation types are claimed by this processor.  If {@code
     * true} is returned, the annotation types are claimed and subsequent
     * processors will not be asked to process them; if {@code false}
     * is returned, the annotation types are unclaimed and subsequent
     * processors may be asked to process them.  A processor may
     * always return the same boolean value or may vary the result
     * based on its own chosen criteria.
     *
     * <p>The input set will be empty if the processor supports {@code
     * "*"} and the root elements have no annotations.  A {@code
     * Processor} must gracefully handle an empty set of annotations.
     *
     * @param annotations the annotation types requested to be processed
     * @param roundEnv  environment for information about the current and prior round
     * @return whether or not the set of annotation types are claimed by this processor
     */
	@Override
	public boolean process(Set< ? extends TypeElement> annoations, RoundEnvironment env)
	{
		return true;
	}
}
