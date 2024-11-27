package main;

import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Graphics;

public class Goalkeeper extends Player {
    
    public Goalkeeper(int x, int y, Color teamColor, ImageIcon playerImage, int DIAMETER) {
        super(x, y, teamColor, playerImage);  // Llamar al constructor de Player
        this.DIAMETER = DIAMETER;  // Establecer un diámetro mayor para el arquero
    }

    // Métodos específicos del arquero pueden ir aquí si es necesario.
    // Por ejemplo, agregar funcionalidades como moverse solo en la línea de gol, etc.

    // Sobrescribir el método de dibujo si se necesita un estilo de dibujo específico
    @Override
    public void draw(Graphics g) {
        // Llamar al método de la clase base
        super.draw(g);
    }
}
