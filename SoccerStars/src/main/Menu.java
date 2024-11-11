package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Menu extends JPanel implements ActionListener {
    private final int GAME_WIDTH = 1000;
    private final int GAME_HEIGHT = 600;

    private JButton playButton;
    private JButton volumeButton;
    private JButton tutorialButton;
    private JButton resolutionButton;
    private JSlider volumeSlider;

    public Menu() {
        setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;

        // Botón Jugar
        gbc.gridy = 0;
        playButton = new JButton("Jugar");
        playButton.addActionListener(this);
        add(playButton, gbc);

        // Botón de Configuración de Volumen
        gbc.gridy = 1;
        volumeButton = new JButton("Volumen");
        volumeButton.addActionListener(e -> showVolumeOptions());
        add(volumeButton, gbc);

        // Botón de Configuración de Resolución
        gbc.gridy = 2;
        resolutionButton = new JButton("Resolucion");
        resolutionButton.addActionListener(e -> showResolutionOptions());
        add(resolutionButton, gbc);

        // Botón de Tutorial
        gbc.gridy = 3;
        tutorialButton = new JButton("Tutorial");
        tutorialButton.addActionListener(e -> showTutorial());
        add(tutorialButton, gbc);
    }

    private void showVolumeOptions() {
        volumeSlider = new JSlider(0, 100, 50); // Rango de 0 a 100, valor inicial 50
        volumeSlider.setMajorTickSpacing(10);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);

        int result = JOptionPane.showConfirmDialog(this, volumeSlider, "Configuración de Volumen", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            int volume = volumeSlider.getValue();
            System.out.println("Volumen ajustado a: " + volume);
        }
    }

    private void showResolutionOptions() {
        String[] resolutions = {"800x600", "1024x768", "1280x720", "1600x900", "1920x1080"};
        String selectedResolution = (String) JOptionPane.showInputDialog(this, "Selecciona la resolución:",
                "Configuración de Resolución", JOptionPane.PLAIN_MESSAGE, null, resolutions, resolutions[0]);

        if (selectedResolution != null) {
            switch (selectedResolution) {
                case "800x600":
                    setPreferredSize(new Dimension(800, 600));
                    break;
                case "1024x768":
                    setPreferredSize(new Dimension(1024, 768));
                    break;
                case "1280x720":
                    setPreferredSize(new Dimension(1280, 720));
                    break;
                case "1600x900":
                    setPreferredSize(new Dimension(1600, 900));
                    break;
                case "1920x1080":
                    setPreferredSize(new Dimension(1920, 1080));
                    break;
            }
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (topFrame != null) {
                topFrame.pack();
                topFrame.setLocationRelativeTo(null); // Centrar la ventana
            }
        }
    }

    private void showTutorial() {
        String tutorialText = "Este es un tutorial para jugar.\n" +
                "1. Usa el mouse para seleccionar un jugador.\n" +
                "2. Arrastra para definir la dirección y fuerza del tiro.\n" +
                "3. Usa el botón de Jugar para comenzar.\n" +
                "¡Diviértete!";
        JOptionPane.showMessageDialog(this, tutorialText, "Tutorial", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == playButton) {
            showTeamSelection(); // Paso 1: Seleccionar equipo
        }
    }

    private void showTeamSelection() {
        String[] teams = {"Equipo A", "Equipo B", "Equipo C", "Equipo D"};
        JComboBox<String> teamSelection1 = new JComboBox<>(teams);
        JComboBox<String> teamSelection2 = new JComboBox<>(teams);

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Equipo para Jugador 1:  "));
        panel.add(teamSelection1);
        panel.add(new JLabel("Equipo para Jugador 2:  "));
        panel.add(teamSelection2);

        int result = JOptionPane.showConfirmDialog(this, panel, "Seleccionar Equipos", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String selectedTeam1 = (String) teamSelection1.getSelectedItem();
            String selectedTeam2 = (String) teamSelection2.getSelectedItem();

            if (selectedTeam1.equals(selectedTeam2)) {
                JOptionPane.showMessageDialog(this, "No se puede seleccionar el mismo equipo. Elige equipos diferentes.", "Error", JOptionPane.ERROR_MESSAGE);
                showTeamSelection(); // Reintentar selección
            } else {
                showFormationSelection(selectedTeam1, selectedTeam2); // Paso 2: Seleccionar formación para cada jugador
            }
        }
    }

    private void showFormationSelection(String team1, String team2) {
        String[] formations = {"1-3-2", "1-2-3", "1-4-1", "1-2-1-2"};
        JComboBox<String> formationSelection1 = new JComboBox<>(formations);
        JComboBox<String> formationSelection2 = new JComboBox<>(formations);

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Formación para " + team1 + ":  "));
        panel.add(formationSelection1);
        panel.add(new JLabel("Formación para " + team2 + ":  "));
        panel.add(formationSelection2);

        int result = JOptionPane.showConfirmDialog(this, panel, "Seleccionar Formación", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            showFinalContinueOption(); // Paso 3: Botón de continuar para empezar el juego
        }
    }

    private void showFinalContinueOption() {
        int result = JOptionPane.showConfirmDialog(this, "¿Listo para comenzar?", "Continuar", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            startGame(); // Iniciar el juego
        }
    }

    private void startGame() {
        JFrame gameFrame = new JFrame("Soccer Stars");
        GamePanel gamePanel = new GamePanel();

        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setResizable(false);
        gameFrame.add(gamePanel);
        gameFrame.pack();
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setVisible(true);

        gamePanel.startGame();

        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        topFrame.dispose(); // Cerrar el menú
    }
}
