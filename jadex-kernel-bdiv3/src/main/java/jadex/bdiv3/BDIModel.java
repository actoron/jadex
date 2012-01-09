package jadex.bdiv3;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import jadex.bridge.modelinfo.IModelInfo;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.collection.MultiCollection;
import jadex.kernelbase.CacheableKernelModel;
import jadex.micro.MicroModel;

/**
 * 
 */
public class BDIModel extends MicroModel
{
	/**
	 *  Create a new model.
	 */
	public BDIModel(IModelInfo modelinfo)
	{
		super(modelinfo);
	}
}
