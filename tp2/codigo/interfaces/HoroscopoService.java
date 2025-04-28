package interfaces;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface HoroscopoService extends Remote {
    String obtenerHoroscopo(String signo) throws RemoteException;
}
