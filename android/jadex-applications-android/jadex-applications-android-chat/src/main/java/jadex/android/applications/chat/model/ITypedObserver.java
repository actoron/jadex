package jadex.android.applications.chat.model;

public interface ITypedObserver<T> {

	public abstract void update(ITypedObservable<T> observable, T param, int notificationType);

	public abstract void update(ITypedObservable<T> observable, T param);

}
