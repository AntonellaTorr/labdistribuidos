import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class sc {
    private static final int PUERTO = 6789;
    private static final Map<String, String> cacheHoroscopo = new HashMap<>();
    private static final Map<String, String> cacheClima = new HashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            System.out.println("Servidor Central (SC) esperando conexiones en el puerto " + PUERTO + "...");

            while (true) {
                Socket socket = serverSocket.accept();
                new ClienteHandler(socket).start();
            }
        } catch (IOException e) {
            System.out.println("Error en el Servidor Central: " + e.getMessage());
        }
    }

    private static class ClienteHandler extends Thread {
        private Socket socket;

        public ClienteHandler(Socket socket) {
            this.socket = socket;
        }
        @Override
        public void run() {
            try (DataInputStream in = new DataInputStream(socket.getInputStream());
                 DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
        
                // Leer la consulta del cliente
                String consulta = in.readUTF().trim();
                System.out.println("Sirviendo consulta "+consulta);
                String respuesta;
                boolean[] deCache = {true};
                if (esFecha(consulta)) {
                    // Si es una fecha, consultar a SP
                    synchronized (cacheClima) {
                        respuesta = cacheClima.computeIfAbsent(consulta, fecha -> {deCache[0]=false;return consultarServidor("localhost", 6791, fecha);});
                    }
                } else {
                    
                    // Si no es una fecha, consultar a SH (se asume que es un signo zodiacal)
                    synchronized (cacheHoroscopo) {
                        respuesta = cacheHoroscopo.computeIfAbsent(consulta.toLowerCase(), signo -> {deCache[0]=false;return consultarServidor("localhost", 6790, signo);});
                    }
                }
                if (deCache[0]){
                    respuesta += "- de cache";
                }
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(5000, 10000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Enviar la respuesta al cliente
                out.writeUTF(respuesta);
                System.out.println("Fin de consulta "+consulta);

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
        
        private boolean esFecha(String consulta) {
            return consulta.matches("\\d{2}-\\d{2}-\\d{4}"); // Verifica formato DD-MM-YYYY
        }
        

        private String consultarServidor(String host, int puerto, String consulta) {
            try (Socket socket = new Socket(host, puerto);
                 DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                 DataInputStream in = new DataInputStream(socket.getInputStream())) {

                out.writeUTF(consulta);
                return in.readUTF();
            } catch (IOException e) {
                return "Error al conectar con el servidor en puerto " + puerto;
            }
        }
    }
}
