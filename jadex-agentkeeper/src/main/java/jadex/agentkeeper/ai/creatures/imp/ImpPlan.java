package jadex.agentkeeper.ai.creatures.imp;

import jadex.agentkeeper.ai.oldai.basic.GehHinUndArbeit;

@SuppressWarnings("serial")
public abstract class ImpPlan extends GehHinUndArbeit
{
	public abstract void aktion();
	
	@Override
	public void gegnerNaehe( long id )
	{
		
	}
}
