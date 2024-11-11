
package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

public class Player {
	private int x, y;
    private double velX, velY;
    final int DIAMETER = 40;
    private final double FRICTION = 0.98;
    private final double FORCE_MULTIPLIER = 0.2;
    private final double COLLISION_ELASTICITY = 0.8;
    private Color teamColor; // Nuevo: color del equipo
    
    public Player(int x, int y, Color teamColor) {
        this.x = x;
        this.y = y;
        this.teamColor = teamColor;
    }
    
    // Modificar el método draw para usar el color del equipo
    public void draw(Graphics g) {
        g.setColor(teamColor);
        g.fillOval(x, y, DIAMETER, DIAMETER);
        // Agregar un borde blanco para mejor visibilidad
        g.setColor(Color.WHITE);
        g.drawOval(x, y, DIAMETER, DIAMETER);
    }
    
 // Método para colisión con otro jugador
    public boolean collidesWithPlayer(Player other) {
        double dx = (x + DIAMETER / 2) - (other.getX() + other.getDiameter() / 2);
        double dy = (y + DIAMETER / 2) - (other.getY() + other.getDiameter() / 2);
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance < (DIAMETER / 2 + other.getDiameter() / 2);
    }
    
    // Getters y setters necesarios
    public double getVelX() {
        return velX;
    }
    public double getVelY() {
        return velY;
    }

    public void setVelocity(double velX, double velY) {
            this.velX = velX;
            this.velY = velY;
    }


    public void setPosition(double x, double y) {
        this.x = (int) x;  // Casting explícito de double a int
        this.y = (int) y;
    }	
    
 // Método para obtener la posición X
    public int getX() {
        return (int) x;
    }

    // Método para obtener la posición Y
    public int getY() {
        return (int) y;
    }

    // Método para obtener el diámetro del jugador
    public int getDiameter() {
        return DIAMETER;
    }

    public void update() {
            x += (int)velX;
            y += (int)velY;

            // Aplicar fricción normal
            velX *= FRICTION;
            velY *= FRICTION;

        // Detener el movimiento cuando la velocidad es muy baja
        if (Math.abs(velX) < 1) velX = 0;
        if (Math.abs(velY) < 1) velY = 0;
    }
    
    
    public void shoot(double dx, double dy) {
        // Calcular la magnitud del vector
        double magnitude = Math.sqrt(dx * dx + dy * dy);
        
        // Normalizar el vector
        double dirX = dx / magnitude;
        double dirY = dy / magnitude;
        
        // Aplicar la fuerza con el multiplicador
        this.velX = -dirX * magnitude * FORCE_MULTIPLIER;
        this.velY = -dirY * magnitude * FORCE_MULTIPLIER;
    }
   
    public boolean contains(Point p) {
        return new Rectangle(x, y, DIAMETER, DIAMETER).contains(p);
    }

    // Método para verificar colisión con la pelota
    public boolean collidesWith(Ball ball) {
        double dx = (x + DIAMETER / 2) - (ball.getX() + ball.getDiameter() / 2);
        double dy = (y + DIAMETER / 2) - (ball.getY() + ball.getDiameter() / 2);
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance < (DIAMETER / 2 + ball.getDiameter() / 2);
    }

    public void handleCollision(Ball ball) {
        // Obtener los centros
        double playerCenterX = x + DIAMETER / 2.0;
        double playerCenterY = y + DIAMETER / 2.0;
        double ballCenterX = ball.getX() + ball.getDiameter() / 2.0;
        double ballCenterY = ball.getY() + ball.getDiameter() / 2.0;

        // Calcular vector de colisión
        double dx = ballCenterX - playerCenterX;
        double dy = ballCenterY - playerCenterY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance == 0) return; // Evitar división por cero

        // Normalizar el vector de colisión
        dx /= distance;
        dy /= distance;

        // Calcular velocidades relativas
        double relativeVelX = ball.getVelX() - velX;
        double relativeVelY = ball.getVelY() - velY;

        // Calcular producto punto de velocidad relativa y normal de colisión
        double dotProduct = relativeVelX * dx + relativeVelY * dy;

        // Calcular impulso
        double impulseMagnitude = -(1 + COLLISION_ELASTICITY) * dotProduct;
        
        // Calcular efecto basado en el punto de impacto y la velocidad relativa
        double spinFactor = (relativeVelX * dy - relativeVelY * dx) * 0.1;
        
        // Aplicar efecto a la pelota
        ball.addSpin(spinFactor);

        // Calcular nuevas velocidades
        double newBallVelX = ball.getVelX() + dx * impulseMagnitude;
        double newBallVelY = ball.getVelY() + dy * impulseMagnitude;

        // Aplicar las nuevas velocidades
        ball.setVelocity(newBallVelX, newBallVelY);

        // Separar los objetos para evitar que se peguen
        double overlap = (DIAMETER/2 + ball.getDiameter()/2) - distance;
        if (overlap > 0) {
            ball.setPosition(
                ball.getX() + dx * overlap,
                ball.getY() + dy * overlap
            );
        }
    }
    
 // Método adicional para agregar efecto intencionalmente
    public void addSpinToBall(Ball ball, double hitAngle) {
        // Calcular el efecto basado en el ángulo de golpe
        double spinAmount = Math.sin(hitAngle) * 5.0; // 5.0 es la cantidad máxima de efecto
        ball.addSpin(spinAmount);
    }

    public void checkWallCollision(int width, int height) {
        double reboundFactor = 0.7; // Reduce la velocidad en cada rebote (entre 0 y 1)

        if (x < 0) {
            x = 0;
            velX = -velX * reboundFactor;
        } else if (x + DIAMETER > width) {
            x = width - DIAMETER;
            velX = -velX * reboundFactor;
        }

        if (y < 0) {
            y = 0;
            velY = -velY * reboundFactor;
        } else if (y + DIAMETER > height) {
            y = height - DIAMETER;
            velY = -velY * reboundFactor;
        }
    }
    
    // Método para verificar si el jugador está estático
    public boolean isStatic() {
        return Math.abs(velX) < 0.1 && Math.abs(velY) < 0.1;
    }
    
    // Método para resetear la posición
    public void reset(int x, int y) {
        this.x = x;
        this.y = y;
        this.velX = 0;
        this.velY = 0;
    }
    
    public void move() {
        // Método para actualizar posición en base a la velocidad (si es necesario)
    }
}
