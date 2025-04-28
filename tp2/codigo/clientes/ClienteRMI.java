package clientes;
import interfaces.ServidorCentralService;

import java.rmi.Naming;
import java.util.Scanner;


public class ClienteRMI {
    public static void main(String[] args) {
        try {
            //conectar al servidor central
            ServidorCentralService sc = (ServidorCentralService) Naming.lookup("//localhost:1100/ServidorCentralService");

            Scanner scanner = new Scanner(System.in);
            System.out.println("Ingrese uno o ambos:");
            System.out.print("Signo zodiacal (o deje vacio): ");
            String signo = scanner.nextLine().trim();

            System.out.print("Fecha (DD-MM-YYYY) (o deje vacio): ");
            String fecha = scanner.nextLine().trim();

            if (signo.isEmpty() && fecha.isEmpty()) {
                System.out.println("Debe ingresar al menos un signo o una fecha.");
                return;
            }

            //enviar consulta
            String respuesta = sc.obtenerPrediccion(signo, fecha);
            System.out.println("\nRespuesta del servidor:\n" + respuesta);

        } catch (Exception e) {
            System.out.println("Error en el cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
