import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class sh {
    private static final int PUERTO = 6790;
    private static final Map<String, String> horoscopos = new HashMap<>();

    static {
        horoscopos.put("aries", "Hoy es un buen dia para iniciar nuevos proyectos.");
        horoscopos.put("tauro", "La paciencia te llevara al exito.");
        horoscopos.put("geminis", "Comunicarte bien sera clave en el dia de hoy.");
        horoscopos.put("cancer", "Confia en tu intuicion para tomar decisiones.");
        horoscopos.put("leo", "Hoy brillaras con luz propia.");
        horoscopos.put("virgo", "La organizacion te traera buenos resultados.");
        horoscopos.put("libra", "Encuentra el equilibrio entre trabajo y descanso.");
        horoscopos.put("escorpio", "Hoy puedes descubrir algo importante.");
        horoscopos.put("sagitario", "Un viaje inesperado podria surgir.");
        horoscopos.put("capricornio", "La disciplina sera clave en tu exito.");
        horoscopos.put("acuario", "Nuevas ideas revolucionaran tu dia.");
        horoscopos.put("piscis", "Tu creatividad estara en su punto maximo.");
    }

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            System.out.println("Servidor de Horoscopos (SH) esperando conexiones en el puerto " + PUERTO + "...");

            while (true) {
                Socket socket = serverSocket.accept();
                new ClienteHandler(socket).start(); // Maneja cada cliente en un hilo separado
            }
        } catch (IOException e) {
            System.out.println("Error en el Servidor de Horoscopos: " + e.getMessage());
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

                String signo = in.readUTF().trim().toLowerCase();

                // Respuesta para el signo solicitado
                System.out.println("SIGNO INGRESADO" +signo);
                String respuesta = horoscopos.getOrDefault(signo, "Signo no encontrado.");
                out.writeUTF("Prediccion para " + signo + ": " + respuesta);
            } catch (IOException e) {
                System.out.println("Error en la comunicacion con el cliente: " + e.getMessage());
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
