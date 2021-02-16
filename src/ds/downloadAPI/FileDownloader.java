package ds.downloadAPI;

import ds.credential.Credential;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileDownloader {

    public void downloadFile(String fileName, Credential toCred) throws IOException {
        URL obj = new URL(toCred.getIp() + ":" + toCred.getIp() + "?filename=" + fileName);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);
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
            System.out.println("The hash recieved is - " + hashStr.toString());
        } else {
            System.out.println("GET request not worked");
        }
    }
}
