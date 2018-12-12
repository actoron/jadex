package jadex.rules.parser.conditions;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.antlr.runtime.TokenStream;

import jadex.commons.SReflect;
import jadex.rules.rulesystem.rules.FunctionCall;
import jadex.rules.rulesystem.rules.MethodCall;
import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.OAVObjectType;
import jadex.rules.state.OAVTypeModel;

/**
 *  Static helper methods for conditions parser.
 */
public class SConditions
{
	/**
	 * 
	 * @param var
	 * @param type
	 */
    protected static void adaptConditionType(Variable var, OAVObjectType type)
    {
    	if(type==null)
        		throw new RuntimeException("Type must not be null.");
        	
        	OAVObjectType otype = var.getType();
//        	System.out.println("Having: "+otype+" "+type);
        	if(otype==null)
        	{
        		var.setType(type);
        	}
        	else if(!otype.equals(type))
        	{
        		// Check compatibility and use most specific type
        		if(otype instanceof OAVJavaType)
        		{
        			try
        			{
	        			Class oclazz = ((OAVJavaType)otype).getClazz();
	        			Class clazz = ((OAVJavaType)type).getClazz();
	        			if(oclazz.isAssignableFrom(clazz))
	        			{
	        				var.setType(type);
	        				//System.out.println("Setting: "+type);
	        			}
	        			else if(!clazz.isAssignableFrom(oclazz))
	        				throw new RuntimeException("Incompatible variable types: "+var+" "+oclazz+" "+clazz);
        			}
        			catch(Exception e)
        			{
        				e.printStackTrace();
        			}
        		}
        		else
        		{
        			if(type.isSubtype(otype))
        			{
        				var.setType(type);
        				//System.out.println("Setting: "+type);
        			}
        			else if(!otype.isSubtype(type))
        				throw new RuntimeException("Incompatible variable types: "+var+" "+otype+" "+type);
        		}
        	}
    }
    
    /**
     * 
     * @param otype
     * @param name
     * @param params
     * @return
     */
    protected static MethodCall	createMethodCall(OAVObjectType otype, String name, List params)
    {
    	if(!(otype instanceof OAVJavaType))
    	{
    		throw new RuntimeException("Method calls only supported for java types: "+otype+"."+name+params);
    	}
    	OAVJavaType	jtype	= (OAVJavaType)otype;
    	Class	clazz	= jtype.getClazz();
    	
    	Method[] methods	= SReflect.getMethods(clazz, name);
    	Method	method	= null;
    	
    	// Find one matching regardless of param types (hack???).
    	boolean	found	= false;
    	for(int i=0; i<methods.length; i++)
    	{
    		if(methods[i].getParameterTypes().length==params.size())
    		{
    			// First match.
    			if(!found)
    			{
    				found	= true;
    				method	= methods[i];
    			}
    			
    			// More than one match.
    			else
    			{
    				found	= false;
    				break;
    			}
    		}
    	}
    	
    	if(!found)
    	{
        	Class[]	argtypes	= new Class[params.size()];
        	for(int i=0; i<argtypes.length; i++)
        	{
        		if(params.get(i) instanceof Variable)
        		{
        			OAVObjectType	optype	= ((Variable)params.get(i)).getType();
        			if(!(optype instanceof OAVJavaType))
        			{
        				throw new RuntimeException("Method calls only supported for java types: "+otype+"."+name+params+", "+optype);
        			}
        			argtypes[i]	= ((OAVJavaType)optype).getClazz();
        		}
        		else if(params.get(i) instanceof FunctionCall)
        		{
        			FunctionCall	funcall	= (FunctionCall)params.get(i);
        			argtypes[i]	= funcall.getFunction().getReturnType();
        		}
        		else	// Literal
        		{
        			argtypes[i]	= params.get(i)!=null ? params.get(i).getClass() : null;
        		}
        	}
        	
        	Class[][]	paramtypes	= new Class[methods.length][];
        	for(int i=0; i<methods.length; i++)
        	{
        		paramtypes[i]	= methods[i].getParameterTypes();
        	}
        	int[]	results	= SReflect.matchArgumentTypes(argtypes, paramtypes);
        	
        	if(results.length==0)
        	{
        		throw new RuntimeException("No matching method found: "+otype+"."+name+params);
        	}
        	else if(results.length>1)
        	{
        		System.out.println("Warning: ambiguous methods: "+otype+"."+name+params);
        	}
        	method	= methods[results[0]];
    	}
    
    	return new MethodCall(jtype, method, params);
    }
    
    /**
     * 
     * @param tmodel
     * @param valuesource
     * @return
     */
    protected static OAVObjectType getValueSourceType(OAVTypeModel tmodel, Object valuesource)
    {
    	OAVObjectType ret = null;
    	
    	if(valuesource instanceof OAVAttributeType)
    	{
    		ret = ((OAVAttributeType)valuesource).getType();
    	}
    	else if(valuesource instanceof MethodCall)
    	{
    		Class rettype = ((MethodCall)valuesource).getMethod().getReturnType();
    		if(rettype!=null)
    			ret = tmodel.getJavaType(rettype);
    	}
    	else if(valuesource instanceof FunctionCall)
    	{
    		Class rettype = ((FunctionCall)valuesource).getFunction().getReturnType();
    		if(rettype!=null)
    			ret = tmodel.getJavaType(rettype);
    	}
    	
    	return ret;
    }

