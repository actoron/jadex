package jadex.android.standalone.platformapp;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParserException;

import android.content.res.AssetFileDescriptor;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.content.res.Resources.NotFoundException;
import android.graphics.Movie;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * This class extends {@link Resources} to support two Resources at once. If one
 * fails to find the requested resource with a {@link NotFoundException}, the
 * other will be called.
 */
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
		CharSequence text;
		try
		{
			text = super.getQuantityText(id, quantity);
		}
		catch (NotFoundException e)
		{
			text = r2.getQuantityText(id, quantity);
		}
		return text;
	}

	@Override
	public String getString(int id) throws NotFoundException
	{
		String text;
		try
		{
			text = super.getString(id);
		}
		catch (NotFoundException e)
		{
			text = r2.getString(id);
		}
		return text;
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
		String text;
		try
		{
			text = super.getQuantityString(id, quantity, formatArgs);
		}
		catch (NotFoundException e)
		{
			text = r2.getQuantityString(id, quantity, formatArgs);
		}
		return text;
	}

	@Override
	public String getQuantityString(int id, int quantity) throws NotFoundException
	{
		String text;
		try
		{
			text = super.getQuantityString(id, quantity);
		}
		catch (NotFoundException e)
		{
			text = r2.getQuantityString(id, quantity);
		}
		return text;
	}

	@Override
	public CharSequence getText(int id, CharSequence def)
	{
		CharSequence text;
		text = super.getText(id, def);
		if (text == null || text.equals(def))
		{
			text = r2.getText(id, def);
		}
		return text;
	}

	@Override
	public CharSequence[] getTextArray(int id) throws NotFoundException
	{
		CharSequence[] result;
		try
		{
			result = super.getTextArray(id);
		}
		catch (NotFoundException e)
		{
			result = r2.getTextArray(id);
		}
		return result;
	}

	@Override
	public String[] getStringArray(int id) throws NotFoundException
	{
		String[] result;
		try
		{
			result = super.getStringArray(id);
		}
		catch (NotFoundException e)
		{
			result = r2.getStringArray(id);
		}
		return result;
	}

	@Override
	public int[] getIntArray(int id) throws NotFoundException
	{

		int[] result;
		try
		{
			result = super.getIntArray(id);
		}
		catch (NotFoundException e)
		{
			result = r2.getIntArray(id);
		}
		return result;
	}

	@Override
	public TypedArray obtainTypedArray(int id) throws NotFoundException
	{

		TypedArray result;
		try
		{
			result = super.obtainTypedArray(id);
		}
		catch (NotFoundException e)
		{
			result = r2.obtainTypedArray(id);
		}
		return result;
	}

	@Override
	public float getDimension(int id) throws NotFoundException
	{
		float result;
		try
		{
			result = super.getDimension(id);
		}
		catch (NotFoundException e)
		{
			result = r2.getDimension(id);
		}
		return result;
	}

	@Override
	public int getDimensionPixelOffset(int id) throws NotFoundException
	{
		int result;
		try
		{
			result = super.getDimensionPixelOffset(id);
		}
		catch (NotFoundException e)
		{
			result = r2.getDimensionPixelOffset(id);
		}
		return result;
	}

	@Override
	public int getDimensionPixelSize(int id) throws NotFoundException
	{
		int result;
		try
		{
			result = super.getDimensionPixelSize(id);
		}
		catch (NotFoundException e)
		{
			result = r2.getDimensionPixelSize(id);
		}
		return result;

	}

	@Override
	public float getFraction(int id, int base, int pbase)
	{

		float result;
		try
		{
			result = super.getFraction(id, base, pbase);
		}
		catch (NotFoundException e)
		{
			result = r2.getFraction(id, base, pbase);
		}
		return result;
	}

	@Override
	public Drawable getDrawable(int id) throws NotFoundException
	{

		Drawable result;
		try
		{
			result = super.getDrawable(id);
		}
		catch (NotFoundException e)
		{
			result = r2.getDrawable(id);
		}
		return result;
	}

	@Override
	public Movie getMovie(int id) throws NotFoundException
	{

		Movie result;
		try
		{
			result = super.getMovie(id);
		}
		catch (NotFoundException e)
		{
			result = r2.getMovie(id);
		}
		return result;
	}

	@Override
	public int getColor(int id) throws NotFoundException
	{

		int result;
		try
		{
			result = super.getColor(id);
		}
		catch (NotFoundException e)
		{
			result = r2.getColor(id);
		}
		return result;
	}

	@Override
	public ColorStateList getColorStateList(int id) throws NotFoundException
	{
		ColorStateList result;
		try
		{
			result = super.getColorStateList(id);
		}
		catch (NotFoundException e)
		{
			result = r2.getColorStateList(id);
		}
		return result;
	}

	@Override
	public boolean getBoolean(int id) throws NotFoundException
	{

		boolean result;
		try
		{
			result = super.getBoolean(id);
		}
		catch (NotFoundException e)
		{
			result = r2.getBoolean(id);
		}
		return result;
	}

	@Override
	public int getInteger(int id) throws NotFoundException
	{

		int result;
		try
		{
			result = super.getInteger(id);
		}
		catch (NotFoundException e)
		{
			result = r2.getInteger(id);
		}
		return result;
	}

	@Override
	public XmlResourceParser getLayout(int id) throws NotFoundException
	{

		XmlResourceParser result;
		try
		{
			result = super.getLayout(id);
		}
		catch (NotFoundException e)
		{
			result = r2.getLayout(id);
		}
		return result;
	}

	@Override
	public XmlResourceParser getAnimation(int id) throws NotFoundException
	{
		XmlResourceParser result;
		try
		{
			result = super.getAnimation(id);
		}
		catch (NotFoundException e)
		{
			result = r2.getAnimation(id);
		}
		return result;
	}

	@Override
	public XmlResourceParser getXml(int id) throws NotFoundException
	{

		XmlResourceParser result;
		try
		{
			result = super.getXml(id);
		}
		catch (NotFoundException e)
		{
			result = r2.getXml(id);
		}
		return result;
	}
	@Override
	public InputStream openRawResource(int id) throws NotFoundException
	{

		InputStream result;
		try
		{
			result = super.openRawResource(id);
		}
		catch (NotFoundException e)
		{
			result = r2.openRawResource(id);
		}
		return result;
	}

	@Override
	public InputStream openRawResource(int id, TypedValue value) throws NotFoundException
	{
		InputStream result;
		try
		{
			result = super.openRawResource(id, value);
		}
		catch (NotFoundException e)
		{
			result = r2.openRawResource(id, value);
		}
		return result;
	}

	@Override
	public AssetFileDescriptor openRawResourceFd(int id) throws NotFoundException
	{
		AssetFileDescriptor result;
		try
		{
			result = super.openRawResourceFd(id);
		}
		catch (NotFoundException e)
		{
			result = r2.openRawResourceFd(id);
		}
		return result;
	}

	@Override
	public void getValue(int id, TypedValue outValue, boolean resolveRefs) throws NotFoundException
	{
		try
		{
			super.getValue(id, outValue, resolveRefs);
		}
		catch (NotFoundException e)
		{
			r2.getValue(id, outValue, resolveRefs);
		}
	}

	@Override
	public void getValue(String name, TypedValue outValue, boolean resolveRefs) throws NotFoundException
	{
		try
		{
			super.getValue(name, outValue, resolveRefs);
		}
		catch (NotFoundException e)
		{
			r2.getValue(name, outValue, resolveRefs);
		}
	}

	@Override
	public TypedArray obtainAttributes(AttributeSet set, int[] attrs)
	{
		TypedArray result;
		result = super.obtainAttributes(set, attrs);
		if (result == null)
		{
			result = r2.obtainAttributes(set, attrs);
		}
		return result;
	}

