package de.tischner.nashfinder;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.tischner.nashfinder.game.StrategicGame;
import de.tischner.nashfinder.game.util.ActionProfile;
import de.tischner.nashfinder.game.util.PlayerAction;
import de.tischner.nashfinder.game.util.SupportSet;
import de.tischner.nashfinder.locale.ErrorMessages;
import de.tischner.nashfinder.nash.NashEquilibrium;
import de.tischner.nashfinder.util.EExpectedUtilty;
import de.tischner.nashfinder.util.SetUtil;
import de.tischner.nashfinder.util.StrategicGameParser;
import de.tischner.nashfinder.util.SupportSetParser;
import net.sf.javailp.Linear;
import net.sf.javailp.OptType;
import net.sf.javailp.Problem;
import net.sf.javailp.Result;
import net.sf.javailp.Solver;
import net.sf.javailp.SolverFactory;
import net.sf.javailp.SolverFactoryLpSolve;

/**
 * Class that finds nash equilibria in games.
 * 
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class NashFinder {

	/**
	 * The timeout for LCP solving in milliseconds.
	 */
	private static final int TIMEOUT_MILLIS = 1000;
	/**
	 * If solving the LCP should output detailed information or not.
	 */
	private static final int VERBOSE_VALUE = 0;

	/**
	 * Game to solve.
	 */
	private final StrategicGame<String, String> mGame;
	/**
	 * Results, after {@link #computeNashEquilibria()} was called.
	 */
	private final Map<List<SupportSet<String, String>>, NashEquilibrium<String, String>> mResults;
	/**
	 * Specific support sets to solve the game for.
	 */
	private final List<SupportSet<String, String>> mSpecificSupportSets;
	/**
	 * If the game should be solved for specific given support sets.
	 */
	private final boolean mUseSpecificSupportSets;

	/**
	 * Creates a new NashFinder that is able to solve the given game. After
	 * creation, use {@link #computeNashEquilibria()} and then get the results,
	 * for example by {@link #printResults()}.
	 * 
	 * @param gamePath
	 *            Path to the game file in the json-format
	 */
	public NashFinder(final String gamePath) {
		this(gamePath, null);
	}

	/**
	 * Creates a new NashFinder that is able to solve the given game for given
	 * support sets. After creation, use {@link #computeNashEquilibria()} and
	 * then get the results, for example by {@link #printResults()}.
	 * 
	 * @param gameFileName
	 *            Path to the game file in the json-format.
	 * @param specificSupportSets
	 *            Specific support sets to solve the game for. The format is
	 *            <tt>[H,T][T]</tt>, where every player has the given actions.
	 *            For example, player 1 has actions <tt>H</tt> and <tt>T</tt>,
	 *            whereas player 2 only has action <tt>T</tt>.
	 */
	public NashFinder(final String gameFileName, final String specificSupportSets) {
		this.mUseSpecificSupportSets = specificSupportSets != null && specificSupportSets.length() != 0;
		this.mGame = StrategicGameParser.parseStrategicGameJson(gameFileName);

		if (this.mUseSpecificSupportSets) {
			this.mSpecificSupportSets = SupportSetParser.parseSupportSets(specificSupportSets, this.mGame.getPlayers());
		} else {
			this.mSpecificSupportSets = null;
		}

		this.mResults = new LinkedHashMap<>();
	}

	/**
	 * Computes nash equilibria of the given game. Results can be get, for
	 * example, with {@link #printResults()}.
	 */
	public void computeNashEquilibria() {
		final List<List<SupportSet<String, String>>> supportSetsToProcess = buildSupportSets();
		for (final List<SupportSet<String, String>> supportSets : supportSetsToProcess) {
			// Solve the LCP for the given support sets
			final SupportSet<String, String> firstPlayerSet = supportSets.get(0);
			final SupportSet<String, String> secondPlayerSet = supportSets.get(1);

			final SolverFactory factory = new SolverFactoryLpSolve();
			factory.setParameter(Integer.valueOf(Solver.VERBOSE), Integer.valueOf(VERBOSE_VALUE));
			factory.setParameter(Integer.valueOf(Solver.TIMEOUT), Integer.valueOf(TIMEOUT_MILLIS));
			final Problem problem = new Problem();
			final Linear linear = new Linear();
			linear.add(Integer.valueOf(1), EExpectedUtilty.FIRST_PLAYER);
			linear.add(Integer.valueOf(1), EExpectedUtilty.SECOND_PLAYER);
			problem.setObjective(linear, OptType.MAX);
			problem.setVarType(EExpectedUtilty.FIRST_PLAYER, Double.class);
			problem.setVarType(EExpectedUtilty.SECOND_PLAYER, Double.class);

			// Player 1 against Player 2
			addConstraintsForConstellation(problem, firstPlayerSet, secondPlayerSet, EExpectedUtilty.FIRST_PLAYER);
			// Player 2 against Player 1
			addConstraintsForConstellation(problem, secondPlayerSet, firstPlayerSet, EExpectedUtilty.SECOND_PLAYER);

			final Solver solver = factory.get();
			final Result result = solver.solve(problem);

			this.mResults.put(supportSets, NashEquilibrium.extractFromLcpResults(result, this.mGame));
		}
	}

	/**
	 * Prints the results of the game to the console. Results are obtained by
	 * using {@link #computeNashEquilibria()} prior to this method.
	 */
	public void printResults() {
		System.out.println(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		final String lineSeparator = System.lineSeparator();

		boolean isFirstEntry = true;
		for (final Entry<List<SupportSet<String, String>>, NashEquilibrium<String, String>> entry : this.mResults
				.entrySet()) {
			if (isFirstEntry) {
				isFirstEntry = false;
			} else {
				result.append(lineSeparator);
			}
			result.append(entry.getKey() + lineSeparator);
			result.append(entry.getValue());
		}

		return result.toString();
	}

	/**
	 * Adds the constraints for the given player constellation to the given LCP,
	 * for finding nash equilibria.
	 * 
	 * @param problem
	 *            Problem to add constraints to
	 * @param protagonistSet
	 *            Support set of the protagonist player
	 * @param antagonistSet
	 *            Support set of the antagonist player
	 * @param protagonistExpectedUtiltyVar
	 *            Key to save the expected utility of the protagonist with
	 */
	private void addConstraintsForConstellation(final Problem problem, final SupportSet<String, String> protagonistSet,
			final SupportSet<String, String> antagonistSet, final EExpectedUtilty protagonistExpectedUtiltyVar) {
		final String protagonist = protagonistSet.getPlayer();

		// Best response constraints
		final Iterator<String> responseIter = antagonistSet.getActions();
		while (responseIter.hasNext()) {
			final String response = responseIter.next();
			final Linear linear = new Linear();
			final Iterator<String> actionIter = protagonistSet.getActions();
			while (actionIter.hasNext()) {
				final String action = actionIter.next();
				final ActionProfile<String> profile = new ActionProfile<>();
				profile.addAction(action);
				profile.addAction(response);
				final int payoff = this.mGame.getPayoffForPlayer(profile, protagonist);

				linear.add(Integer.valueOf(payoff), new PlayerAction<>(protagonist, action));
			}
			linear.add(Integer.valueOf(-1), protagonistExpectedUtiltyVar);
			problem.add(linear, ">=", Integer.valueOf(0));
		}

		// Correct chance distribution
		final Linear linear = new Linear();
		Iterator<String> actionIter = protagonistSet.getActions();
		while (actionIter.hasNext()) {
			final String action = actionIter.next();
			linear.add(Integer.valueOf(1), new PlayerAction<>(protagonist, action));
		}
		problem.add(linear, "=", Integer.valueOf(1));

		// Lower bound
		actionIter = protagonistSet.getActions();
		while (actionIter.hasNext()) {
			final String action = actionIter.next();
			problem.setVarLowerBound(new PlayerAction<>(protagonist, action), Integer.valueOf(0));
		}
	}

	/**
	 * Builds and gets the support set constellations to use for solving the
	 * current game for.
	 * 
	 * @return List of support set constellations for solving the current game
	 *         for
	 */
	private List<List<SupportSet<String, String>>> buildSupportSets() {
		final List<List<SupportSet<String, String>>> supportSets = new LinkedList<>();

		if (this.mUseSpecificSupportSets) {
			// Only use the given support set
			// Validate the support set before using it
			for (final SupportSet<String, String> supportSet : this.mSpecificSupportSets) {
				final String player = supportSet.getPlayer();
				if (!this.mGame.hasPlayer(player)) {
					throw new IllegalStateException(ErrorMessages.SUPPORT_SET_INVALID);
				}
				final Iterator<String> actionIter = supportSet.getActions();
				while (actionIter.hasNext()) {
					final String action = actionIter.next();
					if (!this.mGame.hasPlayerAction(player, action)) {
						throw new IllegalStateException(ErrorMessages.SUPPORT_SET_INVALID);
					}
				}
			}

			supportSets.add(this.mSpecificSupportSets);
			return supportSets;
		}

		// Build all possible support sets
		Set<String> firstPlayerActions = null;
		Set<String> secondPlayerActions = null;
		String firstPlayer = null;
		String secondPlayer = null;

		final Iterator<String> playerIter = this.mGame.getPlayers();
		if (playerIter.hasNext()) {
			firstPlayer = playerIter.next();
			if (playerIter.hasNext()) {
				secondPlayer = playerIter.next();

				firstPlayerActions = this.mGame.getPlayerActions(firstPlayer);
				secondPlayerActions = this.mGame.getPlayerActions(secondPlayer);
			}
		}
		if (firstPlayerActions == null || secondPlayerActions == null || firstPlayer == null || secondPlayer == null) {
			throw new IllegalArgumentException(ErrorMessages.BUILD_SUPPORT_SETS_GAME_INVALID);
		}

		final Set<Set<String>> powerOfFirstPlayer = SetUtil.powerSet(firstPlayerActions);
		final Set<Set<String>> powerOfSecondPlayer = SetUtil.powerSet(secondPlayerActions);
		for (final Set<String> firstPlayerSet : powerOfFirstPlayer) {
			for (final Set<String> secondPlayerSet : powerOfSecondPlayer) {
				final List<SupportSet<String, String>> supportSetConstellation = new LinkedList<>();
				supportSetConstellation.add(new SupportSet<>(firstPlayer, firstPlayerSet));
				supportSetConstellation.add(new SupportSet<>(secondPlayer, secondPlayerSet));

				supportSets.add(supportSetConstellation);
			}
		}

		return supportSets;
	}
}
