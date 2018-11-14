/*
 * PlayerBeanInfo.java
 *
 * Generated by Protege plugin Beanynizer.
 * Changes will be lost!
 */

package jadex.bdi.examples.blackjack;


/**
 *  Java beaninfo class for concept Player of blackjack_beans ontology.
 */
public class PlayerBeanInfo extends jadex.commons.beans.SimpleBeanInfo
{
	//-------- bean related methods --------

	/** The property descriptors, constructed on first access. */
	private jadex.commons.beans.PropertyDescriptor[] pds = null;

	/**
	 *  Get the property descriptors.
	 *  @return The property descriptors.
	 */
	public jadex.commons.beans.PropertyDescriptor[] getPropertyDescriptors() {
		if(pds==null) {
			try {
				pds = new jadex.commons.beans.PropertyDescriptor[]{
					 new jadex.commons.beans.PropertyDescriptor("colorvalue", Player.class, "getColorValue", "setColorValue")
					, new jadex.commons.beans.PropertyDescriptor("name", Player.class, "getName", "setName")
					, new jadex.commons.beans.PropertyDescriptor("strategyname", Player.class, "getStrategyName", "setStrategyName")
					, new jadex.commons.beans.PropertyDescriptor("account", Player.class, "getAccount", "setAccount")
					, new jadex.commons.beans.PropertyDescriptor("playingstate", Player.class, "getState", "setState")
					, new jadex.commons.beans.IndexedPropertyDescriptor("cards", Player.class,
						"getCards", "setCards", "getCard", "setCard")
					, new jadex.commons.beans.PropertyDescriptor("bet", Player.class, "getBet", "setBet")
					, new jadex.commons.beans.PropertyDescriptor("cardCnt", Player.class, "getCardCnt", null)
				};
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		return pds;
	}
}