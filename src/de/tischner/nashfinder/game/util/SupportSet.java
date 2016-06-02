package de.tischner.nashfinder.game.util;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 
 * @author Daniel Tischner
 *
 * @param <PLAYER>
 * @param <ACTION>
 */
public final class SupportSet<PLAYER, ACTION> {

	private final Set<ACTION> mActions;
	private final PLAYER mPlayer;

	public SupportSet(final PLAYER player) {
		mPlayer = player;
		mActions = new LinkedHashSet<ACTION>();
	}

	public boolean addAction(final ACTION action) {
		return mActions.add(action);
	}

	public Iterator<ACTION> getActions() {
		return mActions.iterator();
	}

	public PLAYER getPlayer() {
		return mPlayer;
	}

	public boolean hasAction(final ACTION action) {
		return mActions.contains(action);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();

		result.append("Player: " + mPlayer);
		result.append(", Actions: " + mActions);

		return result.toString();
	}
}
