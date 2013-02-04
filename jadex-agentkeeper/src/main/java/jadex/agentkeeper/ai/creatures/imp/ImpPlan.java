package jadex.agentkeeper.ai.creatures.imp;

import jadex.agentkeeper.ai.oldai.basic.GehHinUndArbeit;

/**
 * TODO: Refractor in English and as BDIv3 Agent-Plan
 * 
 * @author Philip Willuweit p.willuweit@gmx.de
 */
public abstract class ImpPlan extends GehHinUndArbeit
{
	public abstract void aktion();
	
	@Override
	public void gegnerNaehe( long id )
	{
		
	}
}
