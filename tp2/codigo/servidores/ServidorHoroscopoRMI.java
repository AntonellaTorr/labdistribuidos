package servidores;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import interfaces.HoroscopoService;

public class ServidorHoroscopoRMI extends UnicastRemoteObject implements HoroscopoService {

    private final Map<String, String> horoscopos = new HashMap<>();

    protected ServidorHoroscopoRMI() throws RemoteException {
        super();
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

    @Override
    public String obtenerHoroscopo(String signo) throws RemoteException {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(2000, 5000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Prediccion para " + signo + ": " + horoscopos.getOrDefault(signo.toLowerCase(), "Signo no encontrado.");
    }

    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1098);
            ServidorHoroscopoRMI obj = new ServidorHoroscopoRMI();
            java.rmi.Naming.rebind("//localhost:1098/HoroscopoService", obj);
            System.out.println("Servidor de Horóscopos RMI listo.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
