import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.JOptionPane;

/**
 * Tower representa la torre de apilamiento de tazas y tapas.
 * Es la clase principal del simulador: gestiona la creacion, adicion,
 * eliminacion y reorganizacion de elementos, ademas del control visual.
 * 
 * @author Sergio Gonzalez
 * @version 02Mar26
 */
public class Tower {
    // atributos
    private int width;                    // ancho de la torre (cm)
    private int maxHeight;                // altura maxima (cm)
    private ArrayList<Cup> cups;          // lista de tazas
    private ArrayList<Lid> lids;          // lista de tapas
    private Canvas canvas;                // lienzo para dibujar
    private boolean visible;              // estado de visibilidad
    private boolean lastOperationOk;      // indica si la ultima operacion fue exitosa

    // constantes de visualizacion
    private static final int BASE_X = 50;    // posicion X base en pixeles
    private static final int BASE_Y = 550;   // posicion Y base (parte inferior del canvas)
    private static final int SCALE  = 20;    // factor de escala (cm a pixeles)

    /**
     * Constructor para objetos de la clase Tower.
     * 
     * @param width     ancho de la torre en cm
     * @param maxHeight altura maxima de la torre en cm
     */
    public Tower(int width, int maxHeight) {
        this.width = width;
        this.maxHeight = maxHeight;
        this.cups = new ArrayList<Cup>();
        this.lids = new ArrayList<Lid>();
        this.visible = true;
        this.lastOperationOk = true;
        this.canvas = Canvas.getCanvas();
        drawTowerBase();
    }

    /**
     * Constructor que crea una torre con un numero dado de tazas.
     * Las tazas tienen tamaños impares consecutivos: 1, 3, 5, ... (2*i-1).
     * El ancho y altura de la torre se calculan automaticamente.
     * No se incluyen tapas.
     * 
     * @param cups numero de tazas a crear (de 1 a cups)
     */
    public Tower(int cups) {
        this.cups = new ArrayList<Cup>();
        this.lids = new ArrayList<Lid>();
        this.visible = true;
        this.lastOperationOk = true;
        this.canvas = Canvas.getCanvas();
        // calcular ancho y altura automaticamente
        this.width = 2 * cups - 1;       // tamaño de la taza mas grande
        this.maxHeight = cups * cups;     // suma de 1+3+5+...+(2n-1) = n^2
        // crear las tazas con tamaños impares
        for (int i = 1; i <= cups; i++) {
            int size = 2 * i - 1;
            Cup newCup = new Cup(size, generateRandomColor());
            this.cups.add(newCup);
        }
        drawTowerBase();
        if (visible) { draw(); }
    }

    // ===================== MANEJO DE TAZAS =====================

    /**
     * Adiciona una taza nueva a la torre generando su color automaticamente.
     * Solo puede existir una taza por cada numero.
     * 
     * @param number numero/tamaño de la taza en cm
     */
    public void pushCup(int number) {
        if (number <= 0) {
            showError("El numero de la taza debe ser positivo.");
            lastOperationOk = false;
            return;
        }
        if (findCupByNumber(number) != null) {
            showError("Ya existe una taza con el numero " + number + ".");
            lastOperationOk = false;
            return;
        }
        Cup newCup = new Cup(number, generateRandomColor());
        lastOperationOk = addCup(newCup);
        if (!lastOperationOk) {
            showError("No se pudo adicionar la taza. Verifique el espacio disponible.");
        } else if (visible) {
            erase();
            draw();
        }
    }

    /**
     * Remueve la taza del tope de la torre (ultima ingresada).
     */
    public void popCup() {
        if (cups.isEmpty()) {
            showError("No hay tazas para remover.");
            lastOperationOk = false;
            return;
        }
        // primero borrar todo del canvas
        if (visible) {
            erase();
        }
        // luego remover de la lista
        removeLastCup();
        lastOperationOk = true;
        // redibujar lo que queda
        if (visible) {
            draw();
        }
    }

    /**
     * Remueve una taza especifica de la torre buscandola por su numero.
     * 
     * @param number numero de la taza a remover
     */
    public void removeCup(int number) {
        for (int i = 0; i < cups.size(); i++) {
            if (cups.get(i).getNumber() == number) {
                if (visible) { erase(); }
                removeCupByIndex(i);
                lastOperationOk = true;
                if (visible) { draw(); }
                return;
            }
        }
        showError("No se encontro una taza con el numero " + number + ".");
        lastOperationOk = false;
    }

