package jadex.application.space.envsupport;

import jadex.application.model.MApplicationType;
import jadex.application.model.MSpaceInstance;
import jadex.application.model.MSpaceType;
import jadex.application.space.envsupport.dataview.IDataView;
import jadex.application.space.envsupport.environment.AvatarMapping;
import jadex.application.space.envsupport.environment.IEnvironmentSpace;
import jadex.application.space.envsupport.math.IVector2;
import jadex.application.space.envsupport.math.Vector2Double;
import jadex.application.space.envsupport.math.Vector3Double;
import jadex.application.space.envsupport.observer.graphics.drawable.DrawableCombiner;
import jadex.application.space.envsupport.observer.graphics.drawable.Primitive;
import jadex.application.space.envsupport.observer.graphics.drawable.RegularPolygon;
import jadex.application.space.envsupport.observer.graphics.drawable.Text;
import jadex.application.space.envsupport.observer.graphics.drawable.TexturedRectangle;
import jadex.application.space.envsupport.observer.graphics.layer.GridLayer;
import jadex.application.space.envsupport.observer.graphics.layer.Layer;
import jadex.application.space.envsupport.observer.graphics.layer.TiledLayer;
import jadex.application.space.envsupport.observer.perspective.IPerspective;
import jadex.application.space.envsupport.observer.perspective.Perspective2D;
import jadex.commons.SReflect;
import jadex.commons.Tuple;
import jadex.commons.collection.MultiCollection;
import jadex.javaparser.IExpressionParser;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.IValueFetcher;
import jadex.javaparser.SimpleValueFetcher;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;
import jadex.xml.AccessInfo;
import jadex.xml.AttributeConverter;
import jadex.xml.AttributeInfo;
import jadex.xml.BasicTypeConverter;
import jadex.xml.IAttributeConverter;
import jadex.xml.IContext;
import jadex.xml.IObjectObjectConverter;
import jadex.xml.IPostProcessor;
import jadex.xml.IStringObjectConverter;
import jadex.xml.ISubObjectConverter;
import jadex.xml.MappingInfo;
import jadex.xml.ObjectInfo;
import jadex.xml.StackElement;
import jadex.xml.SubObjectConverter;
import jadex.xml.SubobjectInfo;
import jadex.xml.TypeInfo;
import jadex.xml.XMLInfo;
import jadex.xml.bean.BeanAccessInfo;
import jadex.xml.reader.ReadContext;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.text.html.StyleSheet;
import javax.xml.namespace.QName;

/**
 *  Java representation of environment space type for xml description.
 */
public class MEnvSpaceType	extends MSpaceType
{
	//-------- constants --------
	
	/** The border object placement. */
	public static final String	OBJECTPLACEMENT_BORDER	= "border";
	
	/** The center object placement. */
	public static final String	OBJECTPLACEMENT_CENTER	= "center";
	
	//-------- attributes --------
	
	/** The properties. */
	protected Map properties;
	
	//-------- methods --------
	
	/**
	 *  Add a property.
	 *  @param key The key.
	 *  @param value The value.
	 */
	public void addProperty(String key, Object value)
	{
		if(properties==null)
			properties = new MultiCollection();
		properties.put(key, value);
	}
	
	/**
	 *  Get a property.
	 *  @param key The key.
	 *  @return The value.
	 */
	public List getPropertyList(String key)
	{
		return properties!=null? (List)properties.get(key):  null;
	}
	
	/**
	 *  Get the properties.
	 *  @return The properties.
	 */
	public Map getProperties()
	{
		return properties;
	}
	
	/**
	 *  Get a property from a (multi)map.
	 *  @param map The map.
	 *  @param name The name.
	 *  @return The property.
	 */
	public Object getProperty(String name)
	{
		Object ret = null;
		if(properties!=null)
		{
			Object tmp = properties.get(name);
			ret = (tmp instanceof List)? ((List)tmp).get(0): tmp; 
		}
		return ret;
	}

//	/**
//	 *  Get a string representation of this AGR space type.
//	 *  @return A string representation of this AGR space type.
//	 */
//	public String	toString()
//	{
//		StringBuffer	sbuf	= new StringBuffer();
//		sbuf.append(SReflect.getInnerClassName(getClass()));
//		sbuf.append("(name=");
//		sbuf.append(getName());
//		sbuf.append(", dimensions=");
//		sbuf.append(getDimensions());
//		sbuf.append(", component action types=");
//		sbuf.append(getMEnvComponentActionTypes());
//		sbuf.append(", space action types=");
//		sbuf.append(getMEnvSpaceActionTypes());
//		sbuf.append(", class=");
//		sbuf.append(getClazz());
//		sbuf.append(")");
//		return sbuf.toString();
//	}
	
	//-------- static part --------
	
	/**
	 *  Get the XML mapping.
	 */
	public static Set getXMLMapping()
	{
		Set types = new HashSet();
		
		IStringObjectConverter typeconv = new ClassConverter();
		IStringObjectConverter colorconv = new ColorConverter();
		IStringObjectConverter tcolorconv = new TolerantColorConverter();
		final IStringObjectConverter expconv = new ExpressionConverter();
//		ITypeConverter tdoubleconv = new TolerantDoubleTypeConverter();
		IStringObjectConverter tintconv = new TolerantIntegerTypeConverter();
		IObjectObjectConverter nameconv = new NameAttributeToObjectConverter();
		
		IAttributeConverter attypeconv = new AttributeConverter(typeconv, null);
		IAttributeConverter atexconv = new AttributeConverter(expconv, null);
		IAttributeConverter atcolconv = new AttributeConverter(colorconv, null);
		IAttributeConverter attcolconv = new AttributeConverter(tcolorconv, null);
		IAttributeConverter attintconv = new AttributeConverter(tintconv, null);
		ISubObjectConverter sunameconv = new SubObjectConverter(nameconv, null);
		ISubObjectConverter suexconv = new SubObjectConverter(new IObjectObjectConverter() 
		{
			public Object convertObject(Object val, IContext context) throws Exception
			{
				return expconv.convertString((String)val, context);
			}
		}, null);
		
		String uri =  "http://jadex.sourceforge.net/jadex-envspace"; 
		
		TypeInfo ti_po = new TypeInfo(new XMLInfo("abstract_propertyobject"), new ObjectInfo(MultiCollection.class), 
			new MappingInfo(null, new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo(new QName(uri, "property"), "properties", null, null, new BeanAccessInfo(AccessInfo.THIS)))
			}));		
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "objecttype")}), new ObjectInfo(MObjectType.class)));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "envspacetype")}), new ObjectInfo(MEnvSpaceType.class),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("class", "clazz", null, null, new BeanAccessInfo("property")), attypeconv),
			new AttributeInfo(new AccessInfo("width", null, null, null, new BeanAccessInfo("property")), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("height", null, null, null, new BeanAccessInfo("property")), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("depth", null, null, null, new BeanAccessInfo("property")), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("border", null, null, null, new BeanAccessInfo("property"))),
			new AttributeInfo(new AccessInfo("neighborhood", null, null, null, new BeanAccessInfo("property"))),
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo(new QName(uri, "property"), "properties", null, null, new BeanAccessInfo("property"))),
			new SubobjectInfo(new AccessInfo(new QName(uri, "objecttype"), "objecttypes", null, null, new BeanAccessInfo("property"))),
			new SubobjectInfo(new AccessInfo(new QName(uri, "avatarmapping"), "avatarmappings", null, null, new BeanAccessInfo("property"))),
			new SubobjectInfo(new AccessInfo(new QName(uri, "actiontype"), "actiontypes", null, null, new BeanAccessInfo("property"))),
			new SubobjectInfo(new AccessInfo(new QName(uri, "processtype"), "processtypes", null, null, new BeanAccessInfo("property"))),
			new SubobjectInfo(new AccessInfo(new QName(uri, "tasktype"), "tasktypes", null, null, new BeanAccessInfo("property"))),
			new SubobjectInfo(new AccessInfo(new QName(uri, "perceptgenerator"), "perceptgenerators", null, null, new BeanAccessInfo("property"))),
			new SubobjectInfo(new AccessInfo(new QName(uri, "perceptprocessor"), "perceptprocessors", null, null, new BeanAccessInfo("property"))),
			new SubobjectInfo(new AccessInfo(new QName(uri, "dataview"), "dataviews", null, null, new BeanAccessInfo("property"))),
			new SubobjectInfo(new AccessInfo(new QName(uri, "spaceexecutor"), null, null, null, new BeanAccessInfo("property"))),
			new SubobjectInfo(new AccessInfo(new QName(uri, "perspective"), "perspectives", null, null, new BeanAccessInfo("property"))),
			new SubobjectInfo(new AccessInfo(new QName(uri, "percepttype"), "percepttypes", null, null, new BeanAccessInfo("property"))),
			new SubobjectInfo(new AccessInfo(new QName(uri, "dataprovider"), "dataproviders", null, null, new BeanAccessInfo("property"))),
			new SubobjectInfo(new AccessInfo(new QName(uri, "dataconsumer"), "dataconsumers", null, null, new BeanAccessInfo("property")))
			})));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "avatarmapping")}), new ObjectInfo(AvatarMapping.class), 
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("componenttype", "componentType")),
			new AttributeInfo(new AccessInfo("objecttype", "objectType")),
			new AttributeInfo(new AccessInfo("createavatar", "createAvatar")),
			new AttributeInfo(new AccessInfo("createcomponent", "createComponent")),
			new AttributeInfo(new AccessInfo("killavatar", "killAvatar")),
			new AttributeInfo(new AccessInfo("killcomponent", "killComponent"))
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo(new QName(uri, "name"), "componentName"))
			})));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "name")}), null, 
			new MappingInfo(null, null, new AttributeInfo(new AccessInfo((String)null, AccessInfo.THIS), atexconv))));

