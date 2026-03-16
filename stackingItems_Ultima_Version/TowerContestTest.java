import static org.junit.Assert.*;
import org.junit.Test;

/**
 * TowerContestTest contiene los casos de prueba de unidad para el ciclo 3.
 * Prueba la clase StackingCups y el metodo simulate() de Tower.
 * Todas las pruebas se ejecutan en modo invisible.
 *
 * @author Sergio Gonzalez
 * @version 15mar26
 */
public class TowerContestTest {

    // ===================== isSolvable =====================

    @Test
    public void testIsSolvableShouldReturnTrueForValidHeight() {
        StackingCups sc = new StackingCups(4, 9);
        assertTrue(sc.isSolvable());
    }

    @Test
    public void testIsSolvableShouldReturnFalseForHeightTooHigh() {
        StackingCups sc = new StackingCups(4, 100);
        assertFalse(sc.isSolvable());
    }

    @Test
    public void testIsSolvableShouldReturnFalseForHeightTooLow() {
        StackingCups sc = new StackingCups(4, 6);
        assertFalse(sc.isSolvable());
    }

    @Test
    public void testIsSolvableShouldReturnTrueForMinHeight() {
        StackingCups sc = new StackingCups(4, 7); // min = 2*4-1 = 7
        assertTrue(sc.isSolvable());
    }

    @Test
    public void testIsSolvableShouldReturnTrueForMaxHeight() {
        StackingCups sc = new StackingCups(4, 16); // max = 4^2 = 16
        assertTrue(sc.isSolvable());
    }

    // ===================== getMinHeight / getMaxHeight =====================

    @Test
    public void testGetMinHeightShouldReturn2nMinus1() {
        StackingCups sc = new StackingCups(5, 9);
        assertEquals(9L, sc.getMinHeight()); // 2*5-1 = 9
    }

    @Test
    public void testGetMaxHeightShouldReturnNSquared() {
        StackingCups sc = new StackingCups(5, 9);
        assertEquals(25L, sc.getMaxHeight()); // 5^2 = 25
    }

    // ===================== solve =====================

    @Test
    public void testSolveShouldReturnTrueForValidCase() {
        StackingCups sc = new StackingCups(4, 9);
        assertTrue(sc.solve());
    }

    @Test
    public void testSolveShouldReturnFalseForImpossibleCase() {
        StackingCups sc = new StackingCups(4, 100);
        assertFalse(sc.solve());
    }

    @Test
    public void testSolveShouldFindSolutionForMinHeight() {
        StackingCups sc = new StackingCups(4, 7);
        assertTrue(sc.solve());
        assertNotNull(sc.getSolution());
    }

    @Test
    public void testSolveShouldFindSolutionForMaxHeight() {
        StackingCups sc = new StackingCups(4, 16);
        assertTrue(sc.solve());
        assertNotNull(sc.getSolution());
    }

    @Test
    public void testSolveForImpossibleShouldLeaveNullSolution() {
        StackingCups sc = new StackingCups(4, 100);
        sc.solve();
        assertNull(sc.getSolution());
    }

    // ===================== getSolution =====================

    @Test
    public void testGetSolutionShouldContainAllCups() {
        StackingCups sc = new StackingCups(4, 9);
        sc.solve();
        int[] sol = sc.getSolution();
        assertNotNull(sol);
        assertEquals(4, sol.length);
    }

    @Test
    public void testGetSolutionShouldUseEachCupOnce() {
        StackingCups sc = new StackingCups(4, 9);
        sc.solve();
        int[] sol = sc.getSolution();
        boolean[] used = new boolean[5];
        for (int cup : sol) {
            assertFalse(used[cup]); // no repetidos
            used[cup] = true;
        }
    }

    // ===================== verifySolution =====================

    @Test
    public void testVerifySolutionShouldReturnTrueAfterSolve() {
        StackingCups sc = new StackingCups(4, 9);
        sc.solve();
        assertTrue(sc.verifySolution());
    }

    @Test
    public void testVerifySolutionShouldReturnFalseIfNotSolved() {
        StackingCups sc = new StackingCups(4, 100);
        sc.solve();
        assertFalse(sc.verifySolution());
    }

    @Test
    public void testVerifySolutionForAllHeightsN4() {
        for (int h = 7; h <= 16; h++) {
            StackingCups sc = new StackingCups(4, h);
            sc.solve();
            assertTrue("Fallo para h=" + h, sc.verifySolution());
        }
    }

    // ===================== getSolutionHeights =====================

    @Test
    public void testGetSolutionHeightsShouldReturnOddNumbers() {
        StackingCups sc = new StackingCups(4, 16);
        sc.solve();
        int[] heights = sc.getSolutionHeights();
        assertNotNull(heights);
        for (int h : heights) {
            assertEquals(1, h % 2); // todos impares
        }
    }

    @Test
    public void testGetSolutionHeightsShouldReturnNullIfNotSolved() {
        StackingCups sc = new StackingCups(4, 100);
        sc.solve();
        assertNull(sc.getSolutionHeights());
    }

    // ===================== simulate =====================

    @Test
    public void testSimulateShouldSucceedWithValidSolution() {
        StackingCups sc = new StackingCups(4, 9);
        sc.solve();
        Tower tower = new Tower(10, 20);
        tower.makeInvisible();
        tower.simulate(sc.getSolution());
        assertTrue(tower.ok());
    }

    @Test
    public void testSimulateShouldFailWithNullOrder() {
        Tower tower = new Tower(10, 20);
        tower.makeInvisible();
        tower.simulate(null);
        assertFalse(tower.ok());
    }

    @Test
    public void testSimulateShouldAddCorrectNumberOfCups() {
        StackingCups sc = new StackingCups(4, 9);
        sc.solve();
        Tower tower = new Tower(10, 20);
        tower.makeInvisible();
        tower.simulate(sc.getSolution());
        assertEquals(4, tower.getCupsCount());
    }

    @Test
    public void testSimulateShouldWorkForN1() {
        StackingCups sc = new StackingCups(1, 1);
        sc.solve();
        Tower tower = new Tower(5, 5);
        tower.makeInvisible();
        tower.simulate(sc.getSolution());
        assertTrue(tower.ok());
        assertEquals(1, tower.getCupsCount());
    }
}