package jadex.bpmn.model;

import java.util.List;

/**
 * 
 */
public interface IAssociationTarget
{
	/**
	 * 
	 */
	public String getAssociationsDescription();
	
	/**
	 * 
	 */
//	public List getAssociations();
	
	/**
	 * 
	 */
	public void addAssociation(MAssociation asso);
}
