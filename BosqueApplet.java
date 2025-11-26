import java.applet.Applet;
import java.awt.Color; // Importación específica
import java.awt.Font; // Importación específica
import java.awt.Graphics; // Importación específica
import java.awt.Point; // Importación específica
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * Estructuras de Datos utilizadas: Matriz, Lista, Cola, Pila y Arrays 1D.
 */
public class BosqueApplet extends Applet implements KeyListener {

    // --- 1. ESTRUCTURAS DE DATOS PRINCIPALES ---
    
    // Matriz (Array 2D): Mapa del Bosque (10x10)
    private final int FILAS = 10;
    private final int COLUMNAS = 10;
    // Códigos de la Matriz: 0:Pasto, 1:Obstáculo(Árbol), 2:Hongo(NPC), 3:Tesoro(Ingrediente), 4:Enemigo(Planta), 5:Aventurero
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
    
    // Cola (Queue): Gestión de Diálogos y Mensajes
    private Queue<String> mensajesCola = new LinkedList<>();
    
    // Pila (Stack): Historial de Movimientos para Deshacer
    private Stack<Point> historialMovimiento = new Stack<>(); 

    // Array (1D): Atributos del Personaje (Salud y Posición)
    private int salud = 100;
    private int ataque = 15;
    private int aventureroX = 1; // Columna inicial
    private int aventureroY = 1; // Fila inicial

    // --- VARIABLES DE INTERFAZ ---
    private final int TAMANO_CELDA = 40;
    // Simplificamos la cadena de texto para eliminar cualquier posible error de codificación.
    private String mensajeActual = "Bienvenido al Bosque! Usa las flechas (arriba, abajo, izq, der) para moverte. Presiona 'U' para Deshacer.";
    private boolean juegoTerminado = false;

    /**
     * Método de inicialización del Applet. Se ejecuta al inicio.
     */
    public void init() {
        // Configuramos el tamaño del Applet
        setSize(COLUMNAS * TAMANO_CELDA + 10, FILAS * TAMANO_CELDA + 120);
        addKeyListener(this);
        setFocusable(true); // Asegura que el Applet pueda recibir eventos de teclado
        
        // Inicializamos la Cola de mensajes
        mensajesCola.offer("El Aventurero ha despertado en el claro. Presiona ENTER para leer mensajes.");
        mensajesCola.offer("Tu objetivo es encontrar el Hongo Dorado (codigo 6).");
    }

    /**
     * Lógica de dibujo principal. .
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

                // Dibujar elemento según código de la Matriz
                int codigo = mapa[y][x];
                g.setFont(new Font("SansSerif", Font.PLAIN, 24));
                g.setColor(Color.BLACK);
                
                switch (codigo) {
                    case 1: // Obstáculo (Árbol/Roca)
                        g.setColor(new Color(77, 65, 48)); 
                        g.fillRect(posX, posY, TAMANO_CELDA, TAMANO_CELDA);
                        g.drawString("A", posX + 13, posY + 30); // 'A' por Arbol
                        break;
                    case 2: // Hongo Parlante (NPC)
                        g.drawString("Honguito", posX + 13, posY + 30); // 'H' por Hongo
                        break;
                    case 3: // Tesoro (Ingrediente Mágico)
                        g.drawString("Tesoro", posX + 13, posY + 30); // 'T' por Tesoro
                        break;
                    case 4: // Enemigo (Planta Venenosa)
                        g.drawString("Poison", posX + 13, posY + 30); // 'E' por Enemigo
                        break;
                    case 5: // Aventurero
                        g.drawString("M1", posX + 13, posY + 30); // 'P' por Personaje
                        break;
                    case 6: // Meta (Hongo Dorado)
                        g.drawString("Golden Price", posX + 13, posY + 30); // 'M' por Meta
                        break;
                }
            }
        }

        // --- 2. Dibuja el Panel de Información y Mensajes ---
        int panelY = FILAS * TAMANO_CELDA + 10;
        int panelAltura = 100;
        g.setColor(new Color(64, 64, 64)); // Gris oscuro
        g.fillRect(0, panelY, COLUMNAS * TAMANO_CELDA, panelAltura);

        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        // Atributos (Array 1D)
        g.drawString("SALUD (Array 1D): " + salud + "/100", 10, panelY + 20);
        g.drawString("ATAQUE (Array 1D): " + ataque, 10, panelY + 40);
        g.drawString("POSICION (Array 1D): (" + aventureroX + ", " + aventureroY + ")", 10, panelY + 60);
        
        // Inventario (Lista)
        g.drawString("INVENTARIO (Lista): " + String.join(", ", inventario), 200, panelY + 20);
        g.drawString("Tamaño Pila Deshacer: " + historialMovimiento.size(), 200, panelY + 40);

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
     * @param nuevoX La nueva columna a la que se intenta mover.
     * @param nuevoY La nueva fila a la que se intenta mover.
     */
    private void intentarMover(int nuevoX, int nuevoY) {
        if (juegoTerminado) return;

        // 1. Verificar límites del mapa
        if (nuevoX >= 0 && nuevoX < COLUMNAS && nuevoY >= 0 && nuevoY < FILAS) {
            int destino = mapa[nuevoY][nuevoX];

            // 2. Solo mover si la celda no es un Obstáculo (código 1)
            if (destino != 1) {
                
                // Guardar posición actual en la Pila antes de mover
                historialMovimiento.push(new Point(aventureroX, aventureroY));

                // Limpiar la posición anterior
                mapa[aventureroY][aventureroX] = 0; 

                // 3. Lógica de interacción con elementos
                switch (destino) {
                    case 0: // Pasto (Movimiento normal)
                        mensajeActual = "Te has movido a un claro vacio.";
                        break;
                    case 2: // Hongo Parlante (NPC) - Usa Cola y Lista
                        if (!mensajesCola.isEmpty()) {
                            // Cambiamos el emoji por texto
                            mensajeActual = "Hongo dice: " + mensajesCola.poll();
                        } else {
                            mensajeActual = "Hongo: 'No tengo mas que decir, sigue tu camino!'";
                        }
                        // Solo añadimos si la lista no está llena.
                        if (inventario.size() < 4) {
                            inventario.add("Pocion de Dialogo"); // Añadimos a la Lista de Inventario
                        }
                        break;
                    case 3: // Tesoro (Ingrediente Magico) - Usa Lista
                        String nuevoItem = "Ingrediente Magico (" + (inventario.size() + 1) + ")";
                        inventario.add(nuevoItem); // Añadir a la Lista (Inventario)
                        mensajeActual = "Has encontrado un Tesoro! " + nuevoItem + " agregado al Inventario.";
                        // El tesoro se consume
                        mapa[nuevoY][nuevoX] = 0;
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
                        mensajeActual = "FELICIDADES! Encontraste el Hongo Dorado. PROYECTO CUMPLIDO!";
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

    // Métodos no usados de KeyListener
    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {} 
}