package jadex.simulation.model;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

/**
 * 
 * @author Vilenica
 * 
 */
public class Dataprovider {

	/* The name. */
	private String name;

	/* List of sources */
	private ArrayList<Source> sourceList;// = new ArrayList<Source>();

//	/* List of data */
	private ArrayList<Data> dataList;// = new ArrayList<Data>();


	@XmlElementWrapper(name = "Sources")
	@XmlElement(name = "Source")
	public ArrayList<Source> getSourceList() {
		return sourceList;
	}

	public void setSourceList(ArrayList<Source> sourceList) {
		this.sourceList = sourceList;
	}

	@XmlElementWrapper(name = "Datas")
	@XmlElement(name = "Data")
	public ArrayList<Data> getDataList() {
		return dataList;
	}

	public void setDataList(ArrayList<Data> dataList) {
		this.dataList = dataList;
	}

	@XmlAttribute(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
