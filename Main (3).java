import java.util.Scanner;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// ========================================================
// 1. CLASE PRINCIPAL: Controla el flujo en la Consola
// ========================================================
public class Main {
    private static ExecutorService poolHilos;
    private static ScheduledExecutorService poolProgramado;

    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);
        
        System.out.println("=== PANEL DE CONTROL CONCURRENTE (MODO CONSOLA) ===");
        System.out.print("Ingresa la cantidad inicial de segundos para el juego: ");
        int segundosIniciales = teclado.nextInt();
        
        System.out.println("\n[Presiona ENTER para iniciar el juego]");
        teclado.nextLine(); // Limpiar buffer
        teclado.nextLine(); // Esperar Enter

        System.out.println("--- JUEGO INICIADO ---");

        // 1. Iniciamos el hilo del cronómetro independiente
        Cronometro tareaCronometro = new Cronometro(segundosIniciales);
        poolHilos = Executors.newCachedThreadPool();
        poolHilos.execute(tareaCronometro);

        // 2. Iniciamos la tarea programada recurrente cada 400 milisegundos
        Jugar tareaJuego = new Jugar();
        poolProgramado = Executors.newSingleThreadScheduledExecutor();
        poolProgramado.scheduleAtFixedRate(tareaJuego, 0, 400, TimeUnit.MILLISECONDS);

        // 3. Simulación de detención automática cuando acabe el tiempo
        try {
            // Dejamos correr los hilos el tiempo solicitado por el usuario + 1 segundo extra
            Thread.sleep((segundosIniciales + 1) * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // --- DETENER TODO AL FINALIZAR ---
        detenerTodo();
        teclado.close();
    }

    public static void detenerTodo() {
        if (poolHilos != null && !poolHilos.isShutdown()) {
            poolHilos.shutdownNow();
        }
        if (poolProgramado != null && !poolProgramado.isShutdown()) {
            poolProgramado.shutdownNow();
        }
        System.out.println("\n--- JUEGO FINALIZADO ---");
    }
}

// ========================================================
// 2. CLASE CRONOMETRO: Cuenta regresiva concurrente
// ========================================================
class Cronometro implements Runnable {
    private int ini;

    public Cronometro(int segs) {
        this.ini = segs;
    }

    @Override
    public void run() {
        try {
            for (int s = ini; s >= 0; s--) {
                String formatoSegundos = (s < 10) ? "0" + s : String.valueOf(s);
                
                // Imprime el reloj de forma segura en la consola
                System.out.println("\n[RELOJ] -> 00:00:" + formatoSegundos);
                
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            System.out.println("\n[RELOJ] -> XX (Cronómetro Interrumpido)");
        }
    }
}

// ========================================================
// 3. CLASE JUGAR: Tarea recurrente cada 400ms (Simula la Matriz)
// ========================================================
class Jugar implements Runnable {
    private final Random random = new Random();

    @Override
    public void run() {
        try {
            // Elige una fila y columna aleatoria del tablero 4x4
            int fila = random.nextInt(4);
            int col = random.nextInt(4);

            // Imprime la casilla encendida en la consola
            System.out.print("[Matriz 4x4] Encendiendo Casilla: C" + fila + col + " | ");
            Thread.sleep(200); // Se mantiene encendida un instante corto
            
        } catch (InterruptedException e) {
            // Hilo detenido limpiamente
        }
    }
}