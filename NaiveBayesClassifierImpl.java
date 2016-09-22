import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Your implementation of a naive bayes classifier. Please implement all four methods.
 */

public class NaiveBayesClassifierImpl implements NaiveBayesClassifier {

	private final double DELTA = 0.00001; // delta for smoothing

	// data members
	private int v = -1; // size of vocabulary, given in train
	private int numHam = 0; // number of ham messages
	private int numSpam = 0; // number of spam messages
	private int numTokensHam = 0; // number of word tokens from spam
	private int numTokensSpam = 0; // number of word tokens from ham
	// dictionary of word counts for ham and spam
	private Map<String,SHCount> counts = new HashMap<String,SHCount>();

	/**
	 * Trains the classifier with the provided training data and vocabulary size
	 */
	@Override
	public void train(Instance[] trainingData, int v) {
		this.v = v;

		// add training data
		for(Instance inst : trainingData) {
			addInst(inst);
		}
	}

	/**
	 * Adds word tokens from a message to the dictionary of counts
	 * @param inst the message to add
	 */
	private void addInst(Instance inst) {

		if(inst.label == Label.HAM) {
			// instance is a ham example
			numHam++;

			for(String token : inst.words) {
				SHCount count = counts.get(token);
				if(count != null) {
					// word type already in dictionary
					count.incHam();
				} else {
					// add word type to dictionary
					count = new SHCount();
					count.incHam();
					counts.put(token, count);
				}
				numTokensHam++;
			}
		} else {
			// instance is a spam example
			numSpam++;

			for(String token : inst.words) {
				SHCount count = counts.get(token);
				if(count != null) {
					// word type already in dictionary
					count.incSpam();
				} else {
					// add word type to dictionary
					count = new SHCount();
					count.incSpam();
					counts.put(token, count);
				}
				numTokensSpam++;
			}
		}
	}

	/**
	 * Returns the prior probability of the label parameter, i.e. P(SPAM) or P(HAM)
	 */
	@Override
	public double p_l(Label label) {
		double total = (double) numHam + (double) numSpam;
		if(label == Label.HAM) {
			return (double) numHam / total;
		} else {
			return (double) numSpam / total;
		}
	}

	/**
	 * Returns the smoothed conditional probability of the word given the label,
	 * i.e. P(word|SPAM) or P(word|HAM)
	 */
	@Override
	public double p_w_given_l(String word, Label label) {

		// find count of the specified word for the label
		SHCount data = counts.get(word);
		double count;
		double total = 0;
		try {
			if(label == Label.HAM) {
				total = numTokensHam;
				count = data.getHam();
			} else {
				total = numTokensSpam;
				count = data.getSpam();
			}
		} catch(NullPointerException e) {
			count = 0;
		}

		// calculate conditional probability with smoothing
		return (count + DELTA) / (total + (v * DELTA));
	}

	/**
	 * Classifies an array of words as either SPAM or HAM. 
	 */
	@Override
	public Label classify(String[] words) {
		
		// find prior probability for initialization
		double spamSum = Math.log(p_l(Label.SPAM));
		double hamSum = Math.log(p_l(Label.HAM));
		
		// add conditional probabilities of each word
		for(String str : words) {
			spamSum += Math.log(p_w_given_l(str,Label.SPAM));
			hamSum += Math.log(p_w_given_l(str,Label.HAM));
		}
		
		// return largest
		if(spamSum > hamSum) {
			return Label.SPAM;
		} else {
			return Label.HAM;
		}
	}

	/**
	 * Print out 5 most informative words.
	 */
	public void show_informative_5words() {
		
		// add all word types to a list and sort by informativeness
		List<String> informativeRank = new ArrayList<String>();
		informativeRank.addAll(counts.keySet());
		Collections.sort(informativeRank, informativeCompare);
		Collections.reverse(informativeRank); // descending order
		
		// print results
		for(int i = 0; i < 5; i++) {
		System.out.println(informativeRank.get(i));
		}
	}
	
	/**
	 * Comparator to use in comparing informativeness of a string by the ratio
	 * of the smoothed conditional probability of the word given label spam
	 * versus label ham
	 */
	public Comparator<String> informativeCompare = new Comparator<String>() {

		/**
		 * Compares informativeness of each word
		 * @param str1 first string
		 * @param str2 second string
		 * @return a negative integer, zero, or a positive integer if the
		 * informativeness of the first string is less than, equal, or greater
		 * than the informativeness of the second string
		 */
		@Override
		public int compare(String str1, String str2) {
			
			// find conditional probability ratio given spam versus ham for
			// both words
			double ratio1 = 
				p_w_given_l(str1, Label.HAM) / p_w_given_l(str1, Label.SPAM);
			double ratio2 = 
				p_w_given_l(str2, Label.HAM) / p_w_given_l(str2, Label.SPAM);
			
			// find max of each and its inverse
			ratio1 = Math.max(ratio1, 1/ratio1);
			ratio2 = Math.max(ratio2, 1/ratio2);
			
			return Double.compare(ratio1, ratio2);
		}
		
	};

}
