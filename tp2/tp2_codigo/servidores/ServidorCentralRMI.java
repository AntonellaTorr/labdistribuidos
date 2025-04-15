package servidores;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;
import java.util.Map;

import interfaces.*;

public class ServidorCentralRMI extends UnicastRemoteObject implements ServidorCentralService {

    private final Map<String, String> cacheHoroscopo = new HashMap<>();
    private final Map<String, String> cacheClima = new HashMap<>();

    protected ServidorCentralRMI() throws RemoteException {
        super();
    }

    @Override
    public synchronized String obtenerPrediccion(String signo, String fecha) throws RemoteException {
        String respuestaHoroscopo = "";
        String respuestaClima = "";

        try {
            if (signo != null && !signo.isEmpty()) {
                if (!cacheHoroscopo.containsKey(signo.toLowerCase())) {
                    HoroscopoService hs = (HoroscopoService) java.rmi.Naming.lookup("//localhost:1098/HoroscopoService");
                    String pred = hs.obtenerHoroscopo(signo);
                    cacheHoroscopo.put(signo.toLowerCase(), pred);
                    respuestaHoroscopo = pred;
                } else {
                    respuestaHoroscopo = cacheHoroscopo.get(signo.toLowerCase()) + " (Respuesta cacheada)";
                }
            }

            if (fecha != null && !fecha.isEmpty()) {
                if (!cacheClima.containsKey(fecha)) {
                    PronosticoService ps = (PronosticoService) java.rmi.Naming.lookup("//localhost:1099/PronosticoService");
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
