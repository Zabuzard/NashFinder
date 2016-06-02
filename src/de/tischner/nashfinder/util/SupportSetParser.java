package de.tischner.nashfinder.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.tischner.nashfinder.game.util.SupportSet;

/**
 * 
 * @author Daniel Tischner
 *
 */
public final class SupportSetParser {
	public static <PLAYER> SupportSet<PLAYER, String> parseSupportSet(final String supportSet, final PLAYER player) {
		if (supportSet == null || supportSet.length() == 0 || player == null) {
			return null;
		}

		SupportSet<PLAYER, String> supportSetToReturn = new SupportSet<>(player);
		String[] actions = supportSet.split(",");
		for (String action : actions) {
			supportSetToReturn.addAction(action.trim());
		}

		return supportSetToReturn;
	}

	public static <PLAYER> List<SupportSet<PLAYER, String>> parseSupportSets(final String supportSets,
			final Iterator<PLAYER> players) {
		List<SupportSet<PLAYER, String>> supportSetsAsList = new LinkedList<>();
		if (supportSets == null || supportSets.length() == 0 || players == null) {
			return supportSetsAsList;
		}

		Pattern pattern = Pattern.compile("\\[(.*?)\\]");
		Matcher matcher = pattern.matcher(supportSets.trim());
		while (matcher.find()) {
			String supportSetAsText = matcher.group(1).trim();
			if (!players.hasNext()) {
				throw new IllegalArgumentException(
						"Can not parse support sets. The size of players does not match the amount of found support sets. But every support set needs to be assigned to a player.");
			}
			PLAYER player = players.next();
			SupportSet<PLAYER, String> supportSet = parseSupportSet(supportSetAsText, player);
			if (supportSet == null) {
				throw new IllegalArgumentException(
						"Can not parse support sets. The following support set may be in the wrong format: "
								+ supportSetAsText);
			}
			supportSetsAsList.add(supportSet);
		}
		if (players.hasNext()) {
			System.out.println(supportSetsAsList);
			System.out.println(supportSets);
			System.out.println();
			System.out.flush();
			System.err.flush();
			throw new IllegalArgumentException(
					"The size of players does not match the amount of found support sets. There are some players left that have no support sets assigned to.");
		}

		return supportSetsAsList;
	}

	/**
	 * Utility class. No implementation.
	 */
	private SupportSetParser() {

	}
}
