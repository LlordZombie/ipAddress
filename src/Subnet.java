public class Subnet {
    private IpAddress addr;
    private IpAddress mask;

    public Subnet(String subnet){
        String[] split = subnet.split("\\.");
        if (!split[1].contains(".")){

        }

    }
    public static int getNetmask(String network) {
        int suffix =Integer.parseInt(network);
        return (suffix == -1) ? -1 : ~0 << (32 - suffix);
    }

}
