import java.applet.Applet;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * Proyecto Final Estructura de Datos: El mini Bosque.
 * Estructuras de Datos: Matriz, Lista, Cola, Pila y Arrays 1D.
 */
public class BosqueApplet extends Applet implements KeyListener {

    // --- 1. ESTRUCTURAS DE DATOS PRINCIPALES ---
    
    // Matriz (Array 2D): Mapa del Bosque (10x10)
    private final int FILAS = 10;
    private final int COLUMNAS = 10;
    // Codigos de la Matriz: 0:Pasto, 1:Obstaculo(arbol), 2:Hongo(NPC), 3:Tesoro(Ingrediente), 4:Enemigo(Planta), 5:Aventurero
    private int[][] mapa = {
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 5, 0, 2, 0, 0, 0, 3, 0, 1},
        {1, 0, 1, 0, 1, 0, 1, 0, 1, 1},
        {1, 0, 1, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 1, 0, 1, 1, 1, 4, 0, 1},
        {1, 0, 0, 0, 0, 0, 1, 0, 0, 1},
        {1, 0, 1, 1, 1, 0, 1, 0, 1, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 3, 0, 0, 0, 6, 1}, // 6: Meta (Hongo Dorado)
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
    };
    
    // Lista (ArrayList): Inventario del Aventurero
    private ArrayList<String> inventario = new ArrayList<>();
    
    // Cola (Queue): Gestion de Dialogos y Mensajes
    private Queue<String> mensajesCola = new LinkedList<>();
    
    // Pila (Stack): Historial de Movimientos para Deshacer
    private Stack<Point> historialMovimiento = new Stack<>(); 

    // Array (1D): Atributos del Personaje (Salud y Posicion)
    private int salud = 100;
    private int ataque = 15;
    private int aventureroX = 1; // Columna inicial
    private int aventureroY = 1; // Fila inicial

    // --- VARIABLES DE INTERFAZ ---
    private final int TAMANO_CELDA = 40;
    private String mensajeActual = "Bienvenido al Bosque! Usa ←↑↓→ para moverte. Presiona 'U' para Deshacer.";
    private boolean juegoTerminado = false;

    /**
     * Metodo de inicializacion del Applet. Se ejecuta al inicio.
     */
    public void init() {
        // Configuramos la medida del Applet
        setSize(COLUMNAS * TAMANO_CELDA + 10, FILAS * TAMANO_CELDA + 120);
        addKeyListener(this);
        
        // Inicializamos la Cola de mensajes
        mensajesCola.offer("El Aventurero ha despertado en el claro. Presiona ENTER para leer mensajes.");
        mensajesCola.offer("Tu objetivo es encontrar el Hongo Dorado (codigo 6).");
    }

    /**
     * Logica de dibujo principal. Se llama con repaint().
     */
    public void paint(Graphics g) {
        // Establecer el color de fondo del bosque
        setBackground(new Color(247, 254, 231)); 
        
        // --- 1. Dibuja el Mapa (Matriz) ---
        for (int y = 0; y < FILAS; y++) {
            for (int x = 0; x < COLUMNAS; x++) {
                int posX = x * TAMANO_CELDA;
                int posY = y * TAMANO_CELDA;

                // Dibujar fondo base (Pasto)
                g.setColor(new Color(163, 230, 53)); 
                g.fillRect(posX, posY, TAMANO_CELDA, TAMANO_CELDA);
                g.setColor(new Color(101, 163, 13)); 
                g.drawRect(posX, posY, TAMANO_CELDA, TAMANO_CELDA);

                // Dibujar elemento segun codigo de la Matriz
                int codigo = mapa[y][x];
                g.setFont(new Font("SansSerif", Font.PLAIN, 24));
                g.setColor(Color.BLACK);
                
                switch (codigo) {
                    case 1: // Obstaculo (arbol/Roca)
                        g.setColor(new Color(77, 65, 48)); 
                        g.fillRect(posX, posY, TAMANO_CELDA, TAMANO_CELDA);
                        g.drawString("🌳", posX + 8, posY + 30);
                        break;
                    case 2: // Hongo Parlante (NPC)
                        g.drawString("🍄", posX + 8, posY + 30);
                        break;
                    case 3: // Tesoro (Ingrediente Magico)
                        g.drawString("⭐", posX + 8, posY + 30);
                        break;
                    case 4: // Enemigo (Planta Venenosa)
                        g.drawString("❌", posX + 8, posY + 30);
                        break;
                    case 5: // Aventurero
                        g.drawString("🚶", posX + 8, posY + 30);
                        break;
                    case 6: // Meta (Hongo Dorado)
                        g.drawString("👑", posX + 8, posY + 30);
                        break;
                }
            }
        }

        // --- 2. Dibuja el Panel de Informacion y Mensajes ---
        int panelY = FILAS * TAMANO_CELDA + 10;
        int panelAltura = 100;
        g.setColor(new Color(64, 64, 64)); // Gris oscuro
        g.fillRect(0, panelY, COLUMNAS * TAMANO_CELDA, panelAltura);

        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        // Atributos (Array 1D)
        g.drawString("SALUD (Array 1D): " + salud + "/100", 10, panelY + 20);
        g.drawString("ATAQUE (Array 1D): " + ataque, 10, panelY + 40);
        g.drawString("POSICIoN (Array 1D): (" + aventureroX + ", " + aventureroY + ")", 10, panelY + 60);
        
        // Inventario (Lista)
        g.drawString("INVENTARIO (Lista): " + String.join(", ", inventario), 200, panelY + 20);
        g.drawString("Tamano Pila Deshacer: " + historialMovimiento.size(), 200, panelY + 40);

        // Mensajes (Cola)
        g.setColor(new Color(255, 255, 153)); // Amarillo claro para el mensaje
        g.drawString("MENSAJE (Cola): " + mensajeActual, 10, panelY + 85);
        
        // Mensaje de juego terminado
        if (juegoTerminado) {
            g.setColor(Color.RED);
            g.setFont(new Font("SansSerif", Font.BOLD, 36));
            g.drawString("JUEGO TERMINADO!", 50, FILAS * TAMANO_CELDA / 2);
        }
    }

    /**
     * Movimiento y las interacciones.
     * @param nuevoX La nueva columna a la que se intenta mover.
     * @param nuevoY La nueva fila a la que se intenta mover.
     */
    private void intentarMover(int nuevoX, int nuevoY) {
        if (juegoTerminado) return;

        // 1. Verificar limites del mapa
        if (nuevoX >= 0 && nuevoX < COLUMNAS && nuevoY >= 0 && nuevoY < FILAS) {
            int destino = mapa[nuevoY][nuevoX];

            // 2. Solo mover si la celda no es un Obstaculo (codigo 1)
            if (destino != 1) {
                
                // Guardar posicion actual en la Pila antes de mover
                historialMovimiento.push(new Point(aventureroX, aventureroY));

                // Limpiar la posicion anterior
                mapa[aventureroY][aventureroX] = 0; 

                // 3. Logica de interaccion con elementos
                switch (destino) {
                    case 0: // Pasto (Movimiento normal)
                        mensajeActual = "Te has movido a un claro vacio.";
                        break;
                    case 2: // Hongo Parlante (NPC) - Usa Cola y Lista
                        if (!mensajesCola.isEmpty()) {
                            mensajeActual = "🍄 Hongo dice: " + mensajesCola.poll();
                        } else {
                            mensajeActual = "Hongo: 'No tengo mas que decir, sigue tu camino!'";
                        }
                        if (inventario.size() < 4) {
                            inventario.add("Pocion de Dialogo"); // Anadimos a la Lista de Inventario
                        }
                        break;
                    case 3: // Tesoro (Ingrediente Magico) - Usa Lista
                        String nuevoItem = "Ingrediente Magico (" + inventario.size() + ")";
                        inventario.add(nuevoItem); // Anadir a la Lista (Inventario)
                        mensajeActual = "Has encontrado un Tesoro! " + nuevoItem + " agregado al Inventario.";
                        break;
                    case 4: // Enemigo (Planta Venenosa) - Usa Array 1D para salud
                        salud -= 20; // Reduce salud
                        mensajeActual = "Planta Venenosa! Te ha quitado 20 de salud. Salud actual: " + salud;
                        if (salud <= 0) {
                            juegoTerminado = true;
                            mensajeActual = "HAS PERDIDO. Te quedaste sin salud.";
                        } else {
                            // Limpiamos el enemigo despues del "combate"
                            destino = 0; 
                        }
                        break;
                    case 6: // Meta (Hongo Dorado)
                        juegoTerminado = true;
                        mensajeActual = "FELICIDADES! Encontraste el Hongo Dorado. MISION CUMPLIDA!";
                        break;
                }
                
                // 4. Actualizar la posicion del Aventurero en el Array 1D y Matriz
                aventureroX = nuevoX;
                aventureroY = nuevoY;
                mapa[aventureroY][aventureroX] = 5; 
                
                // Repintar la interfaz
                repaint(); 
            } else {
                mensajeActual = "Movimiento bloqueado! Hay un arbol en el camino.";
            }
        }
    }
    
    /**
     * Implementa la funcion Deshacer usando la Pila.
     */
    private void deshacerMovimiento() {
        if (historialMovimiento.isEmpty() || juegoTerminado) {
            mensajeActual = "No hay movimientos previos para deshacer.";
            return;
        }

        // 1. Sacar la posicion anterior de la Pila (LIFO)
        Point posicionPrevia = historialMovimiento.pop();
        
        // 2. Limpiar la posicion actual en la Matriz
        mapa[aventureroY][aventureroX] = 0;

        // 3. Restaurar la posicion del Aventurero
        aventureroX = posicionPrevia.x;
        aventureroY = posicionPrevia.y;
        
        // 4. Actualizar la Matriz con la posicion restaurada
        mapa[aventureroY][aventureroX] = 5;
        
        mensajeActual = "Movimiento deshecho. Volviste a (" + aventureroX + ", " + aventureroY + ")";
        repaint();
    }


    // ----------------------------------------------------

    public void keyPressed(KeyEvent e) {
        int nuevaX = aventureroX;
        int nuevaY = aventureroY;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                nuevaY--;
                break;
            case KeyEvent.VK_DOWN:
                nuevaY++;
                break;
            case KeyEvent.VK_LEFT:
                nuevaX--;
                break;
            case KeyEvent.VK_RIGHT:
                nuevaX++;
                break;
            case KeyEvent.VK_ENTER: // Comando para avanzar en la cola de mensajes
                if (!mensajesCola.isEmpty()) {
                     mensajeActual = "MENSAJE (ENTER): " + mensajesCola.poll();
                     repaint();
                } else {
                    mensajeActual = "No hay mas mensajes en la Cola.";
                }
                return; 
            case KeyEvent.VK_U: // Comando para Deshacer (Usa Pila)
                 deshacerMovimiento();
                 return;
            default:
                return;
        }
        
        // Intentar mover solo si el movimiento no es ENTER o U
        intentarMover(nuevaX, nuevaY);
    }

    // Metodos no usados de KeyListener
    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}
}