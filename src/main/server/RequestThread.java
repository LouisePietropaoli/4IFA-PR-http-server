import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RequestThread extends Thread {
    static String STATUS_OK = "200 OK";
    static String STATUS_ERROR = "404 Not Found";
    private final Request httpRequest;
    private final Socket client;

    RequestThread(Request httpRequest, Socket client) {
        super();
        this.httpRequest = httpRequest;
        this.client = client;
    }

    @SneakyThrows
    public void start() {
        handleClient();
    }

    private void handleClient() throws IOException {
        System.out.println("Connection, sending data : " +  client.toString());

        // create reader to read client request from client's socket
        BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));

        // create request builder to send response
        StringBuilder requestBuilder = new StringBuilder();
        String line;
        while (!(line = br.readLine()).isBlank()) {
            requestBuilder.append(line + "\r\n");
        }

        try {
            Path filePath = buildResourceFilePath(httpRequest.getPath());
            if (Files.exists(filePath)) {
                httpRequest.setContentType(getContentType(filePath));
                httpRequest.setStatus(STATUS_OK);
                httpRequest.setContent(Files.readAllBytes(filePath));
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

    private void sendResponse(Socket client) throws IOException {


        switch (httpRequest.getMethod().toString()) {
            case "GET" -> sendGetResponse(client);
            case "POST" -> System.out.println("dsfsd method is used");
            case "PUT" -> System.out.println("Get methsdfdsfod is used");
            case "DELETE" -> System.out.println("Get mesdthod is used");
        }
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
        if(path.equals("/")) path = "index.html";
        return Paths.get("doc/", path);
    }
}
