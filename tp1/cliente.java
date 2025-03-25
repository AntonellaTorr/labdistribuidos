import java.net.*;
import java.io.*;
import java.util.Scanner;

public class cliente {
    public static void main(String[] args) {
        // Verificar que se pasen los parámetros necesarios
        if (args.length < 2) {
            System.out.println("Uso: java cliente <host> <puerto>");
            return;
        }

        String host = args[0];
        int puerto = Integer.parseInt(args[1]);

        try (Socket socket = new Socket(host, puerto);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             DataInputStream in = new DataInputStream(socket.getInputStream());
             Scanner scanner = new Scanner(System.in)) {

            // Solicitar al usuario ingresar signo zodiacal o fecha
            System.out.println("Ingrese uno de los siguientes:");
            System.out.print("Signo zodiacal (o deje vacio para fecha): ");
            String signo = scanner.nextLine().trim();

            System.out.print("Fecha (DD-MM-YYYY) (o deje vacio para signo): ");
            String fecha = scanner.nextLine().trim();

            // Comprobar qué parámetro se ha ingresado
            if (signo.isEmpty() && fecha.isEmpty()) {
                System.out.println("Debe ingresar al menos un signo o una fecha.");
                return;
            }

            // Enviar la consulta al servidor central (signo o fecha)
            if (!signo.isEmpty()) {
                out.writeUTF(signo);  // Enviar solo signo
            } else {
                out.writeUTF(fecha);  // Enviar solo fecha
            }

            // Recibir la respuesta consolidada de SC
            String respuesta = in.readUTF();
            System.out.println("Respuesta del servidor central:\n" + respuesta);

        } catch (IOException e) {
            System.out.println("Error en el cliente: " + e.getMessage());
        }
    }
}
