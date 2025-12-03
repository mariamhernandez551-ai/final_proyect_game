import java.applet.Applet;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;

public class BosqueApplet extends Applet implements KeyListener {

    // --- COLORES MÁS FUERTES ---
    private final Color PASTO = new Color(120, 200, 120); // Verde vivo
    private final Color TRONCO = new Color(210, 180, 140); // Marrón claro
    private final Color HONGO = new Color(255, 160, 80); // Naranja fuerte
    private final Color FLOR = new Color(255, 230, 80); // Amarillo fuerte
    private final Color PLANTA = new Color(255, 120, 160); // Rosa fuerte
    private final Color MIN = new Color(80, 180, 255); // Azul cielo fuerte
    private final Color HONGO_DORADO = new Color(255, 215, 0); // Dorado fuerte
    private final Color PANEL = new Color(220, 230, 241); // Panel info azul muy claro

    // --- MAPAS DE NIVELES ---
    private final int[][][] NIVELES = {
        // Nivel 1: Recolectar 3 flores mágicas
        {
            {1,1,1,1,1,1,1,1,1,1},
            {1,0,0,0,2,0,0,0,0,1},
            {1,0,1,1,1,1,0,1,0,1},
            {1,0,1,3,0,0,0,1,0,1},
            {1,0,1,1,1,1,0,1,0,1},
            {1,0,0,0,3,0,0,0,0,1},
            {1,0,1,1,1,1,1,1,0,1},
            {1,0,0,4,0,0,0,0,0,1},
            {1,0,1,1,1,1,1,1,1,1},
            {1,0,0,0,0,3,0,0,0,1}
        },
        // Nivel 2: Vencer 5 plantas pícaras
        {
            {1,1,1,1,1,1,1,1,1,1},
            {1,0,4,0,2,0,4,0,0,1},
            {1,0,1,1,1,1,0,1,0,1},
            {1,0,1,3,0,0,0,1,0,1},
            {1,0,1,1,1,1,0,1,0,1},
            {1,0,0,0,4,0,0,0,0,1},
            {1,0,1,1,1,1,1,1,0,1},
            {1,0,0,4,0,0,0,0,0,1},
            {1,0,1,1,1,1,1,1,1,1},
            {1,0,0,0,0,3,0,0,0,1}
        },
        // Nivel 3: Encontrar el hongo dorado
        {
            {1,1,1,1,1,1,1,1,1,1},
            {1,0,0,0,2,0,0,0,0,1},
            {1,0,1,1,1,1,0,1,0,1},
            {1,0,1,4,0,0,0,1,0,1},
            {1,0,1,1,1,1,0,1,0,1},
            {1,0,0,0,0,0,0,0,0,1},
            {1,0,1,1,1,1,1,1,0,1},
            {1,0,0,4,0,0,0,0,0,1},
            {1,0,1,1,1,1,1,1,1,1},
            {1,0,0,0,0,6,0,0,0,1} // 6 = Hongo dorado
        }
    };

    // --- CÓDIGOS DE CELDA ---
    // 0: Pasto, 1: Tronco, 2: Honguito parlante, 3: Flor mágica, 4: Planta pícara, 5: Min, 6: Hongo dorado

    // --- VARIABLES DEL JUEGO ---
    private int[][] mapa;
    private int nivelActual = 0;
    private int floresRecolectadas = 0;
    private int plantasVencidas = 0;
    private boolean nivelCompletado = false;
    private boolean juegoGanado = false;

    private ArrayList<String> inventario;
    private Queue<String> mensajes;
    private Stack<int[]> historialMovimientos;
    private int[] minAtributos = new int[5]; // Salud, Ataque, Defensa, PosX, PosY

    private int minX, minY;
    private final int TILE_SIZE = 40;

    @Override
    public void init() {
        iniciarNivel(0);
        this.setSize(400, 520);
        this.addKeyListener(this);
        this.setFocusable(true);
        setBackground(Color.WHITE);
    }