    /**
     *  Lookahead for an object condition.
     *  Excludes (and ...) (not ...) (test ...)
     *  conditions but allows (test.MyObject ...) object conditions.
     */
    protected static boolean	lookaheadObjectCE(TokenStream input)
    {
    	try
    	{
	    	boolean	ret	= !"(".equals(input.LT(1).getText())
	    			&& (!"collect".equals(input.LT(5).getText()) || ".".equals(input.LT(6).getText()))
	    		|| "(".equals(input.LT(1).getText()) && (!"and".equals(input.LT(2).getText())
	        		&& !"not".equals(input.LT(2).getText())
	    			&& !"collect".equals(input.LT(2).getText())
	    			&& !"test".equals(input.LT(2).getText()))
	    		|| "(".equals(input.LT(1).getText()) && ".".equals(input.LT(3).getText());
	//    	System.out.println("lookahead ObjectCE: "+ret+", "
	//    		+input.toString(input.index(), input.size())
	//    		+"'"+input.LT(1).getText()+"' "
	//    		+"'"+input.LT(2).getText()+"' "
	//    		+"'"+input.LT(3).getText()+"'");
	    	return ret;
    	}
    	catch(Throwable e)
    	{
    		e.printStackTrace();
    		throw new RuntimeException(e);
    	}
    }
    
    /**
     *  Lookahead for an function call.
     *  Excludes (contains ...) (excludes ...)
     *  conditions but allows (contains.MyObject ...) function calls.
     */
    protected static boolean lookaheadFunctionCall(TokenStream input)
    {
    	boolean	ret	= (!"contains".equals(input.LT(2).getText())
       			&& !"excludes".equals(input.LT(2).getText())
       			&& !"==".equals(input.LT(2).getText())
       			&& !"!=".equals(input.LT(2).getText())
    			&& !">".equals(input.LT(2).getText())
    			&& !"<".equals(input.LT(2).getText())
    			&& !">=".equals(input.LT(2).getText())
    			&& !"<=".equals(input.LT(2).getText()))
    		|| ".".equals(input.LT(3).getText());
//    	System.out.println("lookahead ObjectCE: "+ret+", "
//    		+input.toString(input.index(), input.size())
//    		+"'"+input.LT(1).getText()+"' "
//    		+"'"+input.LT(2).getText()+"' "
//    		+"'"+input.LT(3).getText()+"'");
    	return ret;
    }
    
    /**
     *  Convert slot name(s) to attribute(s) types.
     *  @param otype	The object type.
     *  @param slotname	The slotname string.
     *  @return The attribute type or an array of attribute types. 
     */
    protected static Object	convertAttributeTypes(OAVTypeModel tmodel, OAVObjectType otype, String slotname, String[] imports)
    {
    	Object	ret;
    	
    	// Value source is null for this.
    	if(slotname.toLowerCase().equals("this"))
    	{
    		ret = null;
    	}
    	else if(slotname.indexOf(".")!=-1)
    	{
    		StringTokenizer	stok	= new StringTokenizer(slotname, ".");
    		List aret = new ArrayList();
    		int num = stok.countTokens();
    		for(int i=0; i<num; i++)
    		{
    			String attrname = stok.nextToken();
    			int idx = attrname.indexOf(":");
    			if(idx!=-1)
    			{
    				String typename = attrname.substring(0, idx);
    				attrname = attrname.substring(idx+1);
//    				System.out.println("Found type: "+typename+" "+attrname);
    				otype = getObjectType(tmodel, typename, imports);
    			}
    			idx = attrname.indexOf("[");
    			if(idx!=-1)
    			{
    				String key = attrname.substring(idx+1, attrname.indexOf("]"));
    				attrname = attrname.substring(0, idx);
    				OAVAttributeType attr = otype.getAttributeType(attrname);
    				aret.add(new Object[]{attr, key});
    				otype	= attr.getType();
    			}
    			else
    			{
    				OAVAttributeType attr = otype.getAttributeType(attrname);
    				aret.add(attr);
    				otype	= attr.getType();
    			}
    			
    		}
    		ret	= aret;
    	}
    	else
    	{
    		ret	= otype.getAttributeType(slotname);
    	}
    	return ret;
    }
    
    /**
     *  Get an object type while considering imports for Java types.
     *  @param tmodel	The type model.
     *  @param typename	The name of the type to search for.
     *  @param imports	The imports (if any).
     *  @param loader	The class loader.
     *  @return	The type.
     */
    protected static OAVObjectType	getObjectType(OAVTypeModel tmodel, String typename, String[] imports)
    {
    	OAVObjectType	ret	= null;
    	try
    	{
    		ret	= tmodel.getObjectType(typename);
    	}
    	catch(Exception e){}
    	
    	// If not found, search for class in imports.
    	if(ret==null)
    	{
    		Class	clazz	= SReflect.findClass0(typename, imports, tmodel.getClassLoader());
    		if(clazz==null)
    			throw new RuntimeException("Type not found in type model: "+typename);
    		ret	= tmodel.getJavaType(clazz);
    	}
    	
    	return ret;
    }
}
