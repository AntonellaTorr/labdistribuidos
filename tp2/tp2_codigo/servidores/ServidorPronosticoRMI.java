package servidores;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.concurrent.ThreadLocalRandom;

import interfaces.PronosticoService;

public class ServidorPronosticoRMI extends UnicastRemoteObject implements PronosticoService {

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

    protected ServidorPronosticoRMI() throws RemoteException {
        super();
    }

    @Override
    public String obtenerPronostico(String fecha) throws RemoteException {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(2000, 5000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int index = Integer.parseInt(fecha.substring(0, 2)) % pronosticos.length;
        return "El clima para la fecha ingresada será: " + pronosticos[index];
    }

    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);
            ServidorPronosticoRMI obj = new ServidorPronosticoRMI();
            java.rmi.Naming.rebind("//localhost:1099/PronosticoService", obj);
            System.out.println("Servidor de Clima RMI listo.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
