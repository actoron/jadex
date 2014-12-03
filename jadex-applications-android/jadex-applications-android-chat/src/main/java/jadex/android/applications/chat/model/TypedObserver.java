package jadex.android.applications.chat.model;

public abstract class TypedObserver<T> {
	public static int NOTIFICATION_TYPE_NONE = -1;
	
	public void update(TypedObservable<T> paramObservable, T paramObject) {
		update(paramObservable, paramObject, NOTIFICATION_TYPE_NONE);
	}
	public void update(TypedObservable<T> paramObservable, T paramObject, int notificationType) {
		update(paramObservable, paramObject);
	}
}