    private void iniciarNivel(int nivel) {
        // Copia el mapa del nivel
        mapa = new int[10][10];
        for (int i = 0; i < 10; i++)
            System.arraycopy(NIVELES[nivel][i], 0, mapa[i], 0, 10);

        inventario = new ArrayList<>();
        mensajes = new LinkedList<>();
        historialMovimientos = new Stack<>();
        nivelActual = nivel;
        floresRecolectadas = 0;
        plantasVencidas = 0;
        nivelCompletado = false;
        juegoGanado = false;

        // Busca posición inicial de Min
        minX = 1;
        minY = 1;
        mapa[minY][minX] = 5;

        // Atributos: Salud, Ataque, Defensa, PosX, PosY
        minAtributos[0] = 100;
        minAtributos[1] = 10;
        minAtributos[2] = 5;
        minAtributos[3] = minX;
        minAtributos[4] = minY;

        // Mensajes de bienvenida por nivel
        if (nivel == 0) {
            mensajes.offer("¡Bienvenido, Min el explorador!");
            mensajes.offer("Nivel 1: Recolecta 3 flores mágicas.");
        } else if (nivel == 1) {
            mensajes.offer("¡Nivel 2! Vence 5 plantas pícaras.");
        } else if (nivel == 2) {
            mensajes.offer("¡Nivel final! Encuentra el hongo dorado.");
        }
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        // Fondo del mapa
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 400, 400);

        // Dibuja el mapa
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                int x = j * TILE_SIZE;
                int y = i * TILE_SIZE;

