///A Simple Web Server (WebServer.java)

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class WebServer {
    static int PORT_NUMBER = 3000;
    static String HOST = "localhost";

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
                handleClient(remoteClientSocket);
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }
    }

    private static void handleClient(Socket client) throws IOException {
        System.out.println("Connection, sending data : " +  client.toString());

        // create reader to read client request from client's socket
        BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));

        //PrintWriter out = new PrintWriter(remote.getOutputStream());

        // create request builder to send response
        StringBuilder requestBuilder = new StringBuilder();
        String line;
        while (!(line = br.readLine()).isBlank()) {
            requestBuilder.append(line + "\r\n");
        }

        parseRequest(client, requestBuilder);
        try {
            sendResponse(client, requestBuilder);
        } catch (IOException e)
        {
            System.err.println("SendResponse: " + e);
        }
        try {
            sendResponse(client, requestBuilder);
        } catch (IOException e)
        {
            System.err.println("SendResponse: " + e);
        }

        String request = requestBuilder.toString();
        System.out.println(request);
    }

    private static void parseRequest(Socket client, StringBuilder requestBuilder) {
        String request = requestBuilder.toString();
        String[] requestsLines = request.split("\r\n");
        String[] requestLine = requestsLines[0].split(" ");
        String method = requestLine[0];
        String path = requestLine[1];
        String version = requestLine[2];
        String host = requestsLines[1].split(" ")[1];

        List<String> headers = new ArrayList<>();
        //headers starts with 3rd line of the request
        for (int h = 2; h < requestsLines.length; h++) {
            String header = requestsLines[h];
            headers.add(header);
        }

        String accessLog = String.format("Client %s, method %s, path %s, version %s, host %s, headers %s",
                client.toString(), method, path, version, host, headers.toString());
        System.out.println(accessLog);
    }

    private static void sendResponse(Socket client, StringBuilder requestBuilder) throws IOException {
        OutputStream clientOutput = client.getOutputStream();
        clientOutput.write("HTTP/1.1 200 OK\r\n".getBytes());
        clientOutput.write(("ContentType: text/html\r\n").getBytes());
        clientOutput.write("\r\n".getBytes());
        clientOutput.write("<b>It works!</b>".getBytes());
        clientOutput.write("\r\n\r\n".getBytes());
        clientOutput.flush();
        client.close();
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
