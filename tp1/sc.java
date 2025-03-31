import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

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
                System.out.println("Consulta recibida: " + consulta);
        
                String respuestaClima = null;
                String respuestaHoroscopo = null;
        
                // Dividir la consulta en palabras
                String[] partes = consulta.split("\\s+");
        
                for (String parte : partes) {
                    if (esFecha(parte)) {
                        synchronized (cacheClima) {
                            respuestaClima = cacheClima.computeIfAbsent(parte, fecha -> consultarServidor("localhost", 6791, fecha));
                        }
                    } else {
                        synchronized (cacheHoroscopo) {
                            respuestaHoroscopo = cacheHoroscopo.computeIfAbsent(parte.toLowerCase(), signo -> consultarServidor("localhost", 6790, signo));
                        }
                    }
                }
        
                // Construir la respuesta según los datos obtenidos
                StringBuilder respuesta = new StringBuilder();
                if (respuestaClima != null) {
                    respuesta.append("Clima: ").append(respuestaClima).append("\n");
                }
                if (respuestaHoroscopo != null) {
                    respuesta.append("Horóscopo: ").append(respuestaHoroscopo);
                }
        
                // Enviar la respuesta al cliente
                out.writeUTF(respuesta.toString().trim());
        
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
