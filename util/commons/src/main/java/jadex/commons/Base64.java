package jadex.commons;

/** 
 * Base64<br>
 * Contains lots of utilities for coding and decoding text and character arrays fore
 * and back to the Base64 encoding. It is quite the fastest that I know.
 */
public final class Base64
{
   // ------------------------ ENCODER --------------------
   /** This is a wrapper to <code>toCharArray(final byte[] text, final int from, final int len)</code>
    * @param text
    * @return an encoded char array from the text
    */
   public final static char[] toCharArray(final byte[] text)
   {
      return toCharArray(text, 0, text.length);
   }

   /** This will encode the text without line feeds added
    * @param text
    * @param from where to start
    * @param len how long is the byte array
    * @return an encoded char array from the text
    */
   public final static char[] toCharArray(final byte[] text, final int from, final int len)
   {
      final char[] code;
      int bi = from + len;
      int ci = (len / 3) << 2;
      int i;
      switch (len % 3) { // length of padding
      case 1:
         ci += 4;
         code = new char[ci];
         i = text[--bi] << 4;
         code[--ci] = '=';
         code[--ci] = '=';
         code[--ci] = C[i & 0x3F];
         code[--ci] = C[(i >>> 6) & 0x3F];
         break;
      case 2:
         ci += 4;
         code = new char[ci];
         i = (text[--bi] & 0xFF) << 2 | text[--bi] << 10;
         code[--ci] = '=';
         code[--ci] = C[i & 0x3F];
         code[--ci] = C[(i >>> 6) & 0x3F];
         code[--ci] = C[(i >>> 12) & 0x3F];
         break;
      default: // 0
         code = new char[ci];
      }

      while (ci > 00)
      {
         i = (text[--bi] & 0xFF) | (text[--bi] & 0xFF) << 8 | text[--bi] << 16;
         code[--ci] = C[i & 0x3F];
         code[--ci] = C[(i >>> 6) & 0x3F];
         code[--ci] = C[(i >>> 12) & 0x3F];
         code[--ci] = C[(i >>> 18) & 0x3F];
      }
      return code;
   }
   
   /**
    * Encode and omit padding. 
    * @param text
    * @return encoded text
    */
   public final static byte[] encodeNoPadding(final byte[] text)
   {
	   byte[] ret = encode(text);
	   if (text.length % 3 == 1)
	   {
		   byte[] tmp = ret;
		   ret = new byte[tmp.length - 2];
		   System.arraycopy(tmp, 0, ret, 0, ret.length);
	   }
	   else if (text.length % 3 == 2)
	   {
		   byte[] tmp = ret;
		   ret = new byte[tmp.length - 1];
		   System.arraycopy(tmp, 0, ret, 0, ret.length);
	   }
	   
	   return ret;
   }

   /** 
    * @param text
    * @return encoded text
    */
   public final static byte[] encode(final byte[] text)
   {
      return encode(text, 0, text.length);
   }

   /** This will encode the text without line feeds added
    * @param text
    * @param from where to start
    * @param len how long is the byte array
    * @return an encoded byte array from the text
    */
   public final static byte[] encode(final byte[] text, final int from, final int len)
   {
      final byte[] code;
      int bi = from + len;
      int ci = (len / 3) << 2;
      int i;
      switch (len % 3) { // length of padding
      case 1:
         ci += 4;
         code = new byte[ci];
         i = text[--bi] << 4;
         code[--ci] = '=';
         code[--ci] = '=';
         code[--ci] = B[i & 0x3F];
         code[--ci] = B[(i >>> 6) & 0x3F];
         break;
      case 2:
         ci += 4;
         code = new byte[ci];
         i = (text[--bi] & 0xFF) << 2 | text[--bi] << 10;
         code[--ci] = '=';
         code[--ci] = B[i & 0x3F];
         code[--ci] = B[(i >>> 6) & 0x3F];
         code[--ci] = B[(i >>> 12) & 0x3F];
         break;
      default: // 0
         code = new byte[ci];
      }

      while (ci > 00)
      {
         i = (text[--bi] & 0xFF) | (text[--bi] & 0xFF) << 8 | text[--bi] << 16;
         code[--ci] = B[i & 0x3F];
         code[--ci] = B[(i >>> 6) & 0x3F];
         code[--ci] = B[(i >>> 12) & 0x3F];
         code[--ci] = B[(i >>> 18) & 0x3F];
      }
      return code;
   }