    // ===================== MANEJO DE TAPAS =====================

    /**
     * Adiciona una tapa a la torre. La tapa toma el color de su taza.
     * Solo puede existir una tapa por cada numero.
     * 
     * @param number numero de la taza a la que pertenece la tapa
     */
    public void pushLid(int number) {
        if (number <= 0) {
            showError("El numero de la tapa debe ser positivo.");
            lastOperationOk = false;
            return;
        }
        if (findLidByNumber(number) != null) {
            showError("Ya existe una tapa para la taza numero " + number + ".");
            lastOperationOk = false;
            return;
        }
        Cup cup = findCupByNumber(number);
        String color = (cup != null) ? cup.getColor() : generateRandomColor();
        Lid newLid = new Lid(number, color);
        lastOperationOk = addLid(newLid);
        if (!lastOperationOk) {
            showError("No se pudo adicionar la tapa. Verifique el espacio disponible.");
        } else if (visible) {
            erase();
            draw();
        }
    }

    /**
     * Remueve la tapa del tope de la torre (ultima ingresada).
     */
    public void popLid() {
        if (lids.isEmpty()) {
            showError("No hay tapas para remover.");
            lastOperationOk = false;
            return;
        }
        if (visible) { erase(); }
        removeLastLid();
        lastOperationOk = true;
        if (visible) { draw(); }
    }

    /**
     * Remueve una tapa especifica buscandola por el numero de su taza.
     * 
     * @param number numero de la taza cuya tapa se quiere remover
     */
    public void removeLid(int number) {
        for (int i = 0; i < lids.size(); i++) {
            if (lids.get(i).getCupNumber() == number) {
                if (visible) { erase(); }
                removeLidByIndex(i);
                lastOperationOk = true;
                if (visible) { draw(); }
                return;
            }
        }
        showError("No se encontro una tapa para la taza numero " + number + ".");
        lastOperationOk = false;
    }

    // ===================== REORGANIZACION =====================

    /**
     * Ordena los elementos de la torre de mayor a menor (base a cima).
     * El numero menor queda en la cima. Si la taza y su tapa estan en
     * la torre, la tapa se coloca sobre la taza. Solo incluye los que quepan.
     */
    public void orderTower() {
        Collections.sort(cups, new Comparator<Cup>() {
            public int compare(Cup c1, Cup c2) {
                return c2.getNumber() - c1.getNumber(); // descendente: mayor abajo
            }
        });
        reorderLidsAfterCups();
        removeElementsThatDontFit();
        lastOperationOk = true;
        if (visible) {
            erase();
            draw();
        }
    }

    /**
     * Invierte el orden actual de los elementos en la torre.
     * Solo incluye los elementos que quepan.
     */
    public void reverseTower() {
        Collections.reverse(cups);
        reorderLidsAfterCups();
        removeElementsThatDontFit();
        lastOperationOk = true;
        if (visible) {
            erase();
            draw();
        }
    }

    /**
     * Intercambia la posicion de dos objetos en la torre.
     * Los objetos se identifican por tipo ("cup" o "lid") y numero.
     * Ejemplo: swap({"cup","4"}, {"lid","4"})
     * 
     * @param o1 array {"tipo","numero"} del primer objeto
     * @param o2 array {"tipo","numero"} del segundo objeto
     */
    public void swap(String[] o1, String[] o2) {
        int idx1 = findElementIndex(o1);
        int idx2 = findElementIndex(o2);
        if (idx1 == -1 || idx2 == -1) {
            showError("Uno o ambos objetos no existen en la torre.");
            lastOperationOk = false;
            return;
        }
        if (visible) { erase(); }
        swapByIndex(idx1, idx2);
        lastOperationOk = true;
        if (visible) { draw(); }
    }

