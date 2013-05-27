package jadex.bpmn.editor.gui;

import jadex.bridge.service.annotation.ParameterInfo;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.asm4.ClassReader;
import org.kohsuke.asm4.Type;
import org.kohsuke.asm4.tree.ClassNode;
import org.kohsuke.asm4.tree.LocalVariableNode;
import org.kohsuke.asm4.tree.MethodNode;


/**
 *  Static helper methods.
 */
public class SHelper
{

	/**
	 *  Get parameter names via asm reader.
	 *  @param m The method.
	 *  @return The list of parameter names or null
	 */
	public static List<String> getParameterNames(Method m)
	{
		List<String> ret = null;
		
		// Try to find via annotation
		boolean anused = false;
		Annotation[][] annos = m.getParameterAnnotations();
		if(annos!=null && annos.length>0)
		{
			ret = new ArrayList<String>();
			for(Annotation[] ans: annos)
			{
				boolean found = false;
				for(Annotation an: ans)
				{
					if(an instanceof ParameterInfo)
					{
						ret.add(((ParameterInfo)an).value());
						found = true;
						anused = true;
						break;
					}
				}
				if(!found)
					ret.add(null);
			}
		}
		
		// Try to find via debug info
		if(!anused)
		{
			Class<?> deccl = m.getDeclaringClass();
			String mdesc = Type.getMethodDescriptor(m);
			String url = Type.getType(deccl).getInternalName() + ".class";
	
			InputStream is = deccl.getClassLoader().getResourceAsStream(url);
			if(is!=null)
			{
				ClassNode cn = null;
				try
				{
					cn = new ClassNode();
					ClassReader cr = new ClassReader(is);
					cr.accept(cn, 0);
				}
				catch(Exception e)
				{
				}
				finally
				{
					try
					{
						is.close();
					}
					catch(Exception e)
					{
					}
				}
		
				if(cn!=null)
				{
					List<MethodNode> methods = cn.methods;
					ret = new ArrayList<String>();
					for(MethodNode method: methods)
					{
						if(method.name.equals(m.getName()) && method.desc.equals(mdesc))
						{
							Type[] argtypes = Type.getArgumentTypes(method.desc);
			
							List<LocalVariableNode> lvars = method.localVariables;
							if(lvars!=null && lvars.size()>0)
							{
								for(int i=0; i<argtypes.length; i++)
								{
									// first local variable represents the "this" object
									ret.add(lvars.get(i+1).name);
								}
							}
							break;
						}
					}
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get return value name.
	 *  @param m The method.
	 */
	public static String getReturnValueName(Method m)
	{
		String ret = null;
		
		// Try to find via annotation
		Annotation[] annos = m.getAnnotations();
		if(annos!=null && annos.length>0)
		{
			for(Annotation an: annos)
			{
				if(an instanceof ParameterInfo)
				{
					ret = ((ParameterInfo)an).value();
					break;
				}
			}
		}
		
		return ret;
	}
}
