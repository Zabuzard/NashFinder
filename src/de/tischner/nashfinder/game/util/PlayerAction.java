package de.tischner.nashfinder.game.util;

/**
 * Associates a given action to a given player.
 * 
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
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
		this.mPlayer = player;
		this.mAction = action;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof PlayerAction)) {
			return false;
		}
		final PlayerAction<?, ?> other = (PlayerAction<?, ?>) obj;
		if (this.mAction == null) {
			if (other.mAction != null) {
				return false;
			}
		} else if (!this.mAction.equals(other.mAction)) {
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
	 * Gets the associated action.
	 * 
	 * @return The associated action
	 */
	public ACTION getAction() {
		return this.mAction;
	}

	/**
	 * Gets the associated player.
	 * 
	 * @return The associated player
	 */
	public PLAYER getPlayer() {
		return this.mPlayer;
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
		result = prime * result + ((this.mAction == null) ? 0 : this.mAction.hashCode());
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
		return this.mPlayer + ":" + this.mAction;
	}
}
