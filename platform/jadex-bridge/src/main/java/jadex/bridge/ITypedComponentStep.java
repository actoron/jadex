package jadex.bridge;


/**
 *  Component step with explicit return type.
 */
public interface ITypedComponentStep<T> extends IComponentStep<T>
{
	public Class<?>	getReturnType();
}
