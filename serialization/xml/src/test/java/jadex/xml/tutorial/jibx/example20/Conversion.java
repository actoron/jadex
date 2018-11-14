
package jadex.xml.tutorial.jibx.example20;

public class Conversion
{
    private Conversion () {}
    
    public static String serializeDollarsCents(int cents) {
        StringBuffer buff = new StringBuffer();
        buff.append(cents / 100);
        int extra = cents % 100;
        if (extra != 0) {
            buff.append('.');
            if (extra < 10) {
                buff.append('0');
            }
            buff.append(extra);
        }
        return buff.toString();
    }
    
    public static int deserializeDollarsCents(String text) {
        if (text == null) {
            return 0;
        } else {
            int split = text.indexOf('.');
            int cents = 0;
            if (split > 0) {
                cents = Integer.parseInt(text.substring(0, split)) * 100;
                text = text.substring(split+1);
            }
            return cents + Integer.parseInt(text);
        }
    }
    
    public static String serializeIntArray(int[] values) {
        StringBuffer buff = new StringBuffer();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                buff.append(' ');
            }
            buff.append(values[i]);
        }
        return buff.toString();
    }
    
    private static int[] resizeArray(int[] array, int size) {
        int[] copy = new int[size];
        System.arraycopy(array, 0, copy, 0, Math.min(array.length, size));
        return copy;
    }
    
    public static int[] deserializeIntArray(String text) {
        if (text == null) {
            return new int[0];
        } else {
            int split = 0;
            text = text.trim();
            int fill = 0;
            int[] values = new int[10];
            while (split < text.length()) {
                int base = split;
                split = text.indexOf(' ', split);
                if (split < 0) {
                    split = text.length();
                }
                int value = Integer.parseInt(text.substring(base, split));
                if (fill >= values.length) {
                    values = resizeArray(values, values.length*2);
                }
                values[fill++] = value;
                while (split < text.length() && text.charAt(++split) == ' ');
            }
            return resizeArray(values, fill);
        }
    }
}
