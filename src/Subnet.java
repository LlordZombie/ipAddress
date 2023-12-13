import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * Represents an IPv4 subnet and provides methods for subnet manipulation.
 * Implements the Comparable interface for natural ordering based on the network address.
 */
public class Subnet implements Comparable<Subnet> {

    /**
     * Represents the local network subnet (127.0.0.1/255.0.0.0).
     */
    public static final Subnet LOCALNET = new Subnet(new IpAddress(127, 0, 0, 1), new IpAddress(255, 0, 0, 0));
    /**
     * Represents a private network subnet with IP addresses starting from 10.0.0.0.
     */
    public static final Subnet PRIVATENET10 = new Subnet(new IpAddress(10, 0, 0, 0), new IpAddress(255, 0, 0, 0));
    /**
     * Exception thrown when an invalid subnet is encountered.
     */
    private static final IllegalArgumentException invalidSubnet = new IllegalArgumentException("Invalid Subnet");
    /**
     * The network address of the subnet.
     */
    private IpAddress addr;
    /**
     * The subnet mask.
     */
    private IpAddress mask;

    /**
     * Constructs a subnet from a string representation (e.g., "192.168.1.0/24").
     *
     * @param subnet A string representing the subnet.
     */
    public Subnet(String subnet) {
        set(subnet.split("/"));
    }

    /**
     * Constructs a subnet with a network address and a suffix indicating the subnet size.
     *
     * @param addr   The network address.
     * @param suffix The suffix indicating the subnet size.
     */
    public Subnet(IpAddress addr, int suffix) {
        set(new String[]{addr.toString(), String.valueOf(suffix)});
    }

    /**
     * Constructs a subnet with a network address and a subnet mask.
     *
     * @param addr The network address.
     * @param mask The subnet mask.
     */
    public Subnet(IpAddress addr, IpAddress mask) {
        set(new String[]{addr.toString(), mask.toString()});
    }

    /**
     * Constructs a subnet with two string representations of the network address and subnet mask.
     *
     * @param addr The network address.
     * @param mask The subnet mask.
     */
    public Subnet(String addr, String mask) {
        set(new String[]{addr, mask});
    }

    /**
     * Constructs a subnet based on an IP address, automatically determining the subnet prefix.
     *
     * @param addr The IP address.
     */
    public Subnet(IpAddress addr) {
        String[] subnet = {addr.toString(), getClassPrefix(addr)};
        set(subnet);
    }

