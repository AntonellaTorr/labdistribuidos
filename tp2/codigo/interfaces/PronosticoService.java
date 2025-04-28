package interfaces;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PronosticoService extends Remote {
    String obtenerPronostico(String fecha) throws RemoteException;
}
