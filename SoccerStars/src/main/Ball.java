package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.ImageIcon;

public class Ball {
private double x, y;
    private double velX, velY;
    private double spin = 0; // Nueva variable para el efecto
    private final int DIAMETER = 40;
    private final double FRICTION = 0.99;
    private final double SPIN_DECAY = 0.98; // Decaimiento del efecto
    private final double SPIN_FACTOR = 0.08; // Qué tanto afecta el efecto a la trayectoria
    private ImageIcon ballImage;

    public Ball(int x, int y) {
        this.x = x;
        this.y = y;
        
        loadBallImage();
    }
    
    private void loadBallImage() {
        try {
            String path = "/resources/ball.png"; // Ruta de la imagen ball.png
            InputStream is = getClass().getResourceAsStream(path);

            if (is == null) {
                System.out.println("Error: no se encontró la imagen en la ruta " + path);
                return;
            }

            // Cargar la imagen con ImageIcon
            ImageIcon icon = new ImageIcon(is.readAllBytes());

            // Redimensionar la imagen para que tenga el tamaño correcto (40x40)
            Image img = icon.getImage().getScaledInstance(DIAMETER, DIAMETER, Image.SCALE_SMOOTH);
            ballImage = new ImageIcon(img);  // Devolver el ImageIcon redimensionado
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // 1. Dibujar la imagen de la pelota (primero)
        if (ballImage != null) {
        	g2d.drawImage(ballImage.getImage(), (int) x, (int) y, DIAMETER, DIAMETER, null);
        } else {
            // Si la imagen no se carga, dibujar un círculo gris como fallback
            g2d.setColor(Color.GRAY);
            g2d.fillOval((int) x, (int) y, DIAMETER, DIAMETER);
        }

        // 2. Dibujar los efectos visuales (como el indicador de spin)
        if (Math.abs(spin) > 0.1) {
            g2d.setColor(new Color(255, 0, 0, 100));  // Rojo con transparencia
            int spinIndicatorSize = 4;  // Tamaño del indicador
            g2d.fillOval((int)(x + DIAMETER / 2 - spinIndicatorSize / 2),
                        (int)(y + DIAMETER / 2 - spinIndicatorSize / 2),
                        spinIndicatorSize, spinIndicatorSize);
        }

        // Otras posibles representaciones visuales de la pelota o física (por ejemplo, una línea de dirección, etc.)
    }

    public void update() {
        // Aplicar efecto a la velocidad
        if (Math.abs(spin) > 0.1) {
            // El efecto afecta perpendicular al movimiento
            double speed = Math.sqrt(velX * velX + velY * velY);
            if (speed > 0.1) {
                // Calcular vector perpendicular normalizado
                double perpX = -velY / speed;
                double perpY = velX / speed;
               
                // Aplicar fuerza perpendicular basada en el efecto
                velX += perpX * spin * SPIN_FACTOR;
                velY += perpY * spin * SPIN_FACTOR;
            }
           
            // Reducir el efecto gradualmente
            spin *= SPIN_DECAY;
        }

        // Actualizar posición
        x += velX;
        y += velY;

        // Aplicar fricción
        velX *= FRICTION;
        velY *= FRICTION;

        // Detener cuando la velocidad es muy baja
        if (Math.abs(velX) < 2) velX = 0;
        if (Math.abs(velY) < 2) velY = 0;
    }
   
    public void addSpin(double spinAmount) {
        this.spin += spinAmount;
        // Limitar el efecto máximo
        this.spin = Math.max(-10, Math.min(10, this.spin));
    }


    public int getX() {
        return (int) x;
    }

    public int getY() {
        return (int) y;
    }

    public int getDiameter() {
        return DIAMETER;
    }

    public void setVelocity(double velX, double velY) {
        this.velX = velX;
        this.velY = velY;
    }

 // Getters para las velocidades
    public double getVelX() {
        return velX;
    }
   
    public double getVelY() {
        return velY;
    }
   
    // Método para establecer la posición
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
    

    // Método para manejar colisiones con los bordes
    public void checkWallCollision(int width, int height) {
        if (x < 0) {
            x = 0;
            velX = -velX;
        } else if (x + DIAMETER > width) {
            x = width - DIAMETER;
            velX = -velX;
        }

        if (y < 0) {
            y = 0;
            velY = -velY;
        } else if (y + DIAMETER > height) {
            y = height - DIAMETER;
            velY = -velY;
        }
    }
   
    // Método para verificar si la pelota está estática
    public boolean isStatic() {
        return Math.abs(velX) < 0.1 && Math.abs(velY) < 0.1;
    }
}
