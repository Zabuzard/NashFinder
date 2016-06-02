package de.tischner.nashfinder.game.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public final class ActionProfile<ACTION> {

	private final List<ACTION> mProfile;

	public ActionProfile() {
		mProfile = new LinkedList<>();
	}

	public boolean addAction(final ACTION action) {
		return mProfile.add(action);
	}

	public Iterator<ACTION> getActions() {
		return mProfile.iterator();
	}

	public int size() {
		return mProfile.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();

		result.append(mProfile);

		return result.toString();
	}

}
