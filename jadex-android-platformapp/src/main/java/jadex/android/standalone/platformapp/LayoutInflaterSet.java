package jadex.android.standalone.platformapp;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LayoutInflaterSet extends LayoutInflater
{

	private LayoutInflater l2;

	protected LayoutInflaterSet(LayoutInflater l1, LayoutInflater l2)
	{
		super(l1, l1.getContext());
		this.l2 = l2;
	}

	@Override
	public LayoutInflater cloneInContext(Context newContext)
	{
		return l2.cloneInContext(newContext);
	}

	@Override
	public Context getContext()
	{
		Context context = super.getContext();
		if (context == null)
		{
			context = l2.getContext();
		}
		return context;
	}

	@Override
	public void setFactory(Factory factory)
	{
		super.setFactory(factory);
	}

	@Override
	public Filter getFilter()
	{
		Filter filter = super.getFilter();
		if (filter == null)
		{
			filter = l2.getFilter();
		}
		return filter;
	}

	@Override
	public void setFilter(Filter filter)
	{
		super.setFilter(filter);
	}

	@Override
	public View inflate(int resource, ViewGroup root)
	{
		View view = super.inflate(resource, root);
		if (view == null){
			view = l2.inflate(resource, root);
		}
		return view;
	}

	@Override
	public View inflate(XmlPullParser parser, ViewGroup root)
	{
		View view = super.inflate(parser, root);
		if (view == null){
			view = l2.inflate(parser, root);
		}
		return view;
	}

	@Override
	public View inflate(int resource, ViewGroup root, boolean attachToRoot)
	{
		View view = super.inflate(resource, root, attachToRoot);
		if (view == null){
			view = l2.inflate(resource, root, attachToRoot);
		}
		return view;
	}

	@Override
	public View inflate(XmlPullParser parser, ViewGroup root, boolean attachToRoot)
	{
		View view = super.inflate(parser, root, attachToRoot);
		if (view == null){
			view = l2.inflate(parser, root, attachToRoot);
		}
		return view;
	}

	@Override
	protected View onCreateView(String name, AttributeSet attrs) throws ClassNotFoundException
	{
		View view =
		null;
		try
		{
			view = super.onCreateView(name, attrs);
		}
		catch (ClassNotFoundException e)
		{
		}
		if (view == null){
			view = l2.createView(name, null, attrs);
		}
		return view;
	}
	

}
