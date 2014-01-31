package jadex.android.standalone.platformapp;

import jadex.android.commons.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.LayoutInflater.Factory;

final class ClientAppLayoutFactory implements Factory
{
	private final Class[] mConstructorSignature = new Class[]
			{Context.class, AttributeSet.class};
	private final Object[] mConstructorArgs = new Object[2];

	private ClassLoader cl;
	/**
	 * 
	 */

	public ClassLoader getClassLoader()
	{
		return cl;
	}

	public void setClassLoader(ClassLoader cl)
	{
		this.cl = cl;
	}

	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs)
	{
		View result = null;
		Class<?> loadClass;
		try
		{
			loadClass = cl.loadClass(name);
			Constructor<?> constructor = loadClass.getConstructor(mConstructorSignature);
			Object[] args = mConstructorArgs;
			args[0] = context;
			args[1] = attrs;
			result = (View) constructor.newInstance(args);
		}
		catch (ClassNotFoundException e)
		{
			Logger.d("Class not found in client app classes: " + name);
		}
		catch (SecurityException e)
		{
			Logger.d("Cannot access class: " + name);
		}
		catch (NoSuchMethodException e)
		{
			Logger.d("Cannot instanciate class (wrong constructor?): " + name);
		}
		catch (IllegalArgumentException e)
		{
			Logger.d("Cannot instanciate class (wrong constructor?): " + name);
		}
		catch (InstantiationException e)
		{
			Logger.d("Cannot instanciate class (wrong constructor?): " + name);
		}
		catch (IllegalAccessException e)
		{
			Logger.d("Cannot access class: " + name);
		}
		catch (InvocationTargetException e)
		{
			e.printStackTrace();
		}

		return result;
	}
}