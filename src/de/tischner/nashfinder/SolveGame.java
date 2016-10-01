package de.tischner.nashfinder;

import de.tischner.nashfinder.locale.ErrorMessages;

/**
 * Command line program that solves strategic games by solving <i>linear
 * complementary problems</i> (LCP).
 * 
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class SolveGame {

	/**
	 * Argument index of the game file to solve.
	 */
	private static final int GAME_FILE_ARG_INDEX = 0;
	/**
	 * Maximal length of arguments that are accepted.
	 */
	private static final int MAX_ARG_LENGTH = 2;
	/**
	 * Length of arguments that are required.
	 */
	private static final int REQUIRED_ARG_LENGTH = 1;
	/**
	 * Argument index of the specific support sets to solve the game for.
	 */
	private static final int SUPPORT_SETS_ARG_INDEX = 1;

	/**
	 * Starts the command line program for solving strategic games.
	 * 
	 * @param args
	 *            The first argument is the game to solve, as path to a
	 *            json-file. The second argument is optional and specifies a
	 *            specific support sets to solve the game. If not given, the
	 *            game is solved for all possible support set combinations.<br/>
	 *            <br/>
	 *            An example call would be:
	 *            <tt>java SolveGame matching-pennies.json
	 *            "[H,T][T]"</tt>
	 */
	public static void main(final String[] args) {
		// The first argument is not optional and specifies the game file to use
		// for computation
		if (args == null || args.length < REQUIRED_ARG_LENGTH) {
			throw new IllegalArgumentException(ErrorMessages.SOLVE_GAME_WRONG_ARGUMENT_NUMBER);
		}
		// The second argument is optional and may specify a specific support
		// sets to use for computation
		boolean useSpecificSupportSets = args.length >= MAX_ARG_LENGTH;
		String specificSupportSets = null;
		if (useSpecificSupportSets) {
			specificSupportSets = args[SUPPORT_SETS_ARG_INDEX];
		}
		String gameFileName = args[GAME_FILE_ARG_INDEX];

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
	private SolveGame() {

	}
}
