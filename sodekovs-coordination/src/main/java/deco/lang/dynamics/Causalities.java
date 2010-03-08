package deco.lang.dynamics;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import deco.lang.dynamics.causalities.DecentralMechanismLink;
import deco.lang.dynamics.causalities.DirectLink;
import deco.lang.dynamics.mechanism.DecentralizedCausality;
import deco.lang.dynamics.mechanism.DirectCausality;
import deco.lang.dynamics.mechanism.MechanismConfiguration;

/**
 * Causal link definitions.<br>
 * Direct and decentralized links are distinguished. 
 * The detailed configurations of links can be specified as well.
 * 
 * @author Jan Sudeikat
 *
 */
@XmlRootElement(name="causalities")
public class Causalities {
	
	//----------attributes----------
	
	/** The direct coordination links. */
	ArrayList<DirectLink> direct_links = new ArrayList<DirectLink>();

	/** The decentralized coordination links. */
	ArrayList<DecentralMechanismLink> dml = new ArrayList<DecentralMechanismLink>();

	/** The realizations / configurations of decentralized links. */
	private ArrayList<DecentralizedCausality> realizations = new ArrayList<DecentralizedCausality>();
	
	/** The realizations / configurations of direct links. */
	private ArrayList<DirectCausality> directLinkRealizations = new ArrayList<DirectCausality>();
	
	//----------methods-------------
	
	@XmlElementWrapper(name="direct_links")
	@XmlElement(name="direct_link")
	public ArrayList<DirectLink> getDirectLinks() {
		return direct_links;
	}

	public void setDirect_links(ArrayList<DirectLink> direct_links) {
		this.direct_links = direct_links;
	}

	@XmlElementWrapper(name="deco_links")
	@XmlElement(name="deco_link")
	public ArrayList<DecentralMechanismLink> getDml() {
		return dml;
	}

	public void setDml(ArrayList<DecentralMechanismLink> dml) {
		this.dml = dml;
	}
	
	public void addDml(DecentralMechanismLink dml){
		this.dml.add(dml);
	}
	
	public void addDirectLink(DirectLink dl){
		this.direct_links.add(dl);	
	}
	
	@XmlElementWrapper(name="deco-link-realizations")
	@XmlElement(name="realization")
	public ArrayList<DecentralizedCausality> getRealizations() {
		return realizations;
	}

	public void setRealizations(ArrayList<DecentralizedCausality> realizations) {
		this.realizations = realizations;
	}
	
	public void addRealization(DecentralizedCausality c){
		this.realizations.add(c);
	}
	
	@XmlElementWrapper(name="direct_link_configurations")
	@XmlElement(name="direct_link_configuration")
	public ArrayList<DirectCausality> getDirectLinkRealizations() {
		return directLinkRealizations;
	}

	public void setDirectLinkRealizations(
			ArrayList<DirectCausality> directLinkRealizations) {
		this.directLinkRealizations = directLinkRealizations;
	}
	
	public DirectCausality getDirectLinkRealizationByName(String name){
		for (DirectCausality c : this.directLinkRealizations){
			if (c.getId().equalsIgnoreCase(name)) return c;
		}
		return null;
	}
	
	//----------utility-------------
	
	/**
	 * Get the configuration of a DCM by its name.
	 * 
	 * @param name
	 * @return
	 */
	public DecentralizedCausality getDCMRealizationByName(String name){
		for (DecentralizedCausality c : this.realizations){
			if (c.getId().equalsIgnoreCase(name)) return c;
		}
		return null;
	}
	
	/**
	 * Get the configuration of a DCM by its implementation name.
	 * 
	 * @param name
	 * @return
	 */
	public MechanismConfiguration getDCMRealizationByImplName(String name){
		for (DecentralizedCausality c : this.realizations){
			if (c.getMechanismConfiguration().getMechanism_id().equalsIgnoreCase(name)) return c.getMechanismConfiguration();
		}
		return null;
	}
	
}