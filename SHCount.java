
/**
 * Object to keep ham and spam counts for a unique word
 * @author clzfun
 *
 */
public class SHCount {
	
	private int numHam; // count for ham
	private int numSpam; // count for spam
	
	/**
	 * Returns count for ham
	 * @return the count
	 */
	public int getHam() {
		return numHam;
	}
	
	/**
	 * Increment ham count by one
	 */
	public void incHam() {
		this.numHam++;
	}
	
	/**
	 * Returns count for spam
	 * @return the count
	 */
	public int getSpam() {
		return numSpam;
	}
	
	/**
	 * Increments spam count by one
	 */
	public void incSpam() {
		this.numSpam++;
	}

}
