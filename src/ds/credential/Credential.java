package ds.credential;

public class Credential {
    private String ip;
    private int port;
    private String username;

    public Credential(String ip, int port, String username) {
        this.ip = ip;
        this.port = port;
        this.username = username;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object compare){
        return this.ip.equals(((Credential) compare).getIp()) && this.port == ((Credential) compare).getPort();
    }

    @Override
    public int hashCode(){
        return (this.ip + new Integer(this.port).toString()).hashCode();
    }
}
