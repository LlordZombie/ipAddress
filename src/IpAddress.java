import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Represents an IPv4 address and provides methods for manipulation and comparison.
 * Implements the Comparable interface for natural ordering based on the numerical value of the IP address.
 */
public class IpAddress implements Comparable<IpAddress> {
    /**
     * Represents the localhost IP address (127.0.0.1).
     */
    public static final IpAddress LOCALHOST = new IpAddress();
    /**
     * Represents a default IP address for a modem (10.0.0.138).
     */
    public static final IpAddress MODEM = new IpAddress(10, 0, 0, 138);
    /**
     * The numerical representation of the IP address.
     */
    private int ip;

    /**
     * Default constructor, initializes the IP address to the localhost (127.0.0.1).
     */
    public IpAddress() {
        set(new int[]{127, 0, 0, 1});
    }

    /**
     * Constructor with a single integer parameter, initializes the IP address.
     *
     * @param ip The numerical representation of the IP address.
     */
    private IpAddress(int ip) {
        set(ip);
    }

    /**
     * Constructor with four integer parameters, initializes the IP address.
     *
     * @param o1 First octet.
     * @param o2 Second octet.
     * @param o3 Third octet.
     * @param o4 Fourth octet.
     */
    public IpAddress(int o1, int o2, int o3, int o4) {
        set(o1, o2, o3, o4);
    }

    /**
     * Constructor with an array of integers, initializes the IP address.
     *
     * @param ip An array representing the four octets of the IP address.
     */
    public IpAddress(int[] ip) {
        set(ip);
    }

    /**
     * Constructor with a string representation of the IP address, initializes the IP address.
     *
     * @param ip A string representing the IP address in dot-decimal notation.
     */
    public IpAddress(String ip) {
        set(ip);
    }

    /**
     * Sets the IP address based on an array of integers representing the four octets.
     *
     * @param ip An array representing the four octets of the IP address.
     * @throws IllegalArgumentException if the array length is not 4 or if any octet is outside the valid range.
     */
    public void set(int[] ip) {
        if (ip.length != 4 || Arrays.stream(ip).anyMatch(octet -> octet < 0 || octet > 255)) {
            throw new IllegalArgumentException("Invalid IP address range");
        } else {
            this.ip = IntStream.of(ip).reduce((result, octet) -> (result << 8) | octet).orElse(0);
        }
    }

    /**
     * Sets the IP address based on a string representation in dot-decimal notation.
     *
     * @param ip A string representing the IP address in dot-decimal notation.
     * @throws IllegalArgumentException if the string format is invalid or if any octet is outside the valid range.
     */
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

    /**
     * Sets the IP address based on a single integer value.
     *
     * @param ip The numerical representation of the IP address.
     */
    public void set(int ip) {
        this.ip = ip;
    }

    /**
     * Sets the IP address based on four integer parameters representing the four octets.
     *
     * @param o3 First octet.
     * @param o2 Second octet.
     * @param o1 Third octet.
     * @param o0 Fourth octet.
     */
    public void set(int o3, int o2, int o1, int o0) {
        int[] o = {o3, o2, o1, o0};
        set(o);
    }

    /**
     * Gets the numerical representation of the IP address.
     *
     * @return The numerical representation of the IP address.
     */
    public int getAsInt() {
        return this.ip;
    }

    /**
     * Gets the IP address as an array of four integers representing the four octets.
     *
     * @return An array representing the four octets of the IP address.
     */
    public int[] getAsArray() {
        int[] ipArray = new int[4];
        int temp = ip;
        for (int i = 3; i >= 0; i--) {
            ipArray[i] = temp & 0xFF;
            temp >>>= 8;
        }
        return ipArray;
    }

    /**
     * Gets a specific octet of the IP address.
     *
     * @param num The index of the octet (0-3).
     * @return The value of the specified octet.
     * @throws IllegalArgumentException if the index is out of bounds.
     */
    public int getOctet(int num) {
        try {
            return this.getAsArray()[num];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Wrong Index");
        }
    }

    /**
     * Returns the string representation of the IP address in dot-decimal notation.
     *
     * @return The string representation of the IP address.
     */
    public String toString() {
        int[] ipArray = getAsArray();
        return ipArray[0] + "." + ipArray[1] + "." + ipArray[2] + "." + ipArray[3];
    }

    /**
     * Checks if the provided object is equal to this IpAddress.
     *
     * @param o The object to compare.
     * @return True if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IpAddress ipAddress)) return false;
        return ip == ipAddress.ip;
    }

    /**
     * Compares this IpAddress with another IpAddress for order.
     *
     * @param o The IpAddress to be compared.
     * @return A negative integer, zero, or a positive integer as this IpAddress is less than, equal to, or greater than the specified IpAddress.
     */
    @Override
    public int compareTo(IpAddress o) {
        return Integer.compareUnsigned(this.getAsInt(), o.getAsInt());
    }
}