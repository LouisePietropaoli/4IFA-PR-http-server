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
    Request httpRequest;

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
                httpRequest = new Request();
                handleClient(remoteClientSocket);
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }
    }

    synchronized private void handleClient(Socket client) throws IOException {
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
            Path filePath = buildResourceFilePath(httpRequest.getPath());
            if (Files.exists(filePath)) {
                httpRequest.setContentType(getContentType(filePath));
                httpRequest.setStatus(STATUS_OK);
                httpRequest.setContent(Files.readAllBytes(filePath));
                //sendResponse(client, httpRequest.getMethod().getMethodByIdentifier(method), STATUS_OK, contentType, Files.readAllBytes(filePath));
                sendResponse(client);
            } else {
                byte[] notFoundContent = "<h1> Not found :-( </h1>".getBytes();
                httpRequest.setContent(notFoundContent);
                httpRequest.setContentType("text/html");
                httpRequest.setStatus(STATUS_ERROR);
                sendResponse(client);

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
    }

    private void sendResponse(Socket client) throws IOException {


        switch (httpRequest.getMethod().toString()) {
            case "GET" -> sendGetResponse(client);
            case "POST" -> System.out.println("dsfsd method is used");
            case "PUT" -> System.out.println("Get methsdfdsfod is used");
            case "DELETE" -> System.out.println("Get mesdthod is used");
        }

        client.close();
    }

    private void sendGetResponse(Socket client) throws IOException {
        OutputStream clientOutput = client.getOutputStream();
        clientOutput.write(("HTTP/1.1 \r\n" + httpRequest.getStatus()).getBytes());
        clientOutput.write(("ContentType: " + httpRequest.getContentType() + "\r\n").getBytes());
        clientOutput.write("\r\n".getBytes());
        clientOutput.write(httpRequest.getContent());
        clientOutput.write("\r\n\r\n".getBytes());
        clientOutput.flush();
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
