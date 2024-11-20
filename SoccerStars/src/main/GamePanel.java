
package main;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import javax.swing.Timer;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import java.io.File;

public class GamePanel extends JPanel implements ActionListener, MouseListener, MouseMotionListener {

	// Modificar dimensiones
    private final int WIDTH = 1280;          // Aumentamos ancho
    private final int HEIGHT = 800;          // Aumentamos alto
    private final int HEADER_HEIGHT = 80;    // Espacio para marcador y nombres
    private final int FIELD_HEIGHT = HEIGHT - HEADER_HEIGHT; // Altura real del campo
    private boolean dragging = false;
    private Point dragStart;
    private Point dragCurrent;
    private final int MAX_DRAG_LENGTH = 100;
    private boolean isSpinEnabled = false; // Variable para controlar si el efecto está activado
    private double spinAngle = 0.0;        // Ángulo para el efecto
    
    private Player player1;
    private Player player2;
    private Ball ball;
    private Goal goal;
    private Goal leftGoal;
    private Goal rightGoal;
    private Timer timer;
    private boolean isPlayer1Turn = true; // Control de turnos
    private int player1Score = 0;
    private int player2Score = 0;
    private boolean canShoot = true;     
    private static final int PLAYERS_PER_TEAM = 5;
    private ArrayList<Player> teamRed;
    private ArrayList<Player> teamBlue;
    private boolean isRedTeamTurn = true; // true para equipo rojo, false para equipo azul
    private Player selectedPlayer;
    private BufferedImage backgroundImage;

    
    public GamePanel() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(new Color(34, 139, 34)); // Verde oscuro para el campo
        
        // Inicializar equipos
        initializePlayers();
        
        // Inicializar pelota y goles
        ball = new Ball(WIDTH / 2, HEADER_HEIGHT + (FIELD_HEIGHT / 2));
        leftGoal = new Goal(0, HEADER_HEIGHT + (FIELD_HEIGHT/2) - 100, true);
        rightGoal = new Goal(WIDTH - 20, HEADER_HEIGHT + (FIELD_HEIGHT/2) - 60, false);
        
        timer = new Timer(16, this);
        
