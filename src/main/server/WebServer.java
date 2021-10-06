///A Simple Web Server (WebServer.java)

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class WebServer {

    static int PORT_NUMBER = 3001;

    /**
     * WebServer constructor.
     */
    protected void start() {
        ServerSocket serverSocket;

        System.out.println("Webserver starting up on port " + PORT_NUMBER);
        System.out.println("(press ctrl-c to exit)");
        try {
            // create the main server socket
            serverSocket = new ServerSocket(PORT_NUMBER);
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return;
        }

        System.out.println("Waiting for connection...");
        for (;;) {
            try {
                // wait for a connection and create new socket for client
                Socket remoteClientSocket = serverSocket.accept();
                handleRequest(remoteClientSocket);
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }
    }

    private void handleRequest(Socket client) throws IOException {
        System.out.println("Connection, sending data : " +  client.toString());

        // create reader to read client request from client's socket
        BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));

        //PrintWriter out = new PrintWriter(remote.getOutputStream());

        // create request builder to send response
        StringBuilder requestBuilder = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            StringTokenizer parsedRequest = new StringTokenizer(line);
            if(!line.isBlank())
                requestBuilder.append(line + "\r\n");
        }
        Request httpRequest = parseRequest(client, requestBuilder);
        //create new thread
        new RequestThread(httpRequest, client);



    }

    private Request parseRequest(Socket client, StringBuilder requestBuilder) {
        Request httpRequest = new Request();
        String request = requestBuilder.toString();
        //split each request line
        String[] requestsLines = request.split("\r\n");
        //split first line which contains method, version, resource
        String[] requestLine = requestsLines[0].split(" ");

        httpRequest.setMethod(Request.Method.getMethodByIdentifier(requestLine[0]));
        httpRequest.setPath(requestLine[1]);
        httpRequest.setVersion(requestLine[2]);
        httpRequest.setHost(requestsLines[1].split(" ")[1]);

        ArrayList<String> headers = new ArrayList<>();
        //headers starts with 3rd line of the request
        for (int h = 2; h < requestsLines.length; h++) {
            String header = requestsLines[h];
            headers.add(header);
        }
        httpRequest.setHeaders(headers);

        String accessLog = String.format("Client %s, method %s, path %s, version %s, host %s, headers %s",
                client.toString(),
                httpRequest.getMethod(),
                httpRequest.getPath(),
                httpRequest.getVersion(),
                httpRequest.getHost(),
                httpRequest.getHeaders().toString());
        System.out.println(accessLog);

        return httpRequest;
    }

    /**
     * Start the application.
     *
     * @param args
     *            Command line parameters are not used.
     */
    public static void main(String args[]) {
        WebServer ws = new WebServer();
        ws.start();
    }
}