   /** This will encode the text without line feeds added. It will write all output
    * to the dest buffer begining from dstart.<br>
    * Both src and dest may be the same provided (sstart<=dstart).
    * @param src
    * @param sstart where to start
    * @param len how long is the byte array
    * @param dest
    * @param dstart
    * @return index to the buffer byte ofter the text
    */
   public final static int encode(final byte[] src, final int sstart, final int len, final byte[] dest, final int dstart)
   {
      int bi = sstart + len;
      int ci = ((len / 3) << 2) + dstart;
      int dend;
      int i;
      switch (len % 3) { // length of padding
      case 1:
         ci += 4;
         dend = ci;
         i = src[--bi] << 4;
         dest[--ci] = '=';
         dest[--ci] = '=';
         dest[--ci] = B[i & 0x3F];
         dest[--ci] = B[(i >>> 6) & 0x3F];
         break;
      case 2:
         ci += 4;
         dend = ci;
         i = (src[--bi] & 0xFF) << 2 | src[--bi] << 10;
         dest[--ci] = '=';
         dest[--ci] = B[i & 0x3F];
         dest[--ci] = B[(i >>> 6) & 0x3F];
         dest[--ci] = B[(i >>> 12) & 0x3F];
         break;
      default:
         dend = ci;
      }

      while (ci > dstart)
      {
         i = (src[--bi] & 0xFF) | (src[--bi] & 0xFF) << 8 | src[--bi] << 16;
         dest[--ci] = B[i & 0x3F];
         dest[--ci] = B[(i >>> 6) & 0x3F];
         dest[--ci] = B[(i >>> 12) & 0x3F];
         dest[--ci] = B[(i >>> 18) & 0x3F];
      }
      return dend;
   }

   /** This will encode the text. The CRLF comes after <code>lb</code> characters.
    * @param text
    * @param llen the length of line (without CRLF) must be a multiple of 4
    * @return an encoded char array from the text
    */
   public final static char[] toCharArray(final byte[] text, final int llen)
   {
      final char[] code;
      int bi = text.length;
      int ci = (bi / 3) << 2;
      int i;
      //@goal determine padding
      switch (bi % 3) { // length of padding
      case 1: // @goal pad two text
         ci += 4;
         ci += (ci / llen) << 1;
         code = new char[ci];

         if (ci % llen == 4)
         {
            code[--ci] = '\n';
            code[--ci] = '\r';
         }
         i = text[--bi] << 4;
         code[--ci] = '=';
         code[--ci] = '=';
         code[--ci] = C[i & 0x3F];
         code[--ci] = C[(i >>> 6) & 0x3F];
         break;
      case 2: // @goal pad one byte
         ci += 4;
         ci += (ci / llen) << 1;
         code = new char[ci];

         if (ci % llen == 4)
         {
            code[--ci] = '\n';
            code[--ci] = '\r';
         }
         i = (text[--bi] & 0xFF) << 2 | text[--bi] << 10;
         code[--ci] = '=';
         code[--ci] = C[i & 0x3F];
         code[--ci] = C[(i >>> 6) & 0x3F];
         code[--ci] = C[(i >>> 12) & 0x3F];
         break;
      default: // 0
         ci += (ci / llen) << 1;
         code = new char[ci];
      }
      //@goal calculate the line breaking constant and variable
      int lmax = (llen >>> 2) - 1; // will break after this many quadruples
      int li = (ci % (llen + 2)) >>> 2; // initialize the counter

      while (bi > 00)
      {

         if (li > 0)
         {
            --li;

         }
         else
         {
            code[--ci] = '\n';
            code[--ci] = '\r';
            li = lmax;
         }
         i = (text[--bi] & 0xFF) | (text[--bi] & 0xFF) << 8 | text[--bi] << 16;
         code[--ci] = C[i & 0x3F];
         code[--ci] = C[(i >>> 6) & 0x3F];
         code[--ci] = C[(i >>> 12) & 0x3F];
         code[--ci] = C[(i >>> 18) & 0x3F];
      }
      return code;
   }

