import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        // Verificar que se pasen los parámetros necesarios
        if (args.length < 2) {
            System.out.println("Uso: java Cliente <host> <puerto>");
            return;
        }

        String host = args[0];
        int puerto = Integer.parseInt(args[1]);

        try (Socket socket = new Socket(host, puerto);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             DataInputStream in = new DataInputStream(socket.getInputStream());
             Scanner scanner = new Scanner(System.in)) {

            // Solicitar al usuario ingresar signo zodiacal o fecha
            System.out.println("Ingrese uno o ambos:");
            System.out.print("Signo zodiacal (o deje vacío para solo fecha): ");
            String signo = scanner.nextLine().trim();

            System.out.print("Fecha (DD-MM-YYYY) (o deje vacío para solo signo): ");
            String fecha = scanner.nextLine().trim();

            // Validar que al menos uno se haya ingresado
            if (signo.isEmpty() && fecha.isEmpty()) {
                System.out.println("Debe ingresar al menos un signo o una fecha.");
                return;
            }

            // Construir la consulta según lo ingresado
            StringBuilder consulta = new StringBuilder();
            if (!signo.isEmpty()) {
                consulta.append(signo);
            }
            if (!fecha.isEmpty()) {
                if (consulta.length() > 0) {
                    consulta.append(" "); // Separador entre signo y fecha
                }
                consulta.append(fecha);
            }

            // Enviar la consulta al servidor
            out.writeUTF(consulta.toString());

            // Recibir la respuesta consolidada de SC
            String respuesta = in.readUTF();
            System.out.println("Respuesta del servidor:\n" + respuesta);

        } catch (IOException e) {
            System.out.println("Error en el cliente: " + e.getMessage());
        }
    }
}
