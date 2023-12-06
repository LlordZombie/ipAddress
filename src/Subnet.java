import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class Subnet implements Comparable<Subnet> {
    public static final Subnet LOCALNET = new Subnet(new IpAddress(172, 0, 0, 1), new IpAddress(255, 0, 0, 0));
    public static final Subnet PRIVATENET10 = new Subnet(new IpAddress(10, 0, 0, 0), new IpAddress(255, 0, 0, 0));
    private static final IllegalArgumentException invalidSubnet = new IllegalArgumentException("Invalid Subnet");
    private IpAddress ip;
    private IpAddress subnetmask;

    public Subnet(String subnet) {
        String[] split = subnet.split(Pattern.quote("/"));
        set(split);
    }

    public Subnet(IpAddress ip, int suffix) {
        String[] subnet = {ip.toString(), String.valueOf(suffix)};
        set(subnet);
    }

    public Subnet(IpAddress ip, IpAddress subnetmask) {
        String[] subnet = {ip.toString(), subnetmask.toString()};
        set(subnet);
    }

    public Subnet(String ip, String subnetmask) {
        String[] subnet = {ip, subnetmask};
        set(subnet);
    }

    public Subnet(IpAddress ip) {
        String[] subnet = new String[2];
        subnet[0] = ip.toString();
        int netClass = getClass(ip) - 'A';
        IpAddress[] a = {new IpAddress(255, 0, 0, 0), new IpAddress(255, 255, 0, 0), new IpAddress(255, 255, 255, 0)};
        try {
            subnet[1] = a[netClass].toString();
        } catch (ArrayIndexOutOfBoundsException e) {
            throw invalidSubnet;
        }
        set(subnet);
    }

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

    private static char getClass(IpAddress ipAddress) {
        int ip = ipAddress.getAsInt();
        int counter = 0;
        while (counter <= 4 && ((ip << counter) >>> 31) == 1) {
            counter++;
        }
        return (char) ('A' + counter);
    }

    public IpAddress getNetMask() {
        return subnetmask;
    }

    public IpAddress getNetAddress() {
        IpAddress a = new IpAddress();
        a.set(this.ip.getAsInt() & this.subnetmask.getAsInt());
        return a;
    }

    public int getNumberOfHosts() {
        return ~this.subnetmask.getAsInt() - 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Subnet subnet = (Subnet) o;

        if (!Objects.equals(ip, subnet.ip)) return false;
        return Objects.equals(subnetmask, subnet.subnetmask);
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        return ip.toString() + "/" + subnetmask.toString();
    }

    public boolean isInNetwork(IpAddress ip) {
        Subnet net1 = new Subnet(this.getNetAddress(), this.subnetmask);
        Subnet net2 = new Subnet(ip, this.subnetmask);
        Subnet net3 = new Subnet(net2.getNetAddress(), this.subnetmask);
        return net1.equals(net3);
    }

    public IpAddress getBroadcastAddress() {
        IpAddress a = new IpAddress();
        int addr = this.getNetAddress().getAsInt() + (~this.subnetmask.getAsInt());
        a.set(addr);
        return a;
    }

    public IpAddress getFirstIp() {
        IpAddress a = new IpAddress();
        a.set(this.getNetAddress().getAsInt() + 1);
        return a;
    }

    public IpAddress getLastIp() {
        IpAddress a = new IpAddress();
        a.set(this.getBroadcastAddress().getAsInt() - 1);
        return a;
    }

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

    public Subnet getNextSubnet() {
        IpAddress a = getNetAddress();
        a.set(this.getBroadcastAddress().getAsInt() + 1);
        return new Subnet(a, this.subnetmask);
    }

    public Subnet[] splitNet(int n) {
        Subnet[] newNets = new Subnet[n];
        int newMask = 32 - (int) Math.ceil(Math.log((double) getHostNum() / 2) / Math.log(2));
        Subnet current = new Subnet(this.getNetAddress(), newMask);
        for (int i = 0; i < n; i++) {
            newNets[i] = current;
            current = current.getNextSubnet();
        }
        return newNets;
    }

    private int getHostNum() {
        return (int) Math.pow(2, 32 - getSuffix());
    }

    private int getSuffix() {
        try {
            int maskInt = this.subnetmask.getAsInt();
            int suffix = 0;
            while (((maskInt << suffix) & 0x80000000) != 0) {
                suffix++;
            }
            return suffix;
        } catch (IllegalArgumentException e) {
            throw invalidSubnet;
        }
    }

    private void set(String[] subnet) {
        IpAddress addr = new IpAddress(subnet[0]);
        IpAddress mask = new IpAddress();
        try {
            if (subnet[1].contains(".")) {
                try {
                    mask = new IpAddress(subnet[1]);
                    if (!isValidSnm(mask)) {
                        throw invalidSubnet;
                    }
                } catch (IllegalArgumentException e) {
                    throw invalidSubnet;
                }
            } else {
                int suffix;
                try {
                    suffix = Integer.parseInt(subnet[1]);
                } catch (NumberFormatException e) {
                    throw invalidSubnet;
                }
                if (suffix < 0 || suffix > 32) {
                    throw invalidSubnet;
                }
                mask.set(~0 << (32 - suffix));
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw invalidSubnet;
        }
        this.ip = addr;
        this.subnetmask = mask;
    }

    @Override
    public int compareTo(Subnet o) {
        return this.getNetAddress().compareTo(o.getNetAddress());
    }
}
