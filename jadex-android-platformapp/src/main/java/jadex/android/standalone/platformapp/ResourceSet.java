package jadex.android.standalone.platformapp;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParserException;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Movie;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class ResourceSet extends Resources
{
	private Resources r2;

	public ResourceSet(Resources r1, Resources r2)
	{
		super(r1.getAssets(), r1.getDisplayMetrics(), r1.getConfiguration());
		this.r2 = r2;
	}

	@Override
	public CharSequence getText(int id) throws NotFoundException
	{
		CharSequence text;
		try
		{
			text = super.getText(id);
		}
		catch (NotFoundException e)
		{
			text = r2.getText(id);
		}
		return text;
	}

	@Override
	public CharSequence getQuantityText(int id, int quantity) throws NotFoundException
	{

		return super.getQuantityText(id, quantity);
	}

	@Override
	public String getString(int id) throws NotFoundException
	{

		try
		{
			return super.getString(id);
		}
		catch (NotFoundException e)
		{
			return r2.getString(id);
		}
	}

	@Override
	public String getString(int id, Object... formatArgs) throws NotFoundException
	{

		try
		{
			return super.getString(id, formatArgs);
		}
		catch (NotFoundException e)
		{
			return r2.getString(id, formatArgs);
		}
	}

	@Override
	public String getQuantityString(int id, int quantity, Object... formatArgs) throws NotFoundException
	{

		return super.getQuantityString(id, quantity, formatArgs);
	}

	@Override
	public String getQuantityString(int id, int quantity) throws NotFoundException
	{

		return super.getQuantityString(id, quantity);
	}

	@Override
	public CharSequence getText(int id, CharSequence def)
	{

		try
		{
			return super.getText(id, def);
		}
		catch (NotFoundException e)
		{
			return r2.getText(id, def);
		}
	}

	@Override
	public CharSequence[] getTextArray(int id) throws NotFoundException
	{

		return super.getTextArray(id);
	}

	@Override
	public String[] getStringArray(int id) throws NotFoundException
	{

		return super.getStringArray(id);
	}

	@Override
	public int[] getIntArray(int id) throws NotFoundException
	{

		return super.getIntArray(id);
	}

	@Override
	public TypedArray obtainTypedArray(int id) throws NotFoundException
	{

		return super.obtainTypedArray(id);
	}

	@Override
	public float getDimension(int id) throws NotFoundException
	{

		return super.getDimension(id);
	}

	@Override
	public int getDimensionPixelOffset(int id) throws NotFoundException
	{

		return super.getDimensionPixelOffset(id);
	}

	@Override
	public int getDimensionPixelSize(int id) throws NotFoundException
	{

		return super.getDimensionPixelSize(id);
	}

	@Override
	public float getFraction(int id, int base, int pbase)
	{

		return super.getFraction(id, base, pbase);
	}

	@Override
	public Drawable getDrawable(int id) throws NotFoundException
	{

		return super.getDrawable(id);
	}

	@Override
	public Movie getMovie(int id) throws NotFoundException
	{

		return super.getMovie(id);
	}

	@Override
	public int getColor(int id) throws NotFoundException
	{

		return super.getColor(id);
	}

	@Override
	public ColorStateList getColorStateList(int id) throws NotFoundException
	{

		return super.getColorStateList(id);
	}

	@Override
	public boolean getBoolean(int id) throws NotFoundException
	{

		return super.getBoolean(id);
	}

	@Override
	public int getInteger(int id) throws NotFoundException
	{

		return super.getInteger(id);
	}

	@Override
	public XmlResourceParser getLayout(int id) throws NotFoundException
	{

		return super.getLayout(id);
	}

	@Override
	public XmlResourceParser getAnimation(int id) throws NotFoundException
	{

		return super.getAnimation(id);
	}

	@Override
	public XmlResourceParser getXml(int id) throws NotFoundException
	{

		return super.getXml(id);
	}

	@Override
	public InputStream openRawResource(int id) throws NotFoundException
	{

		return super.openRawResource(id);
	}

	@Override
	public InputStream openRawResource(int id, TypedValue value) throws NotFoundException
	{

		return super.openRawResource(id, value);
	}

	@Override
	public AssetFileDescriptor openRawResourceFd(int id) throws NotFoundException
	{

		return super.openRawResourceFd(id);
	}

	@Override
	public void getValue(int id, TypedValue outValue, boolean resolveRefs) throws NotFoundException
	{

		super.getValue(id, outValue, resolveRefs);
	}

	@Override
	public void getValue(String name, TypedValue outValue, boolean resolveRefs) throws NotFoundException
	{

		super.getValue(name, outValue, resolveRefs);
	}

	@Override
	public TypedArray obtainAttributes(AttributeSet set, int[] attrs)
	{

		return super.obtainAttributes(set, attrs);
	}

	@Override
	public void updateConfiguration(Configuration config, DisplayMetrics metrics)
	{

		super.updateConfiguration(config, metrics);
	}

	@Override
	public DisplayMetrics getDisplayMetrics()
	{

		return super.getDisplayMetrics();
	}

	@Override
	public Configuration getConfiguration()
	{

		return super.getConfiguration();
	}

	@Override
	public int getIdentifier(String name, String defType, String defPackage)
	{

		return super.getIdentifier(name, defType, defPackage);
	}

	@Override
	public String getResourceName(int resid) throws NotFoundException
	{

		return super.getResourceName(resid);
	}

	@Override
	public String getResourcePackageName(int resid) throws NotFoundException
	{

		return super.getResourcePackageName(resid);
	}

	@Override
	public String getResourceTypeName(int resid) throws NotFoundException
	{

		return super.getResourceTypeName(resid);
	}

	@Override
	public String getResourceEntryName(int resid) throws NotFoundException
	{

		return super.getResourceEntryName(resid);
	}

	@Override
	public void parseBundleExtras(XmlResourceParser parser, Bundle outBundle) throws XmlPullParserException, IOException
	{

		super.parseBundleExtras(parser, outBundle);
	}

	@Override
	public void parseBundleExtra(String tagName, AttributeSet attrs, Bundle outBundle) throws XmlPullParserException
	{

		super.parseBundleExtra(tagName, attrs, outBundle);
	}

	// public ResourceSet(AssetManager assets, DisplayMetrics metrics,
	// Configuration config)
	// {
	// super(assets, metrics, config);
	// }

}
