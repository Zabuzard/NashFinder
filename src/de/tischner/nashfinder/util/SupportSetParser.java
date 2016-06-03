package de.tischner.nashfinder.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.tischner.nashfinder.game.util.SupportSet;
import de.tischner.nashfinder.locale.ErrorMessages;

/**
 * Utility class that provides methods for parsing
 * {@link de.tischner.nashfinder.game.util.SupportSet SupportSets} from various
 * sources.
 * 
 * @author Daniel Tischner
 *
 */
public final class SupportSetParser {

	/**
	 * Separator that separates actions in a support set string format.
	 */
	private static final String SUPPORT_SET_ACTION_SEPARATOR = ",";
	/**
	 * Needle that non-greedy matches one support set in a list of support sets,
	 * given in the string format. The support set is accessible by the first
	 * grouping.
	 */
	private static final String SUPPORT_SETS_NEEDLE = "\\[(.*?)\\]";

	/**
	 * Parses a support set of a given string for a given player.
	 * 
	 * @param supportSet
	 *            The support set to parse. The format is <tt>H, T</tt>, where
	 *            the given player has access to the actions <tt>H</tt> and
	 *            <tt>T</tt>.
	 * @param player
	 *            Player to assign the support set to
	 * @return The parsed support set
	 */
	public static <PLAYER> SupportSet<PLAYER, String> parseSupportSet(final String supportSet, final PLAYER player) {
		if (supportSet == null || supportSet.isEmpty() || player == null) {
			return null;
		}

		SupportSet<PLAYER, String> supportSetToReturn = new SupportSet<>(player);
		String[] actions = supportSet.split(SUPPORT_SET_ACTION_SEPARATOR);
		for (String action : actions) {
			supportSetToReturn.addAction(action.trim());
		}

		return supportSetToReturn;
	}

	/**
	 * Parses support sets of a given string by assigning a support set to every
	 * player in the given iterator.
	 * 
	 * @param supportSets
	 *            Support sets for all players. The format is <tt>[H,T][T]</tt>,
	 *            where every player has the given actions. For example, player
	 *            1 has actions <tt>H</tt> and <tt>T</tt>, whereas player 2 only
	 *            has action <tt>T</tt>.
	 * @param players
	 *            Players to assign the sets to
	 * @return A list of support sets in the same order than the given player
	 *         iterator
	 */
	public static <PLAYER> List<SupportSet<PLAYER, String>> parseSupportSets(final String supportSets,
			final Iterator<PLAYER> players) {
		List<SupportSet<PLAYER, String>> supportSetsAsList = new LinkedList<>();
		if (supportSets == null || supportSets.isEmpty() || players == null) {
			return supportSetsAsList;
		}

		Pattern pattern = Pattern.compile(SUPPORT_SETS_NEEDLE);
		Matcher matcher = pattern.matcher(supportSets.trim());
		while (matcher.find()) {
			String supportSetAsText = matcher.group(1).trim();
			if (!players.hasNext()) {
				throw new IllegalArgumentException(ErrorMessages.SUPPORT_SET_PARSE_ERROR_PLAYER_SIZE);
			}
			PLAYER player = players.next();
			SupportSet<PLAYER, String> supportSet = parseSupportSet(supportSetAsText, player);
			if (supportSet == null) {
				throw new IllegalArgumentException(
						ErrorMessages.SUPPORT_SET_PARSE_ERROR_FORMAT + " Got: " + supportSetAsText);
			}
			supportSetsAsList.add(supportSet);
		}
		if (players.hasNext()) {
			throw new IllegalArgumentException(ErrorMessages.SUPPORT_SET_PARSE_ERROR_PLAYER_SIZE);
		}

		return supportSetsAsList;
	}

	/**
	 * Utility class. No implementation.
	 */
	private SupportSetParser() {

	}
}
