package de.tischner.nashfinder;

import net.sf.javailp.Linear;
import net.sf.javailp.OptType;
import net.sf.javailp.Problem;
import net.sf.javailp.Result;
import net.sf.javailp.Solver;
import net.sf.javailp.SolverFactory;
import net.sf.javailp.SolverFactoryLpSolve;

/**
 * 
 * @author Daniel Tischner
 *
 */
public final class Program {
	
	/**
	 * Utility class. No implementation.
	*/
	private Program() {

	}
	
	/**
	 * 
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// The first argument is not optional and specifies the game file to use
		// for computation
		if (args == null || args.length < 1) {
			throw new IllegalArgumentException(
					"Wrong number of arguments. The first argument is not optional and specifies the game file to use for computation.");
		}
		// The second argument is optional and may specify a specific support
		// sets to use for computation
		boolean useSpecificSupportSets = args.length >= 2;
		String specificSupportSets = null;
		if (useSpecificSupportSets) {
			specificSupportSets = args[1];
		}
		String gameFileName = args[0];

		NashFinder nashFinder = null;
		if (useSpecificSupportSets) {
			nashFinder = new NashFinder(gameFileName, specificSupportSets);
		} else {
			nashFinder = new NashFinder(gameFileName);
		}

		nashFinder.computeNashEquilibria();
		
		// Testing Java ILP
		SolverFactory factory = new SolverFactoryLpSolve();
		factory.setParameter(Solver.VERBOSE, 0);
		factory.setParameter(Solver.TIMEOUT, 100);
		
		Problem problem = new Problem();
		
		Linear linear = new Linear();
		linear.add(143, "x");
		linear.add(60, "y");
		
		problem.setObjective(linear, OptType.MAX);
		
		linear = new Linear();
		linear.add(120, "x");
		linear.add(210, "y");
		
		problem.add(linear, "<=", 15000);
		
		linear = new Linear();
		linear.add(110, "x");
		linear.add(30, "y");
		
		problem.add(linear, "<=", 4000);
		
		linear = new Linear();
		linear.add(1, "x");
		linear.add(1, "y");
		
		problem.add(linear, "<=", 75);
		
		problem.setVarType("x", Integer.class);
		problem.setVarType("y", Integer.class);
		
		Solver solver = factory.get();
		Result result = solver.solve(problem);
		
		System.out.println(result);
		
		problem.setVarUpperBound("x", 16);
		
		solver = factory.get();
		result = solver.solve(problem);
		
		System.out.println(result);
	}
}
