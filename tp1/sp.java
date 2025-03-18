import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class sp {
    private static final int PUERTO = 6791;
    private static final Map<String, String> pronosticos = new HashMap<>();

    static {
        pronosticos.put("01-03-2025", "Soleado con 30 grados.");
        pronosticos.put("02-03-2025", "Lluvias dispersas con 18 grados.");
        pronosticos.put("03-03-2025", "Nublado con 22 grados.");
        pronosticos.put("04-03-2025", "Soleado con 25 grados.");
        pronosticos.put("05-03-2025", "Tormentas con 20 grados.");
        pronosticos.put("06-03-2025", "Soleado con 28 grados.");
        pronosticos.put("07-03-2025", "Lluvias ligeras con 21 grados.");
        pronosticos.put("08-03-2025", "Parcialmente nublado con 24 grados.");
        pronosticos.put("09-03-2025", "Soleado con 27 grados.");
        pronosticos.put("10-03-2025", "Nublado con posibilidad de lluvias, 19 grados.");
    }

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            System.out.println("Servidor de Clima (SP) esperando conexiones en el puerto " + PUERTO + "...");

            while (true) {
                Socket socket = serverSocket.accept();
                new ClienteHandler(socket).start(); // Maneja cada cliente en un hilo separado
            }
        } catch (IOException e) {
            System.out.println("Error en el Servidor de Clima: " + e.getMessage());
        }
    }

    private static class ClienteHandler extends Thread {
        private final Socket socket;

        public ClienteHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (DataInputStream in = new DataInputStream(socket.getInputStream());
                 DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

                String fecha = in.readUTF().trim();

                // Buscar el pronóstico de clima según la fecha
                String respuesta = pronosticos.getOrDefault(fecha, "Fecha no encontrada.");
                out.writeUTF("El clima para la fecha ingresada sera: "+respuesta);
            } catch (IOException e) {
                System.out.println("Error en la comunicación con el cliente: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Error al cerrar el socket: " + e.getMessage());
                }
            }
        }
    }
}