/*
		types.add(new TypeInfo(null, new QName[]{new QName(uri, "createcomponent")}, MultiCollection.class, null, null,
			null, null, null,
			new SubobjectInfo[]{
			new SubobjectInfo(new BeanAttributeInfo(new QName(uri, "name"), "name", null, null, null, "")),
			new SubobjectInfo(new BeanAttributeInfo(new QName(uri, "argument"), "arguments", null, null, null, ""))
			}));

		types.add(new TypeInfo(null, new QName[]{new QName(uri, "name")}, IParsedExpression.class, null, null,
			null, null, null,
			new SubobjectInfo[]{
			new SubobjectInfo(new BeanAttributeInfo(new QName(uri, "name"), "name", null, null, null, "")),
			new SubobjectInfo(new BeanAttributeInfo(new QName(uri, "argument"), "arguments", null, null, null, ""))
			}));
*/
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "percepttype")}), new ObjectInfo(MultiCollection.class), 
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("objecttype", "objecttypes", null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("componenttype", "componenttypes", null, null, new BeanAccessInfo(AccessInfo.THIS)))
			}, 
			new SubobjectInfo[]{
			// fetches the name attribute of the objecttype/componenttype and return it as object
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "objecttypes"), new QName(uri, "objecttype")}), new AccessInfo(new QName(uri, "objecttype"), "objecttypes", null, null, new BeanAccessInfo(AccessInfo.THIS)), sunameconv),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "componenttypes"), new QName(uri, "componenttype")}), new AccessInfo(new QName(uri, "componenttype"), "componenttypes", null, null, new BeanAccessInfo(AccessInfo.THIS)), sunameconv)
			})));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "actiontype")}), new ObjectInfo(MultiCollection.class),
			new MappingInfo(ti_po, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("class", "clazz", null, null, new BeanAccessInfo(AccessInfo.THIS)), attypeconv),
			new AttributeInfo(new AccessInfo("name", null, null, null, new BeanAccessInfo(AccessInfo.THIS)))
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo(new QName(uri, "parameter"), "parameters", null, null, new BeanAccessInfo(AccessInfo.THIS)))
			})));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "parameter")}), new ObjectInfo(HashMap.class), 
			new MappingInfo(null, null, new AttributeInfo(new AccessInfo("value", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), atexconv),
			new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("class", "clazz", null, null, new BeanAccessInfo(AccessInfo.THIS)), attypeconv)
			})));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "processtype")}), new ObjectInfo(MultiCollection.class),
			new MappingInfo(ti_po, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("class", "clazz", null, null, new BeanAccessInfo(AccessInfo.THIS)), attypeconv),
			new AttributeInfo(new AccessInfo("name", null, null, null, new BeanAccessInfo(AccessInfo.THIS)))
			})));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "tasktype")}), new ObjectInfo(MultiCollection.class),
			new MappingInfo(ti_po, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("class", "clazz", null, null, new BeanAccessInfo(AccessInfo.THIS)), attypeconv),
			new AttributeInfo(new AccessInfo("name", null, null, null, new BeanAccessInfo(AccessInfo.THIS)))
			})));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri,"perceptgenerator")}), new ObjectInfo(MultiCollection.class),
			new MappingInfo(ti_po, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("class", "clazz", null, null, new BeanAccessInfo(AccessInfo.THIS)), attypeconv),
			new AttributeInfo(new AccessInfo("name", null, null, null, new BeanAccessInfo(AccessInfo.THIS)))
			})));

		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri,"perceptprocessor")}), new ObjectInfo(MultiCollection.class),
			new MappingInfo(ti_po, new AttributeInfo[]{	
			new AttributeInfo(new AccessInfo("class", "clazz", null, null, new BeanAccessInfo(AccessInfo.THIS)), attypeconv),
			new AttributeInfo(new AccessInfo("componenttype", null, null, null, new BeanAccessInfo(AccessInfo.THIS)))
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo(new QName(uri, "percepttype"), "percepttypes", null, null, new BeanAccessInfo(AccessInfo.THIS)), sunameconv),
			})));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri,"perceptprocessor"), new QName(uri, "percepttype")}), new ObjectInfo(HashMap.class),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name", null, null, null, new BeanAccessInfo(AccessInfo.THIS)))
			})));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "dataview")}), new ObjectInfo(MultiCollection.class),
			new MappingInfo(ti_po, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("class", "clazz", null, null, new BeanAccessInfo(AccessInfo.THIS)), attypeconv),
			new AttributeInfo(new AccessInfo("name", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("objecttype", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("creator", null, null, new IObjectCreator()
			{
				public Object createObject(Map args) throws Exception
				{
					Map sourceview = (Map)args.get("sourceview");
					IEnvironmentSpace space = (IEnvironmentSpace)args.get("space");
					IDataView ret = (IDataView)((Class)MEnvSpaceInstance.getProperty(sourceview, "clazz")).newInstance();
					
					Map	props	= null;
					List lprops = (List)sourceview.get("properties");
					if(lprops!=null)
					{
						SimpleValueFetcher	fetcher	= new SimpleValueFetcher();
						fetcher.setValue("$space", space);
						fetcher.setValue("$object", args.get("object"));
						fetcher.setValue("$view", args.get(ret));
						props = MEnvSpaceInstance.convertProperties(lprops, fetcher);
					}
					
					ret.init(space, props);
					return ret;
				}
			}, new BeanAccessInfo(AccessInfo.THIS)))
			})));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "perspective")}), new ObjectInfo(MultiCollection.class),
			new MappingInfo(ti_po, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("class", "clazz", null, null, new BeanAccessInfo(AccessInfo.THIS)), attypeconv),		
			new AttributeInfo(new AccessInfo("name", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("opengl", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.BOOLEAN_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("invertxaxis", null, null, Boolean.FALSE, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.BOOLEAN_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("invertyaxis", null, null, Boolean.TRUE, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.BOOLEAN_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("objectplacement", null, null, OBJECTPLACEMENT_BORDER, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.STRING_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("zoomlimit", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("bgcolor", null, null, null, new BeanAccessInfo(AccessInfo.THIS)),  new AttributeConverter(colorconv, null)),
			new AttributeInfo(new AccessInfo("creator", null, null, new IObjectCreator()
			{
				public Object createObject(Map args) throws Exception
				{
					IValueFetcher fetcher = (IValueFetcher)args.get("fetcher");
					args = (Map)args.get("object");
					
					IPerspective ret = (IPerspective)((Class)MEnvSpaceInstance.getProperty(args, "clazz")).newInstance();
					boolean opengl = MEnvSpaceInstance.getProperty(args, "opengl")!=null? 
						((Boolean)MEnvSpaceInstance.getProperty(args, "opengl")).booleanValue(): true;
					ret.setOpenGl(opengl);
//					String name = (String)MEnvSpaceInstance.getProperty(args, "name");
//					System.out.println("Perspective: "+name+" using opengl="+opengl);
					
					// Hack!!!
					if(ret instanceof Perspective2D)
					{
						Perspective2D pers = (Perspective2D)ret;
						Boolean invertx = (Boolean)MEnvSpaceInstance.getProperty(args, "invertxaxis");
						pers.setInvertYAxis(invertx.booleanValue());
						Boolean inverty = (Boolean)MEnvSpaceInstance.getProperty(args, "invertyaxis");
						pers.setInvertYAxis(inverty.booleanValue());
						
						pers.setBackground((Color)MEnvSpaceInstance.getProperty(args, "bgcolor"));
						
						String	placement	= (String)MEnvSpaceInstance.getProperty(args, "objectplacement");
						if(OBJECTPLACEMENT_CENTER.equals(placement))
							pers.setObjectShift(new Vector2Double(0.5));
						
						Double zoomlimit = (Double)MEnvSpaceInstance.getProperty(args, "zoomlimit");
						if(zoomlimit != null)
							pers.setZoomLimit(zoomlimit.doubleValue());
					}
					
					List drawables = (List)args.get("drawables");
					if(drawables!=null)
					{
						for(int k=0; k<drawables.size(); k++)
						{
							Map sourcedrawable = (Map)drawables.get(k);
							Map tmp = new HashMap();
							tmp.put("fetcher", fetcher);
							tmp.put("object", sourcedrawable);
							ret.addVisual(MEnvSpaceInstance.getProperty(sourcedrawable, "objecttype"), 
								((IObjectCreator)MEnvSpaceInstance.getProperty(sourcedrawable, "creator")).createObject(tmp));
						}
					}
					
					List prelayers = (List)args.get("prelayers");
					if(prelayers!=null)
					{
						List targetprelayers = new ArrayList();
						for(int k=0; k<prelayers.size(); k++)
						{
							Map layer = (Map)prelayers.get(k);
//							System.out.println("prelayer: "+layer);
							targetprelayers.add(((IObjectCreator)MEnvSpaceInstance.getProperty(layer, "creator")).createObject(layer));
						}
						((Perspective2D) ret).setPrelayers((Layer[]) targetprelayers.toArray(new Layer[0]));
					}
					
					List postlayers = (List)args.get("postlayers");
					if(postlayers!=null)
					{
						List targetpostlayers = new ArrayList();
						for(int k=0; k<postlayers.size(); k++)
						{
							Map layer = (Map)postlayers.get(k);
//							System.out.println("postlayer: "+layer);
							targetpostlayers.add(((IObjectCreator)MEnvSpaceInstance.getProperty(layer, "creator")).createObject(layer));
						}
						((Perspective2D) ret).setPostlayers((Layer[]) targetpostlayers.toArray(new Layer[0]));
					}
					
					return ret;
				}
			}, new BeanAccessInfo(AccessInfo.THIS)))
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo(new QName(uri, "drawable"), "drawables", null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "prelayers"), new QName(uri, "gridlayer")}), new AccessInfo(new QName(uri, "gridlayer"), "prelayers", null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "prelayers"), new QName(uri, "tiledlayer")}), new AccessInfo(new QName(uri, "tiledlayer"), "prelayers", null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "prelayers"), new QName(uri, "colorlayer")}), new AccessInfo(new QName(uri, "colorlayer"), "prelayers", null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "postlayers"), new QName(uri, "gridlayer")}), new AccessInfo(new QName(uri, "gridlayer"), "postlayers", null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "postlayers"), new QName(uri, "tiledlayer")}), new AccessInfo(new QName(uri, "tiledlayer"), "postlayers", null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "postlayers"), new QName(uri, "colorlayer")}), new AccessInfo(new QName(uri, "colorlayer"), "postlayers", null, null, new BeanAccessInfo(AccessInfo.THIS)))
			})));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "drawable")}), new ObjectInfo(MultiCollection.class),
			new MappingInfo(ti_po, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("objecttype", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("x", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("y", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("rotatex", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("rotatey", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("rotatez", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("width", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("height", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("position", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("rotation", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("size", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("creator", null, null, new IObjectCreator()
			{
				public Object createObject(Map args) throws Exception
				{
					IValueFetcher fetcher = (IValueFetcher)args.get("fetcher");
					args = (Map)args.get("object");
					Object position = MEnvSpaceInstance.getProperty(args, "position");
					if(position==null)
					{
						position = Vector2Double.getVector2((Double)MEnvSpaceInstance.getProperty(args, "x"),
							(Double)MEnvSpaceInstance.getProperty(args, "y"));
					}				
					Object rotation = MEnvSpaceInstance.getProperty(args, "rotation");
					if(rotation==null)
					{
						Double rx = (Double)MEnvSpaceInstance.getProperty(args, "rotatex");
						Double ry = (Double)MEnvSpaceInstance.getProperty(args, "rotatey");
						Double rz = (Double)MEnvSpaceInstance.getProperty(args, "rotatez");
						rotation = Vector3Double.getVector3(rx!=null? rx: new Double(0), ry!=null? ry: new Double(0), rz!=null? rz: new Double(0));
					}
					Object size = MEnvSpaceInstance.getProperty(args, "size");
					if(size==null)
					{
						size = Vector2Double.getVector2((Double)MEnvSpaceInstance.getProperty(args, "width"),
							(Double)MEnvSpaceInstance.getProperty(args, "height"));
					}
					DrawableCombiner ret = new DrawableCombiner(position, rotation, size);
					
					List parts = (List)args.get("parts");
					if(parts!=null)
					{
						for(int l=0; l<parts.size(); l++)
						{
							Map sourcepart = (Map)parts.get(l);
							int layer = MEnvSpaceInstance.getProperty(sourcepart, "layer")!=null? ((Integer)MEnvSpaceInstance.getProperty(sourcepart, "layer")).intValue(): 0;
							ret.addPrimitive((Primitive)((IObjectCreator)MEnvSpaceInstance.getProperty(sourcepart, "creator")).createObject(sourcepart), layer);
						}
					}
					
					List props = (List)args.get("properties");
					MEnvSpaceInstance.setProperties(ret, props, fetcher);
					
					return ret;
				}
			}, new BeanAccessInfo(AccessInfo.THIS)))
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo(new QName(uri, "texturedrectangle"), "parts", null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new SubobjectInfo(new AccessInfo(new QName(uri, "triangle"), "parts", null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new SubobjectInfo(new AccessInfo(new QName(uri, "rectangle"), "parts", null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new SubobjectInfo(new AccessInfo(new QName(uri, "regularpolygon"), "parts", null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new SubobjectInfo(new AccessInfo(new QName(uri, "ellipse"), "parts", null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new SubobjectInfo(new AccessInfo(new QName(uri, "text"), "parts", null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new SubobjectInfo(new AccessInfo(new QName(uri, "drawcondition"), null, null, null, new BeanAccessInfo(AccessInfo.THIS)), suexconv)
			})));

		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "texturedrectangle")}), new ObjectInfo(MultiCollection.class),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("x", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("y", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("rotatex", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("rotatey", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("rotatez", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("width", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("height", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("position", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("rotation", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("size", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("abspos", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.BOOLEAN_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("abssize", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.BOOLEAN_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("absrot", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.BOOLEAN_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("color", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(attcolconv, null)),
			new AttributeInfo(new AccessInfo("imagepath", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("layer", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(attintconv, null)),
			new AttributeInfo(new AccessInfo("creator", null, null, new IObjectCreator()
			{
				public Object createObject(Map args) throws Exception
				{
					Object position = MEnvSpaceInstance.getProperty(args, "position");
					if(position==null)
					{
						position = Vector2Double.getVector2((Double)MEnvSpaceInstance.getProperty(args, "x"),
							(Double)MEnvSpaceInstance.getProperty(args, "y"));
					}				
					Object rotation = MEnvSpaceInstance.getProperty(args, "rotation");
					if(rotation==null)
					{
						Double rx = (Double)MEnvSpaceInstance.getProperty(args, "rotatex");
						Double ry = (Double)MEnvSpaceInstance.getProperty(args, "rotatey");
						Double rz = (Double)MEnvSpaceInstance.getProperty(args, "rotatez");
						rotation = Vector3Double.getVector3(rx!=null? rx: new Double(0), ry!=null? ry: new Double(0), rz!=null? rz: new Double(0));
					}
					Object size = MEnvSpaceInstance.getProperty(args, "size");
					if(size==null)
					{
						size = Vector2Double.getVector2((Double)MEnvSpaceInstance.getProperty(args, "width"),
							(Double)MEnvSpaceInstance.getProperty(args, "height"));
					}
					int absFlags = Boolean.TRUE.equals(MEnvSpaceInstance.getProperty(args, "abspos"))? Primitive.ABSOLUTE_POSITION : 0;
					absFlags |= Boolean.TRUE.equals(MEnvSpaceInstance.getProperty(args, "abssize"))? Primitive.ABSOLUTE_SIZE : 0;
					absFlags |= Boolean.TRUE.equals(MEnvSpaceInstance.getProperty(args, "absrot"))? Primitive.ABSOLUTE_ROTATION : 0;

					IParsedExpression exp = (IParsedExpression)MEnvSpaceInstance.getProperty(args, "drawcondition");
					return new TexturedRectangle(position, rotation, size, absFlags, MEnvSpaceInstance.getProperty(args, "color"), (String)MEnvSpaceInstance.getProperty(args, "imagepath"), exp);
				}
			}, new BeanAccessInfo(AccessInfo.THIS)))
			}, 
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo(new QName(uri, "drawcondition"), null, null, null, new BeanAccessInfo(AccessInfo.THIS)), suexconv)
			})));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "triangle")}), new ObjectInfo(MultiCollection.class),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("x", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("y", null, null,null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("rotatex", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("rotatey", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("rotatez", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("width", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("height", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("position", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("rotation", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("size", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("abspos", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.BOOLEAN_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("abssize", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.BOOLEAN_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("absrot", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.BOOLEAN_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("color", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), attcolconv),
			new AttributeInfo(new AccessInfo("layer", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), attintconv),
			new AttributeInfo(new AccessInfo("creator", null, null, new IObjectCreator()
			{
				public Object createObject(Map args) throws Exception
				{
					Object position = MEnvSpaceInstance.getProperty(args, "position");
					if(position==null)
					{
						position = Vector2Double.getVector2((Double)MEnvSpaceInstance.getProperty(args, "x"),
							(Double)MEnvSpaceInstance.getProperty(args, "y"));
					}				
					Object rotation = MEnvSpaceInstance.getProperty(args, "rotation");
					if(rotation==null)
					{
						Double rx = (Double)MEnvSpaceInstance.getProperty(args, "rotatex");
						Double ry = (Double)MEnvSpaceInstance.getProperty(args, "rotatey");
						Double rz = (Double)MEnvSpaceInstance.getProperty(args, "rotatez");
						rotation = Vector3Double.getVector3(rx!=null? rx: new Double(0), ry!=null? ry: new Double(0), rz!=null? rz: new Double(0));
					}
					Object size = MEnvSpaceInstance.getProperty(args, "size");
					if(size==null)
					{
						size = Vector2Double.getVector2((Double)MEnvSpaceInstance.getProperty(args, "width"),
							(Double)MEnvSpaceInstance.getProperty(args, "height"));
					}
					
					int absFlags = Boolean.TRUE.equals(MEnvSpaceInstance.getProperty(args, "abspos"))? Primitive.ABSOLUTE_POSITION : 0;
					absFlags |= Boolean.TRUE.equals(MEnvSpaceInstance.getProperty(args, "abssize"))? Primitive.ABSOLUTE_SIZE : 0;
					absFlags |= Boolean.TRUE.equals(MEnvSpaceInstance.getProperty(args, "absrot"))? Primitive.ABSOLUTE_ROTATION : 0;
					
					IParsedExpression exp = (IParsedExpression)MEnvSpaceInstance.getProperty(args, "drawcondition");
					return new Primitive(Primitive.PRIMITIVE_TYPE_TRIANGLE, position, rotation, size, absFlags, MEnvSpaceInstance.getProperty(args, "color"), exp);
					//return new Triangle(position, rotation, size, absFlags, MEnvSpaceInstance.getProperty(args, "color"), exp);
				}
			}, new BeanAccessInfo(AccessInfo.THIS)))
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo(new QName(uri, "drawcondition"), null, null, null, new BeanAccessInfo(AccessInfo.THIS)), suexconv)
			})));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "rectangle")}), new ObjectInfo(MultiCollection.class),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("x", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("y", null, null,null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("rotatex", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("rotatey", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("rotatez", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("width", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("height", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("position", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("rotation", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("size", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("abspos", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.BOOLEAN_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("abssize", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.BOOLEAN_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("absrot", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.BOOLEAN_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("color", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), attcolconv),
			new AttributeInfo(new AccessInfo("layer", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), attintconv),
			new AttributeInfo(new AccessInfo("creator", null, null, new IObjectCreator()		
			{
				public Object createObject(Map args) throws Exception
				{
					Object position = MEnvSpaceInstance.getProperty(args, "position");
					if(position==null)
					{
						position = Vector2Double.getVector2((Double)MEnvSpaceInstance.getProperty(args, "x"),
							(Double)MEnvSpaceInstance.getProperty(args, "y"));
					}				
					Object rotation = MEnvSpaceInstance.getProperty(args, "rotation");
					if(rotation==null)
					{
						Double rx = (Double)MEnvSpaceInstance.getProperty(args, "rotatex");
						Double ry = (Double)MEnvSpaceInstance.getProperty(args, "rotatey");
						Double rz = (Double)MEnvSpaceInstance.getProperty(args, "rotatez");
						rotation = Vector3Double.getVector3(rx!=null? rx: new Double(0), ry!=null? ry: new Double(0), rz!=null? rz: new Double(0));
					}
					Object size = MEnvSpaceInstance.getProperty(args, "size");
					if(size==null)
					{
						size = Vector2Double.getVector2((Double)MEnvSpaceInstance.getProperty(args, "width"),
							(Double)MEnvSpaceInstance.getProperty(args, "height"));
					}
					int absFlags = Boolean.TRUE.equals(MEnvSpaceInstance.getProperty(args, "abspos"))? Primitive.ABSOLUTE_POSITION : 0;
					absFlags |= Boolean.TRUE.equals(MEnvSpaceInstance.getProperty(args, "abssize"))? Primitive.ABSOLUTE_SIZE : 0;
					absFlags |= Boolean.TRUE.equals(MEnvSpaceInstance.getProperty(args, "absrot"))? Primitive.ABSOLUTE_ROTATION : 0;
					
					IParsedExpression exp = (IParsedExpression)MEnvSpaceInstance.getProperty(args, "drawcondition");
					return new Primitive(Primitive.PRIMITIVE_TYPE_RECTANGLE, position, rotation, size, absFlags, MEnvSpaceInstance.getProperty(args, "color"), exp);
					//return new Rectangle(position, rotation, size, absFlags, MEnvSpaceInstance.getProperty(args, "color"), exp);
				}
			}, new BeanAccessInfo(AccessInfo.THIS)))
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo(new QName(uri, "drawcondition"), null, null, null, new BeanAccessInfo(AccessInfo.THIS)), suexconv)
			})));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "regularpolygon")}), new ObjectInfo(MultiCollection.class),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("x", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("y", null, null,null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("rotatex", null, null,null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("rotatey", null, null,null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("rotatez", null, null,null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("width", null, null,null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("height", null, null,null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("position", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("rotation", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("size", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("abspos", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.BOOLEAN_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("abssize", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.BOOLEAN_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("absrot", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.BOOLEAN_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("color", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), attcolconv),
			new AttributeInfo(new AccessInfo("vertices", null, null, new Integer(3), new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.INTEGER_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("layer", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), attintconv),
			new AttributeInfo(new AccessInfo("creator", null, null, new IObjectCreator()		
			{
				public Object createObject(Map args) throws Exception
				{
					Object position = MEnvSpaceInstance.getProperty(args, "position");
					if(position==null)
					{
						position = Vector2Double.getVector2((Double)MEnvSpaceInstance.getProperty(args, "x"),
							(Double)MEnvSpaceInstance.getProperty(args, "y"));
					}				
					Object rotation = MEnvSpaceInstance.getProperty(args, "rotation");
					if(rotation==null)
					{
						Double rx = (Double)MEnvSpaceInstance.getProperty(args, "rotatex");
						Double ry = (Double)MEnvSpaceInstance.getProperty(args, "rotatey");
						Double rz = (Double)MEnvSpaceInstance.getProperty(args, "rotatez");
						rotation = Vector3Double.getVector3(rx!=null? rx: new Double(0), ry!=null? ry: new Double(0), rz!=null? rz: new Double(0));
					}
					Object size = MEnvSpaceInstance.getProperty(args, "size");
					if(size==null)
					{
						size = Vector2Double.getVector2((Double)MEnvSpaceInstance.getProperty(args, "width"),
							(Double)MEnvSpaceInstance.getProperty(args, "height"));
					}
					
					int absFlags = Boolean.TRUE.equals(MEnvSpaceInstance.getProperty(args, "abspos"))? Primitive.ABSOLUTE_POSITION : 0;
					absFlags |= Boolean.TRUE.equals(MEnvSpaceInstance.getProperty(args, "abssize"))? Primitive.ABSOLUTE_SIZE : 0;
					absFlags |= Boolean.TRUE.equals(MEnvSpaceInstance.getProperty(args, "absrot"))? Primitive.ABSOLUTE_ROTATION : 0;
					
					int vertices  = MEnvSpaceInstance.getProperty(args, "vertices")==null? 3: 
						((Integer)MEnvSpaceInstance.getProperty(args, "vertices")).intValue();
					
					IParsedExpression exp = (IParsedExpression)MEnvSpaceInstance.getProperty(args, "drawcondition");
					return new RegularPolygon(position, rotation, size, absFlags, MEnvSpaceInstance.getProperty(args, "color"), vertices, exp);
				}
			}, new BeanAccessInfo(AccessInfo.THIS)))
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo(new QName(uri, "drawcondition"), null, null, null, new BeanAccessInfo(AccessInfo.THIS)), suexconv)
			})));
	
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "ellipse")}), new ObjectInfo(MultiCollection.class),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("x", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("y", null, null,null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("rotatex", null, null,null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("rotatey", null, null,null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("rotatez", null, null,null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("width", null, null,null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("height", null, null,null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("position", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("rotation", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("size", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("abspos", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.BOOLEAN_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("abssize", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.BOOLEAN_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("absrot", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.BOOLEAN_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("color", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), attcolconv),
			new AttributeInfo(new AccessInfo("layer", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), attintconv),
			new AttributeInfo(new AccessInfo("creator", null, null, new IObjectCreator()		
			{
				public Object createObject(Map args) throws Exception
				{
					Object position = MEnvSpaceInstance.getProperty(args, "position");
					if(position==null)
					{
						position = Vector2Double.getVector2((Double)MEnvSpaceInstance.getProperty(args, "x"),
							(Double)MEnvSpaceInstance.getProperty(args, "y"));
					}				
					Object rotation = MEnvSpaceInstance.getProperty(args, "rotation");
					if(rotation==null)
					{
						Double rx = (Double)MEnvSpaceInstance.getProperty(args, "rotatex");
						Double ry = (Double)MEnvSpaceInstance.getProperty(args, "rotatey");
						Double rz = (Double)MEnvSpaceInstance.getProperty(args, "rotatez");
						rotation = Vector3Double.getVector3(rx!=null? rx: new Double(0), ry!=null? ry: new Double(0), rz!=null? rz: new Double(0));
					}
					Object size = MEnvSpaceInstance.getProperty(args, "size");
					if(size==null)
					{
						size = Vector2Double.getVector2((Double)MEnvSpaceInstance.getProperty(args, "width"),
							(Double)MEnvSpaceInstance.getProperty(args, "height"));
					}
					
					int absFlags = Boolean.TRUE.equals(MEnvSpaceInstance.getProperty(args, "abspos"))? Primitive.ABSOLUTE_POSITION : 0;
					absFlags |= Boolean.TRUE.equals(MEnvSpaceInstance.getProperty(args, "abssize"))? Primitive.ABSOLUTE_SIZE : 0;
					absFlags |= Boolean.TRUE.equals(MEnvSpaceInstance.getProperty(args, "absrot"))? Primitive.ABSOLUTE_ROTATION : 0;
					
					IParsedExpression exp = (IParsedExpression)MEnvSpaceInstance.getProperty(args, "drawcondition");
					return new Primitive(Primitive.PRIMITIVE_TYPE_ELLIPSE, position, rotation, size, absFlags, MEnvSpaceInstance.getProperty(args, "color"), exp);
					//return new Ellipse(position, rotation, size, absFlags, MEnvSpaceInstance.getProperty(args, "color"), exp);
				}
			}, new BeanAccessInfo(AccessInfo.THIS)))
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo(new QName(uri, "drawcondition"), null, null, null, new BeanAccessInfo(AccessInfo.THIS)), suexconv)
			})));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "text")}), new ObjectInfo(MultiCollection.class),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("x", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("y", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("position", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("font", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.STRING_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("style", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.INTEGER_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("size", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.INTEGER_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("abspos", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.BOOLEAN_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("abssize", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.BOOLEAN_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("color", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), atcolconv),
			new AttributeInfo(new AccessInfo("layer", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), attintconv),
			new AttributeInfo(new AccessInfo("text", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.STRING_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("align", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.STRING_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("creator", null, null, new IObjectCreator()
			{
				public Object createObject(Map args) throws Exception
				{
					Object position = MEnvSpaceInstance.getProperty(args, "position");
					if(position==null)
					{
						position = Vector2Double.getVector2((Double)MEnvSpaceInstance.getProperty(args, "x"),
							(Double)MEnvSpaceInstance.getProperty(args, "y"));
					}
					
					String fontname = (String) MEnvSpaceInstance.getProperty(args, "font");
					if(fontname==null)
					{
						fontname = "Default";
					}
					Integer fontstyle = (Integer) MEnvSpaceInstance.getProperty(args, "style");
					if (fontstyle==null)
					{
						fontstyle = new Integer(Font.PLAIN);
					}
					Integer fontsize = (Integer) MEnvSpaceInstance.getProperty(args, "size");
					if (fontsize==null)
					{
						fontsize = new Integer(12);
					}
					Font font = new Font(fontname, fontstyle.intValue(), fontsize.intValue());
					
					String text = (String) MEnvSpaceInstance.getProperty(args, "text");
					text = String.valueOf(text);
					
					// Hack! Upstream needs to provide _proper_ newlines not a \n literal
					// Attributes are probably not the right place for text...
					text = text.replaceAll("\\\\n", "\n").replaceAll("\\\\\\\\", "\\");
					
					String aligntxt = String.valueOf(MEnvSpaceInstance.getProperty(args, "align"));
					int align = Text.ALIGN_LEFT;
					if (aligntxt.equals("right"))
						align = Text.ALIGN_RIGHT;
					else if (aligntxt.equals("center"))
						align = Text.ALIGN_CENTER;
					
					int absFlags = Boolean.TRUE.equals(MEnvSpaceInstance.getProperty(args, "abspos"))? Primitive.ABSOLUTE_POSITION : 0;
					absFlags |= Boolean.TRUE.equals(MEnvSpaceInstance.getProperty(args, "abssize"))? Primitive.ABSOLUTE_SIZE : 0;
					
					IParsedExpression exp = (IParsedExpression)MEnvSpaceInstance.getProperty(args, "drawcondition");
					return new Text(position, font, (Color)MEnvSpaceInstance.getProperty(args, "color"), text, align, absFlags, exp);
				}
			}, new BeanAccessInfo(AccessInfo.THIS)))
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo(new QName(uri, "drawcondition"), null, null, null, new BeanAccessInfo(AccessInfo.THIS)), suexconv)
			})));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "gridlayer")}), new ObjectInfo(MultiCollection.class),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("color", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), attcolconv),
			new AttributeInfo(new AccessInfo("width", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("height", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("type", null, null, "gridlayer", new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("creator", null, null, new IObjectCreator()
			{
				public Object createObject(Map args) throws Exception
				{
					IVector2 size = Vector2Double.getVector2((Double)MEnvSpaceInstance.getProperty(args, "width"),
							(Double)MEnvSpaceInstance.getProperty(args, "height"));
					return new GridLayer(size, MEnvSpaceInstance.getProperty(args, "color"));
				}
			}, new BeanAccessInfo(AccessInfo.THIS)))
			})));

		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "tiledlayer")}), new ObjectInfo(MultiCollection.class),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("imagepath", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("width", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("height", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("color", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), attcolconv),
			new AttributeInfo(new AccessInfo("type", null, null, "tiledlayer", new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("creator", null, null, new IObjectCreator()
			{
				public Object createObject(Map args) throws Exception
				{
					IVector2 size = Vector2Double.getVector2((Double)MEnvSpaceInstance.getProperty(args, "width"),
						(Double)MEnvSpaceInstance.getProperty(args, "height"));
					return new TiledLayer(size, MEnvSpaceInstance.getProperty(args, "color"), (String)MEnvSpaceInstance.getProperty(args, "imagepath"));
				}
			}, new BeanAccessInfo(AccessInfo.THIS)))
			})));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "colorlayer")}), new ObjectInfo(MultiCollection.class),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("color", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), attcolconv),
			new AttributeInfo(new AccessInfo("type", null, null, "colorlayer", new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("creator", null, null, new IObjectCreator()
			{
				public Object createObject(Map args) throws Exception
				{
					return new Layer(Layer.LAYER_TYPE_COLOR, MEnvSpaceInstance.getProperty(args, "color"));
				}
			}, new BeanAccessInfo(AccessInfo.THIS)))
			})));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "spaceexecutor")}), new ObjectInfo(MultiCollection.class), 
			new MappingInfo(ti_po, null, new AttributeInfo(new AccessInfo("expression", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), atexconv),
			new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("class", "clazz", null, null, new BeanAccessInfo(AccessInfo.THIS)), attypeconv)
			})));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "envspacetype"), new QName(uri, "property")}), new ObjectInfo(HashMap.class), 
			new MappingInfo(null, null, new AttributeInfo(new AccessInfo("value", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), atexconv),
			new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("class", "clazz", null, null, new BeanAccessInfo(AccessInfo.THIS)), attypeconv),
			new AttributeInfo(new AccessInfo("dynamic", null, null, Boolean.FALSE, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.BOOLEAN_CONVERTER, null))
			})));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "envspace"), new QName(uri, "property")}), new ObjectInfo(HashMap.class),
			new MappingInfo(null, null, new AttributeInfo(new AccessInfo("value", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), atexconv),
			new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("class", "clazz", null, null, new BeanAccessInfo(AccessInfo.THIS)), attypeconv),
			new AttributeInfo(new AccessInfo("dynamic", null, null, Boolean.FALSE, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.BOOLEAN_CONVERTER, null))
			})));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "processtype"), new QName(uri, "property")}), new ObjectInfo(HashMap.class), 
			new MappingInfo(null, null, new AttributeInfo(new AccessInfo("value", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), atexconv),
			new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("class", "clazz", null, null, new BeanAccessInfo(AccessInfo.THIS)), attypeconv),
			new AttributeInfo(new AccessInfo("dynamic", null, null, Boolean.FALSE, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.BOOLEAN_CONVERTER, null))
			})));

		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "tasktype"), new QName(uri, "property")}), new ObjectInfo(HashMap.class), 
			new MappingInfo(null, null, new AttributeInfo(new AccessInfo("value", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), atexconv),
			new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("class", "clazz", null, null, new BeanAccessInfo(AccessInfo.THIS)), attypeconv),
			new AttributeInfo(new AccessInfo("dynamic", null, null, Boolean.FALSE, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.BOOLEAN_CONVERTER, null))
			})));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "actiontype"), new QName(uri, "property")}), new ObjectInfo(HashMap.class),
			new MappingInfo(null, null, new AttributeInfo(new AccessInfo("value", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), atexconv),
			new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("class", "clazz", null, null, new BeanAccessInfo(AccessInfo.THIS)), attypeconv),
			new AttributeInfo(new AccessInfo("dynamic", null, null, Boolean.FALSE, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.BOOLEAN_CONVERTER, null))
			})));

		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "percepttype"), new QName(uri, "componenttypes"), new QName(uri, "componenttype")}), new ObjectInfo(HashMap.class),			
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name", null, null, null, new BeanAccessInfo(AccessInfo.THIS)))
			})));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "perceptgenerator"), new QName(uri, "property")}), new ObjectInfo(HashMap.class), 
			new MappingInfo(null, null, new AttributeInfo(new AccessInfo("value", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), atexconv),
			new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("class", "clazz", null, null, new BeanAccessInfo(AccessInfo.THIS)), attypeconv),
			new AttributeInfo(new AccessInfo("dynamic", null, null, Boolean.FALSE, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.BOOLEAN_CONVERTER, null))
			})));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "perceptprocessor"), new QName(uri, "property")}), new ObjectInfo(HashMap.class),
			new MappingInfo(null, null, new AttributeInfo(new AccessInfo("value", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), atexconv),
			new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("class", "clazz", null, null, new BeanAccessInfo(AccessInfo.THIS)), attypeconv),
			new AttributeInfo(new AccessInfo("dynamic", null, null, Boolean.FALSE, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.BOOLEAN_CONVERTER, null))
			})));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "dataview"), new QName(uri, "property")}), new ObjectInfo(HashMap.class), 
			new MappingInfo(null, null, new AttributeInfo(new AccessInfo("value", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), atexconv),
			new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("class", "clazz", null, null, new BeanAccessInfo(AccessInfo.THIS)), attypeconv),
			new AttributeInfo(new AccessInfo("dynamic", null, null, Boolean.FALSE, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.BOOLEAN_CONVERTER, null))
			})));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "perspective"), new QName(uri, "property")}), new ObjectInfo(HashMap.class), 
			new MappingInfo(null, null, new AttributeInfo(new AccessInfo("value", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), atexconv),
			new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("class", "clazz", null, null, new BeanAccessInfo(AccessInfo.THIS)), attypeconv),
			new AttributeInfo(new AccessInfo("dynamic", null, null, Boolean.FALSE, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.BOOLEAN_CONVERTER, null))
			})));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "object"), new QName(uri, "property")}), new ObjectInfo(HashMap.class), 
			new MappingInfo(null, null, new AttributeInfo(new AccessInfo("value", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), atexconv),
			new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("class", "clazz", null, null, new BeanAccessInfo(AccessInfo.THIS)), attypeconv),
			new AttributeInfo(new AccessInfo("dynamic", null, null, Boolean.FALSE, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.BOOLEAN_CONVERTER, null))
			})));

		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "avatar"), new QName(uri, "property")}), new ObjectInfo(HashMap.class), 
			new MappingInfo(null, null, new AttributeInfo(new AccessInfo("value", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), atexconv),
			new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("class", "clazz", null, null, new BeanAccessInfo(AccessInfo.THIS)), attypeconv),
			new AttributeInfo(new AccessInfo("dynamic", null, null, Boolean.FALSE, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.BOOLEAN_CONVERTER, null))
			})));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "process"), new QName(uri, "property")}), new ObjectInfo(HashMap.class), 
			new MappingInfo(null, null, new AttributeInfo(new AccessInfo("value", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), atexconv),
			new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("class", "clazz", null, null, new BeanAccessInfo(AccessInfo.THIS)), attypeconv),
			new AttributeInfo(new AccessInfo("dynamic", null, null, Boolean.FALSE, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.BOOLEAN_CONVERTER, null))
			})));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "objecttype"), new QName(uri, "property")}), new ObjectInfo(MObjectTypeProperty.class),
			new MappingInfo(null, null, new AttributeInfo(new AccessInfo("value"), atexconv),
			new AttributeInfo[] {
				new AttributeInfo(new AccessInfo("class", "type", null, null, new BeanAccessInfo(AccessInfo.THIS)), attypeconv),
				new AttributeInfo(new AccessInfo("dynamic"), new AttributeConverter(BasicTypeConverter.BOOLEAN_CONVERTER, null)),
				new AttributeInfo(new AccessInfo("event"), new AttributeConverter(BasicTypeConverter.BOOLEAN_CONVERTER, null))
			}
//			
//			new AttributeInfo[]{
//			new AttributeInfo(new AccessInfo("name", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
//			new AttributeInfo(new AccessInfo("class", "clazz", null, null, new BeanAccessInfo(AccessInfo.THIS)), attypeconv),
//			new AttributeInfo(new AccessInfo("dynamic", null, null, Boolean.FALSE, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.BOOLEAN_CONVERTER, null)),
//			new AttributeInfo(new AccessInfo("event", null, null, Boolean.FALSE, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.BOOLEAN_CONVERTER, null))
//			}
		)));

		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "drawable"), new QName(uri, "property")}), new ObjectInfo(HashMap.class), 
			new MappingInfo(null, null, new AttributeInfo(new AccessInfo("value", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), atexconv),
			new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("class", "clazz", null, null, new BeanAccessInfo(AccessInfo.THIS)), attypeconv),
			new AttributeInfo(new AccessInfo("dynamic", null, null, Boolean.FALSE, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.BOOLEAN_CONVERTER, null))
			})));

		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "spaceexecutor"), new QName(uri, "property")}), new ObjectInfo(HashMap.class),
			new MappingInfo(null, null, new AttributeInfo(new AccessInfo("value", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), atexconv),
			new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("class", "clazz", null, null, new BeanAccessInfo(AccessInfo.THIS)), attypeconv),
			new AttributeInfo(new AccessInfo("dynamic", null, null, Boolean.FALSE, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.BOOLEAN_CONVERTER, null))
			})));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "dataconsumer"), new QName(uri, "property")}), new ObjectInfo(HashMap.class), 
			new MappingInfo(null, null, new AttributeInfo(new AccessInfo("value", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), atexconv),
			new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("class", "clazz", null, null, new BeanAccessInfo(AccessInfo.THIS)), attypeconv),
			new AttributeInfo(new AccessInfo("dynamic", null, null, Boolean.FALSE, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.BOOLEAN_CONVERTER, null))
			})));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "plugin"), new QName(uri, "property")}), new ObjectInfo(HashMap.class), 
			new MappingInfo(null, null, new AttributeInfo(new AccessInfo("value", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), atexconv),
			new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("class", "clazz", null, null, new BeanAccessInfo(AccessInfo.THIS)), attypeconv),
			new AttributeInfo(new AccessInfo("dynamic", null, null, Boolean.FALSE, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.BOOLEAN_CONVERTER, null))
			})));

		// type instance declarations.
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "envspace")}), 
			new ObjectInfo(MEnvSpaceInstance.class, new IPostProcessor()
			{
				public Object postProcess(IContext context, Object object)
				{
					MSpaceInstance	si	= (MSpaceInstance)object;
					MApplicationType	apptype	= (MApplicationType)context.getRootObject();
					List spacetypes = apptype.getMSpaceTypes();
					for(int i=0; i<spacetypes.size(); i++)
					{
						MSpaceType st = (MSpaceType)spacetypes.get(i);
						if(st.getName().equals(si.getTypeName()))
						{
							si.setType(st);
							break;
						}
					}
					return null;
				}
				
				public int getPass()
				{
					return 1;
				}
			}),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("type", "typeName")),
			new AttributeInfo(new AccessInfo("width", null, null, null, new BeanAccessInfo("property")), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("height", null, null, null, new BeanAccessInfo("property")), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("depth", null, null, null, new BeanAccessInfo("property")), new AttributeConverter(BasicTypeConverter.DOUBLE_CONVERTER, null)),
			new AttributeInfo(new AccessInfo("border", null, null, null, new BeanAccessInfo("property"))),
			new AttributeInfo(new AccessInfo("neighborhood", null, null, null, new BeanAccessInfo("property")))
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo(new QName(uri, "property"), "properties", null, null, new BeanAccessInfo("property"))),
			new SubobjectInfo(new AccessInfo(new QName(uri, "object"), "objects", null, null, new BeanAccessInfo("property"))),
			new SubobjectInfo(new AccessInfo(new QName(uri, "avatar"), "avatars", null, null, new BeanAccessInfo("property"))),
			new SubobjectInfo(new AccessInfo(new QName(uri, "process"), "processes", null, null, new BeanAccessInfo("property"))),
			new SubobjectInfo(new AccessInfo(new QName(uri, "spaceaction"), "spaceactions", null, null, new BeanAccessInfo("property"))),
			new SubobjectInfo(new AccessInfo(new QName(uri, "dataprovider"), "dataproviders", null, null, new BeanAccessInfo("property"))),
			new SubobjectInfo(new AccessInfo(new QName(uri, "dataconsumer"), "dataconsumers", null, null, new BeanAccessInfo("property"))),
			new SubobjectInfo(new AccessInfo(new QName(uri, "observer"), "observers", null, null, new BeanAccessInfo("property")))			
			})));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "object")}), new ObjectInfo(MultiCollection.class),
			new MappingInfo(ti_po, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("type", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("number", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.INTEGER_CONVERTER, null))
			})));
			
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "avatar")}), new ObjectInfo(MultiCollection.class),
			new MappingInfo(ti_po, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("type", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("owner", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			})));
			
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "process")}), new ObjectInfo(MultiCollection.class),
			new MappingInfo(ti_po, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("type", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			})));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "spaceaction")}), new ObjectInfo(MultiCollection.class),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("type", null, null, null, new BeanAccessInfo(AccessInfo.THIS)))
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo(new QName(uri, "parameter"), "parameters", null, null, new BeanAccessInfo(AccessInfo.THIS)))
			})));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "spaceaction"), new QName(uri, "parameter")}), new ObjectInfo(HashMap.class),
			new MappingInfo(null, null, new AttributeInfo(new AccessInfo("value", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), atexconv),
			new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name", null, null, null, new BeanAccessInfo(AccessInfo.THIS)))
			})));

		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "observer")}), new ObjectInfo(MultiCollection.class),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("dataview", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("perspective", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("killonexit", null, null, Boolean.TRUE, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.BOOLEAN_CONVERTER, null))
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo(new QName(uri, "plugin"), "plugins", null, null, new BeanAccessInfo(AccessInfo.THIS))),
			})));
			
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "plugin")}), new ObjectInfo(MultiCollection.class),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			//new BeanAccessInfo("class", null, null, typeconv, null, ""),
			new AttributeInfo(new AccessInfo("class", "clazz", null, null, new BeanAccessInfo(AccessInfo.THIS)), attypeconv),
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo(new QName(uri, "property"), "properties", null, null, new BeanAccessInfo(AccessInfo.THIS))),
			})));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "dataprovider")}), new ObjectInfo(MultiCollection.class), 
			new MappingInfo(null,
			new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name", null, null, null, new BeanAccessInfo(AccessInfo.THIS)))
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo(new QName(uri, "source"), "source", null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new SubobjectInfo(new AccessInfo(new QName(uri, "data"), "data", null, null, new BeanAccessInfo(AccessInfo.THIS)))
			})));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "data")}), new ObjectInfo(HashMap.class), 
			new MappingInfo(null, null, new AttributeInfo(new AccessInfo("content", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), atexconv),
			new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			})));

		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "source")}), new ObjectInfo(HashMap.class), 
			new MappingInfo(null, null, new AttributeInfo(new AccessInfo("content", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), atexconv),
			new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("objecttype", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("aggregate", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), new AttributeConverter(BasicTypeConverter.BOOLEAN_CONVERTER, null))},
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo(new QName(uri, "includecondition"), null, null, null, new BeanAccessInfo(AccessInfo.THIS)), suexconv)	
			})));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "dataconsumer")}), new ObjectInfo(MultiCollection.class),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("class", null, null, null, new BeanAccessInfo(AccessInfo.THIS)), attypeconv)
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo(new QName(uri, "property"), "properties", null, null, new BeanAccessInfo(AccessInfo.THIS)))
			})));
		
		return types;
	}
		
	/**
	 *  Parse class names.
	 */
	static class ExpressionConverter implements IStringObjectConverter
	{
		// Hack!!! Should be configurable.
		protected static IExpressionParser	exp_parser	= new JavaCCExpressionParser();

		/**
		 *  Convert a string value to a type.
		 *  @param val The string value to convert.
		 */
		public Object convertString(String val, IContext context)
		{
			Object ret = null;
			
//			System.out.println("Found expression: "+val);
			try
			{
				ret = exp_parser.parseExpression((String)val, ((MApplicationType)
					context.getRootObject()).getAllImports(), null, context.getClassLoader());
			}
			catch(Exception e)
			{
				reportError(context, e.toString());
			}
			
			return ret;
		}
	}
	
	/**
	 *  Parse class names.
	 */
	public static class ClassConverter	implements IStringObjectConverter
	{
		/**
		 *  Convert a string value to a type.
		 *  @param val The string value to convert.
		 */
		public Object convertString(String val, IContext context)
		{
			Object ret = val;
			if(val instanceof String)
			{
				ret = SReflect.findClass0((String)val, ((MApplicationType)
					context.getRootObject()).getAllImports(), context.getClassLoader());
				if(ret==null)
				{
					reportError(context, "Class not found: "+val);
				}
			}
			return ret;
		}
	}
	
	/**
	 *  Parse class names.
	 */
	static class ColorConverter	implements IStringObjectConverter
	{
		protected StyleSheet ss = new StyleSheet();
		
		/**
		 *  Convert a string value to a type.
		 *  @param val The string value to convert.
		 */
		public Object convertString(String val, IContext context)
		{
			Object ret = val;
		
//			if(!(val instanceof String))
//				throw new RuntimeException("Source value must be string: "+val);
		
			if(val instanceof String)
			{
				String	str	= (String)val;
				String	alpha	= null;
				
				if((str.startsWith("#")) && (str.length()==9))
				{
					alpha	= str.substring(7);
					str	= str.substring(0, 7);
				}
				
				// Cannot use CSS.stringToColor() because they haven't made it public :-(
				Color	c	= ss.stringToColor((String)val);
				if(alpha!=null)
				{
					ret	= new Color(c.getRed(), c.getGreen(), c.getBlue(), Integer.parseInt(alpha, 16));
				}
				else if(c!=null)
				{
					ret = c;
				}
			}
			
//			System.out.println("tolerant conv: "+val+" "+ret+" "+(ret!=null? ""+ret.getClass(): ""));
			
			return ret;
		}
	}
	
	/**
	 *  String -> Double/String converter.
	 *  Converts to a double if possible. Otherwise string will be kept.
	 */
	static class TolerantDoubleTypeConverter implements IStringObjectConverter
	{
		/**
		 *  Convert a string value to another type.
		 *  @param val The string value to convert.
		 */
		public Object convertString(String val, IContext context)
		{
			Object ret = val;
			if(val instanceof String)
			{
				try{ret = new Double((String)val);}
				catch(Exception e){}
			}
			return ret;
		}
	}
	
	/**
	 *  String -> Double/String converter.
	 *  Converts to a integer if possible. Otherwise string will be kept.
	 */
	static class TolerantIntegerTypeConverter implements IStringObjectConverter
	{
		/**
		 *  Convert a string value to another type.
		 *  @param val The string value to convert.
		 */
		public Object convertString(String val, IContext context)
		{
			Object ret = val;
			if(val instanceof String)
			{
				try{ret = new Integer((String)val);}
				catch(Exception e){}
			}
			return ret;
		}
	}
	
	/**
	 *  String -> Double/String converter.
	 *  Converts to a integer if possible. Otherwise string will be kept.
	 */
	static class TolerantColorConverter implements IStringObjectConverter
	{
		/**
		 *  Convert a string value to another type.
		 *  @param val The string value to convert.
		 */
		public Object convertString(String val, IContext context)
		{
			Object ret = val;
			if(val instanceof String)
			{
				ret = new ColorConverter().convertString(val, context);
			}
			
			return ret;
		}
		
	}
	
	/**
	 *  Name attribute to object converter.
	 */
	static class NameAttributeToObjectConverter implements IObjectObjectConverter
	{
		/**
		 *  Test if a converter accepts a specific input type.
		 *  @param inputtype The input type.
		 *  @return True, if accepted.
		 */
		public Object convertObject(Object val, IContext context)
		{
			Object ret = null;
			if(val instanceof Map)
			{
				ret = (String)((Map)val).get("name");
			}
			return ret;
		}
	}

	/**
	 *  Report an error including the line and column.
	 */
	protected static void reportError(IContext context, String error)
	{
		MultiCollection	report	= (MultiCollection)context.getUserContext();
		String	pos;
		Tuple	stack	= new Tuple(((ReadContext)context).getStack().toArray());
		if(stack.getEntities().length>0)
		{
			StackElement	se	= (StackElement)stack.get(stack.getEntities().length-1);
			pos	= " (line "+se.getLocation().getLineNumber()+", column "+se.getLocation().getColumnNumber()+")";
		}
		else
		{
			pos	= " (line 0, column 0)";			
		}
		report.put(stack, error+pos);
	}
}
