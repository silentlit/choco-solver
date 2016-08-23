/**
 * Copyright (c) 2016, Ecole des Mines de Nantes
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    This product includes software developed by the <organization>.
 * 4. Neither the name of the <organization> nor the
 *    names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY <COPYRIGHT HOLDER> ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.chocosolver.solver;

import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.exception.SolverException;
import org.chocosolver.solver.search.loop.monitors.IMonitorSolution;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.RealVar;
import org.chocosolver.solver.variables.SetVar;
import org.chocosolver.util.ProblemMaker;
import org.chocosolver.util.criteria.Criterion;
import org.chocosolver.util.tools.ArrayUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.text.MessageFormat;
import java.util.List;

import static org.chocosolver.solver.search.strategy.Search.inputOrderLBSearch;
import static org.chocosolver.solver.search.strategy.Search.randomSearch;
import static org.chocosolver.solver.variables.IntVar.MAX_INT_BOUND;
import static org.chocosolver.solver.variables.IntVar.MIN_INT_BOUND;
import static org.chocosolver.util.ESat.FALSE;
import static org.chocosolver.util.ESat.TRUE;
import static org.testng.Assert.*;

/**
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 23 juil. 2010
 */
public class ModelTest {

    final static int[] capacites = {0, 34};
    final static int[] energies = {6, 4, 3};
    final static int[] volumes = {7, 5, 2};
    final static int[] nbOmax = {4, 6, 17};
    final static int n = 3;

    /** For autonumbering anonymous models. */
    private static int modelInitNumber;

    /** @return next model's number, for anonymous models. */
    private static synchronized int nextModelNum() {
        return modelInitNumber++;
    }

