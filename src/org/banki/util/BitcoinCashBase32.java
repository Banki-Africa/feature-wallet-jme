package org.banki.util;

public class BitcoinCashBase32
{
  public static final String CHARSET = "qpzry9x8gf2tvdw0s3jn54khce6mua7l";
  private static final char[] CHARS = "qpzry9x8gf2tvdw0s3jn54khce6mua7l".toCharArray();
  


  private static Map charPositionMap = new HashMap(32);
  static { for (int i = 0; i < CHARS.length; i++) {
      charPositionMap.put(new Character(CHARS[i]), new Integer(i));
    }
    if (charPositionMap.size() != 32)
      throw new RuntimeException("The charset must contain 32 unique characters.");
  }
  
  public BitcoinCashBase32() {}
  
  public static String encode(byte[] byteArray) { StringBuffer sb = new StringBuffer();
    
    for (int i = 0; i < byteArray.length; i++) {
      int val = byteArray[i];
      
      if ((val < 0) || (val > 31)) {
        throw new RuntimeException("This method assumes that all bytes are only from 0-31. Was: " + val);
      }
      sb.append(CHARS[val]);
    }
    return sb.toString();
  }
  
  public static byte[] decode(String base32String) {
    byte[] bytes = new byte[base32String.length()];
    
    char[] charArray = base32String.toCharArray();
    for (int i = 0; i < charArray.length; i++) {
      Integer position = (Integer)charPositionMap.get(new Character(charArray[i]));
      if (position == null) {
        throw new RuntimeException("There seems to be an invalid char: " + charArray[i]);
      }
      bytes[i] = ((byte)position.intValue());
    }
    return bytes;
  }
}
