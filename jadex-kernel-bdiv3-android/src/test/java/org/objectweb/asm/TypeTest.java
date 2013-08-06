package org.objectweb.asm;

import org.junit.Assert;
import org.junit.Test;

public class TypeTest
{

	@Test
	public void testNoArguments()
	{
		Type[] argumentTypes;
		argumentTypes = Type.getArgumentTypes("V");
		Assert.assertEquals(0, argumentTypes.length);

		argumentTypes = Type.getArgumentTypes("Ljava/lang/Object;");
		Assert.assertEquals(0, argumentTypes.length);
	}
	@Test
	public void testSingleArgumentType()
	{
		Type[] argumentTypes;

		argumentTypes = Type.getArgumentTypes("VLjava/lang/Object;");
		Assert.assertEquals(Type.OBJECT, argumentTypes[0].getSort() );
	}

	@Test
	public void testMultiArgumentType()
	{
		Type[] argumentTypes;

		argumentTypes = Type.getArgumentTypes("VJLjava/lang/Object;ILorg/objectweb/asm/Type;");
		Assert.assertEquals(Type.LONG, argumentTypes[0].getSort());
		Assert.assertEquals(Type.OBJECT, argumentTypes[1].getSort());
		Assert.assertEquals(Type.INT, argumentTypes[2].getSort());
		Assert.assertEquals(Type.OBJECT, argumentTypes[3].getSort());
	}

	@Test
	public void testComplexArgumentType()
	{
		Type[] argumentTypes;
		argumentTypes = Type.getArgumentTypes("VLjava/lang/Object;[Ljava/lang/Object;");
		Assert.assertEquals(Type.OBJECT, argumentTypes[0].getSort());
		Assert.assertEquals(Object.class.getName(), argumentTypes[0].getClassName());

		Assert.assertEquals(Type.ARRAY, argumentTypes[1].getSort());
		Assert.assertEquals(Object[].class.getCanonicalName(), argumentTypes[1].getClassName());
	}

	@Test
	public void testNonPrimitiveReturnType()
	{
		Type[] argumentTypes;

		argumentTypes = Type.getArgumentTypes("Ljava/lang/Object;Lorg/objectweb/asm/Type;");
		Assert.assertEquals(Type.OBJECT, argumentTypes[0].getSort());
		Assert.assertEquals(Type.class.getCanonicalName(), argumentTypes[0].getClassName());
	}
}
