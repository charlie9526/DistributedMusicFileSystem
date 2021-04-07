package ds.downloadAPI;

import ds.credential.Credential;
import ds.node.Node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

public class FileDownloader {

    public void downloadFile(String fileName, Credential toCred) throws IOException {
        
        
        URL obj = new URL(toCred.getIp() + ":" + toCred.getIp() + "?filename=" + fileName);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        Node.logMessage("GET Response Code :: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            MessageDigest digest = null;
            try {
                digest = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            byte[] hash = digest.digest(response.toString().getBytes(StandardCharsets.UTF_8));
            BigInteger noHash = new BigInteger(1, hash);
            String hashStr = noHash.toString(16);
            Node.logMessage("The hash recieved is - " + hashStr.toString());
        } else {
            Node.logMessage("GET request not worked");
        }
    }
}
