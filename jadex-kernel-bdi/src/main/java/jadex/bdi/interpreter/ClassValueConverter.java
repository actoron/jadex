package jadex.bdi.interpreter;

import jadex.commons.SReflect;
import jadex.commons.collection.MultiCollection;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.io.xml.IValueConverter;
import jadex.rules.state.io.xml.StackElement;

import java.util.Collection;
import java.util.List;

/**
 *  Value converter to load classes based on agent imports.
 */
public class ClassValueConverter implements IValueConverter
{
	/**
	 *  Flag to indicate that the converter requires two-pass
	 *  processing, i.e. attribute values are evaluated in 2nd pass.
	 */
	public boolean	isTwoPass()
	{
		return false;
	}
	
	/**
	 *	Convert the given XML string value to an
	 *  OAV object value.
	 *
	 *  @param state	The current OAV state.
	 *  @param stack	The current stack of OAV objects, created from XML.
	 *  @param attribute	The OAV attribute type.
	 *  @param value	The XML string value.
	 *  @param report	Collection for adding any conversion errors (stack element -> error message).
	 *  @return	The OAV object value.
	 */
	public Object convertValue(IOAVState state, List stack, OAVAttributeType attribute, String value, MultiCollection report)
	{
		Object	ret	= null;
		try
		{
			String[]	imports	= getImports(state, stack);
			
			ret	= SReflect.findClass(value, imports, state.getTypeModel().getClassLoader());
		}
		catch(ClassNotFoundException e)
		{
			report.put(stack.get(stack.size()-1), e.toString());
//			throw new RuntimeException(e);
//			System.out.println("Warning: Class not found "+value);
//			// changed, see javaflow bug: http://issues.apache.org/jira/browse/SANDBOX-111
//			//return Void.class;
//			return Void.TYPE;
		}
		return ret;
	}

	protected static String[] getImports(IOAVState state, List stack)
	{
		String[] imports	= null;
		if(!stack.isEmpty())
		{
			Collection	coll	= state.getAttributeValues(((StackElement)stack.get(0)).object, OAVBDIMetaModel.capability_has_imports);
			imports	= coll!=null ? (String[])coll.toArray(new String[coll.size()]) : null;

			String	pkg	= (String)state.getAttributeValue(((StackElement)stack.get(0)).object, OAVBDIMetaModel.capability_has_package);
			if(pkg!=null)
			{
				if(imports!=null)
				{
					String[]	newimports	= new String[imports.length+1];
					System.arraycopy(imports, 0, newimports, 1, imports.length);
					imports	= newimports;
				}
				else
				{
					imports	= new String[1];
				}
				imports[0]	= pkg+".*";
			}
		}
		return imports;
	}
}