     // Inicializar el turno
        isRedTeamTurn = true; // Empiezan los rojos
        canShoot = true;
        
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("resources/Cancha.png");
            if (is != null) {
                backgroundImage = ImageIO.read(is);
            } else {
                System.out.println("No se pudo encontrar el archivo Cancha.png");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void initializePlayers() {
        teamRed = new ArrayList<>();
        teamBlue = new ArrayList<>();

        // Agregar el arquero al equipo rojo
        teamRed.add(new Player(200, HEADER_HEIGHT + (FIELD_HEIGHT / 2), Color.RED)); // Centro
        teamRed.add(new Player(80, HEADER_HEIGHT + (FIELD_HEIGHT / 3), Color.RED)); // Arriba
        teamRed.add(new Player(80, HEADER_HEIGHT + 2 * (FIELD_HEIGHT / 3), Color.RED)); // Abajo
        teamRed.add(new Player(160, HEADER_HEIGHT + (FIELD_HEIGHT / 4), Color.RED)); // Delantero arriba
        teamRed.add(new Player(160, HEADER_HEIGHT + 3 * (FIELD_HEIGHT / 4), Color.RED)); // Delantero abajo


        // Agregar el arquero al equipo azul
        teamBlue.add(new Player(WIDTH - 200, HEADER_HEIGHT + (FIELD_HEIGHT / 2), Color.BLUE)); // Centro
        teamBlue.add(new Player(WIDTH - 80, HEADER_HEIGHT + (FIELD_HEIGHT / 3), Color.BLUE)); // Arriba
        teamBlue.add(new Player(WIDTH - 80, HEADER_HEIGHT + 2 * (FIELD_HEIGHT / 3), Color.BLUE)); // Abajo
        teamBlue.add(new Player(WIDTH - 160, HEADER_HEIGHT + (FIELD_HEIGHT / 4), Color.BLUE)); // Delantero arriba
        teamBlue.add(new Player(WIDTH - 160, HEADER_HEIGHT + 3 * (FIELD_HEIGHT / 4), Color.BLUE)); // Delantero abajo
    }


    
    public void startGame() {
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        updateGame();
        repaint();
    }

 // Modificar el método updateGame
    private void updateGame() {
        // Actualizar todos los jugadores
        for(Player player : teamRed) {
            player.update();
        }
        for(Player player : teamBlue) {
            player.update();
        }
        ball.update();
        
        checkCollisions();
        checkGoals();
        
        // Verificar si todos los objetos están quietos para permitir el siguiente turno
        if (!canShoot && isObjectsStatic()) {
            canShoot = true;
            isRedTeamTurn = !isRedTeamTurn; // Cambiar turno
        }
    }
    
    // Modificar el método isObjectsStatic
    private boolean isObjectsStatic() {
        boolean allStatic = ball.isStatic();
        
        for(Player player : teamRed) {
            allStatic &= player.isStatic();
        }
        for(Player player : teamBlue) {
            allStatic &= player.isStatic();
        }
        
        return allStatic;
    }
    
    
    private void checkGoals() {
        if (leftGoal.checkGoal(ball)) {
            player2Score++;
            resetPositions();
        } else if (rightGoal.checkGoal(ball)) {
            player1Score++;
            resetPositions();
        }
    }
    
    // Modificar el método resetPositions
    private void resetPositions() {
        ball.setPosition(WIDTH / 2, HEADER_HEIGHT + (FIELD_HEIGHT / 2));
        ball.setVelocity(0, 0);
        initializePlayers();
        canShoot = true;
    }

    // Modificar el método de pintado
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (backgroundImage != null) {
            // Definir la altura fija que tendrá la imagen
            int imageHeight = 720;  // La imagen será de 650 px de alto (puedes ajustarlo a lo que desees)
            
            // Obtener la altura y el ancho del panel
            int panelHeight = getHeight();    // 720 px
            int panelWidth = getWidth();      // 1280 px
            
            // Calcular la posición Y para que la imagen se dibuje en la parte inferior
            int yPosition = panelHeight - imageHeight;

            // Asegúrate de que la imagen no quede fuera del panel
            if (yPosition < 0) {
                yPosition = 0;  // Si la imagen es más alta que el panel, ajustamos a la parte superior
            }

            // Dibujar la imagen de fondo con la altura especificada
            g.drawImage(backgroundImage, 0, yPosition, panelWidth, imageHeight, this);
        }

        drawHeader(g);
        
        
        // Dibujar jugadores
        for(Player player : teamRed) {
            player.draw(g);
        }
        for(Player player : teamBlue) {
            player.draw(g);
        }
        
        // Dibujar pelota y goles
        ball.draw(g);
        leftGoal.draw(g);
        rightGoal.draw(g);
        
     // Dibujar línea de dirección si estamos arrastrando
        if (dragging && selectedPlayer != null && dragStart != null && dragCurrent != null) {
            Graphics2D g2d = (Graphics2D) g;
            
            // Configurar el renderizado para líneas más suaves
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setStroke(new BasicStroke(2));
            
            // Calcular el centro del jugador
            int centerX = selectedPlayer.getX() + selectedPlayer.getDiameter() / 2;
            int centerY = selectedPlayer.getY() + selectedPlayer.getDiameter() / 2;
            
            // Calcular el vector de dirección
            double dx = dragCurrent.x - dragStart.x;
            double dy = dragCurrent.y - dragStart.y;
            
            // Calcular la magnitud
            double magnitude = Math.sqrt(dx * dx + dy * dy);
            
            // Limitar la longitud de la línea
            if (magnitude > MAX_DRAG_LENGTH) {
                dx = (dx / magnitude) * MAX_DRAG_LENGTH;
                dy = (dy / magnitude) * MAX_DRAG_LENGTH;
            }
            
            // Calcular el punto final
            int endX = centerX - (int)dx;
            int endY = centerY - (int)dy;
            
            // Dibujar línea principal
            g2d.setColor(Color.RED);
            g2d.drawLine(centerX, centerY, endX, endY);
           
            
            // Dibujar indicador de fuerza
            drawForceIndicator(g2d, magnitude);
            
            // Dibujar punta de flecha
            drawArrowHead(g2d, centerX, centerY, endX, endY);
        }
    }
    
