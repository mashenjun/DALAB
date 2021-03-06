package Process;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Data
@AllArgsConstructor
public class Address implements Serializable {

    public static final String DEFAULT_PROTOCOL = "rmi";

    private final String ip;

    private final int port;

    private final String protocol;


    public Address(String ip,int port) {
        this.ip = ip;
        this.port = port;
        this.protocol = DEFAULT_PROTOCOL;

    }

    public static Address getMyAddress(int port) {
        try {
            return new Address(InetAddress.getLocalHost().getHostAddress(), port);
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String toString() {
        return protocol + "://" + ip + ":" + port;
    }

}