    /**
     * Tapa todas las tazas que tienen su tapa en la torre.
     * Las tazas tapadas cambian su apariencia visual.
     */
    /**
     * Tapa todas las tazas que tienen su tapa en la torre.
     * Mueve cada tapa para que quede inmediatamente encima de su taza.
     * Las tazas tapadas cambian su apariencia visual.
     */
    /**
     * Tapa todas las tazas que tienen su tapa en la torre.
     * Mueve cada tapa para que quede inmediatamente encima de su taza.
     * Las tazas tapadas cambian su apariencia visual.
     */
    public void cover() {
        // guardar las tapas que corresponden a tazas en la torre
        ArrayList<Lid> lidsToPlace = new ArrayList<Lid>();
        for (Cup cup : cups) {
            Lid lid = findLidByNumber(cup.getNumber());
            if (lid != null) {
                cup.setHasLid(true);
                lidsToPlace.add(lid);
            }
        }
        // reconstruir lids en el orden correcto (cada tapa encima de su taza)
        ArrayList<Lid> orderedLids = new ArrayList<Lid>();
        for (Cup cup : cups) {
            if (cup.hasLid()) {
                for (Lid lid : lidsToPlace) {
                    if (lid.getCupNumber() == cup.getNumber()) {
                        orderedLids.add(lid);
                        break;
                    }
                }
            }
        }
        lids = orderedLids;
        lastOperationOk = true;
        if (visible) { erase(); draw(); }
    }

    /**
     * Consulta que intercambio de dos objetos reduciria la altura de la torre.
     * Retorna los dos objetos a intercambiar en formato {"tipo","numero"}.
     * Si no hay intercambio posible retorna un array vacio.
     * 
     * @return array 2D con los dos objetos a intercambiar, o array vacio
     */
    public String[][] swapToReduce() {
        String[][] items = getStackingInfo();
        int currentHeight = getTotalHeight();
        for (int i = 0; i < items.length - 1; i++) {
            for (int j = i + 1; j < items.length; j++) {
                String[][] copy = copyItems(items);
                String[] temp = copy[i];
                copy[i] = copy[j];
                copy[j] = temp;
                int newHeight = simulateHeight(copy);
                if (newHeight < currentHeight) {
                    return new String[][]{items[i], items[j]};
                }
            }
        }
        return new String[0][0];
    }

    // ===================== CONSULTAS =====================

    /**
     * Calcula la altura total de los elementos apilados en la torre.
     * 
     * @return altura total en cm
     */
    public int height() {
        return getTotalHeight();
    }

    /**
     * Retorna los numeros de las tazas tapadas por sus tapas,
     * ordenados de menor a mayor.
     * 
     * @return array con numeros de las tazas que tienen tapa
     */
    public int[] lidedCups() {
        return getLidedCups();
    }

    /**
     * Retorna la informacion de los elementos apilados ordenados de base a cima.
     * Formato: {{"cup","4"},{"lid","4"}} en minusculas.
     * 
     * @return array 2D con [tipo, numero] de cada elemento
     */
    public String[][] stackingItems() {
        return getStackingInfo();
    }

    // ===================== VISIBILIDAD =====================

    /**
     * Hace visible el simulador y dibuja la torre.
     */
    public void makeVisible() {
        visible = true;
        draw();
    }

    /**
     * Hace invisible el simulador y borra la torre del canvas.
     */
    public void makeInvisible() {
        visible = false;
        erase();
    }

    // ===================== OTRAS OPERACIONES =====================

    /**
     * Termina el simulador.
     */
    public void exit() {
        erase();
        System.exit(0);
    }

    /**
     * Simula visualmente la solucion del problema de la maraton.
     * Recibe el orden de tazas calculado por StackingCups y las agrega
     * una por una al simulador con una pausa entre cada paso.
     * Si el orden es null o invalido, muestra un error.
     * 
     * @param cupOrder arreglo con los numeros de taza en orden de base a cima
     */
    public void simulate(int[] cupOrder) {
        if (cupOrder == null || cupOrder.length == 0) {
            showError("No hay solucion para simular.");
            lastOperationOk = false;
            return;
        }
        // limpiar la torre actual
        if (visible) { erase(); }
        cups.clear();
        lids.clear();
        if (visible) { drawTowerBase(); }

        // agregar las tazas una por una con pausa visual
        for (int i = 0; i < cupOrder.length; i++) {
            int cupNumber = cupOrder[i];
            Cup newCup = new Cup(cupNumber, generateRandomColor());
            cups.add(newCup);
            if (visible) {
                erase();
                draw();
                pause(600);
            }
        }
        lastOperationOk = true;
    }

