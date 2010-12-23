package jadex.commons;

import java.util.Collection;

/**
 *  Future that support intermediate results.
 */
public interface IIntermediateFuture extends IFuture
{
    /**
     *  Get the intermediate results that are available.
     *  @return The future result.
     */
    public Collection getIntermediateResults();
}
