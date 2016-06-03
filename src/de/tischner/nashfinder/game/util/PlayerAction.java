package de.tischner.nashfinder.game.util;

/**
 * Associates a given action to a given player.
 * 
 * @author Daniel Tischner
 *
 * @param <PLAYER>
 *            The class of players
 * @param <ACTION>
 *            The class of actions
 */
public final class PlayerAction<PLAYER, ACTION> {

	/**
	 * Action to associate.
	 */
	private final ACTION mAction;
	/**
	 * Player to associate.
	 */
	private final PLAYER mPlayer;

	/**
	 * Associates a given player to a given action.
	 * 
	 * @param player
	 *            Player to associate
	 * @param action
	 *            Action to associate
	 */
	public PlayerAction(final PLAYER player, final ACTION action) {
		mPlayer = player;
		mAction = action;
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
		if (!(obj instanceof PlayerAction)) {
			return false;
		}
		PlayerAction<?, ?> other = (PlayerAction<?, ?>) obj;
		if (mAction == null) {
			if (other.mAction != null) {
				return false;
			}
		} else if (!mAction.equals(other.mAction)) {
			return false;
		}
		if (mPlayer == null) {
			if (other.mPlayer != null) {
				return false;
			}
		} else if (!mPlayer.equals(other.mPlayer)) {
			return false;
		}
		return true;
	}

	/**
	 * Gets the associated action.
	 * 
	 * @return The associated action
	 */
	public ACTION getAction() {
		return mAction;
	}

	/**
	 * Gets the associated player.
	 * 
	 * @return The associated player
	 */
	public PLAYER getPlayer() {
		return mPlayer;
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
		result = prime * result + ((mAction == null) ? 0 : mAction.hashCode());
		result = prime * result + ((mPlayer == null) ? 0 : mPlayer.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return mPlayer + ":" + mAction;
	}
}
