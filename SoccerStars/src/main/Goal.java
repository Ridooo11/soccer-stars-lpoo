package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Goal {
    private int x, y;
    private int width = 20;    // Ancho del poste
    private int height = 120;  // Alto del arco
    private boolean isLeftGoal; // Para distinguir arco izquierdo del derecho
   
    public Goal(int x, int y, boolean isLeftGoal) {
        this.x = x;
        this.y = y;
        this.isLeftGoal = isLeftGoal;
    }
   
    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        // Dibujar los postes verticales
        g.fillRect(x, y, width, height);
        // Dibujar el travesaño horizontal
        g.fillRect(isLeftGoal ? x : x - 30, y, 30, width);
    }
   
    // Método para detectar si la pelota entró al arco
    public boolean checkGoal(Ball ball) {
        Rectangle goalArea = new Rectangle(
            isLeftGoal ? x - 10 : x,
            y,
            30,
            height
        );
        return goalArea.intersects(
            ball.getX(),
            ball.getY(),
            ball.getDiameter(),
            ball.getDiameter()
        );
    }
}