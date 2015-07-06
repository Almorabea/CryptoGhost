package net.almorabea.cryptoghost;

public class Utility {

	   private static String digits = "0123456789abcdef";
	    
	    
	    public static String toHex(byte[] data , int length)
	    {
	        StringBuffer buf = new StringBuffer();
	        
	        for(int i =0 ; i != length ; i++)
	        {
	            int v = data[i] & 0xff ;
	            
	            buf.append(digits.charAt(v >> 4));
	            buf.append(digits.charAt(v & 0xf));
	            
	        }
	        return buf.toString();
	        
	    }
	    
	    
	    public static String toHex (byte[]data)
	    {
	        return toHex (data,data.length);
	    }
	    
	
	
}
