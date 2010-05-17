package jadex.application.space.envsupport;

import jadex.commons.meta.IPropertyMetaData;
import jadex.commons.meta.IPropertyMetaDataSet;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MObjectType implements IPropertyMetaDataSet {

	private String name;
	private Map properties = new HashMap();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void addProperty(MObjectTypeProperty property) {
		properties.put(property.getName(), property);
	}
	
	public IPropertyMetaData getProperty(String name) {
		return (MObjectTypeProperty)properties.get(name);
	}
	
	public Iterator iterator() {
		return properties.values().iterator();
	}
}
