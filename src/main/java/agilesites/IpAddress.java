package agilesites;

class IpAddress  {
  public static void main(String[] args) throws Exception {
     if(args.length!=1)
       System.out.println("usage: <hostname>");
    else System.out.println(java.net.InetAddress.getByName(args[0]).getHostAddress());
  }
}
