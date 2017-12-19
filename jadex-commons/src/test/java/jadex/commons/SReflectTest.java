package jadex.commons;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;

public class SReflectTest {
    @Test
    public void testGetAllFields() {
        Field[] allFields = SReflect.getAllFields(B.class);
        Assert.assertEquals("fieldB", allFields[0].getName());
        Assert.assertEquals("fieldA", allFields[1].getName());
    }
}


class A {
    private int fieldA;
}

class B extends A {
    private int fieldB;
}