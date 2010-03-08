package test;

import java.util.Properties;

public class GetClassPath {
public static void main(String[] argv) {
   getClassPath();
    }
    
    public static void getClassPath(){
    Properties prop = System.getProperties();
    prop.setProperty("java.class.path", getClassPathPriv(prop));
    System.out.println("java.class.path now = " + getClassPathPriv(prop));
    }
    
    private static String getClassPathPriv(Properties prop) {
        return prop.getProperty("java.class.path", null);
      }
}
