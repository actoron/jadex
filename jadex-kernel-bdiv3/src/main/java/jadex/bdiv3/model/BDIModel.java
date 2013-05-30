package jadex.bdiv3.model;

import jadex.bridge.modelinfo.IModelInfo;
import jadex.commons.FieldInfo;
import jadex.commons.Tuple2;
import jadex.micro.MicroModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class BDIModel extends MicroModel
{
	/** The subcapabilities. */
	protected List<Tuple2<FieldInfo, BDIModel>> subcapabilities;


	/** The capability. */
	protected MCapability mcapa;

	/**
	 *  Create a new model.
	 */
	public BDIModel(IModelInfo modelinfo, MCapability mcapa)
	{
		super(modelinfo);
		this.mcapa = mcapa;
	}

	/**
	 *  Get the mcapa.
	 *  @return The mcapa.
	 */
	public MCapability getCapability()
	{
		return mcapa;
	}

	/**
	 *  Set the mcapa.
	 *  @param mcapa The mcapa to set.
	 */
	public void setCapability(MCapability mcapa)
	{
		this.mcapa = mcapa;
	}
	
	/**
	 *  Add a subcapability field.
	 *  @param field The field. 
	 */
	public void addSubcapability(FieldInfo field, BDIModel model)
	{
		if(subcapabilities==null)
			subcapabilities = new ArrayList<Tuple2<FieldInfo, BDIModel>>();
		subcapabilities.add(new Tuple2<FieldInfo, BDIModel>(field, model));
	}
	
	/**
	 *  Get the agent injection fields.
	 *  @return The fields.
	 */
	public Tuple2<FieldInfo, BDIModel>[] getSubcapabilities()
	{
		return subcapabilities==null? new Tuple2[0]: (Tuple2<FieldInfo, BDIModel>[])subcapabilities.toArray(new Tuple2[subcapabilities.size()]);
	}

}
