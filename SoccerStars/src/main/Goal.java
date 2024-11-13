package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Goal {
    private int x, y;
    private int width = 20;    
    private int height = 120;  
    private int POST_WIDTH = 30;
    private int POST_HEIGHT = 10; 
    private boolean isLeftGoal; 
    private Rectangle leftPost;
    private Rectangle rightPost;
    private Rectangle crossbar;
   
    public Goal(int x, int y, boolean isLeftGoal) {
        this.x = x;
        this.y = y;
        this.isLeftGoal = isLeftGoal;
        
        leftPost = new Rectangle(x, y, width, height);
        rightPost = new Rectangle(isLeftGoal ? x + 50 : x, y, POST_WIDTH, POST_HEIGHT);
        crossbar = new Rectangle(isLeftGoal ? x + 10 : x, y, POST_WIDTH, POST_HEIGHT);
    }
   
    public void draw(Graphics g) {
    	g.setColor(Color.WHITE);
        // Dibujar el poste vertical principal
        //g.fillRect(x, y, width, height);
        // Dibujar el travesaño horizontal
        g.fillRect(isLeftGoal ? x + 50 : x - 30, y, POST_WIDTH, POST_HEIGHT);

        // Dibujar bordes rojos para visualizar las hitboxes
        g.setColor(Color.RED);
        //g.drawRect(x, y, width, height); 
        g.drawRect(isLeftGoal ? x + 50 : x - 30, y, POST_WIDTH, POST_HEIGHT); 
    }
   
    
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
    
    public void checkCollisionWithPost(Ball ball) {
        // Factor de rebote (ajustable)
        double reboundFactor = 0.8; // Entre 0 y 1, donde 1 es rebote completo y 0 es sin rebote
        
        // Verificar colisiones con los postes y el travesaño
        if (leftPost.intersects(ball.getX(), ball.getY(), ball.getDiameter(), ball.getDiameter()) ||
            rightPost.intersects(ball.getX(), ball.getY(), ball.getDiameter(), ball.getDiameter()) ||
            crossbar.intersects(ball.getX(), ball.getY(), ball.getDiameter(), ball.getDiameter())) {
            
            // Colisión detectada, aplicar rebote
            double newVelX = -ball.getVelX() * reboundFactor;
            double newVelY = -ball.getVelY() * reboundFactor;

            // Establecer nuevas velocidades al balón
            ball.setVelocity(newVelX, newVelY);
            
            // Opcional: Separar el balón de la colisión si se queda pegado (ajustar su posición)
            double overlap = (ball.getDiameter() / 2 + POST_WIDTH / 2) - Math.sqrt(Math.pow(ball.getX() - x, 2) + Math.pow(ball.getY() - y, 2));
            if (overlap > 0) {
                ball.setPosition(ball.getX() + newVelX * overlap, ball.getY() + newVelY * overlap);
            }
        }
    }

}