                switch (mapa[i][j]) {
                    case 0: // Pasto
                        g.setColor(PASTO);
                        g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
                        break;
                    case 1: // Tronco
                        g.setColor(TRONCO);
                        g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
                        // Dibuja líneas para simular tronco
                        g.setColor(new Color(160, 120, 80));
                        g.drawLine(x+10, y+10, x+30, y+30);
                        g.drawLine(x+30, y+10, x+10, y+30);
                        break;
                    case 2: // Honguito parlante
                        g.setColor(PASTO);
                        g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
                        // Tallo
                        g.setColor(Color.WHITE);
                        g.fillRect(x+17, y+25, 6, 10);
                        // Sombrero
                        g.setColor(HONGO);
                        g.fillOval(x+10, y+10, 20, 18);
                        break;
                    case 3: // Flor mágica
                        g.setColor(PASTO);
                        g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
                        g.setColor(FLOR);
                        g.fillOval(x+13, y+13, 14, 14);
                        g.setColor(new Color(255, 220, 120));
                        g.drawOval(x+13, y+13, 14, 14);
                        break;
                    case 4: // Planta pícara
                        g.setColor(PASTO);
                        g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
                        g.setColor(PLANTA);
                        g.fillOval(x+10, y+10, 20, 20);
                        g.setColor(new Color(200, 80, 120));
                        g.drawLine(x+20, y+30, x+20, y+38);
                        break;
                    case 5: // Min
                        g.setColor(PASTO);
                        g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
                        g.setColor(MIN);
                        g.fillOval(x+10, y+10, 20, 20);
                        g.setColor(Color.BLACK);
                        g.drawOval(x+10, y+10, 20, 20);
                        break;
                    case 6: // Hongo dorado
                        g.setColor(PASTO);
                        g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
                        // Tallo
                        g.setColor(new Color(255, 255, 224));
                        g.fillRect(x+17, y+25, 6, 10);
                        // Sombrero dorado
                        g.setColor(HONGO_DORADO);
                        g.fillOval(x+10, y+10, 20, 18);
                        g.setColor(new Color(255, 215, 0));
                        g.drawOval(x+10, y+10, 20, 18);
                        break;
                }
                // Bordes de celda
                g.setColor(new Color(180, 180, 180));
                g.drawRect(x, y, TILE_SIZE, TILE_SIZE);
            }
        }

        // Panel de información
        g.setColor(PANEL);
        g.fillRect(0, 400, 400, 120);

        g.setColor(new Color(60, 60, 90));
        g.setFont(new Font("Arial", Font.BOLD, 13));
        g.drawString("Min el explorador | HP: " + minAtributos[0] + " | Atk: " + minAtributos[1] + " | Def: " + minAtributos[2], 10, 420);

        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("Inventario: " + inventario, 10, 440);

        // Diálogo
        g.setFont(new Font("Arial", Font.ITALIC, 12));
        String mensajeActual = mensajes.peek();
        if (mensajeActual != null) {
            g.drawString("Mensaje: " + mensajeActual, 10, 460);
        }

        // Objetivo del nivel
        g.setFont(new Font("Arial", Font.BOLD, 12));
        if (nivelActual == 0)
            g.drawString("Objetivo: Recolecta 3 flores mágicas (" + floresRecolectadas + "/3)", 10, 480);
        else if (nivelActual == 1)
            g.drawString("Objetivo: Vence 5 plantas pícaras (" + plantasVencidas + "/5)", 10, 480);
        else if (nivelActual == 2)
            g.drawString("Objetivo: Encuentra el hongo dorado", 10, 480);

        // Mensaje de nivel completado o victoria
        if (nivelCompletado) {
            g.setColor(new Color(120, 180, 255, 180));
            g.fillRoundRect(60, 180, 280, 60, 20, 20);
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 18));
            if (juegoGanado)
                g.drawString("¡Felicidades, ganaste!", 100, 215);
            else
                g.drawString("¡Nivel completado!", 110, 215);
            g.setFont(new Font("Arial", Font.PLAIN, 13));
            g.drawString("Presiona ENTER para continuar", 110, 235);
        }
    }

    // --- MOVIMIENTO Y LÓGICA DE JUEGO ---
    private void moverMin(int dx, int dy) {
        if (nivelCompletado) return;
        int nuevaX = minX + dx;
        int nuevaY = minY + dy;
        if (nuevaX < 0 || nuevaX >= 10 || nuevaY < 0 || nuevaY >= 10) return;
        int celda = mapa[nuevaY][nuevaX];
        if (celda == 1) return; // Tronco

        historialMovimientos.push(new int[]{minX, minY});
        mapa[minY][minX] = 0; // Limpia posición anterior
        minX = nuevaX;
        minY = nuevaY;

        // Interacción
        if (nivelActual == 0 && celda == 3) { // Flor mágica
            floresRecolectadas++;
            inventario.add("Flor mágica");
            mensajes.offer("¡Min encontró una flor mágica!");
            if (floresRecolectadas >= 3) {
                nivelCompletado = true;
            }
        } else if (nivelActual == 1 && celda == 4) { // Planta pícara
            plantasVencidas++;
            inventario.add("Poción");
            minAtributos[0] -= 10;
            mensajes.offer("¡Min venció una planta pícara! (-10 HP, +Poción)");
            if (plantasVencidas >= 5) {
                nivelCompletado = true;
            }
        } else if (nivelActual == 2 && celda == 6) { // Hongo dorado
            inventario.add("Hongo dorado");
            nivelCompletado = true;
            juegoGanado = true;
        } else if (celda == 2) { // Honguito parlante
            mensajes.offer("Honguito: ¡Hola Min! Sigue explorando.");
        }

        mapa[minY][minX] = 5; // Min en nueva posición
        minAtributos[3] = minX;
        minAtributos[4] = minY;
        repaint();
    }

    private void deshacerMovimiento() {
        if (nivelCompletado) return;
        if (!historialMovimientos.empty()) {
            int[] pos = historialMovimientos.pop();
            mapa[minY][minX] = 0;
            minX = pos[0];
            minY = pos[1];
            mapa[minY][minX] = 5;
            mensajes.offer("Min deshizo su último movimiento.");
            repaint();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // --- CAMBIO DE NIVEL CON ENTER ---
        if (nivelCompletado && (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyChar() == '\n')) {
            if (juegoGanado) {
                iniciarNivel(0); // Reinicia el juego
            } else {
                iniciarNivel(nivelActual + 1);
            }
            return;
        }
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP: moverMin(0, -1); break;
            case KeyEvent.VK_DOWN: moverMin(0, 1); break;
            case KeyEvent.VK_LEFT: moverMin(-1, 0); break;
            case KeyEvent.VK_RIGHT: moverMin(1, 0); break;
            case KeyEvent.VK_U: deshacerMovimiento(); break;
        }
    }
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
}