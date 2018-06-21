package jadex.android.applications.chat.model;

public abstract class TypedObserver<T> implements ITypedObserver<T> {
	public static int NOTIFICATION_TYPE_NONE = -1;
	
	@Override
	public void update(ITypedObservable<T> paramObservable, T paramObject) {
		update(paramObservable, paramObject, NOTIFICATION_TYPE_NONE);
	}
	@Override
	public void update(ITypedObservable<T> paramObservable, T paramObject, int notificationType) {
		update(paramObservable, paramObject);
	}
}
