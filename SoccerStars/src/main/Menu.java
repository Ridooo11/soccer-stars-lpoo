package main;

import javax.swing.*;
import java.awt.*;

public class Menu extends JPanel {
    private static final long serialVersionUID = 1L;
    private Dimension currentResolution;
    private JButton playButton, volumeButton, resolutionButton;

    public Menu() {
        setResolution(new Dimension(1280, 800)); // Resolución inicial
        setLayout(new GridBagLayout());
        setBackground(new Color(34, 139, 34)); // Fondo verde estilo campo de fútbol

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;

        // Título del juego
        JLabel title = new JLabel("Soccer Stars", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 48));
        title.setForeground(Color.WHITE);
        gbc.gridy = 0;
        add(title, gbc);

        // Botón Jugar
        gbc.gridy = 1;
        playButton = createButton("JUGAR");
        playButton.addActionListener(e -> showTeamSelectionWindow());
        add(playButton, gbc);

        // Botón Configuración de Volumen
        gbc.gridy = 2;
        volumeButton = createButton("CONFIGURAR VOLUMEN");
        volumeButton.addActionListener(e -> showVolumeWindow());
        add(volumeButton, gbc);

        // Botón Configuración de Resolución
        gbc.gridy = 3;
        resolutionButton = createButton("CONFIGURAR RESOLUCIÓN");
        resolutionButton.addActionListener(e -> showResolutionWindow());
        add(resolutionButton, gbc);

        
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 102, 0)); // Verde oscuro para botones
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JFrame createStyledFrame(String title) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(34, 139, 34)); // Fondo verde campo de fútbol
        frame.setLocationRelativeTo(null);
        return frame;
    }

    private void setResolution(Dimension resolution) {
        currentResolution = resolution;
        setPreferredSize(currentResolution);
    }

    private void showVolumeWindow() {
        JFrame volumeFrame = createStyledFrame("Configuración de Volumen");

        JLabel titleLabel = new JLabel("Ajustar Volumen", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JSlider volumeSlider = new JSlider(0, 100, 50);
        volumeSlider.setMajorTickSpacing(10);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);
        volumeSlider.setForeground(Color.WHITE);
        volumeSlider.setBackground(new Color(34, 139, 34));

        JButton saveButton = createButton("GUARDAR");
        saveButton.addActionListener(e -> {
            System.out.println("Volumen ajustado a: " + volumeSlider.getValue());
            volumeFrame.dispose();
        });

        volumeFrame.add(titleLabel, BorderLayout.NORTH);
        volumeFrame.add(volumeSlider, BorderLayout.CENTER);
        volumeFrame.add(saveButton, BorderLayout.SOUTH);
        volumeFrame.setVisible(true);
    }

    private void showResolutionWindow() {
        JFrame resolutionFrame = createStyledFrame("Configuración de Resolución");

        JLabel titleLabel = new JLabel("Seleccionar Resolución", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        buttonPanel.setBackground(new Color(34, 139, 34));

        String[] resolutions = {"800x600", "1024x768", "1280x800", "1600x900", "1920x1080"};
        for (String resolution : resolutions) {
            JButton resolutionButton = createButton(resolution);
            resolutionButton.addActionListener(e -> {
                switch (resolution) {
                    case "800x600" -> setResolution(new Dimension(800, 600));
                    case "1024x768" -> setResolution(new Dimension(1024, 768));
                    case "1280x800" -> setResolution(new Dimension(1280, 800));
                    case "1600x900" -> setResolution(new Dimension(1600, 900));
                    case "1920x1080" -> setResolution(new Dimension(1920, 1080));
                }
                resolutionFrame.dispose();
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                if (topFrame != null) {
                    topFrame.pack();
                    topFrame.setLocationRelativeTo(null);
                }
            });
            buttonPanel.add(resolutionButton);
        }

        resolutionFrame.add(titleLabel, BorderLayout.NORTH);
        resolutionFrame.add(buttonPanel, BorderLayout.CENTER);
        resolutionFrame.setVisible(true);
    }


    private String team1 = ""; // Equipo del Jugador 1
    private String team2 = ""; // Equipo del Jugador 2

    private void showTeamSelectionWindow() {
        JFrame teamFrame = createStyledFrame("Selección de Equipos");

        String[] teams = {"Argentina", "Nueva Zelanda", "Brasil", "Palestina"};
        JComboBox<String> teamSelection1 = new JComboBox<>(teams);
        JComboBox<String> teamSelection2 = new JComboBox<>(teams);

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBackground(new Color(34, 139, 34));
        panel.add(createStyledLabel("Jugador 1:"));
        panel.add(teamSelection1);
        panel.add(createStyledLabel("Jugador 2:"));
        panel.add(teamSelection2);

        teamFrame.add(panel, BorderLayout.CENTER);

        JButton continueButton = createButton("CONTINUAR");
        continueButton.addActionListener(e -> {
            team1 = (String) teamSelection1.getSelectedItem();
            team2 = (String) teamSelection2.getSelectedItem();

            if (team1.equals(team2)) {
                JOptionPane.showMessageDialog(this, "Selecciona equipos diferentes.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                teamFrame.dispose();
                startGame();
            }
        });
        teamFrame.add(continueButton, BorderLayout.SOUTH);

        teamFrame.setVisible(true);
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(Color.WHITE);
        return label;
    }

    private void startGame() {
        JFrame gameFrame = new JFrame("Soccer Stars");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Pasar equipos seleccionados al GamePanel
        GamePanel gamePanel = new GamePanel(currentResolution.width, currentResolution.height, team1, team2);
        gameFrame.add(gamePanel);

        gameFrame.pack();
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setVisible(true);
        gameFrame.setResizable(false);

        gamePanel.startGame();

        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (topFrame != null) {
            topFrame.dispose(); // Cerrar el menú
        }
    }
}
