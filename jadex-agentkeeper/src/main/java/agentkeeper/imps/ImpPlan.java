package agentkeeper.imps;

import agentkeeper.ai.bdi.basic.GehHinUndArbeit;

@SuppressWarnings("serial")
public abstract class ImpPlan extends GehHinUndArbeit
{
	public abstract void aktion();
	
	@Override
	public void gegnerNaehe( long id )
	{
		
	}
}
