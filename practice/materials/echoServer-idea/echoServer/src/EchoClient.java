
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.net.InetAddress;
import java.net.Socket;
import java.io.IOException;
import java.net.UnknownHostException;

public class EchoClient {

	public static void main(String[] args) {
		
		try {
			
			String ha = InetAddress.getLocalHost().getHostAddress();
			
			if (args.length > 0) ha = args[0];
			
			System.out.println("[client] trying to connect at " + ha + ":1234 ...");

			// create, connect (host:port);
			Socket s = new Socket(ha, 1234);
			
			OutputStream os = s.getOutputStream();
			InputStream is = s.getInputStream();
			
			PrintWriter out = new PrintWriter(new OutputStreamWriter(os));
			BufferedReader in = new BufferedReader(new InputStreamReader(is));
			
			String hello_msg = in.readLine();
			if (hello_msg != null) {
				System.out.println("[client] server said: " + hello_msg);
			}

			String serv_msg;
			String our_msg;

			our_msg = "one...";
			System.out.println("[client] talking to server: " + our_msg);
			out.println(our_msg);
			out.flush();
			serv_msg = in.readLine();
			System.out.println("[client] server said: " + serv_msg);
			
			our_msg = "two...";
			System.out.println("[client] talking to server: " + our_msg);
			out.println(our_msg);
			out.flush();
			serv_msg = in.readLine();
			System.out.println("[client] server said: " + serv_msg);
			
			our_msg = "three...";
			System.out.println("[client] talking to server: " + our_msg);
			out.println(our_msg);
			out.flush();
			serv_msg = in.readLine();
			System.out.println("[client] server said: " + serv_msg);
			
			our_msg = "bye";
			System.out.println("[client] talking to server: " + our_msg);
			out.println(our_msg);
			out.flush();
			serv_msg = in.readLine();
			System.out.println("[client] server said: " + serv_msg);
			
			s.close();
			
		} catch (UnknownHostException e) {
			
			e.printStackTrace();
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}

	}

}
