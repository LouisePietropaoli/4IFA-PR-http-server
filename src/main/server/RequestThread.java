import lombok.SneakyThrows;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        try {
            System.out.println("Connection, sending data : " + client.toString());
            switch (httpRequest.getMethod().toString()) {
                case "GET" -> sendGetResponse();
                case "POST" -> sendPostResponse();
                case "PUT" -> sendPutResponse();
                case "DELETE" -> sendDeleteResponse();
            }
        } catch (Exception e)
        {
            System.err.println("Error: " + e);
        } finally {
            client.close();
        }
    }

    /**
     * Get a resource on the server
     */
    private void sendGetResponse() throws IOException {
        Path filePath = buildResourceFilePath(httpRequest.getPath());
        OutputStream clientOutput = client.getOutputStream();

        if (Files.exists(filePath)) {
            clientOutput.write(("HTTP/1.1 \r\n" + STATUS_OK).getBytes());
            clientOutput.write(("ContentType: " + httpRequest.getContentType() + "\r\n").getBytes());
            clientOutput.write("\r\n".getBytes());
            clientOutput.write(Files.readAllBytes(filePath));
            clientOutput.write("\r\n\r\n".getBytes());
            clientOutput.flush();
        } else {
            clientOutput.write(("HTTP/1.1 \r\n" + STATUS_ERROR).getBytes());
            clientOutput.write(("ContentType: text/html\r\n").getBytes());
            clientOutput.write("\r\n".getBytes());
            clientOutput.write("<h1> Not found :-( </h1>".getBytes());
            clientOutput.write("\r\n\r\n".getBytes());
            clientOutput.flush();
        }

    }

    /**
     * either save a new person in file "persons.txt"
     * or save a binary file sent
     * @throws IOException
     */
    protected void sendPostResponse() throws IOException {
        String fileName = "doc/File-" + new SimpleDateFormat("ddMMyy-hhmmss.SSS").format( new Date() ) + getFileExtension();
        File outputFile = new File(fileName);
        OutputStream outputStream = new FileOutputStream(outputFile);
        outputStream.write(httpRequest.getBody().getBytes());

        OutputStream clientOutput = client.getOutputStream();
        clientOutput.write(("HTTP/1.1 \r\n" + STATUS_OK).getBytes());
        clientOutput.write(("ContentType: text/html" + "\r\n").getBytes());
        clientOutput.write("\r\n".getBytes());
        clientOutput.write(("<h1>File was saved on the server with name :  <b>" + fileName + "</b></h1>").getBytes());
        clientOutput.write("\r\n\r\n".getBytes());
        clientOutput.flush();

    }

    /**
     * update file
     * @throws IOException
     */
    protected void sendPutResponse() throws IOException {

        String fileName = "doc" + httpRequest.getPath();
        File outputFile = new File(fileName);
        OutputStream clientOutput = client.getOutputStream();

        if (outputFile.exists()) {
                OutputStream outputStream = new FileOutputStream(outputFile);
                outputStream.write(httpRequest.getBody().getBytes());

                clientOutput.write(("HTTP/1.1 \r\n" + STATUS_OK).getBytes());
                clientOutput.write(("ContentType: text/html" + "\r\n").getBytes());
                clientOutput.write("\r\n".getBytes());
                clientOutput.write(("<h1>File with name <b>" + fileName + "</b> was updated on the server.</h1>").getBytes());
                clientOutput.write("\r\n\r\n".getBytes());
                clientOutput.flush();
        } else {
            clientOutput.write(("HTTP/1.1 \r\n" + STATUS_ERROR).getBytes());
            clientOutput.write(("ContentType: text/html\r\n").getBytes());
            clientOutput.write("\r\n".getBytes());
            clientOutput.write("<h1> File was not found :-( </h1>".getBytes());
        }
    }

    /**
     * Delete - if exists - a resource on the server
     */
    private void sendDeleteResponse() throws IOException {
        Path fileName = buildResourceFilePath(httpRequest.getPath());
        File filePath = new File(fileName.toString());

        OutputStream clientOutput = client.getOutputStream();

        if(filePath.delete()) {
            clientOutput.write(("HTTP/1.1 \r\n" + STATUS_OK).getBytes());
            clientOutput.write(("ContentType: " + httpRequest.getContentType() + "\r\n").getBytes());
            clientOutput.write("\r\n".getBytes());
            clientOutput.write(("<h1>File with name :  <b>" + fileName + "</b>was deleted on the server.</h1>").getBytes());
        }
        else {
            clientOutput.write(("HTTP/1.1 \r\n" + STATUS_ERROR).getBytes());
            clientOutput.write(("ContentType: text/html\r\n").getBytes());
            clientOutput.write("\r\n".getBytes());
            clientOutput.write("<h1> File was not found :-( </h1>".getBytes());
        }
        clientOutput.write("\r\n\r\n".getBytes());
        clientOutput.flush();
    }

    private String getFileExtension() {
        String extension;
        switch (httpRequest.getContentType()) {
            case "application/json" -> extension = ".json";
            case "text/html" -> extension = ".html";
            case "text/plain" -> extension = ".txt";
            case "image/png" -> extension = ".png";
            case "image/jpg" -> extension = ".jpg";
            case "image/jpeg" -> extension = ".jpeg";
            case "image/gif" -> extension = ".gif";
            default -> extension =  "";
        }

        return extension;
    }

    private Path buildResourceFilePath(String path) {
        if(path.equals("/")) path = "index.html";
        return Paths.get("doc/", path);
    }
}
