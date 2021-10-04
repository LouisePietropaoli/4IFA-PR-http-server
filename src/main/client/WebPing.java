import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

public class WebPing {
    static int PORT_NUMBER = 3000;
    static String HOST = "localhost";
    public static void main(String[] args) {

//        if (args.length != 2) {
//            System.err.println("Usage java WebPing <server host name> <server port number>");
//            return;
//        }

//        String httpServerHost = args[0];
//        int httpServerPort = Integer.parseInt(args[1]);
//        httpServerHost = args[0];
//        httpServerPort = Integer.parseInt(args[1]);

        Socket sock = null;
        BufferedReader socIn = null;

        try {
            InetAddress addr;
            sock = new Socket(HOST, PORT_NUMBER);
            socIn = new BufferedReader(
                    new InputStreamReader(sock.getInputStream()));
            addr = sock.getInetAddress();
            System.out.println("Connected to " + addr);
            sock.close();
        } catch (java.io.IOException e) {
            System.out.println("Can't connect to " + HOST + ":" + PORT_NUMBER);
            System.out.println(e);
        }
    }
}
