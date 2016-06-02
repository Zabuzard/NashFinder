package de.tischner.nashfinder;

import java.util.List;

import de.tischner.nashfinder.game.StrategicGame;
import de.tischner.nashfinder.game.util.SupportSet;
import de.tischner.nashfinder.util.StrategicGameParser;
import de.tischner.nashfinder.util.SupportSetParser;

/**
 * 
 * @author Daniel Tischner
 *
 */
public final class NashFinder {
	
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
