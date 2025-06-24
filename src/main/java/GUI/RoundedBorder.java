package GUI;

import javax.swing.border.AbstractBorder;
import java.awt.*;

public class RoundedBorder extends AbstractBorder {
    private int radius;
    private Color backgroundColor;
    private Color outerColor;

    RoundedBorder(int radius, Color backgroundColor, Color outerColor) {
        this.radius = radius;
        this.backgroundColor = backgroundColor;
        this.outerColor = outerColor;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Prima riempi l'intera area con il colore esterno
        g2d.setColor(outerColor);
        g2d.fillRect(x, y, width, height);

        // Poi disegna il rettangolo arrotondato con il colore di sfondo
        g2d.setColor(backgroundColor);
        g2d.fillRoundRect(x, y, width - 1, height - 1, radius, radius);

        // Infine disegna il bordo
        g2d.setColor(Color.lightGray);
        g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);

        g2d.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(radius/2, radius/2, radius/2, radius/2);
    }

    @Override
    public boolean isBorderOpaque() {
        return true;
    }
}
