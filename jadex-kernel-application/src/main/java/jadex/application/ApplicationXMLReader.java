package jadex.application;

import jadex.bridge.modelinfo.Argument;
import jadex.bridge.modelinfo.ModelInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.component.ComponentXMLReader;
import jadex.xml.AccessInfo;
import jadex.xml.AttributeConverter;
import jadex.xml.AttributeInfo;
import jadex.xml.IContext;
import jadex.xml.IPostProcessor;
import jadex.xml.MappingInfo;
import jadex.xml.ObjectInfo;
import jadex.xml.TypeInfo;
import jadex.xml.XMLInfo;
import jadex.xml.stax.QName;

import java.util.Iterator;
import java.util.Set;

/**
 *  Reader for loading component XML models into a Java representation states.
 */
public class ApplicationXMLReader extends ComponentXMLReader
{
	//-------- constructors --------
	
	/**
	 *  Create a new reader.
	 */
	public ApplicationXMLReader(Set<TypeInfo>[] mappings)
	{
		super(getXMLMapping(mappings));
	}
	
	//-------- methods --------
	
	/**
	 *  Get the type of loaded models.
	 */
	protected	String getModelType(String filename)
	{
		return ApplicationComponentFactory.FILETYPE_APPLICATION;
	}

	/**
	 *  Get the XML mapping.
	 */
	public static Set<TypeInfo> getXMLMapping(Set<TypeInfo>[] mappings)
	{
		String uri = "http://jadex.sourceforge.net/jadex";
		Set<TypeInfo> types = ComponentXMLReader.getXMLMapping(mappings);
		
		// Find type infos.
		TypeInfo	comptype	= null;
		TypeInfo	configtype	= null;
		for(Iterator<TypeInfo> it=types.iterator(); (configtype==null || comptype==null) && it.hasNext(); )
		{
			TypeInfo	ti	= (TypeInfo)it.next();
			if(comptype==null && ti.getXMLInfo().getXMLPath().equals(new XMLInfo(new QName(uri, "componenttype")).getXMLPath()))
			{
				comptype	= ti;
			}
			if(configtype==null && ti.getXMLInfo().getXMLPath().equals(new XMLInfo(new QName(uri, "configuration")).getXMLPath()))
			{
				configtype	= ti;
				it.remove();
			}
		}

		IPostProcessor	appproc	= new IPostProcessor()
		{
			public Object postProcess(IContext context, Object object)
			{
				ModelInfo	mi	= (ModelInfo)object;
				boolean	hasspace	= false;
				return null;
			}
			
			public int getPass()
			{
				return 1;
			}
		};
		
		types.add(new TypeInfo(new XMLInfo(new QName(uri, "applicationtype")), new ObjectInfo(ApplicationModelInfo.class, appproc), new MappingInfo(comptype)));
		types.add(new TypeInfo(new XMLInfo(new QName(uri, "application")),  new ObjectInfo(ApplicationConfigurationInfo.class), new MappingInfo(configtype)));
		types.add(new TypeInfo(new XMLInfo(new QName(uri, "configuration")),  new ObjectInfo(ApplicationConfigurationInfo.class), new MappingInfo(configtype)));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "applicationtype"), new QName(uri, "arguments"), new QName(uri, "argument")}), new ObjectInfo(Argument.class, new ExpressionProcessor()), 
			new MappingInfo(null, "description", "value",
			new AttributeInfo[]{new AttributeInfo(new AccessInfo("class", "clazz"), new AttributeConverter(classconv, reclassconv))}, null)));
			
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "applicationtype"), new QName(uri, "arguments"), new QName(uri, "result")}), new ObjectInfo(Argument.class, new ExpressionProcessor()), 
			new MappingInfo(null, "description", "value",
			new AttributeInfo[]{new AttributeInfo(new AccessInfo("class", "clazz"), new AttributeConverter(classconv, reclassconv))}, null)));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "application"), new QName(uri, "arguments"), new QName(uri, "argument")}), new ObjectInfo(UnparsedExpression.class),//, new ExpressionProcessor()), 
			new MappingInfo(null, null, "value", new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("class", "clazz"), new AttributeConverter(classconv, reclassconv))
			}, null)));
		
		return types;
	}
	
}
