import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class sp {
    private static final int PUERTO = 6791;
    private static final String[] pronosticos = {
         "Soleado con 30 grados.",
         "Lluvias dispersas con 18 grados.",
         "Nublado con 22 grados.",
         "Soleado con 25 grados.",
         "Tormentas con 20 grados.",
         "Lluvias ligeras con 21 grados.",
         "Parcialmente nublado con 24 grados.",
         "Soleado con 27 grados.",
         "Nublado con posibilidad de lluvias, 19 grados."
    };

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


                String dia = in.readUTF().trim().toLowerCase().substring(0, 2);
                int index = Integer.parseInt(dia) % pronosticos.length;

                // Buscar el pronóstico de clima según la fecha
                String respuesta = pronosticos[index];//pronosticos.getOrDefault(fecha, "Fecha no encontrada.");
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(2000, 5000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
