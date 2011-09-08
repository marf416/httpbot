package http;

/**
 * Stores two objects of any kind
 * 
 * @author Marvin Huber
 *
 * @version 1.00
 */
public class Tupel extends Object{

	private Object o1 = null;
	private Object o2 = null;
	
	/**
	 * Creates a new Tupel
	 * 
	 * @param o1 - First object to be stored
	 * @param o2 - Second object to be stored
	 */
	public Tupel (Object o1, Object o2) {
		this.o1 = o1;
		this.o2 = o2;
	}
	
	/**
	 * Overwrite first object with another
	 * 
	 * @param o - Object that will hence be stored as first object
	 */
	public void setFirst (Object o) {
		this.o1 = o;
	}
	
	/**
	 * Overwrite second object with another
	 * 
	 * @param o - Object that will hence be stored as second object
	 */
	public void setSecond (Object o) {
		this.o2 = o;	
	}
	
	/**
	 * Read the first stored object
	 * 
	 * @return First object that is stored
	 */
	public Object first () {
		return o1;
	}
	
	/**
	 * Read the second stored object
	 * 
	 * @return Second object that is stored
	 */
	public Object second () {
		return o2;
	}
	
	/**
	 * Compare type of an other object with this Tupelobject
	 * 
	 * @param o - Any object that should be compared to this Tupelobject
	 * 
	 * @return True if the given parameter was a Tupel of the same types as this Tupelobject, otherwise False
	 */
	public boolean equals (Object o) {
		if( ! (o instanceof Tupel)) {
			return false;
		}
		Tupel obj = (Tupel) o;
		if ((o1.getClass()==obj.first().getClass()) && (o2.getClass()==obj.second().getClass())) {
			return true;
		}
		return false;
	}
	
	/**
	 * Concatenate stored objects as one String
	 * 
	 * @return First and second stored object as String, concatenated with "="
	 */
	public String toString () {
		return o1+"="+o2;
	}
	
}
