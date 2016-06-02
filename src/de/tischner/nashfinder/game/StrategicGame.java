package de.tischner.nashfinder.game;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.tischner.nashfinder.game.util.ActionProfile;

/**
 * 
 * @author Daniel Tischner
 *
 * @param <PLAYER>
 * @param <ACTION>
 */
public final class StrategicGame<PLAYER, ACTION> {

	private final Set<PLAYER> mPlayers;
	private final Map<PLAYER, Set<ACTION>> mPlayerToActions;
	private final Map<ActionProfile<ACTION>, List<Integer>> mProfileToPayoff;

	public StrategicGame() {
		mPlayers = new LinkedHashSet<>();
		mPlayerToActions = new HashMap<>();
		mProfileToPayoff = new HashMap<>();
	}

	public boolean addAction(final ACTION action, final PLAYER player) {
		Set<ACTION> actions = mPlayerToActions.get(player);
		if (actions == null) {
			actions = new LinkedHashSet<>();
		}
		boolean wasAdded = actions.add(action);
		mPlayerToActions.put(player, actions);

		return wasAdded;
	}

	public void addPayoff(final List<Integer> payoff, final ActionProfile<ACTION> actionProfile) {
		if (payoff == null || payoff.size() != mPlayers.size() || payoff.size() != actionProfile.size()) {
			throw new IllegalArgumentException(
					"Could not add payoff. The size of payoff must be equal to the amount of players and the size of the action profile.");
		}
		mProfileToPayoff.put(actionProfile, payoff);
	}

	public boolean addPlayer(final PLAYER player) {
		return mPlayers.add(player);
	}

	public List<Integer> getPayoff(final ActionProfile<ACTION> actionProfile) {
		return mProfileToPayoff.get(actionProfile);
	}

	public int getPayoffForPlayer(final ActionProfile<ACTION> actionProfile, PLAYER player) {
		List<Integer> payoff = getPayoff(actionProfile);

		Iterator<PLAYER> playerIter = mPlayers.iterator();
		for (int singlePayoff : payoff) {
			if (playerIter.hasNext()) {
				PLAYER currentPlayer = playerIter.next();
				if (currentPlayer.equals(player)) {
					return singlePayoff;
				}
			}
		}

		throw new IllegalStateException(
				"Could not find the payoff for the given player. The internal structures may be corrupt.");
	}

	public Iterator<PLAYER> getPlayers() {
		return mPlayers.iterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String separator = System.lineSeparator();
		StringBuilder result = new StringBuilder();

		result.append("Players: " + mPlayers + separator);
		result.append("PlayerToActions:" + mPlayerToActions + separator);
		result.append("ProfileToPayoff: " + mProfileToPayoff);

		return result.toString();
	}
}
