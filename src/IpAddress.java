import java.util.Arrays;
import java.util.stream.IntStream;

public class IpAddress {
    public static final IpAddress LOCALHOST = new IpAddress();
    public static final IpAddress MODEM = new IpAddress(10, 0, 0, 38);
    private int ip;

    public IpAddress() {
        set(new int[]{127, 0, 0, 1});
    }

    private IpAddress(int ip) {
        set(ip);
    }

    public IpAddress(int o1, int o2, int o3, int o4) {
        set(o1, o2, o3, o4);
    }

    public IpAddress(int[] ip) {
        set(ip);
    }
    public IpAddress(String ip) {
        set(ip);
    }

    public void set(int[] ip) {
        if (ip.length != 4 || Arrays.stream(ip).anyMatch(octet -> octet < 0 || octet > 255)) {
            throw new IllegalArgumentException("Invalid IP address range");
        } else {
            this.ip = IntStream.of(ip).reduce((result, octet) -> (result << 8) | octet).orElse(0);
        }
    }


    public void set(String ip) {
        String[] parts = ip.split("\\.");

        if (parts.length != 4 || Arrays.stream(parts).anyMatch(part -> {
            try {
                int octet = Integer.parseInt(part);
                return octet < 0 || octet > 255;
            } catch (NumberFormatException e) {
                return true;
            }
        })) {
            throw new IllegalArgumentException("Invalid IP address format or range");
        } else {
            this.ip = Arrays.stream(parts).mapToInt(Integer::parseInt).reduce((result, octet) -> (result << 8) | octet).orElse(0);
        }
    }

    public void set(int ip) {
        this.ip = ip;
    }

    public void set(int o3, int o2, int o1, int o0) {
        int[] o = {o3, o2, o1, o0};
        set(o);
    }

    public int getAsInt() {
        return this.ip;
    }

    public int[] getAsArray() {
        int[] ipArray = new int[4];
        int temp = ip;
        for (int i = 3; i >= 0; i--) {
            ipArray[i] = temp & 0xFF;  // Extract the least significant octet
            temp >>>= 8;  // Shift right to get the next octet
        }

        return ipArray;

    }

    public int getOctet(int num) {
        try {
        return this.getAsArray()[num];
    }catch (ArrayIndexOutOfBoundsException e){
            throw new IllegalArgumentException("Wrong Index");
        }
    }

    public String toString() {
        String[] ipArray = new String[4];
        int temp = this.ip;
        for (int i = 3; i >= 0; i--) {
            ipArray[i] = String.valueOf(temp & 0xFF);  // Extract the least significant octet
            temp >>>= 8;  // Shift right to get the next octet
        }

        return ipArray[0] + "." + ipArray[1] + "." + ipArray[2] + "." + ipArray[3];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IpAddress ipAddress)) return false;

        return ip == ipAddress.ip;
    }
}