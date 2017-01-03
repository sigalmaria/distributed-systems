package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import fx.ClientGUI;

public class UDPEchoClient extends AbstractChatClient {

	
	private  ClientThread client;
	private DatagramSocket socket;
	private DatagramPacket packet;
	public static int BUFFER_SIZE=256;
	//indicates whether we should stop running threads, stopAll set to true
	//when calling terminate ,disconnet method 
	public Boolean stopAll=false;
	private String address;
	private int port;
	public UDPEchoClient(ClientGUI gui){
		
		super(gui);
	}
	
	
	@Override
	public void sendChatMessage(String msg) {
		//message recieved from the chat 
		byte data[] =msg.getBytes();
		try {
			packet = new DatagramPacket(data, data.length, InetAddress.getByName(address), port);
		} catch (UnknownHostException e1) {
			
			e1.printStackTrace();
		}
		try {
			//sending message as packet
			System.out.println("sending " +msg);
			socket.send(packet);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

	@Override
	public void connect(String address, String port) {
		//user want to connect to the chat, so create socket to connect 
		//to the server 
		try {
			socket = new DatagramSocket();
			this.address = address;
			this.port = Integer.parseInt(port);
		} catch (NumberFormatException e) {
			
			e.printStackTrace();
		} catch (SocketException e) {
			
			e.printStackTrace();
		}
		//start listening to server in another thread 
		client = new ClientThread();
		client.start();

	}

	@Override
	public void disconnect() {
		
		//close socket and stop threads 
		stopAll = true;
		socket.close();
		client=null;
		socket=null;
	}

	@Override
	public void terminate() {
		disconnect();

	}
	
	class ClientThread extends Thread{
		
		
		public  ClientThread(){
			
		}
		public void  run(){
			while(!stopAll){
				System.out.println("running client");
				byte[] buf = new byte[BUFFER_SIZE];
				//wait for packet from sever
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				try {
					socket.receive(packet);
					//convert bytes to string
					String recievedMessage = new String(packet.getData(), 0,packet.getLength());
					System.out.println("message recieved from server ");
					gui.pushChatMessage(recievedMessage);
				} catch (IOException e) {
					
					e.printStackTrace();
				}
				
			}
			
		}
	}

}