   /** This will encode the text. The CRLF comes after <code>lb</code> characters.
    * @param text
    * @param llen the length of line (without CRLF) must be a multiple of 4
    * @return an encoded byte array from the text
    */
   public final static byte[] encode(final byte[] text, final int llen)
   {
      final byte[] code;
      int bi = text.length;
      int ci = (bi / 3) << 2;
      int i;
      //@goal determine padding
      switch (bi % 3) { // length of padding
      case 1: // @goal pad two text
         ci += 4;
         ci += (ci / llen) << 1;
         code = new byte[ci];

         if (ci % llen == 4)
         {
            code[--ci] = '\n';
            code[--ci] = '\r';
         }
         i = text[--bi] << 4;
         code[--ci] = '=';
         code[--ci] = '=';
         code[--ci] = B[i & 0x3F];
         code[--ci] = B[(i >>> 6) & 0x3F];
         break;
      case 2: // @goal pad one byte
         ci += 4;
         ci += (ci / llen) << 1;
         code = new byte[ci];

         if (ci % llen == 4)
         {
            code[--ci] = '\n';
            code[--ci] = '\r';
         }
         i = (text[--bi] & 0xFF) << 2 | text[--bi] << 10;
         code[--ci] = '=';
         code[--ci] = B[i & 0x3F];
         code[--ci] = B[(i >>> 6) & 0x3F];
         code[--ci] = B[(i >>> 12) & 0x3F];
         break;
      default: // 0
         ci += (ci / llen) << 1;
         code = new byte[ci];
      }
      //@goal calculate the line breaking constant and variable
      int lmax = (llen >>> 2) - 1; // will break after this many quadruples
      int li = (ci % (llen + 2)) >>> 2; // initialize the counter

      while (ci > 00)
      {
         if (li > 0)
         {
            --li;
         }
         else
         {
            code[--ci] = '\n';
            code[--ci] = '\r';
            li = lmax;
         }
         i = (text[--bi] & 0xFF) | (text[--bi] & 0xFF) << 8 | text[--bi] << 16;
         code[--ci] = B[i & 0x3F];
         code[--ci] = B[(i >>> 6) & 0x3F];
         code[--ci] = B[(i >>> 12) & 0x3F];
         code[--ci] = B[(i >>> 18) & 0x3F];
      }
      return code;
   }

   //###################### DECODER #####################################
   /** This is a wrapper to <code>decode(final char[] code, final int from, final int len)</code>
    * @param code
    * @return a byte array holding encoded char data
    */
   public final static byte[] decode(final char[] code)
   {
      return decode(code, 0, code.length);
   }

   /** This will decode base64 data without line feeds.
    * The char array should be multiple of 4 in length.
    * @param code
    * @param from start position in code
    * @param len length of the part
    * @return the decoded sequence of text
    */
   public final static byte[] decode(final char[] code, final int from, final int len)
   {
      int bi = (len >>> 2) * 3;
      int ci = from + len;
      int i;
      final byte[] text;

      if (code[ci - 1] == '=')
      {
         if (code[ci - 2] == '=')
         { // padding 2
            ci -= 3;
            bi -= 2;
            text = new byte[bi];
            text[--bi] = (byte) ((I[code[ci]] | I[code[--ci]] << 6) >> 4);
         }
         else
         { // padding 1
            ci -= 2;
            --bi;
            text = new byte[bi];
            i = I[code[ci]] | I[code[--ci]] << 6 | I[code[--ci]] << 12;
            text[--bi] = (byte) (i >>> 2);
            text[--bi] = (byte) (i >>> 10);
         }

      }
      else
      {
         text = new byte[bi];
      }

      while (bi > 00)
      {
         i = I[code[--ci]] | I[code[--ci]] << 6 | I[code[--ci]] << 12 | I[code[--ci]] << 18;
         text[--bi] = (byte) (i);
         text[--bi] = (byte) (i >>> 8);
         text[--bi] = (byte) (i >>> 16);
      }
      return text;
   }
   
