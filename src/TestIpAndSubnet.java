import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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


}
