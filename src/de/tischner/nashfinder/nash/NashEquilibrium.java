package de.tischner.nashfinder.nash;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.tischner.nashfinder.game.StrategicGame;
import de.tischner.nashfinder.game.util.PlayerAction;
import de.tischner.nashfinder.util.EExpectedUtilty;
import de.tischner.nashfinder.util.MathUtil;
import net.sf.javailp.Result;

/**
 * 
 * @author Daniel Tischner
 *
 * @param <PLAYER>
 * @param <ACTION>
 */
public final class NashEquilibrium<PLAYER, ACTION> {

	public static <PLAYER, ACTION> NashEquilibrium<PLAYER, ACTION> extractFromLcpResults(final Result result,
			final StrategicGame<PLAYER, ACTION> game) {
		// If there is no result, there is no nash equilibrium
		if (result == null) {
			return null;
		}
		NashEquilibrium<PLAYER, ACTION> nashEquilibrium = new NashEquilibrium<>();

		PLAYER firstPlayer = null;
		PLAYER secondPlayer = null;
		Set<ACTION> firstPlayerActions = null;
		Set<ACTION> secondPlayerActions = null;

		Iterator<PLAYER> playerIter = game.getPlayers();
		if (playerIter.hasNext()) {
			firstPlayer = playerIter.next();
			if (playerIter.hasNext()) {
				secondPlayer = playerIter.next();

				firstPlayerActions = game.getPlayerActions(firstPlayer);
				secondPlayerActions = game.getPlayerActions(secondPlayer);
			}
		}
		if (firstPlayerActions == null || secondPlayerActions == null || firstPlayer == null || secondPlayer == null) {
			throw new IllegalArgumentException("Could not extract results. The given game may be corrupt.");
		}

		// Extract utility
		nashEquilibrium.setExpectedUtilityForPlayer(firstPlayer,
				MathUtil.roundNumberTo(result.getPrimalValue(EExpectedUtilty.FIRST_PLAYER), 2));
		nashEquilibrium.setExpectedUtilityForPlayer(secondPlayer,
				MathUtil.roundNumberTo(result.getPrimalValue(EExpectedUtilty.SECOND_PLAYER), 2));

		// Extract nash strategies
		NashStrategy<ACTION> firstPlayerStrategy = extractPlayerNashStrategyFromLcpResults(result, game, firstPlayer,
				firstPlayerActions);
		nashEquilibrium.setNashStrategyForPlayer(firstPlayer, firstPlayerStrategy);
		NashStrategy<ACTION> secondPlayerStrategy = extractPlayerNashStrategyFromLcpResults(result, game, secondPlayer,
				secondPlayerActions);
		nashEquilibrium.setNashStrategyForPlayer(secondPlayer, secondPlayerStrategy);

		return nashEquilibrium;
	}

	private static <PLAYER, ACTION> NashStrategy<ACTION> extractPlayerNashStrategyFromLcpResults(final Result result,
			final StrategicGame<PLAYER, ACTION> game, final PLAYER player, final Set<ACTION> playerActions) {
		NashStrategy<ACTION> nashStrategy = new NashStrategy<>();
		for (ACTION action : playerActions) {
			PlayerAction<PLAYER, ACTION> playerAction = new PlayerAction<>(player, action);
			Number probability = result.getPrimalValue(playerAction);
			if (probability != null) {
				nashStrategy.addAction(action, MathUtil.roundNumberTo(probability, 2));
			}
		}
		return nashStrategy;
	}

	private final Map<PLAYER, NashStrategy<ACTION>> mPlayerToStrategy;

	private final Map<PLAYER, Number> mPlayerToUtility;

	public NashEquilibrium() {
		mPlayerToStrategy = new LinkedHashMap<>();
		mPlayerToUtility = new LinkedHashMap<>();
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
		if (!(obj instanceof NashEquilibrium)) {
			return false;
		}
		NashEquilibrium<?, ?> other = (NashEquilibrium<?, ?>) obj;
		if (mPlayerToStrategy == null) {
			if (other.mPlayerToStrategy != null) {
				return false;
			}
		} else if (!mPlayerToStrategy.equals(other.mPlayerToStrategy)) {
			return false;
		}
		if (mPlayerToUtility == null) {
			if (other.mPlayerToUtility != null) {
				return false;
			}
		} else if (!mPlayerToUtility.equals(other.mPlayerToUtility)) {
			return false;
		}
		return true;
	}

	public Number getExpectedUtilityOfPlayer(final PLAYER player) {
		return mPlayerToUtility.get(player);
	}

	public NashStrategy<ACTION> getNashStrategyOfPlayer(final PLAYER player) {
		return mPlayerToStrategy.get(player);
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
		result = prime * result + ((mPlayerToStrategy == null) ? 0 : mPlayerToStrategy.hashCode());
		result = prime * result + ((mPlayerToUtility == null) ? 0 : mPlayerToUtility.hashCode());
		return result;
	}

	public void setExpectedUtilityForPlayer(final PLAYER player, final Number expectedUtility) {
		mPlayerToUtility.put(player, expectedUtility);
	}

	public void setNashStrategyForPlayer(final PLAYER player, final NashStrategy<ACTION> strategy) {
		mPlayerToStrategy.put(player, strategy);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		String lineSeparator = System.lineSeparator();

		boolean isFirstEntry = true;
		for (Entry<PLAYER, NashStrategy<ACTION>> entry : mPlayerToStrategy.entrySet()) {
			Number utility = mPlayerToUtility.get(entry.getKey());
			if (isFirstEntry) {
				isFirstEntry = false;
			} else {
				result.append(lineSeparator);
			}
			result.append("\t" + entry.getKey() + ": " + utility + " " + entry.getValue());
		}

		return result.toString();
	}
}