   /**
    * Decode without padding. 
    * @param code
    * @return a byte array holding encoded char data
    */
   public final static byte[] decodeNoPadding(byte[] code)
   {
	   if (code[code.length - 1] != '=' && code.length % 4 != 0)
	   {
		   if ((code.length + 2) % 4 == 0)
		   {
			   byte[] tmp = code;
			   code = new byte[code.length + 2];
			   System.arraycopy(tmp, 0, code, 0, tmp.length);
			   code[code.length - 2] = '=';
			   code[code.length - 1] = '=';
		   }
		   else
		   {
			   byte[] tmp = code;
			   code = new byte[code.length + 1];
			   System.arraycopy(tmp, 0, code, 0, tmp.length);
			   code[code.length - 1] = '=';
		   }
	   }
	   return decode(code);
   }

   /** 
    * This is a wrapper to <code>decode(final byte[] code, final int from, final int len)</code>
    * @param code
    * @return a byte array holding encoded char data
    */
   public final static byte[] decode(final byte[] code)
   {
      return decode(code, 0, code.length);
   }

   /** 
    * a copy of the char part
    * @param src
    * @param from
    * @param len
    * @return decoded byte array
    */
   public final static byte[] decode(final byte[] src, final int from, final int len)
   {
      int bi = (len >>> 2) * 3;
      int ci = from + len;
      int i;
      final byte[] text;

      if (src[ci - 1] == '=')
      {
         if (src[ci - 2] == '=')
         { // padding 2
            ci -= 3;
            bi -= 2;
            text = new byte[bi];
            text[--bi] = (byte) ((I[src[ci]] | I[src[--ci]] << 6) >> 4);
         }
         else
         { // padding 1
            ci -= 2;
            --bi;
            text = new byte[bi];
            i = I[src[ci]] | I[src[--ci]] << 6 | I[src[--ci]] << 12;
            text[--bi] = (byte) (i >>> 2);
            text[--bi] = (byte) (i >>> 10);
         }

      }
      else
      {
         text = new byte[bi];
      }

      while (bi > 00)
      {
         i = I[src[--ci]] | I[src[--ci]] << 6 | I[src[--ci]] << 12 | I[src[--ci]] << 18;
         text[--bi] = (byte) (i);
         text[--bi] = (byte) (i >>> 8);
         text[--bi] = (byte) (i >>> 16);
      }
      return text;
   }

   /** 
    * <code>decodeCRLF(code, 0, code.length, 76)</code>
    * @param code
    * @return the encoded text;
    */
   public final static byte[] decode76(final char[] code)
   {
      return decodeCRLF(code, 0, code.length, 76);
   }

