import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ArithIface extends Remote {

    int[] add(int[] a, int[]b) throws RemoteException;

}
