import org.junit.jupiter.api.Test;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

public class TestIpAndSubnet {
    @Test
    void ipConstructorTests() {
        IpAddress defaultC = new IpAddress();
        IpAddress intsC = new IpAddress(10, 0, 0, 1);
        IpAddress arrayC = new IpAddress(new int[]{10, 0, 0, 1});
        IpAddress stringC = new IpAddress("192.168.0.1");
        assertEquals(defaultC.toString(), "127.0.0.1");
        assertEquals(intsC.toString(), "10.0.0.1");
        assertEquals(arrayC.toString(), "10.0.0.1");
        assertEquals(stringC.toString(), "192.168.0.1");
    }

    @Test
    void ipSetterTests() {
        IpAddress a = new IpAddress();
        IpAddress compare1 = new IpAddress("123.45.67.89");
        IpAddress compare2 = new IpAddress("10.0.0.1");
        IpAddress compare3 = new IpAddress("192.168.0.1");
        IpAddress compare4 = new IpAddress("200.200.200.200");
        a.set(2066563929);
        assertEquals(a, compare1);
        a.set(10, 0, 0, 1);
        assertEquals(a, compare2);
        a.set(new int[]{192, 168, 0, 1});
        assertEquals(a, compare3);
        a.set("200.200.200.200");
        assertEquals(a, compare4);
    }

    @Test
    void ipGetterTests() {
        IpAddress a = new IpAddress("192.168.0.1");
        assertEquals(a.getAsInt(), -1062731775);
        assertEquals(a.getOctet(0), 192);
        assertArrayEquals(a.getAsArray(), new int[]{192, 168, 0, 1});
    }

    @Test
    void ipConstantsAndEquals() {
        assertEquals(IpAddress.LOCALHOST, new IpAddress("127.0.0.1"));
        assertEquals(IpAddress.MODEM, new IpAddress("10.0.0.38"));
        IpAddress a = new IpAddress("192.168.0.1");
        IpAddress b = new IpAddress(192, 168, 0, 1);
        IpAddress c = new IpAddress(192, 123, 123, 123);
        assertEquals(a, b);
        assertNotEquals(a, c);
    }

    @Test
    void ipSpecialCasesTests() {
        assertThrows(IllegalArgumentException.class, () -> new IpAddress("asdf"));
        assertThrows(IllegalArgumentException.class, () -> new IpAddress("192..123.123.123"));
        assertThrows(IllegalArgumentException.class, () -> new IpAddress(22, 23, 2344, 123));
        assertThrows(IllegalArgumentException.class, () -> new IpAddress("1.2.3"));
        assertThrows(IllegalArgumentException.class, () -> new IpAddress("256.123.123.456"));
        assertThrows(IllegalArgumentException.class, () -> new IpAddress(new int[]{123, 123, 123}));
        assertThrows(IllegalArgumentException.class, () -> new IpAddress("1.1.1.1").getOctet(5));
    }

    @Test
    void subnetConstructorTests() {
        Subnet stringCidrC = new Subnet("192.168.1.0/24");
        Subnet stringDdnC = new Subnet("192.168.1.0/255.255.255.0");
        Subnet ipLenC = new Subnet(new IpAddress(192, 168, 1, 0), 24);
        Subnet ipIpC = new Subnet(new IpAddress(192, 168, 1, 0), new IpAddress(255, 255, 255, 0));
        Subnet stringStringC = new Subnet("192.168.1.0", "255.255.255.0");
        Subnet classAC = new Subnet(new IpAddress(10, 0, 0, 1));
        Subnet classBC = new Subnet(new IpAddress(172, 0, 0, 1));
        Subnet classCC = new Subnet(new IpAddress(200, 0, 0, 1));
        assertEquals(stringCidrC.toString(), "192.168.1.0/255.255.255.0");
        assertEquals(stringDdnC.toString(), "192.168.1.0/255.255.255.0");
        assertEquals(ipLenC.toString(), "192.168.1.0/255.255.255.0");
        assertEquals(ipIpC.toString(), "192.168.1.0/255.255.255.0");
        assertEquals(stringStringC.toString(), "192.168.1.0/255.255.255.0");
        assertEquals(classAC.toString(), "10.0.0.1/255.0.0.0");
        assertEquals(classBC.toString(), "172.0.0.1/255.255.0.0");
        assertEquals(classCC.toString(), "200.0.0.1/255.255.255.0");
    }

    @Test
    void subnetMethodTests() {
        Subnet a = new Subnet("192.168.0.4/255.255.255.0");
        Subnet b = new Subnet("192.168.0.4/255.255.255.252");
        Subnet c = new Subnet("192.168.0.4/255.255.255.0");
        IpAddress nm = a.getNetMask();
        IpAddress na = a.getNetAddress();
        IpAddress bc = a.getBroadcastAddress();
        Subnet next = a.getNextSubnet();
        IpAddress first = a.getFirstIp();
        IpAddress last = a.getLastIp();
        IpAddress[] all = b.getAllIpsInNetwork();
        int num = a.getNumberOfHosts();
        Subnet[] split = a.splitNet(2);
        assertEquals(nm.toString(), "255.255.255.0");
        assertEquals(na.toString(), "192.168.0.0");
        assertEquals(bc.toString(), "192.168.0.255");
        assertEquals(next.toString(), "192.168.1.0/255.255.255.0");
        assertEquals(first.toString(), "192.168.0.1");
        assertEquals(last.toString(), "192.168.0.254");
        assertEquals(Arrays.toString(all), "[192.168.0.4, 192.168.0.5]");
        assertEquals(num, 254);
        assertEquals(Arrays.toString(split), "[192.168.0.0/255.255.255.128, 192.168.0.128/255.255.255.128]");
        assertEquals(a, c);
        assertNotEquals(a, b);
        assertEquals(Subnet.LOCALNET, new Subnet("172.0.0.1/8"));
        assertEquals(Subnet.PRIVATENET10, new Subnet("10.0.0.0/8"));
        assertTrue(a.isInNetwork(new IpAddress("192.168.0.5")));
        assertFalse(a.isInNetwork(new IpAddress("193.168.0.1")));
    }

    @Test
    void subnetSpecialCasesTests() {
        assertThrows(IllegalArgumentException.class, () -> new Subnet(""));
        assertThrows(IllegalArgumentException.class, () -> new Subnet("1233.123.123.123/24"));
        assertThrows(IllegalArgumentException.class, () -> new Subnet("1234.123.23.23", "24"));
        assertThrows(IllegalArgumentException.class, () -> new Subnet(new IpAddress(254, 254, 254, 254)));
        assertThrows(IllegalArgumentException.class, () -> new Subnet("10.0.0.0/123.123.123.123"));
        assertThrows(IllegalArgumentException.class, () -> new Subnet("10.0.0.0/33"));
        assertThrows(IllegalArgumentException.class, () -> new Subnet(new IpAddress(10, 0, 0, 0), 33));
        assertThrows(IllegalArgumentException.class, () -> new Subnet(new IpAddress(10, 0, 0, 0), new IpAddress(123, 123, 123, 123)));
    }
}
