package jadex.bdi.model;

/**
 *  Interface for inhibited element.
 */
public interface IMInhibited extends IMCondition
{
	/**
	 *  Get the referenced goal name.
	 *  @return The name of the inhibited goal.
	 */
	public String getReference();
	
	/**
	 *  Get the inhibition mode (OAVBDIMetaModel.INHIBITS_WHEN_ACTIVE/INHIBITS_WHEN_IN_PROCESS).
	 *  @return The mode.
	 */
	public String getMode();
}
