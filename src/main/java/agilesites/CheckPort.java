package agilesites;

import java.net.Socket;

public class CheckPort {

	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("usage: <host-to-check> <port-to-check>");
			System.exit(2);
		} else {
			Socket serv = null;
			int result = 0;
			try {
				serv = new Socket(args[0], Integer.parseInt(args[1]));				
				System.out.println("Connection accepted.");
				serv.close();
			        System.exit(1);
			} catch(Exception ex) {
				System.out.println(ex.getMessage());
			} finally {
				try { serv.close(); } catch(Exception ex) { }
			}
			System.exit(0);
		}
	}

}
