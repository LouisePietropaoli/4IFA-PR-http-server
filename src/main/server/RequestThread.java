import lombok.SneakyThrows;

import java.io.*;
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
    }

    private void sendResponse(Socket client) throws IOException {


        switch (httpRequest.getMethod().toString()) {
            case "GET" -> sendGetResponse(client);
            case "POST" -> sendPostResponse();
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

    protected void sendPostResponse() throws IOException {
        String fileName = "doc/persons.txt";

        String name = (String) httpRequest.getBody().get("name");
        String age = (String) httpRequest.getBody().get("age");

        // Calling the above method
        appendStrToFile(fileName, "\n" + name + ": " + age + " years old");

        OutputStream clientOutput = client.getOutputStream();
        clientOutput.write(("HTTP/1.1 \r\n" + STATUS_OK).getBytes());
        clientOutput.write(("ContentType: text/html" + "\r\n").getBytes());
        clientOutput.write("\r\n".getBytes());
        clientOutput.write(("<h1>User <b>" + name + "</b> was added ! </h1>").getBytes());
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

    // Method 1
    // TO append string into a file
    public static void appendStrToFile(String fileName,
                                       String str)
    {
        // Try block to check for exceptions
        try {

            // Open given file in append mode by creating an
            // object of BufferedWriter class
            BufferedWriter out = new BufferedWriter(
                    new FileWriter(fileName, true));

            // Writing on output stream
            out.write(str);
            // Closing the connection
            out.close();
        }

        // Catch block to handle the exceptions
        catch (IOException e) {

            // Display message when exception occurs
            System.out.println("exception occoured" + e);
        }
    }
}
