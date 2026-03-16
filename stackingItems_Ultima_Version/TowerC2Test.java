import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

/**
 * TowerC2Test contiene los casos de prueba de unidad para el ciclo 2.
 * Todas las pruebas se ejecutan en modo invisible.
 * 
 * Cada prueba considera dos preguntas:
 * - que deberia hacer?
 * - que NO deberia hacer?
 * 
 * @author Sergio Gonzalez
 * @version 02Mar26
 */
public class TowerC2Test {

    // ===================== CONSTRUCTOR Tower(cups) =====================

    /**
     * Tower(cups) deberia crear la cantidad correcta de tazas.
     */
    @Test
    public void testConstructorCupsShouldCreateCorrectNumberOfCups() {
        Tower tower = new Tower(4);
        tower.makeInvisible();
        assertEquals(4, tower.getCupsCount());
    }

    /**
     * Tower(cups) deberia crear tazas con tamaños impares consecutivos.
     * cups=3 crea tazas de tamaño 1, 3, 5.
     */
    @Test
    public void testConstructorCupsShouldCreateOddSizes() {
        Tower tower = new Tower(3);
        tower.makeInvisible();
        // altura total debe ser 1+3+5 = 9
        assertEquals(9, tower.height());
    }

    /**
     * Tower(cups) NO deberia incluir tapas.
     */
    @Test
    public void testConstructorCupsShouldNotIncludeLids() {
        Tower tower = new Tower(4);
        tower.makeInvisible();
        assertEquals(0, tower.getLidsCount());
    }

    /**
     * Tower(cups) con 1 taza deberia crear solo una taza de tamaño 1.
     */
    @Test
    public void testConstructorCupsWithOneCupShouldCreateSizeOne() {
        Tower tower = new Tower(1);
        tower.makeInvisible();
        assertEquals(1, tower.getCupsCount());
        assertEquals(1, tower.height());
    }

    // ===================== SWAP =====================

    /**
     * swap deberia intercambiar la posicion de dos tazas.
     */
    @Test
    public void testSwapShouldExchangeTwoCups() {
        Tower tower = new Tower(10, 20);
        tower.makeInvisible();
        tower.pushCup(3);
        tower.pushCup(5);
        tower.swap(new String[]{"cup", "3"}, new String[]{"cup", "5"});
        assertTrue(tower.ok());
        // el orden debe haber cambiado: ahora 5 abajo, 3 arriba
        String[][] items = tower.stackingItems();
        assertEquals("cup", items[0][0]);
        assertEquals("5", items[0][1]);
        assertEquals("cup", items[1][0]);
        assertEquals("3", items[1][1]);
    }

    /**
     * swap deberia intercambiar una taza y una tapa.
     */
    @Test
    public void testSwapShouldExchangeCupAndLid() {
        Tower tower = new Tower(10, 20);
        tower.makeInvisible();
        tower.pushCup(3);
        tower.pushCup(5);
        tower.pushLid(3);
        tower.swap(new String[]{"cup", "5"}, new String[]{"lid", "3"});
        assertTrue(tower.ok());
    }

    /**
     * swap NO deberia funcionar si uno de los objetos no existe.
     */
    @Test
    public void testSwapShouldFailIfObjectNotFound() {
        Tower tower = new Tower(10, 20);
        tower.makeInvisible();
        tower.pushCup(3);
        tower.swap(new String[]{"cup", "3"}, new String[]{"cup", "9"});
        assertFalse(tower.ok());
    }

    /**
     * swap NO deberia cambiar la altura total de la torre.
     */
    @Test
    public void testSwapShouldNotChangeHeight() {
        Tower tower = new Tower(10, 20);
        tower.makeInvisible();
        tower.pushCup(3);
        tower.pushCup(5);
        int heightBefore = tower.height();
        tower.swap(new String[]{"cup", "3"}, new String[]{"cup", "5"});
        assertEquals(heightBefore, tower.height());
    }

    // ===================== COVER =====================

    /**
     * cover deberia tapar las tazas que tienen su tapa en la torre.
     */
    @Test
    public void testCoverShouldCoverCupsWithLids() {
        Tower tower = new Tower(10, 20);
        tower.makeInvisible();
        tower.pushCup(3);
        tower.pushCup(5);
        tower.pushLid(3);
        tower.cover();
        assertTrue(tower.ok());
        // la taza 3 debe estar tapada
        int[] lided = tower.lidedCups();
        assertEquals(1, lided.length);
        assertEquals(3, lided[0]);
    }

    /**
     * cover NO deberia tapar tazas que no tienen su tapa en la torre.
     */
    @Test
    public void testCoverShouldNotCoverCupsWithoutLids() {
        Tower tower = new Tower(10, 20);
        tower.makeInvisible();
        tower.pushCup(3);
        tower.pushCup(5);
        tower.cover();
        assertTrue(tower.ok());
        assertEquals(0, tower.lidedCups().length);
    }

