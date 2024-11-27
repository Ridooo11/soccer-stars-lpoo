
package main;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

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
    private int WIDTH = 1280;          // Aumentamos ancho
    private int HEIGHT = 800;          // Aumentamos alto
    private final int HEADER_HEIGHT = 80;    // Espacio para marcador y nombres
    private final int FIELD_HEIGHT = HEIGHT - HEADER_HEIGHT; // Altura real del camp 
    private boolean dragging = false;
    private Point dragStart;
    private Point dragCurrent;
    private final int MAX_DRAG_LENGTH = 100;
    private boolean isSpinEnabled = false; // Variable para controlar si el efecto está activado
    private double spinAngle = 0.0;        
    
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
    private boolean isRedTeamTurn = true; 
    private Player selectedPlayer;
    private BufferedImage backgroundImage;
    private BufferedImage argImage;
    
    private Timer gameTimer; // Temporizador del juego
    private int timeRemaining = 300; // Tiempo en segundos (5 minutos)
    private Timer turnTimer;         // Temporizador para el turno actual
    private int turnTimeRemaining;   // Tiempo restante en segundos
    private static final int TURN_TIME_LIMIT = 30; // Límite de tiempo por turno (30 segundos)
    private GamePanel gamePanel;
    
    private String team1, team2; // Equipos seleccionados
    private ImageIcon team1Image, team2Image; // Usando ImageIcon para las imágenes


    
    public GamePanel(int width, int height, String team1, String team2) {
    	this.WIDTH = width; 
        this.HEIGHT = height; 
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(new Color(34, 139, 34)); // Verde oscuro para el campo
        
        this.team1 = team1;
        this.team2 = team2;
        
        // Inicializar equipos
        loadTeamImages(); // Cargar imágenes
        
        // Inicializar pelota y goles
        ball = new Ball(WIDTH / 2 - 20, HEADER_HEIGHT + (FIELD_HEIGHT / 2) - 20);
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
        
        timer = new Timer(16, this); // Timer para actualizaciones del juego

        // Timer para el temporizador del juego
        gameTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeRemaining--;
                if (timeRemaining <= 0) {
                    endGame();
                }
                repaint(); // Redibujar el marcador con el tiempo restante
            }
        });
        
     // Configuración del temporizador de turno
        turnTimeRemaining = TURN_TIME_LIMIT;
        turnTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                turnTimeRemaining--;
                if (turnTimeRemaining <= 0) {
                    passTurnDueToTimeout(); // Llamar al método que pasa el turno al equipo contrario
                }
                repaint(); // Actualizar el encabezado con el tiempo restante
            }
        });
        
        initializePlayers();
    }
    
    

    private void loadTeamImages() {
        team1Image = loadImageForTeam(team1);  // Usamos ImageIcon para cargar la imagen
        team2Image = loadImageForTeam(team2);
    }

    private ImageIcon loadImageForTeam(String team) {
        try {
            // Ruta de la imagen
            String path = "/resources/" + team.toLowerCase().replace(" ", "_") + "_player.png";
            InputStream is = getClass().getResourceAsStream(path);

            // Verificar si la imagen se encuentra
            if (is == null) {
                System.out.println("Error: no se encontró la imagen en la ruta " + path);
                return null;
            }

            // Cargar la imagen con ImageIcon
            ImageIcon icon = new ImageIcon(is.readAllBytes());

            // Redimensionar la imagen para que tenga el tamaño correcto (40x40)
            Image img = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            return new ImageIcon(img);  // Devolver el ImageIcon redimensionado
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    
    
    private void passTurnDueToTimeout() {
        turnTimer.stop(); // Detener el temporizador del turno
        isRedTeamTurn = !isRedTeamTurn;
        canShoot = true;

        // Reiniciar el temporizador del turno
        startTurnTimer();

        // Reanudar el temporizador del tiempo principal
        gameTimer.start();

        // Redibujar el campo
        repaint();
    }

    
    private void initializePlayers() {
        teamRed = new ArrayList<>();
        teamBlue = new ArrayList<>();

        
        Goalkeeper goalkeeperRed = new Goalkeeper(80, HEADER_HEIGHT + (FIELD_HEIGHT / 2) - 20, Color.RED, team1Image, 60);
        teamRed.add(new Player(200, HEADER_HEIGHT + (FIELD_HEIGHT / 3) + 20, Color.RED, team1Image)); 
        teamRed.add(new Player(200, HEADER_HEIGHT + (FIELD_HEIGHT / 3) + 180, Color.RED, team1Image)); 
        teamRed.add(new Player(300, HEADER_HEIGHT + (FIELD_HEIGHT / 4) + 150, Color.RED, team1Image)); 
        teamRed.add(new Player(400, HEADER_HEIGHT + (FIELD_HEIGHT / 4) + 150, Color.RED, team1Image)); 


       
        Goalkeeper goalkeeperBlue = new Goalkeeper(WIDTH - 140, HEADER_HEIGHT + (FIELD_HEIGHT / 2) - 22, Color.BLUE, team2Image, 60);
        teamBlue.add(new Player(WIDTH - 250, HEADER_HEIGHT + (FIELD_HEIGHT / 3) + 100, Color.BLUE, team2Image)); 
        teamBlue.add(new Player(WIDTH - 350, HEADER_HEIGHT + (FIELD_HEIGHT / 3) + 100, Color.BLUE, team2Image)); 
        teamBlue.add(new Player(WIDTH - 450, HEADER_HEIGHT + (FIELD_HEIGHT / 4) + 100, Color.BLUE, team2Image));
        teamBlue.add(new Player(WIDTH - 450, HEADER_HEIGHT + (FIELD_HEIGHT / 4) + 200, Color.BLUE, team2Image)); 
        
        teamRed.add(goalkeeperRed);
        teamBlue.add(goalkeeperBlue);
    }
    
    private void endGame() {
        timer.stop();
        gameTimer.stop();

        String winner;
        if (player1Score > player2Score) {
            winner = team1 + " gana!";
        } else if (player2Score > player1Score) {
            winner = team2 + " gana";
        } else {
            winner = "¡Es un empate!";
        }

        showResultWindow(winner);
    }
    
    private void showResultWindow(String result) {
        JFrame resultFrame = new JFrame("Resultado del partido");
        resultFrame.setSize(400, 200);
        resultFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        resultFrame.setLayout(new BorderLayout());

        // Etiqueta con el resultado
        JLabel resultLabel = new JLabel(result, SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 20));
        resultFrame.add(resultLabel, BorderLayout.CENTER);

        // Botón para reiniciar el juego
        JButton restartButton = new JButton("Reiniciar");
        restartButton.setFont(new Font("Arial", Font.PLAIN, 16));
        restartButton.addActionListener(e -> {
            resultFrame.dispose(); // Cierra la ventana de resultados
            resetPositions();      // Reinicia las posiciones de jugadores y pelota
            player1Score = 0;
            player2Score = 0;
            timeRemaining = 300;  // Reinicia el tiempo
            startGame();          // Inicia el juego nuevamente
        });
        resultFrame.add(restartButton, BorderLayout.SOUTH);

        // Centrar la ventana en la pantalla
        resultFrame.setLocationRelativeTo(null);
        resultFrame.setVisible(true);
    }


    
    public void startGame() {
        timer.start();
        gameTimer.start();
        startTurnTimer();
    }
    
    private void startTurnTimer() {
        turnTimeRemaining = TURN_TIME_LIMIT; // Reiniciar el tiempo del turno
        turnTimer.start(); // Iniciar el temporizador del turno
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
            isRedTeamTurn = true;
        } else if (rightGoal.checkGoal(ball)) {
            player1Score++;
            resetPositions();
            isRedTeamTurn = false;
        }
    }
    
    // Modificar el método resetPositions
    private void resetPositions() {
        ball.setPosition(WIDTH / 2 - 20, HEADER_HEIGHT + (FIELD_HEIGHT / 2) - 20);
        ball.setVelocity(0, 0);
        initializePlayers();
        canShoot = true;
    }

    // Modificar el método de pintado
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (backgroundImage != null) {
            // Obtener la altura y el ancho del panel
            int panelHeight = getHeight();   
            int panelWidth = getWidth();   
            
            // Calcular la posición Y para que la imagen se dibuje en la parte inferior
            int yPosition = panelHeight - FIELD_HEIGHT;

            // Asegúrate de que la imagen no quede fuera del panel
            if (yPosition < 0) {
                yPosition = 0;  // Si la imagen es más alta que el panel, ajustamos a la parte superior
            }

            
            
            if(panelHeight == 800) {
            	g.drawImage(backgroundImage, 0, yPosition, panelWidth, FIELD_HEIGHT, this);
            } else if(panelHeight == 900) {
            	g.drawImage(backgroundImage, 0, yPosition - 100, panelWidth, FIELD_HEIGHT, this);
            }
        }

        drawHeader(g);
        
        if (!dragging) {
            drawActiveTeamHighlight(g);
        }
        
        for (Player player : teamRed) {
            player.draw(g); // Dibuja el jugador del equipo rojo
        }

        for (Player player : teamBlue) {
            player.draw(g); // Dibuja el jugador del equipo azul
        }
        
      
        
        // Dibujar pelota y goles
        ball.draw(g);
       
        
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
        
        leftGoal.draw(g);
        rightGoal.draw(g);
    }
    
 // Método para dibujar el círculo alrededor de los jugadores del equipo activo
    private void drawActiveTeamHighlight(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Seleccionar el equipo activo según el turno
        ArrayList<Player> activeTeam = isRedTeamTurn ? teamRed : teamBlue;
        Color highlightColor = isRedTeamTurn ? Color.RED : Color.RED;

        // Configurar el color y grosor del círculo
        g2d.setColor(highlightColor);
        g2d.setStroke(new BasicStroke(2)); // Grosor del círculo

        // Dibujar un círculo alrededor de cada jugador del equipo activo
        for (Player player : activeTeam) {
            int x = player.getX();
            int y = player.getY();
            int diameter = player.getDiameter();

            // Ajustar si hay margen en el sprite (reducir un 10% por ejemplo)
            int adjustedDiameter = (int) (diameter * 0.9); // Reducir el diámetro un 10%
            int offset = (diameter - adjustedDiameter) / 2; // Offset para centrar el círculo

            // Dibujar un círculo ajustado alrededor del sprite
            g2d.drawOval(x + offset, y + offset, adjustedDiameter, adjustedDiameter);
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
        
     
        
        // Indicador visual si no pueden disparar
        if (!canShoot) {
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.ITALIC, 14));
            String waitText = "Esperando a que los jugadores se detengan...";
            g.drawString(waitText, (WIDTH - fm.stringWidth(waitText)) / 2, HEADER_HEIGHT - 10);
        }
        
        g.setColor(new Color(48, 48, 48));
        g.fillRect(0, 0, WIDTH, HEADER_HEIGHT);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString(team1, 50, 30);
        g.drawString(team2, WIDTH - 150, 30);

        // Dibujar marcador
        g.setFont(new Font("Arial", Font.BOLD, 40));
        String Endscore = player1Score + " - " + player2Score;
        g.drawString(Endscore, WIDTH / 2 - g.getFontMetrics().stringWidth(Endscore) / 2, 45);

        // Dibujar tiempo restante
        g.setFont(new Font("Arial", Font.BOLD, 16));
        String totalTimeText = String.format("Tiempo restante: %02d:%02d", timeRemaining / 60, timeRemaining % 60);
        g.drawString(totalTimeText, WIDTH / 2 - g.getFontMetrics().stringWidth(totalTimeText) / 2, 70);
        
     // Dibujar tiempo restante del turno
        g.setFont(new Font("Arial", Font.BOLD, 16));
        String timeText = String.format("Tiempo del turno: %02d s", turnTimeRemaining);
        if (isRedTeamTurn) {
        	g.drawString(timeText, 30, 70);
        } else {
        	g.drawString(timeText, WIDTH - 200, 70);
        }
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
        // Detectar clic derecho para cancelar la selección del jugador
        if (e.getButton() == MouseEvent.BUTTON3 && dragging) {
            // Cancelar la selección del jugador sin cambiar el turno
        	cancelShot();
            return;
        }

        // Continuar con la lógica del clic izquierdo
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
                dragging = true; // Inicia el arrastre
                repaint(); // Actualiza la pantalla para eliminar los círculos
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

            // Detener el temporizador del turno
            turnTimer.stop();

            // Verificar si todo está estático antes de cambiar el turno
            Timer checkStaticTimer = new Timer(100, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (isAllStatic()) {
                        canShoot = true;
                        isRedTeamTurn = !isRedTeamTurn; // Cambiar el turno
                        startTurnTimer(); // Reiniciar el temporizador del turno
                        ((Timer) e.getSource()).stop();
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
    
    private void cancelShot() {
        selectedPlayer = null;
        dragStart = null;
        dragCurrent = null;
        dragging = false;     

        repaint(); // Redibujar el campo
    }
    
    


    

    
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
