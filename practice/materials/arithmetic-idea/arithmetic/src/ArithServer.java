
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ArithServer extends UnicastRemoteObject implements ArithIface {

    private String name;
    public ArithServer(String name) throws RemoteException {

        super();
        this.name = name;

    }

    public int[] add(int[] a, int[] b) throws RemoteException {

        int c[] = new int[10];

        System.out.println("[info] client call...");
        for (int i=0; i < 10; i++) { c[i] = a[i] + b[i]; }

        return c;

    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static void main (String[] args) {

        // NOTE: RMI will not start without this... Security is always major milestone, especially in Java :)
        System.setProperty("java.security.policy", "src/security.policy");
        System.setSecurityManager(new SecurityManager());

        try {

            // NOTE: I'm trying to find my "local" IP address...
            String lha = InetAddress.getLocalHost().getHostAddress().toString();
            System.out.println("[info] local ip address found: " + lha);

            // NOTE: Instantiating our remote object. It even has a name :)
            ArithServer obj = new ArithServer("ArithServer");

            // NOTE: Trying to show our remote object to the rest of the world ...
            Naming.rebind("//" + lha + "/ArithServer", obj);

            System.out.println("[info] " + obj.getName() + " bound in registry @ " + lha + ":1099");
            System.out.println("[info] public address for our server is: //" + lha + ":1099/" );

        } catch (Exception e) {

            System.out.println("[halt] ArithServer err: " + e.getMessage());
            // e.printStackTrace();

        }

    }

}