//	 @Override
//	 public void updateConfiguration(Configuration config, DisplayMetrics
//	 metrics)
//	 {
//	
//	 try
//	 {
//	 super.updateConfiguration(config, metrics);
//	 }
//	 catch (NotFoundException e)
//	 {
//	 r2.updateConfiguration(config, metrics);
//	 }
//	 }

	@Override
	public DisplayMetrics getDisplayMetrics()
	{
		DisplayMetrics result;
		result = super.getDisplayMetrics();
		if (result == null)
		{
			result = r2.getDisplayMetrics();
		}
		return result;
	}

	@Override
	public Configuration getConfiguration()
	{
		Configuration result;
		result = super.getConfiguration();
		if (result == null)
		{
			result = r2.getConfiguration();
		}
		return result;
	}

	@Override
	public int getIdentifier(String name, String defType, String defPackage)
	{
		int result;
		result = super.getIdentifier(name, defType, defPackage);
		if (result == 0)
		{
			result = r2.getIdentifier(name, defType, defPackage);
		}
		return result;
	}

	@Override
	public String getResourceName(int resid) throws NotFoundException
	{
		String result;
		try
		{
			result = super.getResourceName(resid);
		}
		catch (NotFoundException e)
		{
			result = r2.getResourceName(resid);
		}
		return result;
	}

	@Override
	public String getResourcePackageName(int resid) throws NotFoundException
	{
		String result;
		try
		{
			result = super.getResourcePackageName(resid);
		}
		catch (NotFoundException e)
		{
			result = r2.getResourcePackageName(resid);
		}
		return result;
	}

	@Override
	public String getResourceTypeName(int resid) throws NotFoundException
	{
		String result;
		try
		{
			result = super.getResourceTypeName(resid);
		}
		catch (NotFoundException e)
		{
			result = r2.getResourceTypeName(resid);
		}
		return result;
	}

	@Override
	public String getResourceEntryName(int resid) throws NotFoundException
	{
		String result;
		try
		{
			result = super.getResourceEntryName(resid);
		}
		catch (NotFoundException e)
		{
			result = r2.getResourceEntryName(resid);
		}
		return result;
	}

	// @Override
	// public void parseBundleExtras(XmlResourceParser parser, Bundle outBundle)
	// throws XmlPullParserException, IOException
	// {
	// super.parseBundleExtras(parser, outBundle);
	// }

	// @Override
	// public void parseBundleExtra(String tagName, AttributeSet attrs, Bundle
	// outBundle) throws XmlPullParserException
	// {
	// super.parseBundleExtra(tagName, attrs, outBundle);
	// }

}