    /**
     * Helper method to check if a subnet mask is valid.
     *
     * @param addr snm, which gets checked
     * @return true, if addr is a valid subnetmask
     */
    private static boolean isValidSnm(IpAddress addr) {
        IpAddress compareValue = new IpAddress();
        for (int i = 0; i < 32; i++) {
            compareValue.set(~0 << (32 - i));
            if (addr.equals(compareValue)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Helper method to determine the IP address class (A, B, C, etc.).
     *
     * @param ipAddress ip, which class gets calculated
     * @return A to E
     */
    private static char getClass(IpAddress ipAddress) {
        int counter = 0;
        while (counter <= 4 && ((ipAddress.getAsInt() << counter) >>> 31) == 1) counter++;
        return (char) ('A' + counter);
    }

    /**
     * Helper method to determine the class prefix based on the IP address.
     *
     * @param addr The Ip Address
     * @return prefix based on class
     */
    private String getClassPrefix(IpAddress addr) {
        int netClass = getClass(addr) - 'A';
        IpAddress[] a = {new IpAddress(255, 0, 0, 0), new IpAddress(255, 255, 0, 0), new IpAddress(255, 255, 255, 0)};
        try {
            return a[netClass].toString();
        } catch (ArrayIndexOutOfBoundsException e) {
            throw invalidSubnet;
        }
    }

    /**
     * Gets the subnet mask of the subnet.
     *
     * @return The subnet mask.
     */
    public IpAddress getNetMask() {
        return mask;
    }

    /**
     * Gets the network address of the subnet.
     *
     * @return The network address.
     */
    public IpAddress getNetAddress() {
        IpAddress a = new IpAddress();
        a.set(this.addr.getAsInt() & this.mask.getAsInt());
        return a;
    }

    /**
     * Gets the number of hosts that can be accommodated in the subnet.
     *
     * @return The number of hosts.
     */
    public int getNumberOfHosts() {
        return ~this.mask.getAsInt() - 1;
    }

    /**
     * compares two Subnets for equality
     *
     * @param o other subnet
     * @return true, if both addresses and snms are the same
     */
    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof Subnet && Objects.equals(addr, ((Subnet) o).addr) && Objects.equals(mask, ((Subnet) o).mask));
    }

    /**
     * @return Subnet as a String
     */
    @Override
    public String toString() {
        return addr.toString() + "/" + mask.toString();
    }

    /**
     * Checks if a given IP address is in the subnet.
     *
     * @param ip The IP address to check.
     * @return True if the IP address is in the subnet, false otherwise.
     */
    public boolean isInNetwork(IpAddress ip) {
        return new Subnet(this.getNetAddress(), this.mask).getNetAddress().equals(new Subnet(ip, this.mask).getNetAddress());
    }

    /**
     * Gets the broadcast address of the subnet.
     *
     * @return The broadcast address.
     */
    public IpAddress getBroadcastAddress() {
        IpAddress a = new IpAddress();
        a.set(this.getNetAddress().getAsInt() | (~this.mask.getAsInt()));
        return a;
    }

    /**
     * Gets the first usable IP address in the subnet.
     *
     * @return The first IP address.
     */
    public IpAddress getFirstIp() {
        IpAddress a = new IpAddress();
        a.set(this.getNetAddress().getAsInt() | 1);
        return a;
    }

    /**
     * Gets the last usable IP address in the subnet.
     *
     * @return The last IP address.
     */
    public IpAddress getLastIp() {
        IpAddress a = new IpAddress();
        a.set(getBroadcastAddress().getAsInt() & (~1));
        return a;
    }

    /**
     * Gets an array of all usable IP addresses in the subnet.
     *
     * @return An array of IP addresses.
     */
    public IpAddress[] getAllIpsInNetwork() {
        List<IpAddress> ipList = new ArrayList<>();
        IpAddress networkAddress = getNetAddress();
        IpAddress lastIpAddress = getLastIp();
        while (Integer.compareUnsigned(networkAddress.getAsInt(), lastIpAddress.getAsInt()) != 0) {
            ipList.add(new IpAddress(networkAddress.toString()));
            int currentIpInt = networkAddress.getAsInt() + 1;
            networkAddress.set(currentIpInt);
        }
        IpAddress[] ipsArray = new IpAddress[ipList.size()];
        ipList.toArray(ipsArray);
        return ipsArray;
    }

    /**
     * Gets the next subnet.
     *
     * @return The next subnet.
     */
    public Subnet getNextSubnet() {
        IpAddress a = getNetAddress();
        a.set(this.getBroadcastAddress().getAsInt() + 1);
        return new Subnet(a, this.mask);
    }

    /**
     * Splits the subnet into a specified number of smaller subnets.
     *
     * @param n The number of subnets to create.
     * @return An array of new subnets.
     */
    public Subnet[] splitNet(int n) {
        Subnet[] newNets = new Subnet[n];
        int newMask = 32 - (int) Math.ceil(Math.log(getHostNum() / 2.0) / Math.log(2));
        Subnet current = new Subnet(getNetAddress(), newMask);
        for (int i = 0; i < n; i++, current = current.getNextSubnet()) {
            newNets[i] = current;
        }
        return newNets;
    }

    /**
     * Calculates the number of hosts that can be accommodated in the subnet.
     *
     * @return The number of hosts.
     */
    private int getHostNum() {
        return (int) Math.pow(2, 32 - getSuffix());
    }

    /**
     * Calculates the suffix length of the subnet mask.
     *
     * @return The suffix length.
     * @throws IllegalArgumentException If the subnet mask is invalid.
     */
    private int getSuffix() {
        try {
            int maskInt = this.mask.getAsInt();
            int suffix = 0;
            while (((maskInt << suffix) & 0x80000000) != 0) {
                suffix++;
            }
            return suffix;
        } catch (IllegalArgumentException e) {
            throw invalidSubnet;
        }
    }

    /**
     * Sets the subnet based on a string array representation.
     *
     * @param subnet The string array representing the subnet.
     * @throws IllegalArgumentException If the subnet is invalid.
     */
    private void set(String[] subnet) {
        try {
            IpAddress addr = new IpAddress(subnet[0]);
            IpAddress mask;

            if (subnet[1].contains(".")) {
                mask = new IpAddress(subnet[1]);
                if (!isValidSnm(mask)) {
                    throw invalidSubnet;
                }
            } else {
                int suffix = Integer.parseInt(subnet[1]);
                if (suffix < 0 || suffix > 32) {
                    throw invalidSubnet;
                }
                IpAddress temp = new IpAddress();
                temp.set(~0 << (32 - suffix));
                mask = temp;
            }

            this.addr = addr;
            this.mask = mask;

        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
            throw invalidSubnet;
        }
    }

    /**
     * Compares this subnet to another subnet based on their network addresses.
     *
     * @param o The subnet to compare.
     * @return A negative integer, zero, or a positive integer as this subnet is less than, equal to, or greater than the specified subnet.
     */

    @Override
    public int compareTo(Subnet o) {
        return this.getNetAddress().compareTo(o.getNetAddress());
    }
}
