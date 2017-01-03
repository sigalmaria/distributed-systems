package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPEchoServer {

	//size for datagram packet
	private DatagramSocket socket;
	public static int BUFFER_SIZE=256;
	public static void main(String[] args) {
		
		
		UDPEchoServer server =new UDPEchoServer();
		server.startServer();
		System.out.println("started server");
		
			
			
	}
	
	public UDPEchoServer(){
		//close the socket on programm shut down
		
		Runtime.getRuntime().addShutdownHook(new Thread(){public void run(){
		
				socket.close();
			
		
		}});
	}
	
	public void startServer(){
		
		int portNumber=13337;
		UDPServerThread ser=new UDPServerThread(portNumber);
		ser.start();
	}
	
	
	
	class UDPServerThread extends Thread{
		
		
		public UDPServerThread(int port){
			
			try {
				socket=new DatagramSocket(port);
			} catch (SocketException e) {
				
				e.printStackTrace();
			}
		}
		public void run(){
			
			while(true){
				System.out.println("server running");
				byte[] data = new byte[BUFFER_SIZE];
				DatagramPacket packet = new DatagramPacket(data, data.length);
				try {
					socket.receive(packet);
					System.out.println("server recieved message");
					//retrieve what client sends 
					byte[] req = packet.getData();
					//convert the bytes to string
					String reqStr = new String(req,0,req.length);
					System.out.println("revieve request from client: " + reqStr);
					//send the packet back to client 
					socket.send(packet);
				} catch (IOException e) {
					
					e.printStackTrace();
				}
				
			}
		}
		
		
		
		
	}

}
