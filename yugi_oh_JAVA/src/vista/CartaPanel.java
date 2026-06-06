package vista;
import javax.swing.*;
import modelo.Carta;
import modelo.CartaMagica;
import modelo.CartaTrampa;
import modelo.Monstruo;
import java.awt.*;


public class CartaPanel extends JPanel {

    public static final int W = 80;
    public static final int H = 110;

    private static final Color COLOR_MONSTRUO_BG = new Color(90, 60, 20);
    private static final Color COLOR_MAGIA_BG    = new Color(20, 70, 50);
    private static final Color COLOR_TRAMPA_BG   = new Color(70, 20, 20);
    private static final Color COLOR_DORSO_BG    = new Color(30, 30, 80);
    private static final Color GOLD              = new Color(212, 175, 55);
    private static final Color GOLD_LIGHT        = new Color(255, 220, 100);

    private final Carta carta;
    private final boolean enCampo;
    private boolean seleccionada;
    private boolean habilitada;

    public CartaPanel(Carta carta) {
        this(carta, false);
    }

    public CartaPanel(Carta carta, boolean enCampo) {
        this.carta      = carta;
        this.enCampo    = enCampo;
        this.seleccionada = false;
        this.habilitada   = true;

        setPreferredSize(new Dimension(W, H));
        setOpaque(false);
        setToolTipText(buildTooltip());
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public Carta getCarta()           { return carta; }
    public boolean isSeleccionada()   { return seleccionada; }

    public void setSeleccionada(boolean v) { this.seleccionada = v; repaint(); }
    public void setHabilitada(boolean v)   { this.habilitada   = v; repaint(); }

    private String buildTooltip() {
        if (carta == null) return "";
        return "<html><b>" + carta.getNombre() + "</b><br/>"
             + "Tipo: " + carta.getTipo() + "<br/>"
             + carta.getDescripcion() + "</html>";
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w    = getWidth();
        int h    = getHeight();
        int yOff = seleccionada ? -8 : 0;

        g2.setColor(new Color(0, 0, 0, 80));
        g2.fillRoundRect(3, 3 + yOff, w - 2, h - 2, 10, 10);

        Color bgColor = getBgColor();
        if (!habilitada) bgColor = bgColor.darker().darker();
        g2.setColor(bgColor);
        g2.fillRoundRect(0, yOff, w - 2, h - 2, 10, 10);

        g2.setColor(seleccionada ? GOLD_LIGHT : GOLD);
        g2.setStroke(new BasicStroke(seleccionada ? 2.5f : 1.5f));
        g2.drawRoundRect(0, yOff, w - 3, h - 3, 10, 10);

        g2.setColor(new Color(255, 255, 255, 30));
        g2.setStroke(new BasicStroke(1));
        g2.drawRoundRect(4, 4 + yOff, w - 11, h - 11, 7, 7);

        int iconH = 28;
        g2.setColor(new Color(0, 0, 0, 60));
        g2.fillRoundRect(4, 4 + yOff, w - 11, iconH, 6, 6);

        g2.setFont(new Font("Serif", Font.BOLD, 9));
        g2.setColor(GOLD);
        String tipo = carta.getTipo().toUpperCase();
        FontMetrics fmT = g2.getFontMetrics();
        g2.drawString(tipo, (w - fmT.stringWidth(tipo)) / 2, 19 + yOff);

        g2.setFont(new Font("Serif", Font.BOLD, 8));
        g2.setColor(Color.WHITE);
        drawWrappedString(g2, carta.getNombre(), 5, 38 + yOff, w - 10, 9);

        if (carta instanceof Monstruo) {
            paintMonstruo(g2, (Monstruo) carta, w, h, yOff);
        } else if (carta instanceof CartaTrampa) {
            paintTrampa(g2, (CartaTrampa) carta, w, yOff);
        } else if (carta instanceof CartaMagica) {
            paintMagia(g2, (CartaMagica) carta, w, yOff);
        }

        if (!habilitada) {
            g2.setColor(new Color(0, 0, 0, 100));
            g2.fillRoundRect(0, yOff, w - 2, h - 2, 10, 10);
        }

        if (seleccionada) {
            g2.setColor(new Color(255, 220, 50, 60));
            g2.fillRoundRect(0, yOff, w - 2, h - 2, 10, 10);
        }
    }

    private void paintMonstruo(Graphics2D g2, Monstruo m, int w, int h, int yOff) {

        g2.setColor(GOLD);
        g2.setFont(new Font("Serif", Font.PLAIN, 7));
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < Math.min(m.getNivel(), 8); i++) stars.append("★");
        String starStr = stars.toString();
        FontMetrics fmS = g2.getFontMetrics();
        g2.drawString(starStr, Math.max(3, (w - fmS.stringWidth(starStr)) / 2), 73 + yOff);

        g2.setColor(new Color(255, 220, 100));
        g2.setFont(new Font("Monospaced", Font.BOLD, 8));
        g2.drawString("ATK:" + m.getAtk(), 5, 85 + yOff);
        g2.drawString("DEF:" + m.getDef(), 5, 95 + yOff);

        if (enCampo) {
            String mode = m.isEnModoAtaque() ? "ATK" : "DEF";
            Color modeColor = m.isEnModoAtaque() ? new Color(255, 80, 80) : new Color(80, 150, 255);
            g2.setColor(modeColor);
            g2.fillRoundRect(w - 28, h - 18 + yOff, 24, 12, 4, 4);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Monospaced", Font.BOLD, 8));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(mode, w - 28 + (24 - fm.stringWidth(mode)) / 2, h - 9 + yOff);
        }
    }

    private void paintTrampa(Graphics2D g2, CartaTrampa ct, int w, int yOff) {
        if (enCampo) {
            g2.setColor(new Color(180, 50, 50));
            g2.setFont(new Font("Serif", Font.BOLD, 8));
            g2.drawString("↓ BOCA ABAJO", 5, 85 + yOff);
        } else {
            g2.setFont(new Font("Serif", Font.PLAIN, 7));
            g2.setColor(new Color(255, 180, 180));
            drawWrappedString(g2, ct.getEfectoDescripcion(), 4, 70 + yOff, w - 8, 7);
        }
    }

    private void paintMagia(Graphics2D g2, CartaMagica cm, int w, int yOff) {
        g2.setFont(new Font("Serif", Font.PLAIN, 7));
        g2.setColor(new Color(180, 255, 180));
        drawWrappedString(g2, cm.getEfectoDescripcion(), 4, 70 + yOff, w - 8, 7);
    }

    private Color getBgColor() {
        if (carta instanceof Monstruo)   return COLOR_MONSTRUO_BG;
        if (carta instanceof CartaMagica) return COLOR_MAGIA_BG;
        if (carta instanceof CartaTrampa) return COLOR_TRAMPA_BG;
        return COLOR_DORSO_BG;
    }

    private void drawWrappedString(Graphics2D g2, String text, int x, int y, int maxW, int lineH) {
        FontMetrics fm = g2.getFontMetrics();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int curY = y;
        for (String word : words) {
            String test = line.length() > 0 ? line + " " + word : word;
            if (fm.stringWidth(test) > maxW) {
                if (line.length() > 0) {
                    g2.drawString(line.toString(), x, curY);
                    curY += lineH + 2;
                    line = new StringBuilder(word);
                } else {
                    g2.drawString(word, x, curY);
                    curY += lineH + 2;
                }
            } else {
                line = new StringBuilder(test);
            }
        }
        if (line.length() > 0) g2.drawString(line.toString(), x, curY);
    }
}