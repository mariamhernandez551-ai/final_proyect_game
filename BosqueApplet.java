import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Font;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * Proyecto Final de Estructura de Datos: "Bosque de los Susurros"
 * Implementación de un juego simple basado en Applet, utilizando las estructuras
 * de Matriz, Lista, Cola y Pila.
 * NOTA: La prueba de diagnóstico de fondo rojo ha sido eliminada.
 * Este código contiene la lógica completa para dibujar el tablero y el panel de información.
 */
public class BosqueApplet extends Applet implements KeyListener {

    // --- 1. ESTRUCTURAS DE DATOS PRINCIPALES ---

    // Matriz (Array 2D): Mapa del Bosque [10x10]
    private int[][] mapa = {
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 0, 0, 0, 2, 0, 0, 0, 0, 1},
        {1, 0, 1, 1, 1, 1, 0, 1, 0, 1},
        {1, 0, 1, 3, 0, 0, 0, 1, 0, 1},
        {1, 0, 1, 1, 1, 1, 0, 1, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 1, 1, 1, 1, 1, 1, 0, 1},
        {1, 0, 0, 4, 0, 0, 0, 0, 0, 1},
        {1, 0, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 0, 0, 0, 0, 3, 0, 0, 0, 1}
    };
    
    // Lista (ArrayList): Inventario del Aventurero
    private ArrayList<String> inventario; 

    // Cola (Queue): Mensajes de Diálogo
    private Queue<String> mensajes;

    // Pila (Stack): Historial de Movimientos (para función 'Deshacer')
    private Stack<int[]> historialMovimientos;

    // Array (1D): Atributos del Personaje [Salud, Ataque, Defensa, PosX, PosY]
    private int[] aventureroAtributos = new int[5]; 
    
    // --- 2. VARIABLES DEL JUEGO ---
    private int aventureroX; // Posición X actual (columna)
    private int aventureroY; // Posición Y actual (fila)
    private final int TILE_SIZE = 40; // Tamaño en píxeles de cada celda

    // --- 3. INICIALIZACIÓN (Applet) ---

    @Override
    public void init() {
        // Inicialización de estructuras de datos
        inventario = new ArrayList<>();
        mensajes = new LinkedList<>();
        historialMovimientos = new Stack<>();

        // Configuración inicial del Aventurero
        aventureroX = 1; 
        aventureroY = 1;
        mapa[aventureroY][aventureroX] = 5; // Posicionar Aventurero (código 5)

        // Llenar Cola de Diálogo
        mensajes.offer("Bienvenido al Bosque. Encuentra el Hongo Dorado!");
        mensajes.offer("Cuidado con las Plantas Pícaras. ¡Recolecta Ingredientes!");
        mensajes.offer("Presiona 'U' para deshacer tu último movimiento.");

        // Inicializar atributos (ejemplo: Salud, Ataque, Defensa, PosX, PosY)
        aventureroAtributos[0] = 100; // Salud
        aventureroAtributos[1] = 10;  // Ataque
        aventureroAtributos[2] = 5;   // Defensa
        aventureroAtributos[3] = aventureroX;
        aventureroAtributos[4] = aventureroY;

        // Configuración de la interfaz
        this.setSize(400, 520); // 10x10 * 40px = 400px (Mapa 400x400) + Panel Inferior
        this.addKeyListener(this);
        this.setFocusable(true);
        setBackground(Color.BLACK);
        
        // Llamada a función JavaScript para indicar a Cheerpj que la inicialización de Java terminó
        try {
            getAppletContext().showDocument(new java.net.URL("javascript:appletInitialized();"));
        } catch (Exception e) {
            // Manejo de excepción si showDocument falla (normal en algunos entornos)
        }
    }

    // --- 4. DIBUJO EN PANTALLA (CÓDIGO DE JUEGO ACTIVO) ---

    @Override
    public void paint(Graphics g) {
        
        // 1. Dibujar Mapa (Matriz 10x10)
        for (int i = 0; i < mapa.length; i++) {
            for (int j = 0; j < mapa[0].length; j++) {
                int x = j * TILE_SIZE;
                int y = i * TILE_SIZE;

                switch (mapa[i][j]) {
                    case 0: g.setColor(new Color(153, 204, 102)); break; // Pasto (Verde claro)
                    case 1: g.setColor(new Color(102, 51, 0)); break;    // Árbol/Roca (Marrón)
                    case 2: g.setColor(Color.ORANGE); break;             // Hongo Parlante (Naranja)
                    case 3: g.setColor(Color.YELLOW); break;             // Ingrediente Mágico (Amarillo)
                    case 4: g.setColor(Color.RED); break;                // Planta Pícara (Rojo)
                    case 5: g.setColor(Color.BLUE); break;               // Aventurero (Azul)
                    default: g.setColor(Color.GRAY); break;              // Desconocido
                }
                g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, TILE_SIZE, TILE_SIZE);
            }
        }
        
        // 2. Dibujar Panel de Información (Debajo del mapa 400x400)
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 400, getWidth(), 120);

        // 3. Mostrar Inventario (Lista)
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("INVENTARIO (Lista):", 10, 420);
        int itemY = 435;
        for (String item : inventario) {
            g.drawString("- " + item, 15, itemY);
            itemY += 15;
        }

        // 4. Mostrar Diálogo (Cola)
        g.drawString("DIÁLOGO (Cola):", 220, 420);
        String mensajeActual = mensajes.peek(); // Solo vemos el primero sin quitarlo
        if (mensajeActual != null) {
             g.drawString(mensajeActual, 225, 435);
        } else {
             g.drawString("El bosque está en silencio...", 225, 435);
        }

        // 5. Mostrar Atributos (Array 1D)
        g.drawString(String.format("HP: %d | Atk: %d | Def: %d", 
            aventureroAtributos[0], aventureroAtributos[1], aventureroAtributos[2]), 10, 510);
    }

    // --- 5. LÓGICA DE MOVIMIENTO ---

    private void moverAventurero(int dx, int dy) {
        int nuevaX = aventureroX + dx;
        int nuevaY = aventureroY + dy;

        // Comprobar límites y Obstáculos (código 1)
        if (nuevaX >= 0 && nuevaX < 10 && nuevaY >= 0 && nuevaY < 10 && mapa[nuevaY][nuevaX] != 1) {

            // 1. Guardar posición anterior en la Pila
            historialMovimientos.push(new int[]{aventureroX, aventureroY});

            // 2. Limpiar posición anterior y mover
            mapa[aventureroY][aventureroX] = 0; // Pasto
            aventureroX = nuevaX;
            aventureroY = nuevaY;

            // 3. Interacción con el nuevo tile
            interactuar(mapa[aventureroY][aventureroX]);

            // 4. Establecer nueva posición del Aventurero
            mapa[aventureroY][aventureroX] = 5;

            // 5. Actualizar atributos de posición
            aventureroAtributos[3] = aventureroX;
            aventureroAtributos[4] = aventureroY;

            // 6. Repintar la interfaz
            repaint(); 
        }
    }

    // --- 6. LÓGICA DE INTERACCIÓN ---

    private void interactuar(int codigo) {
        switch (codigo) {
            case 2: // Hongo Parlante (Cola)
                String dialogo = mensajes.poll(); // Saca el mensaje
                if (dialogo != null) {
                    mensajes.offer(">> " + dialogo); // Vuelve a meterlo o mostrarlo
                } else {
                    mensajes.offer("Hongo: Ya no tengo más que decir...");
                }
                break;
            case 3: // Ingrediente Mágico (Lista)
                inventario.add("Ingrediente Mágico"); // Añadir a la Lista
                mensajes.offer("¡Obtuviste un Ingrediente Mágico! (+Lista)");
                break;
            case 4: // Planta Pícara (Enemigo)
                aventureroAtributos[0] -= 10; // Daño a la Salud
                mensajes.offer("¡Te atacó una Planta Pícara! (-10 HP)");
                break;
            default:
                // No hay interacción especial
                break;
        }
    }

    // --- 7. FUNCIÓN DESHACER (Pila) ---

    private void deshacerMovimiento() {
        if (!historialMovimientos.empty()) {
            // 1. Obtener posición anterior de la Pila
            int[] posicionAnterior = historialMovimientos.pop(); 
            int oldX = posicionAnterior[0];
            int oldY = posicionAnterior[1];

            // 2. Limpiar posición actual y mover
            mapa[aventureroY][aventureroX] = 0; // Pasto en posición actual
            aventureroX = oldX;
            aventureroY = oldY;
            mapa[aventureroY][aventureroX] = 5; // Aventurero en posición anterior

            // 3. Actualizar atributos
            aventureroAtributos[3] = aventureroX;
            aventureroAtributos[4] = aventureroY;

            mensajes.offer("Movimiento deshecho. (+Pila)");
            repaint();
        } else {
            mensajes.offer("No hay movimientos para deshacer.");
        }
    }

    // --- 8. MANEJO DE TECLAS ---

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        
        if (key == KeyEvent.VK_UP) {
            moverAventurero(0, -1);
        } else if (key == KeyEvent.VK_DOWN) {
            moverAventurero(0, 1);
        } else if (key == KeyEvent.VK_LEFT) {
            moverAventurero(-1, 0);
        } else if (key == KeyEvent.VK_RIGHT) {
            moverAventurero(1, 0);
        } else if (e.getKeyChar() == 'u' || e.getKeyChar() == 'U') {
            deshacerMovimiento();
        }
    }

    // Métodos KeyListener no utilizados pero requeridos por la interfaz
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}