package sodekovs.investigation.persist;

import java.util.ArrayList;
import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "http://antisa.com/")
public class OutputModel {

	private int run;
	private int param;
	private Date date;
	private ArrayList<String> res;
	private HelpElement helpElement;

	
	public HelpElement getHelpElement() {
		return helpElement;
	}

	public void setHelpElement(HelpElement helpElement) {
		this.helpElement = helpElement;
	}

	public ArrayList<String> getRes() {
		return res;
	}

	public void setRes(ArrayList<String> res) {
		this.res = res;
	}

	@XmlAttribute(name="Datum")
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getRun() {
		return run;
	}

	@XmlElement(name = "lauefe")
	public void setRun(int run) {
		this.run = run;
	}

	public int getParam() {
		return param;
	}

	public void setParam(int param) {
		this.param = param;
	}

	// public DJ getDj()
	// {
	// return dj;
	// }
	//	 
	// public void setDj( DJ dj )
	// {
	// this.dj = dj;
	// }
	//	 
	// public int getNumberOfPersons()
	// {
	// return numberOfPersons;
	// }
	//	 
	// public void setNumberOfPersons( int numberOfPersons )
	// {
	//	 
	// this.numberOfPersons = numberOfPersons;
	// }
}
