package agentkeeper.state;

public class CreatureState
{
	private int countImp;
	
	public void addCreature(String type)
	{
		if(type.equals("imp"))
		{
			this.countImp++;
		}
	}

	/**
	 * @return the count_imp
	 */
	public int getCountImp()
	{
		return countImp;
	}

	/**
	 * @param count_imp the count_imp to set
	 */
	public void setCountImp(int count_imp)
	{
		this.countImp = count_imp;
	}
	
	

}
