package jadex.bytecode.fastinvocation;

import jadex.commons.Tuple2;

public interface IExtractor<T>
{
	public Tuple2<String[], Object[]> extractValues(T target);
}