   /** This will decode base64 data. The starting point and length must be accurate.
    * The data must end on a multiple of 4 boundary and must include the '=' padding, if any.
    * @param code
    * @param from
    * @param len the length of data
    * @param llen the line length of this base64 (without CRLF)
    * @return the decoded sequence of text
    */
   public final static byte[] decodeCRLF(final char[] code, final int from, final int len, final int llen)
   {
      int bi = ((len - ((len / (llen + 2)) << 1)) >>> 2) * 3;
      int ci = from + len;
      int lmax = (llen >>> 2);
      int li = (ci % (llen + 2)) >>> 2;
      int i;
      if (li == 0)
      { // skip crlf
         ci -= 2;
         li = lmax;
      }
      final byte[] text;

      if (code[ci - 1] == '=')
      {
         if (code[ci - 2] == '=')
         { // padding 2
            ci -= 3;
            bi -= 2;
            text = new byte[bi];
            text[--bi] = (byte) ((I[code[ci]] | I[code[--ci]] << 6) >> 4);
            --li;
         }
         else
         { // padding 1
            ci -= 2;
            --bi;
            text = new byte[bi];
            i = I[code[ci]] | I[code[--ci]] << 6 | I[code[--ci]] << 12;
            text[--bi] = (byte) (i >>> 2);
            text[--bi] = (byte) (i >>> 10);
            --li;
         }

      }
      else
      {
         text = new byte[bi];
      }

      while (bi > 00)
      {

         if (li == 0)
         {
            ci -= 2;
            li = lmax;
         }
         i = I[code[--ci]] | I[code[--ci]] << 6 | I[code[--ci]] << 12 | I[code[--ci]] << 18;
         text[--bi] = (byte) (i);
         text[--bi] = (byte) (i >>> 8);
         text[--bi] = (byte) (i >>> 16);
         --li;
      }
      return text;
   }

   /** 
    * This will decode base64 data with CRLF at 4 character boundary.<br>
    * The sequence may look like:<p>
    * ABCDABCD\r\nABCDABCD or even ABCDABCD#####ABCD###ABCD#ABCD.</p>
    * <p>
    * The array must be multiple of 4 + number of CRLF or illegal characters and 
    * the line length may vary.</p>
    * @param code
    * @param from
    * @param len
    * @return the decoded sequence of text
    */
   public final static byte[] decodeCRLF(final char[] code, final int from, final int len)
   {
      int bi = len;
      int ci = from + len;
      int i;
      //@goal determine the number of valid code

      while (ci > from)
      {
         if (I[code[--ci]] < 0) --bi;
      }
      //@goal allocate byte array
      bi = (bi >>> 2) * 3;
      final byte[] text = new byte[bi];
      //@goal decode the sequence
      ci = from + len;

      while (bi > 00)
      {
         do
            i = I[code[--ci]]; // look ahead
         while (i < 0);
         i |= I[code[--ci]] << 6 | I[code[--ci]] << 12 | I[code[--ci]] << 18;
         text[--bi] = (byte) (i);
         text[--bi] = (byte) (i >>> 8);
         text[--bi] = (byte) (i >>> 16);
      }
      return text;
   }

   /** 
    * @param code
    * @return the decoded array
    */
   public final static byte[] decodeFailSafe(char[] code)
   {
      return decodeFailSafe(code, 0, code.length);
   }

   /** 
    * This removes all bad characters from the char array. It modifies the array
    * in the scope of the process !!! Than simply calls decode;
    * @param code
    * @param from
    * @param len
    * @return decoded text
    */
   public final static byte[] decodeFailSafe(final char[] code, final int from, final int len)
   {
      char c;
      int ci = from;
      int cj = from;
      int ce = from + len;

      while (ci < ce)
      {
         c = code[ci];
         if (c == '=') break;

         try
         {
            if (I[c] >= 0) code[cj++] = c;
         }
         catch (Exception e)
         {/**/}
         ci++;
      }

      switch (((cj - from) & 0x3)) {
      case 1:
         code[cj++] = C[0];
      case 2:
         code[cj++] = '=';
      case 3:
         code[cj++] = '=';
      default:
      }
      return decode(code, from, cj - from);
   }

   //------------------ static fields -------------------

   private static final byte[] B = new byte[] { 
         65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 
         81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99,100,101,102,
        103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,
        119,120,121,122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47, };

   private static final char[] C = new char[] { 
        'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P',
        'Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f',
        'g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v',
        'w','x','y','z','0','1','2','3','4','5','6','7','8','9','+','/', };

   private static final int[]  I = new int[] { 
         -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
         -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
         -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63,
         52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1,  0, -1, -1,
         -1,  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 
         15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, 
         -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 
         41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1, };
}