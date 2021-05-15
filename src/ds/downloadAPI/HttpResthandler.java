package ds.downloadAPI;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import ds.node.Node;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

public class HttpResthandler implements HttpHandler {
    

    public void up(int port, String host) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(host,port), 0);
        server.createContext("/download", this);
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    @Override
    public void handle(HttpExchange t) throws IOException {
        String prop = queryToMap(t.getRequestURI().getQuery());
        //Node.logMessage("Attr is - " + prop);
        String response = getResponseContent();
        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private String queryToMap(String query) {
        String[] entry = query.split("=");
        return entry[1];
    }

    private String getResponseContent() {
        int randInt = new Random().nextInt(8) + 2;
        char[] s = new char[1024 * 1024 * randInt];
        String str = String.copyValueOf(s);
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] hash = digest.digest(str.getBytes(StandardCharsets.UTF_8));
        BigInteger noHash = new BigInteger(1, hash);
        String hashStr = noHash.toString(16);
        Node.logMessage("\nThe hash sent is - " + hashStr.toString(), "ANSI_GREEN");
        return str;
    }

}
