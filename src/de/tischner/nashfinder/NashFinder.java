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
		mUseSpecificSupportSets = specificSupportSets != null && specificSupportSets.length() != 0;
		mGame = StrategicGameParser.parseStrategicGameJson(gameFileName);

		if (mUseSpecificSupportSets) {
			mSpecificSupportSets = SupportSetParser.parseSupportSets(specificSupportSets, mGame.getPlayers());
		} else {
			mSpecificSupportSets = null;
		}

		mResults = new LinkedHashMap<>();
	}

	/**
	 * Computes nash equilibria of the given game. Results can be get, for
	 * example, with {@link #printResults()}.
	 */
	public void computeNashEquilibria() {
		List<List<SupportSet<String, String>>> supportSetsToProcess = buildSupportSets();
		for (List<SupportSet<String, String>> supportSets : supportSetsToProcess) {
			// Solve the LCP for the given support sets
			SupportSet<String, String> firstPlayerSet = supportSets.get(0);
			SupportSet<String, String> secondPlayerSet = supportSets.get(1);

			SolverFactory factory = new SolverFactoryLpSolve();
			factory.setParameter(Solver.VERBOSE, VERBOSE_VALUE);
			factory.setParameter(Solver.TIMEOUT, TIMEOUT_MILLIS);
			Problem problem = new Problem();
			Linear linear = new Linear();
			linear.add(1, EExpectedUtilty.FIRST_PLAYER);
			linear.add(1, EExpectedUtilty.SECOND_PLAYER);
			problem.setObjective(linear, OptType.MAX);
			problem.setVarType(EExpectedUtilty.FIRST_PLAYER, Double.class);
			problem.setVarType(EExpectedUtilty.SECOND_PLAYER, Double.class);

			// Player 1 against Player 2
			addConstraintsForConstellation(problem, firstPlayerSet, secondPlayerSet, EExpectedUtilty.FIRST_PLAYER);
			// Player 2 against Player 1
			addConstraintsForConstellation(problem, secondPlayerSet, firstPlayerSet, EExpectedUtilty.SECOND_PLAYER);

			Solver solver = factory.get();
			Result result = solver.solve(problem);

			mResults.put(supportSets, NashEquilibrium.extractFromLcpResults(result, mGame));
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
		StringBuilder result = new StringBuilder();
		String lineSeparator = System.lineSeparator();

		boolean isFirstEntry = true;
		for (Entry<List<SupportSet<String, String>>, NashEquilibrium<String, String>> entry : mResults.entrySet()) {
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
		String protagonist = protagonistSet.getPlayer();

		// Best response constraints
		Iterator<String> responseIter = antagonistSet.getActions();
		while (responseIter.hasNext()) {
			String response = responseIter.next();
			Linear linear = new Linear();
			Iterator<String> actionIter = protagonistSet.getActions();
			while (actionIter.hasNext()) {
				String action = actionIter.next();
				ActionProfile<String> profile = new ActionProfile<>();
				profile.addAction(action);
				profile.addAction(response);
				int payoff = mGame.getPayoffForPlayer(profile, protagonist);

				linear.add(payoff, new PlayerAction<String, String>(protagonist, action));
			}
			linear.add(-1, protagonistExpectedUtiltyVar);
			problem.add(linear, ">=", 0);
		}

		// Correct chance distribution
		Linear linear = new Linear();
		Iterator<String> actionIter = protagonistSet.getActions();
		while (actionIter.hasNext()) {
			String action = actionIter.next();
			linear.add(1, new PlayerAction<String, String>(protagonist, action));
		}
		problem.add(linear, "=", 1);

		// Lower bound
		actionIter = protagonistSet.getActions();
		while (actionIter.hasNext()) {
			String action = actionIter.next();
			problem.setVarLowerBound(new PlayerAction<String, String>(protagonist, action), 0);
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
		List<List<SupportSet<String, String>>> supportSets = new LinkedList<>();

		if (mUseSpecificSupportSets) {
			// Only use the given support set
			// Validate the support set before using it
			for (SupportSet<String, String> supportSet : mSpecificSupportSets) {
				String player = supportSet.getPlayer();
				if (!mGame.hasPlayer(player)) {
					throw new IllegalStateException(ErrorMessages.SUPPORT_SET_INVALID);
				}
				Iterator<String> actionIter = supportSet.getActions();
				while (actionIter.hasNext()) {
					String action = actionIter.next();
					if (!mGame.hasPlayerAction(player, action)) {
						throw new IllegalStateException(ErrorMessages.SUPPORT_SET_INVALID);
					}
				}
			}

			supportSets.add(mSpecificSupportSets);
			return supportSets;
		} else {
			// Build all possible support sets
			Set<String> firstPlayerActions = null;
			Set<String> secondPlayerActions = null;
			String firstPlayer = null;
			String secondPlayer = null;

			Iterator<String> playerIter = mGame.getPlayers();
			if (playerIter.hasNext()) {
				firstPlayer = playerIter.next();
				if (playerIter.hasNext()) {
					secondPlayer = playerIter.next();

					firstPlayerActions = mGame.getPlayerActions(firstPlayer);
					secondPlayerActions = mGame.getPlayerActions(secondPlayer);
				}
			}
			if (firstPlayerActions == null || secondPlayerActions == null || firstPlayer == null
					|| secondPlayer == null) {
				throw new IllegalArgumentException(ErrorMessages.BUILD_SUPPORT_SETS_GAME_INVALID);
			}

			Set<Set<String>> powerOfFirstPlayer = SetUtil.powerSet(firstPlayerActions);
			Set<Set<String>> powerOfSecondPlayer = SetUtil.powerSet(secondPlayerActions);
			for (Set<String> firstPlayerSet : powerOfFirstPlayer) {
				for (Set<String> secondPlayerSet : powerOfSecondPlayer) {
					List<SupportSet<String, String>> supportSetConstellation = new LinkedList<>();
					supportSetConstellation.add(new SupportSet<String, String>(firstPlayer, firstPlayerSet));
					supportSetConstellation.add(new SupportSet<String, String>(secondPlayer, secondPlayerSet));

					supportSets.add(supportSetConstellation);
				}
			}

			return supportSets;
		}
	}
}
