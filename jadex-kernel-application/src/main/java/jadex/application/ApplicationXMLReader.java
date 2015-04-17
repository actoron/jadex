package jadex.application;

import jadex.bridge.modelinfo.Argument;
import jadex.bridge.modelinfo.ConfigurationInfo;
import jadex.bridge.modelinfo.ModelInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.component.ComponentComponentFactory;
import jadex.component.ComponentXMLReader;
import jadex.xml.AccessInfo;
import jadex.xml.AttributeConverter;
import jadex.xml.AttributeInfo;
import jadex.xml.MappingInfo;
import jadex.xml.ObjectInfo;
import jadex.xml.SubobjectInfo;
import jadex.xml.TypeInfo;
import jadex.xml.XMLInfo;
import jadex.xml.bean.BeanObjectReaderHandler;

import java.util.Set;

import jadex.xml.stax.QName;

/**
 *  Reader for loading component XML models into a Java representation states.
 */
public class ApplicationXMLReader extends ComponentXMLReader
{
	//-------- constructors --------
	
	/**
	 *  Create a new reader.
	 */
	public ApplicationXMLReader(Set[] mappings)
	{
		super(getXMLMapping(mappings));
	}
	
	//-------- methods --------
	
	/**
	 *  Get the type of loaded models.
	 */
	protected	String getModelType()
	{
		return ApplicationComponentFactory.FILETYPE_APPLICATION;
	}

	/**
	 *  Get the XML mapping.
	 */
	public static Set getXMLMapping(Set[] mappings)
	{
		Set types = ComponentXMLReader.getXMLMapping(mappings);
		
		String uri = "http://jadex.sourceforge.net/jadex";
		
		TypeInfo	apptype	= new TypeInfo(new XMLInfo(new QName(uri, "applicationtype")), new ObjectInfo(ModelInfo.class), 
			new MappingInfo(null, "description", null,
			new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("autoshutdown", "autoShutdown")),
			new AttributeInfo(new AccessInfo(new QName("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation"), null, AccessInfo.IGNORE_READWRITE))
			}, 
			new SubobjectInfo[]{
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "arguments"), new QName(uri, "argument")}), new AccessInfo(new QName(uri, "argument"), "argument")),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "arguments"), new QName(uri, "result")}), new AccessInfo(new QName(uri, "result"), "result")),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "services"), new QName(uri, "container")}), new AccessInfo(new QName(uri, "container"), "container")),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "services"), new QName(uri, "providedservice")}), new AccessInfo(new QName(uri, "providedservice"), "providedService")),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "services"), new QName(uri, "requiredservice")}), new AccessInfo(new QName(uri, "requiredservice"), "requiredService")),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "componenttype")}), new AccessInfo(new QName(uri, "componenttype"), "subcomponentType")),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "property")}), new AccessInfo(new QName(uri, "property"), "property", null, null)),//, new BeanAccessInfo(putprop, null, "map", getname))),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "configurations"), new QName(uri, "configuration")}), new AccessInfo(new QName(uri, "configuration"), "configuration", null, null)),//, new BeanAccessInfo(putprop, null, "map", getname))),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "applications"), new QName(uri, "application")}), new AccessInfo(new QName(uri, "configuration"), "configuration", null, null))//, new BeanAccessInfo(putprop, null, "map", getname))),
		}));
		apptype.setReaderHandler(new BeanObjectReaderHandler());
		types.add(apptype);
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "applicationtype"), new QName(uri, "arguments"), new QName(uri, "argument")}), new ObjectInfo(Argument.class, new ExpressionProcessor()), 
				new MappingInfo(null, "description", "value",
				new AttributeInfo[]{new AttributeInfo(new AccessInfo("class", "clazz"), new AttributeConverter(classconv, reclassconv))}, null)));
			
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "applicationtype"), new QName(uri, "arguments"), new QName(uri, "result")}), new ObjectInfo(Argument.class, new ExpressionProcessor()), 
				new MappingInfo(null, "description", "value",
				new AttributeInfo[]{new AttributeInfo(new AccessInfo("class", "clazz"), new AttributeConverter(classconv, reclassconv))}, null)));
			
		types.add(new TypeInfo(new XMLInfo(new QName(uri, "application")),  new ObjectInfo(ConfigurationInfo.class),
			new MappingInfo(null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("type", "typeName")),
				new AttributeInfo(new AccessInfo("autoshutdown", "autoShutdown"))},
				new SubobjectInfo[]{
				new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "component")}), new AccessInfo(new QName(uri, "component"), "componentInstance")),
			})));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "application"), new QName(uri, "arguments"), new QName(uri, "argument")}), new ObjectInfo(UnparsedExpression.class),//, new ExpressionProcessor()), 
			new MappingInfo(null, null, "value", new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("class", "clazz"), new AttributeConverter(classconv, reclassconv))
			}, null)));
		
		return types;
	}
	
}
