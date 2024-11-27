package main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Goal {
    private int x, y;
    private int width = 20;    
    private int height = 120;  
    private int POST_WIDTH = 20;
    private int POST_HEIGHT = 260; 
    private boolean isLeftGoal; 
    private Rectangle leftPost;
    private Rectangle rightPost;
    private Rectangle crossbar;
    public Rectangle goalArea;
   
    public Goal(int x, int y, boolean isLeftGoal) {
        this.x = x;
        this.y = y;
        this.isLeftGoal = isLeftGoal;

        leftPost = new Rectangle(
                isLeftGoal ? x + 50 : x - 50, 
                isLeftGoal ? y - 250 : y - 290,
                POST_WIDTH,
                POST_HEIGHT
            );

       
        rightPost = new Rectangle(
            isLeftGoal ? x + 50 : x - 50, 
            isLeftGoal ? y + 190 : y + 150,
            POST_WIDTH,
            POST_HEIGHT
        );

       
        crossbar = new Rectangle(
            isLeftGoal ? x : x - width,
            y,
            POST_WIDTH + POST_WIDTH, 
            POST_HEIGHT
        );
        
        goalArea = new Rectangle(
                isLeftGoal ? x + 35 : x - 45,
                isLeftGoal ? y + 50 : y,
                30,
                height
            );
    }

   
    public void draw(Graphics g) {
        
        Graphics2D g2d = (Graphics2D) g;

        
        float alpha = 0.0f;  
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        
        g2d.setColor(Color.RED);  

        
        g2d.fillRect(leftPost.x, leftPost.y, leftPost.width, leftPost.height);
        
        g2d.fillRect(rightPost.x, rightPost.y, rightPost.width, rightPost.height);

        
        // g2d.fillRect(crossbar.x, crossbar.y, crossbar.width, crossbar.height);
        
        g2d.fillRect(goalArea.x, goalArea.y, goalArea.width, goalArea.height);
    }

   
    
    public boolean checkGoal(Ball ball) {
        
        return goalArea.intersects(
            ball.getX(),
            ball.getY(),
            ball.getDiameter(),
            ball.getDiameter()
        );
    }
    
    public void checkCollisionWithPost(Ball ball) {
        // Factor de rebote (ajustable)
        double reboundFactor = 1; // Ajusta según la experiencia deseada

        // Verificar colisiones con los postes y el travesaño
        boolean leftPostCollision = leftPost.intersects(
            ball.getX(),
            ball.getY(),
            ball.getDiameter(),
            ball.getDiameter()
        );

        boolean rightPostCollision = rightPost.intersects(
            ball.getX(),
            ball.getY(),
            ball.getDiameter(),
            ball.getDiameter()
        );

        boolean crossbarCollision = crossbar.intersects(
            ball.getX(),
            ball.getY(),
            ball.getDiameter(),
            ball.getDiameter()
        );

        if (leftPostCollision || rightPostCollision || crossbarCollision) {
            // Rebote
            double newVelX = -ball.getVelX() * reboundFactor;
            double newVelY = -ball.getVelY() * reboundFactor;
            ball.setVelocity(newVelX, newVelY);

            // Separación en caso de superposición
            double overlap = (ball.getDiameter() / 2 + POST_WIDTH / 2) -
                             Math.sqrt(Math.pow(ball.getX() - x, 2) + Math.pow(ball.getY() - y, 2));
            if (overlap > 0) {
                ball.setPosition(ball.getX() + newVelX * overlap, ball.getY() + newVelY * overlap);
            }
        }
    }

    
    public void checkCollisionWithPost(Player player) {
        // Factor de rebote (ajustable)
        double reboundFactor = 1.2;

        // Verificar colisiones con los postes y el travesaño
        boolean leftPostCollision = leftPost.intersects(
            player.getX(),
            player.getY(),
            player.getDiameter(),
            player.getDiameter()
        );

        boolean rightPostCollision = rightPost.intersects(
            player.getX(),
            player.getY(),
            player.getDiameter(),
            player.getDiameter()
        );

        boolean crossbarCollision = crossbar.intersects(
            player.getX(),
            player.getY(),
            player.getDiameter(),
            player.getDiameter()
        );

        if (leftPostCollision || rightPostCollision || crossbarCollision) {
            // Rebote
            double newVelX = -player.getVelX() * reboundFactor;
            double newVelY = -player.getVelY() * reboundFactor;
            player.setVelocity(newVelX, newVelY);

            // Separación en caso de superposición
            double overlap = (player.getDiameter() / 2 + POST_WIDTH / 2) -
                             Math.sqrt(Math.pow(player.getX() - x, 2) + Math.pow(player.getY() - y, 2));
            if (overlap > 0) {
                player.setPosition(player.getX() + newVelX * overlap, player.getY() + newVelY * overlap);
            }
        }
    }
    
    public void setPosition(int newX, int newY) {
        this.x = newX;
        this.y = newY;

        // Actualizar las posiciones de los postes
        leftPost.setBounds(
            isLeftGoal ? x + 50 : x - 50,
            isLeftGoal ? y : y - 40,
            POST_WIDTH,
            POST_HEIGHT
        );

        rightPost.setBounds(
            isLeftGoal ? x + 50 : x - 50,
            isLeftGoal ? y + 190 : y + 150,
            POST_WIDTH,
            POST_HEIGHT
        );

        crossbar.setBounds(
            isLeftGoal ? x : x - width,
            y,
            POST_WIDTH + POST_WIDTH,
            POST_HEIGHT
        );
    }



}