    /**
     * Pausa la ejecucion por un tiempo dado en milisegundos.
     * Se usa para la animacion de simulate().
     * 
     * @param milliseconds tiempo de pausa en milisegundos
     */
    private void pause(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            // ignorar interrupcion
        }
    }

    /**
     * Indica si la ultima operacion realizada fue exitosa.
     * 
     * @return true si la ultima operacion fue exitosa, false si no
     */
    public boolean ok() {
        return lastOperationOk;
    }

    // ===================== METODOS INTERNOS DE COLECCION =====================

    /**
     * Agrega una taza a la lista si cabe en la torre.
     * 
     * @param cup taza a agregar
     * @return true si se agrego, false si no cabe
     */
    public boolean addCup(Cup cup) {
        if (cup.getNumber() > width) {
            lastOperationOk = false;
            return false;
        }
        if (getTotalHeight() + cup.getHeight() > maxHeight) {
            lastOperationOk = false;
            return false;
        }
        cups.add(cup);
        lastOperationOk = true;
        return true;
    }

    /**
     * Agrega una tapa a la lista si cabe en la torre.
     * 
     * @param lid tapa a agregar
     * @return true si se agrego, false si no cabe
     */
    public boolean addLid(Lid lid) {
        if (getTotalHeight() + lid.getHeight() > maxHeight) {
            lastOperationOk = false;
            return false;
        }
        Cup cup = findCupByNumber(lid.getCupNumber());
        if (cup != null) {
            cup.setHasLid(true);
        }
        lids.add(lid);
        lastOperationOk = true;
        return true;
    }

    /**
     * Remueve una taza por su indice interno.
     * Si tenia tapa, la remueve tambien.
     * 
     * @param index indice de la taza en la lista
     * @return taza removida, o null si el indice es invalido
     */
    public Cup removeCupByIndex(int index) {
        if (index < 0 || index >= cups.size()) {
            return null;
        }
        Cup removed = cups.remove(index);
        if (removed.hasLid()) {
            removeLidByNumber(removed.getNumber());
            removed.setHasLid(false);
        }
        return removed;
    }

    /**
     * Remueve la ultima taza de la torre.
     * 
     * @return taza removida, o null si no hay tazas
     */
    public Cup removeLastCup() {
        if (cups.isEmpty()) {
            return null;
        }
        return removeCupByIndex(cups.size() - 1);
    }

    /**
     * Remueve una tapa por su indice interno.
     * Actualiza el estado hasLid de la taza correspondiente.
     * 
     * @param index indice de la tapa en la lista
     * @return tapa removida, o null si el indice es invalido
     */
    public Lid removeLidByIndex(int index) {
        if (index < 0 || index >= lids.size()) {
            return null;
        }
        Lid removed = lids.remove(index);
        Cup cup = findCupByNumber(removed.getCupNumber());
        if (cup != null) {
            cup.setHasLid(false);
        }
        return removed;
    }

    /**
     * Remueve la ultima tapa de la torre.
     * 
     * @return tapa removida, o null si no hay tapas
     */
    public Lid removeLastLid() {
        if (lids.isEmpty()) {
            return null;
        }
        return removeLidByIndex(lids.size() - 1);
    }

    /**
     * Remueve una tapa buscandola por numero de taza (uso interno).
     * 
     * @param cupNumber numero de la taza
     * @return tapa removida, o null si no existe
     */
    private Lid removeLidByNumber(int cupNumber) {
        for (int i = 0; i < lids.size(); i++) {
            if (lids.get(i).getCupNumber() == cupNumber) {
                return removeLidByIndex(i);
            }
        }
        return null;
    }

    /**
     * Calcula la altura total de todos los elementos apilados.
     * 
     * @return altura total en cm
     */
    public int getTotalHeight() {
        int total = 0;
        for (Cup cup : cups) {
            total += cup.getHeight();
            if (cup.hasLid()) {
                total += 1; // tapa siempre mide 1 cm
            }
        }
        return total;
    }

    /**
     * Retorna los numeros de las tazas que tienen tapa, ordenados de menor a mayor.
     * 
     * @return array con los numeros de las tazas tapadas
     */
    public int[] getLidedCups() {
        ArrayList<Integer> lidedNumbers = new ArrayList<Integer>();
        for (Cup cup : cups) {
            if (cup.hasLid()) {
                lidedNumbers.add(cup.getNumber());
            }
        }
        Collections.sort(lidedNumbers);
        int[] result = new int[lidedNumbers.size()];
        for (int i = 0; i < lidedNumbers.size(); i++) {
            result[i] = lidedNumbers.get(i);
        }
        return result;
    }

    /**
     * Retorna la informacion de todos los elementos apilados de base a cima.
     * 
     * @return array 2D con [tipo, numero] por elemento
     */
    public String[][] getStackingInfo() {
        ArrayList<String[]> info = new ArrayList<String[]>();
        for (Cup cup : cups) {
            info.add(new String[]{"cup", String.valueOf(cup.getNumber())});
            if (cup.hasLid()) {
                info.add(new String[]{"lid", String.valueOf(cup.getNumber())});
            }
        }
        String[][] result = new String[info.size()][2];
        for (int i = 0; i < info.size(); i++) {
            result[i] = info.get(i);
        }
        return result;
    }

    /**
     * Obtiene el numero de tazas actuales en la torre.
     * 
     * @return cantidad de tazas
     */
    public int getCupsCount() {
        return cups.size();
    }

    /**
     * Obtiene el numero de tapas actuales en la torre.
     * 
     * @return cantidad de tapas
     */
    public int getLidsCount() {
        return lids.size();
    }

    // ===================== VISUALIZACION =====================

    /**
     * Dibuja la torre completa con todos sus elementos desde la base hacia arriba.
     * Los elementos se centran respecto al ancho de la torre.
     */
    public void draw() {
        if (!visible) return;
        int towerCenterX = BASE_X + (width * SCALE / 2);
        int currentY = BASE_Y;
        for (Cup cup : cups) {
            int cupHeightPx = cup.getHeight() * SCALE;
            int cupWidthPx  = cup.getWidth();
            int cupX = towerCenterX - (cupWidthPx / 2);
            currentY -= cupHeightPx;
            cup.makeInvisible();
            cup.moveTo(cupX, currentY);
            cup.makeVisible();
            if (cup.hasLid()) {
                Lid lid = findLidByNumber(cup.getNumber());
                if (lid != null) {
                    int lidHeightPx = lid.getHeight() * SCALE;
                    int lidWidthPx  = lid.getWidth();
                    int lidX = towerCenterX - (lidWidthPx / 2);
                    currentY -= lidHeightPx;
                    lid.makeInvisible();
                    lid.moveTo(lidX, currentY);
                    lid.makeVisible();
                }
            }
        }
    }

    /**
     * Borra la representacion visual de todos los elementos de la torre.
     */
    public void erase() {
        for (Cup cup : cups) {
            cup.makeInvisible();
        }
        for (Lid lid : lids) {
            lid.makeInvisible();
        }
    }

    /**
     * Dibuja la base visual de la torre con marcas de centimetros de altura.
     * No se adicionan numeros, solo marcas visuales.
     */
    private void drawTowerBase() {
        canvas.setVisible(true);
        // marcas de altura se pueden implementar con Rectangle delgados
    }

    // ===================== METODOS AUXILIARES PRIVADOS =====================

    /**
     * Busca una taza por su numero.
     * 
     * @param number numero de la taza
     * @return la taza encontrada, o null si no existe
     */
    /**
     * Busca el indice de un elemento en el orden de apilamiento.
     * El elemento se identifica por tipo ("cup" o "lid") y numero.
     *
     * @param obj array {"tipo","numero"} del objeto
     * @return indice en stackingInfo, o -1 si no existe
     */
    private int findElementIndex(String[] obj) {
        String[][] items = getStackingInfo();
        for (int i = 0; i < items.length; i++) {
            if (items[i][0].equals(obj[0]) && items[i][1].equals(obj[1])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Intercambia dos elementos en la torre por sus indices en stackingInfo.
     *
     * @param idx1 indice del primer elemento
     * @param idx2 indice del segundo elemento
     */
    private void swapByIndex(int idx1, int idx2) {
        String[][] items = getStackingInfo();
        if (idx1 < 0 || idx2 < 0 || idx1 >= items.length || idx2 >= items.length) return;
        String[] temp = items[idx1];
        items[idx1] = items[idx2];
        items[idx2] = temp;
        rebuildFromInfo(items);
    }

    /**
     * Reconstruye las listas de cups y lids a partir de un array de stackingInfo.
     *
     * @param items array 2D con el nuevo orden de elementos
     */
    /**
     * Crea una copia del array de stackingInfo.
     */
    private String[][] copyItems(String[][] items) {
        String[][] copy = new String[items.length][2];
        for (int i = 0; i < items.length; i++) {
            copy[i] = new String[]{items[i][0], items[i][1]};
        }
        return copy;
    }

    /**
     * Simula la altura de la torre dado un orden hipotetico de elementos.
     * Una tapa reduce la altura si queda inmediatamente encima de su taza.
     */
    /**
     * Simula la altura de la torre dado un orden hipotetico de elementos.
     * Solo cuenta los elementos que quepan dentro del ancho y altura maxima.
     */
    private int simulateHeight(String[][] items) {
        int height = 0;
        for (int i = 0; i < items.length; i++) {
            String type = items[i][0];
            int num = Integer.parseInt(items[i][1]);
            int elementHeight = type.equals("cup") ? num : 1;
            // verificar ancho solo para tazas
            if (type.equals("cup") && num > width) continue;
            // verificar si cabe en altura
            if (height + elementHeight <= maxHeight) {
                height += elementHeight;
            }
        }
        return height;
    }

    /**
     * Busca una tapa en la lista original de lids (ignorando hasLid de cup).
     */
    private Lid findLidInOriginal(int cupNumber) {
        for (Lid lid : lids) {
            if (lid.getCupNumber() == cupNumber) {
                return lid;
            }
        }
        return null;
    }

    private void rebuildFromInfo(String[][] items) {
        ArrayList<Cup> newCups = new ArrayList<Cup>();
        ArrayList<Lid> newLids = new ArrayList<Lid>();
        for (Cup cup : cups) { cup.setHasLid(false); }
        for (String[] item : items) {
            int num = Integer.parseInt(item[1]);
            if (item[0].equals("cup")) {
                Cup cup = findCupByNumber(num);
                if (cup != null) newCups.add(cup);
            } else if (item[0].equals("lid")) {
                Lid lid = findLidByNumber(num);
                if (lid != null) {
                    newLids.add(lid);
                    Cup cup = findCupByNumber(num);
                    if (cup != null) cup.setHasLid(true);
                }
            }
        }
        cups = newCups;
        lids = newLids;
    }

    private Cup findCupByNumber(int number) {
        for (Cup cup : cups) {
            if (cup.getNumber() == number) {
                return cup;
            }
        }
        return null;
    }

    /**
     * Busca una tapa por el numero de su taza.
     * 
     * @param cupNumber numero de la taza
     * @return la tapa encontrada, o null si no existe
     */
    private Lid findLidByNumber(int cupNumber) {
        for (Lid lid : lids) {
            if (lid.getCupNumber() == cupNumber) {
                return lid;
            }
        }
        return null;
    }

    /**
     * Reordena la lista de tapas para que coincida con el orden actual de tazas.
     */
    private void reorderLidsAfterCups() {
        ArrayList<Lid> ordered = new ArrayList<Lid>();
        for (Cup cup : cups) {
            Lid lid = findLidByNumber(cup.getNumber());
            if (lid != null) {
                ordered.add(lid);
            }
        }
        lids = ordered;
    }

    /**
     * Elimina elementos que excedan la altura maxima o el ancho de la torre.
     */
    private void removeElementsThatDontFit() {
        int currentHeight = 0;
        ArrayList<Cup> fittingCups = new ArrayList<Cup>();
        ArrayList<Lid> fittingLids = new ArrayList<Lid>();
        for (Cup cup : cups) {
            int elementHeight = cup.getHeight();
            if (cup.hasLid()) {
                elementHeight += 1;
            }
            if (currentHeight + elementHeight <= maxHeight && cup.getNumber() <= width) {
                fittingCups.add(cup);
                if (cup.hasLid()) {
                    Lid lid = findLidByNumber(cup.getNumber());
                    if (lid != null) {
                        fittingLids.add(lid);
                    }
                }
                currentHeight += elementHeight;
            } else {
                cup.setHasLid(false);
            }
        }
        cups = fittingCups;
        lids = fittingLids;
    }

    /**
     * Genera un color aleatorio para asignar a una nueva taza.
     * 
     * @return nombre del color generado
     */
    private String generateRandomColor() {
        String[] colors = {"red", "blue", "green", "yellow", "magenta", "cyan"};
        return colors[(int)(Math.random() * colors.length)];
    }

    /**
     * Muestra un mensaje de error al usuario usando JOptionPane.
     * Solo se muestra si el simulador esta en modo visible.
     * 
     * @param message mensaje de error a mostrar
     */
    private void showError(String message) {
        if (visible) {
            JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}