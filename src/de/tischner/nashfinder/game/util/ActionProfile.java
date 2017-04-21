package de.tischner.nashfinder.game.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * An action profile specifies an ordered list of actions. This may be used to
 * represent the actions of a list of players, for assigning it to a specific
 * payoff.
 * 
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 *
 * @param <ACTION>
 *            Class for actions
 */
public final class ActionProfile<ACTION> {

	/**
	 * The action profile as list of actions.
	 */
	private final List<ACTION> mProfile;

	/**
	 * Creates a new empty action profile.
	 */
	public ActionProfile() {
		this.mProfile = new LinkedList<>();
	}

	/**
	 * Adds a given action to the profile.
	 * 
	 * @param action
	 *            Action to add.
	 * @return <tt>True</tt> (as specified by {@link Collection#add(Object)}).
	 */
	public boolean addAction(final ACTION action) {
		return this.mProfile.add(action);
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
		if (this.mProfile == null) {
			if (other.mProfile != null) {
				return false;
			}
		} else if (!this.mProfile.equals(other.mProfile)) {
			return false;
		}
		return true;
	}

	/**
	 * Gets all actions of this profile in the order they where added.
	 * 
	 * @return All actions of this profile in the order they where added
	 */
	public Iterator<ACTION> getActions() {
		return this.mProfile.iterator();
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
		result = prime * result + ((this.mProfile == null) ? 0 : this.mProfile.hashCode());
		return result;
	}

	/**
	 * Gets the size of this profile, which is the amount of actions.
	 * 
	 * @return The size of this profile, which is the amount of actions
	 */
	public int size() {
		return this.mProfile.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();

		result.append(this.mProfile);

		return result.toString();
	}
}