    /**
     * cover con multiples tapas deberia tapar todas las tazas correspondientes.
     */
    @Test
    public void testCoverShouldCoverAllMatchingCups() {
        Tower tower = new Tower(10, 20);
        tower.makeInvisible();
        tower.pushCup(3);
        tower.pushCup(5);
        tower.pushLid(3);
        tower.pushLid(5);
        tower.cover();
        assertEquals(2, tower.lidedCups().length);
    }

    // ===================== SWAPTTOREDUCE =====================

    /**
     * swapToReduce deberia retornar array vacio si no hay intercambio posible.
     */
    @Test
    public void testSwapToReduceShouldReturnEmptyIfNoReduction() {
        Tower tower = new Tower(10, 20);
        tower.makeInvisible();
        tower.pushCup(5);
        tower.pushCup(3);
        String[][] result = tower.swapToReduce();
        // sin tapas no hay reduccion posible
        assertEquals(0, result.length);
    }

    /**
     * swapToReduce deberia retornar dos objetos si hay intercambio posible.
     * Torre con altura maxima 6: cup(5) + lid(1) = 6cm llena.
     * Si intercambiamos lid(1) con cup(5), cup(5) no cabe → altura se reduce.
     */
    @Test
    public void testSwapToReduceShouldReturnTwoObjectsIfReductionExists() {
        Tower tower = new Tower(10, 6);
        tower.makeInvisible();
        tower.pushCup(5);  // altura: 5
        tower.pushLid(5);  // altura: 6 (torre llena)
        // intercambiar lid(5) con cup(5) dejaria cup(5) arriba y lid abajo
        // cup(5) en posicion 2 no cabe porque ya hay 1cm de la tapa
        String[][] result = tower.swapToReduce();
        assertEquals(2, result.length);
    }

    /**
     * swapToReduce NO deberia modificar el estado de la torre.
     */
    @Test
    public void testSwapToReduceShouldNotModifyTower() {
        Tower tower = new Tower(10, 20);
        tower.makeInvisible();
        tower.pushCup(3);
        tower.pushCup(5);
        tower.pushLid(3);
        String[][] before = tower.stackingItems();
        tower.swapToReduce();
        String[][] after = tower.stackingItems();
        assertEquals(before.length, after.length);
        for (int i = 0; i < before.length; i++) {
            assertEquals(before[i][0], after[i][0]);
            assertEquals(before[i][1], after[i][1]);
        }
    }

    // ===================== PRUEBAS CICLO 1 EN MODO INVISIBLE =====================

    /**
     * pushCup deberia agregar una taza correctamente.
     */
    @Test
    public void testPushCupShouldAddCup() {
        Tower tower = new Tower(10, 20);
        tower.makeInvisible();
        tower.pushCup(3);
        assertTrue(tower.ok());
        assertEquals(1, tower.getCupsCount());
    }

    /**
     * pushCup NO deberia agregar taza duplicada.
     */
    @Test
    public void testPushCupShouldNotAddDuplicate() {
        Tower tower = new Tower(10, 20);
        tower.makeInvisible();
        tower.pushCup(3);
        tower.pushCup(3);
        assertFalse(tower.ok());
        assertEquals(1, tower.getCupsCount());
    }

    /**
     * pushCup NO deberia agregar taza mas ancha que la torre.
     */
    @Test
    public void testPushCupShouldNotAddIfWiderThanTower() {
        Tower tower = new Tower(3, 20);
        tower.makeInvisible();
        tower.pushCup(5);
        assertFalse(tower.ok());
        assertEquals(0, tower.getCupsCount());
    }

    /**
     * height deberia retornar la suma correcta de alturas.
     */
    @Test
    public void testHeightShouldReturnCorrectSum() {
        Tower tower = new Tower(10, 20);
        tower.makeInvisible();
        tower.pushCup(3);
        tower.pushCup(5);
        tower.pushLid(3);
        // 3 + 5 + 1(tapa) = 9
        assertEquals(9, tower.height());
    }

    /**
     * orderTower deberia ordenar de mayor a menor.
     */
    @Test
    public void testOrderTowerShouldSortDescending() {
        Tower tower = new Tower(10, 20);
        tower.makeInvisible();
        tower.pushCup(2);
        tower.pushCup(5);
        tower.pushCup(3);
        tower.orderTower();
        String[][] items = tower.stackingItems();
        assertEquals("5", items[0][1]);
        assertEquals("3", items[1][1]);
        assertEquals("2", items[2][1]);
    }

    /**
     * lidedCups deberia retornar solo las tazas tapadas ordenadas.
     */
    @Test
    public void testLidedCupsShouldReturnSortedCoveredCups() {
        Tower tower = new Tower(10, 20);
        tower.makeInvisible();
        tower.pushCup(3);
        tower.pushCup(5);
        tower.pushLid(5);
        tower.pushLid(3);
        int[] lided = tower.lidedCups();
        assertEquals(2, lided.length);
        assertEquals(3, lided[0]); // ordenado de menor a mayor
        assertEquals(5, lided[1]);
    }
}