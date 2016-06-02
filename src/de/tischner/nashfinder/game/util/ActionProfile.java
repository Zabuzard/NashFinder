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
		if (!(obj instanceof ActionProfile)) {
			return false;
		}
		ActionProfile<?> other = (ActionProfile<?>) obj;
		if (mProfile == null) {
			if (other.mProfile != null) {
				return false;
			}
		} else if (!mProfile.equals(other.mProfile)) {
			return false;
		}
		return true;
	}

	public Iterator<ACTION> getActions() {
		return mProfile.iterator();
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
		result = prime * result + ((mProfile == null) ? 0 : mProfile.hashCode());
		return result;
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
