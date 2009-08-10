package jadex.commons.xml.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class A
{
	protected int i;
	
	protected String s;
	
	protected B b;
	
	protected List bs;
	
	public A()
	{
	}
	
	public A(int i, String s, B b, B[] bs)
	{
		this.i = i;
		this.s = s;
		this.b = b;
		if(bs!=null)
		{
			this.bs = new ArrayList();
			for(int j=0; j<bs.length; j++)
			{
				this.bs.add(bs[j]);
			}
		}
	}

	public int getI()
	{
		return this.i;
	}

	public void setI(int i)
	{
		this.i = i;
	}

	public String getS()
	{
		return this.s;
	}

	public void setS(String s)
	{
		this.s = s;
	}

	public B getB()
	{
		return this.b;
	}

	public void setB(B b)
	{
		this.b = b;
	}

	public B[] getBs()
	{
		return (B[])(this.bs==null? new B[0]: this.bs.toArray(new B[0]));
	}

	public void setBs(B[] bs)
	{
		this.bs = new ArrayList();
		for(int j=0; j<bs.length; j++)
		{
			this.bs.add(bs[j]);
		}
	}
	
	public void addB(B b)
	{
		if(bs==null)
			bs = new ArrayList();
		bs.add(b);
	}

	public String toString()
	{
		return "A [b=" + this.b + ", bs=" + bs + ", i="
				+ this.i + ", s=" + this.s + "]";
	}
}

