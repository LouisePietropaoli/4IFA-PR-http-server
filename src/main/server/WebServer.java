///A Simple Web Server (WebServer.java)

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class WebServer {
    static int PORT_NUMBER = 3001;
    static String HOST = "localhost";
    static String STATUS_OK = "200 OK";
    static String STATUS_ERROR = "404 Not Found";
    String method;
    String path;
    String version;
    String host;
    List<String> headers;

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

    private void handleClient(Socket client) throws IOException {
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

        try {
            parseRequest(client, requestBuilder);
            Path filePath = buildResourceFilePath(path);
            if (Files.exists(filePath)) {
                String contentType = getContentType(filePath);
                sendResponse(client, STATUS_OK, contentType, Files.readAllBytes(filePath));
            } else {
                byte[] notFoundContent = "<h1> Not found :-( </h1>".getBytes();
                sendResponse(client, STATUS_ERROR, "text/html", notFoundContent);

            }
        } catch (IOException e)
        {
            System.err.println("Error: " + e);
        } finally {
            client.close();
        }

        String request = requestBuilder.toString();
        System.out.println(request);

    }

    private void parseRequest(Socket client, StringBuilder requestBuilder) {
        String request = requestBuilder.toString();
        String[] requestsLines = request.split("\r\n");
        String[] requestLine = requestsLines[0].split(" ");
        method = requestLine[0];
        path = requestLine[1];
        version = requestLine[2];
        host = requestsLines[1].split(" ")[1];

        headers = new ArrayList<>();
        //headers starts with 3rd line of the request
        for (int h = 2; h < requestsLines.length; h++) {
            String header = requestsLines[h];
            headers.add(header);
        }

        String accessLog = String.format("Client %s, method %s, path %s, version %s, host %s, headers %s",
                client.toString(), method, path, version, host, headers.toString());
        System.out.println(accessLog);
    }

    private void sendResponse(Socket client, String status, String contentType, byte[] content) throws IOException {
        OutputStream clientOutput = client.getOutputStream();
        clientOutput.write(("HTTP/1.1 \r\n" + status).getBytes());
        clientOutput.write(("ContentType: " + contentType + "\r\n").getBytes());
        clientOutput.write("\r\n".getBytes());
        clientOutput.write(content);
        clientOutput.write("\r\n\r\n".getBytes());
        clientOutput.flush();
        client.close();
    }

    private String getContentType(Path filePath) throws IOException {
        return Files.probeContentType(filePath);
    }

    private Path buildResourceFilePath(String path) {
        System.out.println("PATH===============" + path);
        if(path.equals("/")) path = "index.html";
        return Paths.get("doc/", path);
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