 // Modificar el método drawHeader para mostrar el turno actual
    private void drawHeader(Graphics g) {
        // Fondo del header
        g.setColor(new Color(48, 48, 48));
        g.fillRect(0, 0, WIDTH, HEADER_HEIGHT);
        
        // Dibujar nombres de equipo
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Equipo Rojo", 50, 30);
        g.drawString("Equipo Azul", WIDTH - 150, 30);
        
        // Dibujar marcador
        g.setFont(new Font("Arial", Font.BOLD, 40));
        String score = player1Score + " - " + player2Score;
        FontMetrics fm = g.getFontMetrics();
        int scoreWidth = fm.stringWidth(score);
        g.drawString(score, (WIDTH - scoreWidth) / 2, 45);
        
        // Dibujar indicador de turno con el color del equipo actual
        g.setFont(new Font("Arial", Font.BOLD, 16));
        String turnText = "Turno: " + (isRedTeamTurn ? "Equipo Rojo" : "Equipo Azul");
        g.setColor(isRedTeamTurn ? Color.RED : Color.BLUE);
        g.drawString(turnText, (WIDTH - fm.stringWidth(turnText)) / 2, 70);
        
        // Indicador visual si no pueden disparar
        if (!canShoot) {
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.ITALIC, 14));
            String waitText = "Esperando a que los jugadores se detengan...";
            g.drawString(waitText, (WIDTH - fm.stringWidth(waitText)) / 2, HEADER_HEIGHT - 10);
        }
    }


 // Modificar el método de dibujo del campo
    private void drawField(Graphics g) {
        // Dibujar círculo central
        g.setColor(Color.WHITE);
        int circleDiameter = 120;
        g.drawOval(WIDTH/2 - circleDiameter/2, 
                   HEADER_HEIGHT + (FIELD_HEIGHT/2) - circleDiameter/2, 
                   circleDiameter, 
                   circleDiameter);
        
        // Línea central
        g.drawLine(WIDTH/2, HEADER_HEIGHT, WIDTH/2, HEIGHT);
        
        // Áreas de los arcos (opcional)
        int areaWidth = 150;
        int areaHeight = 300;
        // Área izquierda
        g.drawRect(0, HEADER_HEIGHT + (FIELD_HEIGHT - areaHeight)/2, areaWidth, areaHeight);
        // Área derecha
        g.drawRect(WIDTH - areaWidth, HEADER_HEIGHT + (FIELD_HEIGHT - areaHeight)/2, areaWidth, areaHeight);
    }
    
    private void drawScore(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString(player1Score + " - " + player2Score, WIDTH/2 - 40, 30);
    }
    
    private void drawTurnIndicator(Graphics g) {
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        String turnText = "Turno: " + (isPlayer1Turn ? "Jugador 1" : "Jugador 2");
        g.drawString(turnText, WIDTH/2 - 60, 60);
    }
    
    private void drawForceIndicator(Graphics2D g2d, double magnitude) {
        if (selectedPlayer != null) {
            int centerX = selectedPlayer.getX() + selectedPlayer.getDiameter() / 2;
            int barWidth = 50;
            int barHeight = 5;
            int barX = centerX - barWidth / 2;
            int barY = selectedPlayer.getY() - 15;
            
            // Calcular porcentaje de fuerza
            double forcePct = Math.min(magnitude / MAX_DRAG_LENGTH, 1.0);
            
            // Dibujar barra de fuerza
            g2d.setColor(new Color(0, 0, 0, 100));
            g2d.fillRect(barX, barY, barWidth, barHeight);
            
            // Dibujar progreso
            g2d.setColor(new Color(255, (int)(255 * (1 - forcePct)), 0));
            g2d.fillRect(barX, barY, (int)(barWidth * forcePct), barHeight);
        }
    }

    private void drawArrowHead(Graphics2D g2d, int startX, int startY, int endX, int endY) {
        double angle = Math.atan2(endY - startY, endX - startX);
        int arrowSize = 10;
        
        Point2D end = new Point2D.Double(endX, endY);
        Point2D tip1 = new Point2D.Double(
            endX - arrowSize * Math.cos(angle - Math.PI/6),
            endY - arrowSize * Math.sin(angle - Math.PI/6)
        );
        Point2D tip2 = new Point2D.Double(
            endX - arrowSize * Math.cos(angle + Math.PI/6),
            endY - arrowSize * Math.sin(angle + Math.PI/6)
        );
        
        g2d.drawLine((int)end.getX(), (int)end.getY(), (int)tip1.getX(), (int)tip1.getY());
        g2d.drawLine((int)end.getX(), (int)end.getY(), (int)tip2.getX(), (int)tip2.getY());
    }

    private void checkCollisions() {
        // Verificar colisiones con los jugadores del equipo rojo
        for (Player player : teamRed) {
            if (player.collidesWith(ball)) {
                player.handleCollision(ball);
            }
            leftGoal.checkCollisionWithPost(player);
            rightGoal.checkCollisionWithPost(player);
        }

        // Verificar colisiones con los jugadores del equipo azul
        for (Player player : teamBlue) {
            if (player.collidesWith(ball)) {
                player.handleCollision(ball);
            }
            leftGoal.checkCollisionWithPost(player);
            rightGoal.checkCollisionWithPost(player);
        }

        // Verificar colisiones del balón con los postes
        leftGoal.checkCollisionWithPost(ball);
        rightGoal.checkCollisionWithPost(ball);

        // Verificar colisiones entre jugadores
        checkPlayerCollisions();

        // Verificar colisiones con las paredes
        checkWallCollisions();
    }




    
    private void checkPlayerCollisions() {
        // Colisiones dentro del mismo equipo rojo
        for(int i = 0; i < teamRed.size(); i++) {
            for(int j = i + 1; j < teamRed.size(); j++) {
                if(teamRed.get(i).collidesWithPlayer(teamRed.get(j))) {
                    handlePlayerCollision(teamRed.get(i), teamRed.get(j));
                }
            }
        }
        
        // Colisiones dentro del mismo equipo azul
        for(int i = 0; i < teamBlue.size(); i++) {
            for(int j = i + 1; j < teamBlue.size(); j++) {
                if(teamBlue.get(i).collidesWithPlayer(teamBlue.get(j))) {
                    handlePlayerCollision(teamBlue.get(i), teamBlue.get(j));
                }
            }
        }
        
        // Colisiones entre equipos
        for(Player redPlayer : teamRed) {
            for(Player bluePlayer : teamBlue) {
                if(redPlayer.collidesWithPlayer(bluePlayer)) {
                    handlePlayerCollision(redPlayer, bluePlayer);
                }
            }
        }
    }

    // Método para manejar colisiones entre jugadores
    private void handlePlayerCollision(Player p1, Player p2) {
        // Calcular centros
        double p1CenterX = p1.getX() + p1.getDiameter() / 2.0;
        double p1CenterY = p1.getY() + p1.getDiameter() / 2.0;
        double p2CenterX = p2.getX() + p2.getDiameter() / 2.0;
        double p2CenterY = p2.getY() + p2.getDiameter() / 2.0;

        // Calcular vector de colisión
        double dx = p2CenterX - p1CenterX;
        double dy = p2CenterY - p1CenterY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance == 0) return; // Evitar división por cero

        // Normalizar el vector de colisión
        dx /= distance;
        dy /= distance;

        // Calcular velocidades relativas
        double relativeVelX = p2.getVelX() - p1.getVelX();
        double relativeVelY = p2.getVelY() - p1.getVelY();

        // Calcular producto punto
        double dotProduct = relativeVelX * dx + relativeVelY * dy;

        // Factor de elasticidad para el rebote entre jugadores
        double elasticity = 0.8;

        // Calcular impulso
        double impulseMagnitude = -(1 + elasticity) * dotProduct;

        // Aplicar impulso a ambos jugadores
        p1.setVelocity(
            p1.getVelX() - dx * impulseMagnitude,
            p1.getVelY() - dy * impulseMagnitude
        );
        p2.setVelocity(
            p2.getVelX() + dx * impulseMagnitude,
            p2.getVelY() + dy * impulseMagnitude
        );

        // Separar los jugadores para evitar que se peguen
        double overlap = (p1.getDiameter() + p2.getDiameter()) / 2 - distance;
        if (overlap > 0) {
            p1.setPosition(
                p1.getX() - dx * overlap / 2,
                p1.getY() - dy * overlap / 2
            );
            p2.setPosition(
                p2.getX() + dx * overlap / 2,
                p2.getY() + dy * overlap / 2
            );
        }
    }
    private void checkWallCollisions() {
        // Colisiones para la pelota
        if (ball.getY() < HEADER_HEIGHT) {
            ball.setPosition(ball.getX(), HEADER_HEIGHT);
            ball.setVelocity(ball.getVelX(), -ball.getVelY() * 0.7);
        }
        if (ball.getY() + ball.getDiameter() > HEIGHT) {
            ball.setPosition(ball.getX(), HEIGHT - ball.getDiameter());
            ball.setVelocity(ball.getVelX(), -ball.getVelY() * 0.7);
        }
        if (ball.getX() < 0) {
            ball.setPosition(0, ball.getY());
            ball.setVelocity(-ball.getVelX() * 0.7, ball.getVelY());
        }
        if (ball.getX() + ball.getDiameter() > WIDTH) {
            ball.setPosition(WIDTH - ball.getDiameter(), ball.getY());
            ball.setVelocity(-ball.getVelX() * 0.7, ball.getVelY());
        }

        // Colisiones para el equipo rojo
        for (Player player : teamRed) {
            if (player.getY() < HEADER_HEIGHT) {
                player.setPosition(player.getX(), HEADER_HEIGHT);
                player.setVelocity(player.getVelX(), -player.getVelY() * 0.7);
            }
            if (player.getY() + player.getDiameter() > HEIGHT) {
                player.setPosition(player.getX(), HEIGHT - player.getDiameter());
                player.setVelocity(player.getVelX(), -player.getVelY() * 0.7);
            }
            if (player.getX() < 0) {
                player.setPosition(0, player.getY());
                player.setVelocity(-player.getVelX() * 0.7, player.getVelY());
            }
            if (player.getX() + player.getDiameter() > WIDTH) {
                player.setPosition(WIDTH - player.getDiameter(), player.getY());
                player.setVelocity(-player.getVelX() * 0.7, player.getVelY());
            }
        }

        // Colisiones para el equipo azul
        for (Player player : teamBlue) {
            if (player.getY() < HEADER_HEIGHT) {
                player.setPosition(player.getX(), HEADER_HEIGHT);
                player.setVelocity(player.getVelX(), -player.getVelY() * 0.7);
            }
            if (player.getY() + player.getDiameter() > HEIGHT) {
                player.setPosition(player.getX(), HEIGHT - player.getDiameter());
                player.setVelocity(player.getVelX(), -player.getVelY() * 0.7);
            }
            if (player.getX() < 0) {
                player.setPosition(0, player.getY());
                player.setVelocity(-player.getVelX() * 0.7, player.getVelY());
            }
            if (player.getX() + player.getDiameter() > WIDTH) {
                player.setPosition(WIDTH - player.getDiameter(), player.getY());
                player.setVelocity(-player.getVelX() * 0.7, player.getVelY());
            }
        }
    }
    
 // Método para verificar si es válido seleccionar un jugador
    private boolean isValidPlayerSelection(Point clickPoint) {
        ArrayList<Player> currentTeam = isRedTeamTurn ? teamRed : teamBlue;
        
        for(Player player : currentTeam) {
            if(player.contains(clickPoint)) {
                // Verificar si el jugador está en movimiento
                if(!player.isStatic()) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    
 // Método para verificar si todos los jugadores y la pelota están estáticos
    private boolean isAllStatic() {
        if (!ball.isStatic()) {
            return false;
        }

        for (Player player : teamRed) {
            if (!player.isStatic()) {
                return false;
            }
        }

        for (Player player : teamBlue) {
            if (!player.isStatic()) {
                return false;
            }
        }

        return true;
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        // Solo permitir seleccionar si todo está estático
        if (!canShoot || !isAllStatic()) {
            return;
        }

        Point clickPoint = e.getPoint();
        selectedPlayer = null;

        // Buscar al jugador seleccionado en el equipo que tiene el turno
        for (Player player : (isRedTeamTurn ? teamRed : teamBlue)) {
            if (player.contains(clickPoint)) {
                selectedPlayer = player;
                dragStart = clickPoint;
                dragCurrent = clickPoint;
                dragging = true;
                break;
            }
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        if (dragging && selectedPlayer != null && dragStart != null) {
            dragCurrent = e.getPoint();

            // Calcular vector de disparo
            double dx = dragCurrent.x - dragStart.x;
            double dy = dragCurrent.y - dragStart.y;

            // Limitar la fuerza máxima del disparo
            double magnitude = Math.sqrt(dx * dx + dy * dy);
            if (magnitude > MAX_DRAG_LENGTH) {
                dx = (dx / magnitude) * MAX_DRAG_LENGTH;
                dy = (dy / magnitude) * MAX_DRAG_LENGTH;
            }

            // Aplicar el disparo
            selectedPlayer.shoot(dx, dy);

            // Limpiar estados del arrastre
            selectedPlayer = null;
            dragStart = null;
            dragCurrent = null;
            dragging = false;

            // Esperar a que todo esté estático antes de cambiar el turno
            Timer checkStaticTimer = new Timer(100, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (isAllStatic()) {
                        canShoot = true;
                        isRedTeamTurn = !isRedTeamTurn; // Cambiar el turno
                        ((Timer)e.getSource()).stop();
                    }
                }
            });
            checkStaticTimer.start();

            repaint();
        }
    }
    @Override
    public void mouseDragged(MouseEvent e) {
        if (dragging && selectedPlayer != null) {
            dragCurrent = e.getPoint();
            
            // Calcular ángulo para el efecto
            if (isSpinEnabled && dragStart != null) {
                double dx = e.getX() - dragStart.x;
                double dy = e.getY() - dragStart.y;
                spinAngle = Math.atan2(dy, dx);
            }
            
            repaint();
        }
    }
    
    private void handlePostCollision(Ball ball) {
        // Rebote al chocar con el poste, reduce la velocidad
        ball.setVelocity(-ball.getVelX() * 0.5, -ball.getVelY() * 0.5);
    }

    private void handlePostCollision(Player player) {
        // Rebote para el jugador al chocar con el poste
        player.setVelocity(-player.getVelX() * 0.5, -player.getVelY() * 0.5);
    }
    
    


    

    // Método para activar/desactivar el modo de efecto
    public void toggleSpinMode() {
        isSpinEnabled = !isSpinEnabled;
    }

    // Métodos adicionales del MouseListener y MouseMotionListener
    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    @Override
    public void mouseMoved(MouseEvent e) {}
}
