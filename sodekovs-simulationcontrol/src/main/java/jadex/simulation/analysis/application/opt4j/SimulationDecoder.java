package jadex.simulation.analysis.application.opt4j;

import java.util.HashMap;
import java.util.Map;

import org.opt4j.core.problem.Decoder;
import org.opt4j.core.problem.PhenotypeWrapper;
import org.opt4j.genotype.DoubleMapGenotype;
import org.opt4j.genotype.IntegerMapGenotype;

public class SimulationDecoder implements
		Decoder<IntegerMapGenotype<String>, PhenotypeWrapper<Map<String, Integer>>>
{

	@Override
	public PhenotypeWrapper<Map<String, Integer>> decode(IntegerMapGenotype<String> genotype)
	{
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (String key : genotype.getKeys())
		{
			map.put(key, genotype.getValue(key));
		}
		return new PhenotypeWrapper<Map<String, Integer>>(map);
	}

}
