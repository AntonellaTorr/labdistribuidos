
package servidores;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import interfaces.*;

public class ServidorCentralRMI extends UnicastRemoteObject implements ServidorCentralService {

    private final Map<String, String> cacheHoroscopo = new HashMap<>();
    private final Map<String, String> cacheClima = new HashMap<>();

    private String hostHoroscopo;
    private int puertoHoroscopo;
    private String servicioHoroscopo;

    private String hostClima;
    private int puertoClima;
    private String servicioClima;

    protected ServidorCentralRMI() throws RemoteException {
        super();
        cargarConfiguracion();
    }

    private void cargarConfiguracion() {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("servidores/config.properties")) {
            props.load(fis);
            hostHoroscopo = props.getProperty("hostHoroscopo", "localhost");
            puertoHoroscopo = Integer.parseInt(props.getProperty("puertoHoroscopo", "1098"));
            servicioHoroscopo = props.getProperty("servicioHoroscopo", "HoroscopoService");

            hostClima = props.getProperty("hostClima", "localhost");
            puertoClima = Integer.parseInt(props.getProperty("puertoClima", "1099"));
            servicioClima = props.getProperty("servicioClima", "PronosticoService");

        } catch (IOException e) {
            System.out.println("No se pudo leer config.properties, usando valores por defecto.");
            System.out.println("Buscando archivo en: " + new File("config.properties").getAbsolutePath());
            hostHoroscopo = "localhost";
            puertoHoroscopo = 1098;
            servicioHoroscopo = "HoroscopoService";

            hostClima = "localhost";
            puertoClima = 1099;
            servicioClima = "PronosticoService";
        }
    }

    @Override
    public synchronized String obtenerPrediccion(String signo, String fecha) throws RemoteException {
        String respuestaHoroscopo = "";
        String respuestaClima = "";

        try {
            if (signo != null && !signo.isEmpty()) {
                if (!cacheHoroscopo.containsKey(signo.toLowerCase())) {
                    HoroscopoService hs = (HoroscopoService) java.rmi.Naming.lookup(
                        "//" + hostHoroscopo + ":" + puertoHoroscopo + "/" + servicioHoroscopo);
                    String pred = hs.obtenerHoroscopo(signo);
                    cacheHoroscopo.put(signo.toLowerCase(), pred);
                    respuestaHoroscopo = pred;
                } else {
                    respuestaHoroscopo = cacheHoroscopo.get(signo.toLowerCase()) + " (Respuesta cacheada)";
                }
            }

            if (fecha != null && !fecha.isEmpty()) {
                if (!cacheClima.containsKey(fecha)) {
                    PronosticoService ps = (PronosticoService) java.rmi.Naming.lookup(
                        "//" + hostClima + ":" + puertoClima + "/" + servicioClima);
                    String pred = ps.obtenerPronostico(fecha);
                    cacheClima.put(fecha, pred);
                    respuestaClima = pred;
                } else {
                    respuestaClima = cacheClima.get(fecha) + " (Respuesta cacheada)";
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error al consultar servidores remotos.";
        }

        return (signo != null ? "Horóscopo: " + respuestaHoroscopo + "\n" : "") +
               (fecha != null ? "Clima: " + respuestaClima : "");
    }

    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1100);
            ServidorCentralRMI obj = new ServidorCentralRMI();
            java.rmi.Naming.rebind("//localhost:1100/ServidorCentralService", obj);
            System.out.println("Servidor Central RMI listo.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
