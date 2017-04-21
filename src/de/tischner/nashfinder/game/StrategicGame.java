package de.tischner.nashfinder.game;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.tischner.nashfinder.game.util.ActionProfile;
import de.tischner.nashfinder.locale.ErrorMessages;

/**
 * Object that represents a strategic game where players have several actions
 * and get a payoff based on all players actions.
 * 
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 *
 * @param <PLAYER>
 *            Class of the players
 * @param <ACTION>
 *            Class of the actions
 */
public final class StrategicGame<PLAYER, ACTION> {

	/**
	 * Players of the game.
	 */
	private final Set<PLAYER> mPlayers;
	/**
	 * Data structure that allows a fast access to the actions of a given
	 * player.
	 */
	private final Map<PLAYER, Set<ACTION>> mPlayerToActions;
	/**
	 * Data structure that allows a fast access to the payoff for a given action
	 * profile.
	 */
	private final Map<ActionProfile<ACTION>, List<Integer>> mProfileToPayoff;

	/**
	 * Creates a new empty strategic game.
	 */
	public StrategicGame() {
		this.mPlayers = new LinkedHashSet<>();
		this.mPlayerToActions = new HashMap<>();
		this.mProfileToPayoff = new HashMap<>();
	}

	/**
	 * Adds a given action to the action set of a given player.
	 * 
	 * @param action
	 *            Action to add
	 * @param player
	 *            Player to add action to
	 * @return <tt>True</tt> if the players action set did not already contain
	 *         the specified action
	 */
	public boolean addAction(final ACTION action, final PLAYER player) {
		Set<ACTION> actions = this.mPlayerToActions.get(player);
		if (actions == null) {
			actions = new LinkedHashSet<>();
		}
		boolean wasAdded = actions.add(action);
		this.mPlayerToActions.put(player, actions);

		return wasAdded;
	}

	/**
	 * Adds a given payoff list for a given action profile. The list needs to be
	 * in the same order than the players where added by
	 * {@link #addPlayer(Object)}.
	 * 
	 * @param payoff
	 *            Payoff list to add
	 * @param actionProfile
	 *            Action profile to add the payoff list for
	 */
	public void addPayoff(final List<Integer> payoff, final ActionProfile<ACTION> actionProfile) {
		if (payoff == null || payoff.size() != this.mPlayers.size() || payoff.size() != actionProfile.size()) {
			throw new IllegalArgumentException(ErrorMessages.GAME_ADD_PAYOFF_ILLEGAL_PAYOFF);
		}
		this.mProfileToPayoff.put(actionProfile, payoff);
	}

	/**
	 * Adds a given player to the game.
	 * 
	 * @param player
	 *            Player to add
	 * @return <tt>True</tt> if the game did not already contain the specified
	 *         player
	 */
	public boolean addPlayer(final PLAYER player) {
		return this.mPlayers.add(player);
	}

	/**
	 * Gets the payoff list for a given action profile.
	 * 
	 * @param actionProfile
	 *            Action profile to get the payoff list for
	 * @return Payoff list for the given action profile
	 */
	public List<Integer> getPayoff(final ActionProfile<ACTION> actionProfile) {
		return this.mProfileToPayoff.get(actionProfile);
	}

	/**
	 * Gets the payoff for a given player in a given action profile
	 * 
	 * @param actionProfile
	 *            Action profile to get payoff for
	 * @param player
	 *            Player to get payoff for
	 * @return The payoff for the given player in the given action profile
	 */
	public int getPayoffForPlayer(final ActionProfile<ACTION> actionProfile, PLAYER player) {
		List<Integer> payoff = getPayoff(actionProfile);

		Iterator<PLAYER> playerIter = this.mPlayers.iterator();
		for (int singlePayoff : payoff) {
			if (playerIter.hasNext()) {
				PLAYER currentPlayer = playerIter.next();
				if (currentPlayer.equals(player)) {
					return singlePayoff;
				}
			}
		}
		throw new IllegalStateException(ErrorMessages.GAME_PAYOFF_NOT_FOUND);
	}

	/**
	 * Gets the set of actions for a given player.
	 * 
	 * @param player
	 *            Player to get actions for
	 * @return Set of actions for the given player
	 */
	public Set<ACTION> getPlayerActions(final PLAYER player) {
		return this.mPlayerToActions.get(player);
	}

	/**
	 * Gets all players of this game
	 * 
	 * @return All players of this game
	 */
	public Iterator<PLAYER> getPlayers() {
		return this.mPlayers.iterator();
	}

	/**
	 * Returns whether the game has a given player or not.
	 * 
	 * @param player
	 *            Player in question
	 * @return <tt>True</tt> if the game has the given player, <tt>false</tt>
	 *         otherwise
	 */
	public boolean hasPlayer(final PLAYER player) {
		return this.mPlayers.contains(player);
	}

	/**
	 * Returns whether the given player has a given action or not.
	 * 
	 * @param player
	 *            Player in question
	 * @param action
	 *            Action in question
	 * @return <tt>True</tt> if the player has the given action, <tt>false</tt>
	 *         otherwise
	 */
	public boolean hasPlayerAction(final PLAYER player, final ACTION action) {
		Set<ACTION> playerActions = this.mPlayerToActions.get(player);
		return playerActions != null && playerActions.contains(action);
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

		result.append("Players: " + this.mPlayers + separator);
		result.append("PlayerToActions:" + this.mPlayerToActions + separator);
		result.append("ProfileToPayoff: " + this.mProfileToPayoff);

		return result.toString();
	}
}
