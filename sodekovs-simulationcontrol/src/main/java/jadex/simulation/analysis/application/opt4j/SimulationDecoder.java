package jadex.simulation.analysis.application.opt4j;

import java.util.HashMap;
import java.util.Map;

import org.opt4j.core.problem.Decoder;
import org.opt4j.core.problem.PhenotypeWrapper;
import org.opt4j.genotype.DoubleMapGenotype;

public class SimulationDecoder implements
		Decoder<DoubleMapGenotype<String>, PhenotypeWrapper<Map<String, Double>>>
{

	@Override
	public PhenotypeWrapper<Map<String, Double>> decode(DoubleMapGenotype<String> genotype)
	{
		Map<String, Double> map = new HashMap<String, Double>();
		for (String key : genotype.getKeys())
		{
			map.put(key, genotype.getValue(key));
		}
		return new PhenotypeWrapper<Map<String, Double>>(map);
	}

}