    public static Model knapsack() {
        Model model = new Model("ModelT-" + nextModelNum());
        IntVar power = model.intVar("v_" + n, 0, 9999, true);
        IntVar[] objects = new IntVar[n];
        for (int i = 0; i < n; i++) {
            objects[i] = model.intVar("v_" + i, 0, nbOmax[i], false);
        }
        model.scalar(objects, volumes, "=", model.intVar("capa", capacites[0], capacites[1], true)).post();
        model.scalar(objects, energies, "=", power).post();
        model.setObjective(Model.MAXIMIZE, power);
        model.addHook("obj", power);
        model.getSolver().setSearch(inputOrderLBSearch(objects));
        return model;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static final int ONE = 0, NEXT = 1, ALL = 2, OPT = 3;

    public static void conf(Model s, int... is) throws SolverException {
        for (int i : is) {
            switch (i) {
                case ONE:
                    s.getSolver().solve();
                    break;
                case NEXT:
                    s.getSolver().solve();
                    break;
                case ALL:
                    while (s.getSolver().solve()) ;
                    break;
                case OPT:
                    s.setObjective(Model.MAXIMIZE, (IntVar) s.getVar(0));
                    s.getSolver().solve();
                    break;
                default:
                    fail("unknonw case");
                    break;
            }
        }
    }

    @Test(groups = "1s", timeOut = 60000)
    public void testRight() {
        boolean alive = true;
        int cas = 0;
        while (alive) {
            cas++;
            Model s = knapsack();
            try {
                switch (cas) {
                    case 1:
                        conf(s, ONE);
                        break;
                    case 2:
                        conf(s, ONE, NEXT);
                        break;
                    case 3:
                        conf(s, ONE, NEXT, NEXT);
                        break;
                    case 4:
                        conf(s, ONE, ONE);
                        break;
                    case 5:
                        conf(s, ONE, ALL);
                        break;
                    case 6:
                        conf(s, ONE, OPT);
                        break;
                    case 7:
                        conf(s, ALL);
                        break;
                    case 8:
                        conf(s, OPT);
                        break;
                    case 9:
                        conf(s, ALL, ONE);
                        break;
                    case 10:
                        conf(s, ALL, ALL);
                        break;
                    case 11:
                        conf(s, ALL, OPT);
                        break;
                    case 12:
                        conf(s, ALL, NEXT);
                        break;
                    case 13:
                        conf(s, OPT, ONE);
                        break;
                    case 14:
                        conf(s, OPT, ALL);
                        break;
                    case 15:
                        conf(s, OPT, OPT);
                        break;
                    case 16:
                        conf(s, OPT, NEXT);
                        break;
                    case 17:
                        conf(s, NEXT);
                        break;
                    default:
                        alive = false;

                }
            } catch (SolverException ingored) {
                Assert.fail(MessageFormat.format("Fail on {0}", cas));
            }
        }
    }

    @Test(groups = "1s", timeOut = 60000)
    public void testFH1() {
        Model model = new Model();
        BoolVar b = model.boolVar("b");
        IntVar i = model.intVar("i", MIN_INT_BOUND, MAX_INT_BOUND, true);
        SetVar s = model.setVar("s", new int[]{}, new int[]{2, 3});
        RealVar r = model.realVar("r", 1.0, 2.2, 0.01);

        BoolVar[] bvars = model.retrieveBoolVars();
        Assert.assertEquals(bvars, new BoolVar[]{b});

        IntVar[] ivars = model.retrieveIntVars(false);
        Assert.assertEquals(ivars, new IntVar[]{i});

        SetVar[] svars = model.retrieveSetVars();
        Assert.assertEquals(svars, new SetVar[]{s});

        RealVar[] rvars = model.retrieveRealVars();
        Assert.assertEquals(rvars, new RealVar[]{r});

    }


    @Test(groups = "1s", timeOut = 60000)
    public void testRetrieveInt() {
        Model model = new Model();
        BoolVar b = model.boolVar("b");
        IntVar i = model.intVar("i", 1, 3, false);
        IntVar[] is = model.retrieveIntVars(false);
        Assert.assertEquals(1, is.length);
        IntVar[] is2 = model.retrieveIntVars(true);
        Assert.assertEquals(2, is2.length);
    }

    @Test(groups = "1s", timeOut = 60000)
    public void testRetrieveBool() {
        Model model = new Model();
        BoolVar b = model.boolVar("b");
        IntVar i = model.intVar("i", 1, 3, false);
        IntVar[] bs = model.retrieveBoolVars();
        Assert.assertEquals(1, bs.length);
    }

    @Test(groups = "1s", timeOut = 60000)
    public void testFH2() {
        Model model = new Model();
        BoolVar b = model.boolVar("b");
        model.arithm(b, "=", 2).post();
        while (model.getSolver().solve()) ;
        assertEquals(model.getSolver().isFeasible(), FALSE);
    }

    @Test(groups = "1s", timeOut = 60000)
    public void testJL1() {
        Model s = new Model();
        s.arithm(s.boolVar(true), "!=", s.boolVar(false)).post();
        while (s.getSolver().solve()) ;
    }

    @Test(groups = "10s", timeOut = 60000)
    public void testParBug2() {
        for (int iter = 0; iter < 5000; iter++) {
            Model model = knapsack();
            while (model.getSolver().solve()) ;
            Assert.assertEquals(model.getSolver().getObjectiveManager().getBestSolutionValue(), 51);
        }
    }

    @Test(groups = "10s", timeOut = 60000)
    public void testParBug3() {
        for (int iter = 0; iter < 5000; iter++) {
            Model model = knapsack();
            model.getSolver().setSearch(randomSearch(model.retrieveIntVars(true), iter));
            while (model.getSolver().solve()) ;
            Assert.assertEquals(model.getSolver().getObjectiveManager().getBestSolutionValue(), 51);
        }
    }

    @Test(groups = "1s", timeOut = 60000)
    public void testJL300() {
        Model s = new Model();
        IntVar i = s.intVar("i", -5, 5, false);
        s.setObjective(Model.MAXIMIZE, i);
        s.getSolver().solve();
        assertEquals(s.getSolver().getSolutionCount(), 1);
        assertEquals(i.getValue(), 5);

        s.getSolver().getEngine().flush();
        s.getSolver().reset();
        s.clearObjective();
        while (s.getSolver().solve()) ;
        assertEquals(s.getSolver().getSolutionCount(), 11);
    }

    @Test(groups = "1s", timeOut = 60000)
    public void testMonitors() {
        Model model = new Model();
        IntVar v = model.boolVar("b");
        final int[] c = {0};
        final int[] d = {0};
        IMonitorSolution sm1 = () -> c[0]++;
        IMonitorSolution sm2 = () -> d[0]++;
        model.getSolver().plugMonitor(sm1);
        model.getSolver().plugMonitor(sm2);
        while (model.getSolver().solve()) ;
        assertEquals(2, c[0]);
        assertEquals(2, d[0]);
        // unplug
        model.getSolver().unplugMonitor(sm1);
        model.getSolver().reset();
        while (model.getSolver().solve()) ;
        assertEquals(2, c[0]);
        assertEquals(4, d[0]);
        // plug
        model.getSolver().unplugAllSearchMonitors();
        model.getSolver().reset();
        while (model.getSolver().solve()) ;
        assertEquals(2, c[0]);
        assertEquals(4, d[0]);
    }

    @Test(groups = "1s", timeOut = 60000)
    public void testCriteria() {
        Model model = new Model();
        IntVar v = model.boolVar("b");
        Criterion c1 = () -> model.getSolver().getNodeCount() == 1;
        Criterion c2 = () -> model.getSolver().getSolutionCount() == 1;
        model.getSolver().addStopCriterion(c1);
        model.getSolver().addStopCriterion(c2);
        while (model.getSolver().solve()) ;
        assertEquals(0, model.getSolver().getSolutionCount());
        // unplug
        model.getSolver().removeStopCriterion(c1);
        model.getSolver().reset();
        while (model.getSolver().solve()) ;
        assertEquals(1, model.getSolver().getSolutionCount());
        // plug
        model.getSolver().removeAllStopCriteria();
        model.getSolver().reset();
        while (model.getSolver().solve()) ;
        assertEquals(2, model.getSolver().getSolutionCount());
    }

    @Test(groups = "1s", timeOut = 60000)
    public void testCompSearch() {
        Model model = new Model();
        IntVar[] v = model.boolVarArray("v", 2);
        IntVar[] w = model.boolVarArray("w", 2);
        model.arithm(v[0], "!=", v[1]).post();
        model.arithm(w[0], "!=", w[1]).post();
        model.getSolver().setSearch(inputOrderLBSearch(v));
        model.getSolver().makeCompleteStrategy(true);
        model.getSolver().solve();
        assertEquals(model.getSolver().isSatisfied(), TRUE);
    }

    @Test(groups = "1s", timeOut = 60000)
    public void testAssociates() {
        Model s = new Model();
        BoolVar v = s.boolVar("V");
        Assert.assertEquals(s.getNbVars(), 1);
        s.associates(v);
        Assert.assertEquals(s.getNbVars(), 2);
        s.unassociates(v);
        Assert.assertEquals(s.getNbVars(), 1);
        s.unassociates(v);
        Assert.assertEquals(s.getNbVars(), 0);
    }

    @Test(groups = "1s", timeOut = 60000)
    public void testRestore() throws ContradictionException {
        Model model = new Model();
        IntVar[] v = model.boolVarArray("v", 2);
        model.arithm(v[0], "!=", v[1]).post();
        model.setObjective(Model.MAXIMIZE, v[0]);
        model.getSolver().solve();
        assertTrue(v[0].isInstantiated());
        model.getSolver().solve();
        assertTrue(v[0].isInstantiatedTo(1));
    }

    @Test(groups = "1s", timeOut = 60000)
    public void testHook() {
        Model model = new Model();
        String toto = "TOTO";
        String titi = "TITI";
        model.addHook("toto", toto);
        model.addHook("titi", titi);
        Assert.assertEquals(model.getHooks().size(), 2);
        Assert.assertEquals(model.getHook("toto"), toto);
        model.removeHook("toto");
        Assert.assertEquals(model.getHook("toto"), null);
        Assert.assertEquals(model.getHooks().size(), 1);
        model.removeAllHooks();
        Assert.assertEquals(model.getHooks().size(), 0);
    }

    @Test(groups = "1s", timeOut = 60000)
    public void testName() {
        Model model = new Model();
        Assert.assertTrue(model.getName().startsWith("Model-"));
        model.setName("Revlos");
        Assert.assertEquals(model.getName(), "Revlos");
    }

    @Test(groups = "1s", timeOut = 60000)
    public void testNextSolution() {
        Model s = ProblemMaker.makeNQueenWithBinaryConstraints(8);
        s.getSolver().solve(); //  should not throw exception
    }

    @Test(groups = "1s", timeOut = 60000)
    public void testFindSolution() {
        Model m = ProblemMaker.makeNQueenWithOneAlldifferent(4);
        for (int i = 0; i < 2; i++) {
            Assert.assertNotNull(m.getSolver().findSolution());
        }
        Assert.assertNull(m.getSolver().findSolution());
    }

	@Test(groups = "1s", timeOut = 60000)
	public void testFindAfterReset(){
		Model m = new Model();
		IntVar i = m.intVar("i", 0, 5);
		Solver s = m.getSolver();
		s.findOptimalSolution(i, false);
		s.reset();
		Assert.assertNotNull(s.findSolution());
	}

    @Test(groups = "1s", timeOut = 60000)
    public void testFindAllSolutions() {
        Model m = ProblemMaker.makeNQueenWithOneAlldifferent(4);
        m.getSolver().findAllSolutions();
        Assert.assertEquals(m.getSolver().getSolutionCount(), 2);
    }

	@Test(groups = "1s", timeOut = 60000)
	public void testFindAllSolutionsAfterReset(){
		Model m = new Model();
		IntVar i = m.intVar("i", 0, 5);
		Solver s = m.getSolver();
		s.findOptimalSolution(i, false);
		s.reset();
		Assert.assertEquals(s.findAllSolutions().size(),6);
	}

    @Test(groups = "1s", timeOut = 60000)
    public void testFindAllSolutions2() {
        Model m = ProblemMaker.makeNQueenWithOneAlldifferent(4);
        Assert.assertEquals(m.getSolver().streamSolutions().count(), 2);
    }

    @Test(groups = "10s", timeOut = 60000)
    public void testFindOptimalSolution() {
        Model m = ProblemMaker.makeGolombRuler(10);
        Assert.assertNotNull(m.getSolver().findOptimalSolution((IntVar) m.getHook("objective"), false));
    }

    @Test(groups = "1s", timeOut = 60000)
    public void testFindOptimalSolutionWithSearch() {
        Model m = ProblemMaker.makeGolombRuler(7);
        m.getSolver().setSearch(inputOrderLBSearch((IntVar[]) m.getHook("ticks")));
        Solution s = m.getSolver().findOptimalSolution((IntVar) m.getHook("objective"), false);
        Assert.assertNotNull(s);
        Assert.assertTrue(s.getIntVal((IntVar) m.getHook("objective"))==25);
    }

    @Test(groups = "1s", timeOut = 60000)
    public void testFindAllOptimalSolutions() {
        Model m = ProblemMaker.makeGolombRuler(6);
        m.getSolver().findAllOptimalSolutions((IntVar) m.getHook("objective"), false);
        Assert.assertEquals(m.getSolver().getSolutionCount(), 4);
    }

    @Test(groups = "1s", timeOut = 60000)
    public void testFindAllOptimalSolutions2() {
        Model m = ProblemMaker.makeGolombRuler(6);
        Assert.assertEquals(m.getSolver().streamOptimalSolutions((IntVar) m.getHook("objective"), false).count(), 4);
    }

    @Test(groups = "1s", timeOut = 60000)
    public void testFind() {
        Model m = ProblemMaker.makeGolombRuler(6);
        IntVar[] ticks = (IntVar[]) m.getHook("ticks");
        m.clearObjective();
        m.getSolver().showSolutions();
		List<Solution> front = m.getSolver().findParetoFront(ticks, false);
		for(Solution s:front){
			System.out.println(s.getIntVal(ticks[0]));
		}
        Assert.assertEquals(front.size(), 8);
    }

    @Test(groups = "1s", timeOut = 60000)
    public void testFindAllSolutions3() {
        Model m = ProblemMaker.makeNQueenWithOneAlldifferent(4);
        m.getSolver().eachSolutionWithMeasure((solution, measures) -> {
            System.out.printf("Found solution: %s%n with the following measures:%n%s%n",
                    solution.toString(),
                    measures.toOneLineString());
        });
        Assert.assertEquals(m.getSolver().getSolutionCount(), 2);
    }

    @Test(groups = "1s", timeOut = 60000, expectedExceptions = SolverException.class)
    public void testRecord() {
        Model m = ProblemMaker.makeGolombRuler(6);
		IntVar[] ticks = (IntVar[]) m.getHook("ticks");
		Solution s = new Solution(m);
		m.getSolver().solve();
		// solution not recorded
		System.out.println(s.getIntVal(ticks[0]));
    }

	@Test(groups = "1s", timeOut = 60000, expectedExceptions = SolverException.class)
	public void testRecord2() {
		Model m = ProblemMaker.makeGolombRuler(6);
		IntVar[] ticks = (IntVar[]) m.getHook("ticks");
		IntVar p = m.boolVar();
		Solution s = new Solution(m, ticks);
		m.getSolver().solve();
		s.record();
		System.out.println(s.getIntVal(ticks[0]));
		// not recorded variable
		System.out.println(s.getIntVal(p));
	}

	@Test(groups = "1s", timeOut = 60000)
	public void testRecord3() {
		Model m = ProblemMaker.makeGolombRuler(6);
		IntVar[] ticks = (IntVar[]) m.getHook("ticks");
		IntVar p = m.boolVar();
		Solution s = new Solution(m, ArrayUtils.append(ticks, new IntVar[]{p}));
		m.getSolver().solve();
		s.record();
		System.out.println(s.getIntVal(ticks[0]));
		System.out.println(s.getIntVal(p));
	}
}
