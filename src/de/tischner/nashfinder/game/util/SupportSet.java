package de.tischner.nashfinder.game.util;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Specifies a set of actions a given player has access to.
 * 
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 *
 * @param <PLAYER>
 *            Class of players
 * @param <ACTION>
 *            Class of actions
 */
public final class SupportSet<PLAYER, ACTION> {

	/**
	 * The set of actions the given player has access to.
	 */
	private final Set<ACTION> mActions;
	/**
	 * The player that has access to this support set.
	 */
	private final PLAYER mPlayer;

	/**
	 * Creates a new empty support set for a given player.
	 * 
	 * @param player
	 *            Player to create the support set for
	 */
	public SupportSet(final PLAYER player) {
		this(player, null);
	}

	/**
	 * Creates a new support set for a given player with a given set of actions.
	 * 
	 * @param player
	 *            Player to create the support set for
	 * @param actions
	 *            Set of actions the player has access to
	 */
	public SupportSet(final PLAYER player, final Set<ACTION> actions) {
		this.mPlayer = player;
		this.mActions = new LinkedHashSet<>();
		if (actions != null) {
			for (ACTION action : actions) {
				addAction(action);
			}
		}
	}

	/**
	 * Adds a given action to the set of actions, the player has access to.
	 * 
	 * @param action
	 *            Action to add
	 * @return <tt>True</tt> if this support set did not already contain the
	 *         specified action
	 */
	public boolean addAction(final ACTION action) {
		return this.mActions.add(action);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof SupportSet)) {
			return false;
		}
		SupportSet<?, ?> other = (SupportSet<?, ?>) obj;
		if (this.mActions == null) {
			if (other.mActions != null) {
				return false;
			}
		} else if (!this.mActions.equals(other.mActions)) {
			return false;
		}
		if (this.mPlayer == null) {
			if (other.mPlayer != null) {
				return false;
			}
		} else if (!this.mPlayer.equals(other.mPlayer)) {
			return false;
		}
		return true;
	}

	/**
	 * Gets all actions the player of this support set has access to.
	 * 
	 * @return All actions the player of this support set has access to
	 */
	public Iterator<ACTION> getActions() {
		return this.mActions.iterator();
	}

	/**
	 * Gets the player of this support set.
	 * 
	 * @return The player of this support set
	 */
	public PLAYER getPlayer() {
		return this.mPlayer;
	}

	/**
	 * Returns whether the support set contains the given action.
	 * 
	 * @param action
	 *            Action in question
	 * @return <tt>True</tt> if the support set contains the given action,
	 *         <tt>false</tt> otherwise.
	 */
	public boolean hasAction(final ACTION action) {
		return this.mActions.contains(action);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.mActions == null) ? 0 : this.mActions.hashCode());
		result = prime * result + ((this.mPlayer == null) ? 0 : this.mPlayer.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.mPlayer + ": " + this.mActions;
	}
}
