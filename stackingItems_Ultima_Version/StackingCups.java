import java.util.ArrayList;

/**
 * Resuelve el problema de la maraton Stacking Cups 2025.
 * Dado n tazas y altura objetivo h, encuentra el orden de apilamiento.
 * No realiza ninguna visualizacion.
 * 
 * @author Sergio Gonzalez
 * @version 15mar26
 */
public class StackingCups {

    private int n;
    private long h;
    private int[] solution;
    private boolean solved;

    public StackingCups(int n, long h) {
        this.n = n;
        this.h = h;
        this.solution = null;
        this.solved = false;
    }

    public boolean solve() {
        if (!isSolvable()) {
            solved = false;
            solution = null;
            return false;
        }
        ArrayList<Integer> available = new ArrayList<Integer>();
        for (int i = 1; i <= n; i++) {
            available.add(i);
        }
        int[] result = backtrack(available, new ArrayList<Integer>(), 0);
        if (result != null) {
            solution = result;
            solved = true;
            return true;
        }
        solved = false;
        solution = null;
        return false;
    }

    public int[] getSolution() { return solution; }

    public int[] getSolutionHeights() {
        if (solution == null) return null;
        int[] heights = new int[solution.length];
        for (int i = 0; i < solution.length; i++) {
            heights[i] = 2 * solution[i] - 1;
        }
        return heights;
    }

    public boolean isSolvable() {
        return h >= getMinHeight() && h <= getMaxHeight();
    }

    public long getMinHeight() { return 2L * n - 1; }

    public long getMaxHeight() { return (long) n * n; }

    public boolean verifySolution() {
        if (solution == null) return false;
        return calculateHeight(solution) == h;
    }

    // ===================== PRIVADOS =====================

    private int[] backtrack(ArrayList<Integer> remaining,
                            ArrayList<Integer> current,
                            long currentMax) {
        if (remaining.isEmpty()) {
            return currentMax == h ? toArray(current) : null;
        }
        long maxPossible = currentMax;
        for (int cup : remaining) {
            maxPossible += 2L * cup - 1;
        }
        if (maxPossible < h) return null;

        for (int i = 0; i < remaining.size(); i++) {
            int cup = remaining.get(i);
            ArrayList<Integer> newOrder = new ArrayList<Integer>(current);
            newOrder.add(cup);
            long newHeight = calculateHeight(newOrder);
            if (newHeight > h) continue;
            ArrayList<Integer> newRemaining = new ArrayList<Integer>(remaining);
            newRemaining.remove(i);
            int[] result = backtrack(newRemaining, newOrder, newHeight);
            if (result != null) return result;
        }
        return null;
    }

    private long calculateHeight(ArrayList<Integer> order) {
        if (order.isEmpty()) return 0;
        long[] bottoms = new long[order.size()];
        long[] tops = new long[order.size()];
        bottoms[0] = 0;
        tops[0] = 2L * order.get(0) - 1;
        for (int i = 1; i < order.size(); i++) {
            int cup = order.get(i);
            int prev = order.get(i - 1);
            bottoms[i] = (cup < prev) ? bottoms[i - 1] + 1 : tops[i - 1];
            tops[i] = bottoms[i] + 2L * cup - 1;
        }
        long maxTop = 0;
        for (long top : tops) {
            if (top > maxTop) maxTop = top;
        }
        return maxTop;
    }

    private long calculateHeight(int[] order) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int cup : order) list.add(cup);
        return calculateHeight(list);
    }

    private int[] toArray(ArrayList<Integer> list) {
        int[] result = new int[list.size()];
        for (int i = 0; i < list.size(); i++) result[i] = list.get(i);
        return result;
    }
}