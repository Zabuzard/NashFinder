package de.zabuza.nashfinder;

import java.util.List;

import de.zabuza.nashfinder.game.StrategicGame;
import de.zabuza.nashfinder.game.util.SupportSet;
import de.zabuza.nashfinder.util.StrategicGameParser;
import de.zabuza.nashfinder.util.SupportSetParser;

/**
 * 
 * @author Daniel Tischner
 *
 */
public final class NashFinder {

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
	}

	private final StrategicGame<String, String> mGame;
	private final List<SupportSet<String, String>> mSpecificSupportSets;
	private final boolean mUseSpecificSupportSets;

	public NashFinder(final String gameName) {
		this(gameName, null);
	}

	public NashFinder(final String gameFileName, final String specificSupportSets) {
		mUseSpecificSupportSets = specificSupportSets != null && specificSupportSets.length() != 0;

		mGame = StrategicGameParser.parseStrategicGameJson(gameFileName);

		if (mUseSpecificSupportSets) {
			mSpecificSupportSets = SupportSetParser.parseSupportSets(specificSupportSets, mGame.getPlayers());
		} else {
			mSpecificSupportSets = null;
		}
	}

	public void computeNashEquilibria() {
		System.out.println(mGame);
		System.out.println();
		System.out.println(mUseSpecificSupportSets);
		System.out.println(mSpecificSupportSets);
	}
}
