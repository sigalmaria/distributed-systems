package network;

import java.io.IOException;
import java.io.PrintWriter;

import java.net.ServerSocket;
import java.net.Socket;

import fx.ServerGUI;


public class MyMultiCastServer extends AbstractChatServer {

	public static String GROUP_ADDRESS = "224.0.0.1";
	
	
	private MutliCastServer server;
	//indicates whether to stop the server thread
	private Boolean stopAll=false;
	
	
	public MyMultiCastServer(ServerGUI gui){
		 	super(gui);
	
	 }
	
	
	
	
	
	@Override
	public void receiveConsoleCommand(String command, String msg) {
		

	}

	@Override
	public void start(String port) {
		
		MutliCastServer server = new MutliCastServer(Integer.parseInt(port));
		server.start();
	}

	@Override
	public void stop() {
		stopAll = true;
		if(server!=null && server.out!=null){
			server.out.close();
		}
		if(server!=null && server.serverSocket!=null){
			if(!server.serverSocket.isClosed()){
				try {
					server.serverSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		
		
	   server = null;

	}

	@Override
	public void terminate() {
		stop();

	}
	
	class MutliCastServer extends Thread{
		
		int port;
		ServerSocket serverSocket;
		PrintWriter out;
		public MutliCastServer(int port){
			this.port= port;
			try {
				serverSocket=new ServerSocket(port);
				System.out.println("server established");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			
			}
		}
		
		//send group address to the client , so he can join the multi cast group
		public void run(){
			
		while(!stopAll){
			System.out.println("running");
			out=null;
			try {
				Socket client = serverSocket.accept();
				System.out.println("connection accepted");
				//writing back to client
				out = new PrintWriter(client.getOutputStream(),true);
				out.println(GROUP_ADDRESS);
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
			
		}
			
			
		}
		
		
	}

}
