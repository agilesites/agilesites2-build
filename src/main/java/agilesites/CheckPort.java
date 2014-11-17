package agilesites;

import java.net.ServerSocket;

public class CheckPort {

	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("usage: <port-to-check>");
		} else {
			ServerSocket serv = null;
			int result = 0;
			try {
				serv = new ServerSocket(Integer.parseInt(args[0]));				
			} catch(Exception ex) {
				System.out.println(ex.getMessage());
				result = 1;
			} finally {
				try { serv.close(); } catch(Exception ex) { }
			}
			System.exit(result);
		}
	}

}
