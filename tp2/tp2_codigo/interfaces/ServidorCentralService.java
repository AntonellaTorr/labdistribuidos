package interfaces;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServidorCentralService extends Remote {
    String obtenerPrediccion(String signo, String fecha) throws RemoteException;
}
