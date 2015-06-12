package agilesites;

class IpAddress {
    public static void main(String[] args) throws Exception {
        if (args.length != 1)
            System.out.println("usage: <hostname>");
        if (args[0].equals("localhost"))
            System.out.println("127.0.0.1");
        else try {
            System.out.println(java.net.InetAddress.getByName(args[0]).getHostAddress());
        } catch (Exception ex) {
            System.err.println("!!! " + ex.getMessage());
            System.out.println("127.0.0.1");
        }
    }
}
