package jadex.android.applications.chat.model;

import java.util.Observer;

public interface ITypedObservable<T> {

	public abstract boolean hasChanged();

	public abstract void deleteObservers();

	public abstract void deleteObserver(Observer o);

	public abstract void addObserver(ITypedObserver<T> o);

}
