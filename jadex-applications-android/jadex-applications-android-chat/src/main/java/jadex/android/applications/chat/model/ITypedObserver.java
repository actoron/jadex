package jadex.android.applications.chat.model;

public interface ITypedObserver<T> {

	public abstract void update(ITypedObservable<T> paramObservable, T paramObject, int notificationType);

	public abstract void update(ITypedObservable<T> paramObservable, T paramObject);

}
