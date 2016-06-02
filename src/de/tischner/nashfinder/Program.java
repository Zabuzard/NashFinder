package de.tischner.nashfinder;

/**
 * 
 * @author Daniel Tischner
 *
 */
public final class Program {

	/**
	 * 
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// The first argument is not optional and specifies the game file to use
		// for computation
		if (args == null || args.length < 1) {
			throw new IllegalArgumentException(
					"Wrong number of arguments. The first argument is not optional and specifies the game file to use for computation.");
		}
		// The second argument is optional and may specify a specific support
		// sets to use for computation
		boolean useSpecificSupportSets = args.length >= 2;
		String specificSupportSets = null;
		if (useSpecificSupportSets) {
			specificSupportSets = args[1];
		}
		String gameFileName = args[0];

		NashFinder nashFinder = null;
		if (useSpecificSupportSets) {
			nashFinder = new NashFinder(gameFileName, specificSupportSets);
		} else {
			nashFinder = new NashFinder(gameFileName);
		}

		nashFinder.computeNashEquilibria();
		nashFinder.printResults();
	}

	/**
	 * Utility class. No implementation.
	 */
	private Program() {

	}
}
