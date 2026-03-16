/**
 * Cup representa una taza en la torre de apilamiento.
 * Cada taza tiene un numero (tamaño), color y puede o no tener tapa.
 * La representacion visual se realiza con un rectangulo.
 * 
 * @author Sergio Gonzalez 
 * @version 03Mar26
 */
public class Cup {
    // constante de escala visual (debe coincidir con Tower.SCALE)
    private static final int SCALE = 20;

    // atributos
    private int number;
    private String color;
    private int yPosition;
    private Rectangle rectangle;
    private boolean hasLid;

    public Cup(int number, String color) {
        this.number = number;
        this.color = color;
        this.yPosition = 0;
        this.hasLid = false;
        this.rectangle = new Rectangle();
        rectangle.changeColor(color);
        rectangle.changeSize(number * SCALE, number * SCALE);
    }

    public void makeVisible() {
        rectangle.changeColor(hasLid ? darkenColor(color) : color);
        rectangle.makeVisible();
    }

    public void makeInvisible() {
        rectangle.makeInvisible();
    }

    public void moveTo(int x, int y) {
        rectangle.moveTo(x, y);
        yPosition = y;
    }

    public int getNumber() { return number; }
    public String getColor() { return color; }
    public int getWidth() { return number * SCALE; }
    public int getHeight() { return number; }
    public boolean hasLid() { return hasLid; }
    public void setHasLid(boolean value) { this.hasLid = value; }
    public int getYPosition() { return yPosition; }

    /**
     * Retorna una version diferente del color para indicar que la taza esta tapada.
     * 
     * @param color color original de la taza
     * @return color alternativo para taza tapada
     */
    private String darkenColor(String color) {
        if (color.equals("red"))     return "magenta";
        if (color.equals("blue"))    return "cyan";
        if (color.equals("green"))   return "black";
        if (color.equals("yellow"))  return "green";
        if (color.equals("magenta")) return "red";
        if (color.equals("cyan"))    return "blue";
        return "black";
    }
}