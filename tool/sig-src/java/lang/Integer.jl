package java.lang;

public final class Integer extends Number implements Comparable {

	private int value;

	// Obviously ridiculous way to get the input to flow to the field
	public Integer(String integerConstructorArgString) throws NumberFormatException {
		if(integerConstructorArgString == null){
			throw new NumberFormatException("null");
		}
		value = integerConstructorArgString.length();
	}
	
	public Integer(int integerConstructorArgInt){
		value = integerConstructorArgInt;
	}

	public int intValue(){
		return value;
	}
	
	public int compareTo(Object another){
		Integer anotherInteger = (Integer)another;
		int thisVal = this.value;
		int anotherVal = anotherInteger.value;
		return (thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));
    }
    
    public long longValue(){
    	return (long)value;
    }
    
    public float floatValue(){
    	return (float)value;
    }
    
    public double doubleValue(){
    	return (double)value;
    }
    
    public static Integer valueOf(int integerValue){
    	return new Integer(integerValue);
    }

    public static String toHexString(int i) {
	return "x: " + i;
    }
}
