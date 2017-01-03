package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import fx.ClientGUI;


public class MyUDPCleint extends AbstractChatClient {

	
	private Socket clientSocket;
	
	private MulticastSocket socket;
	//indicates whether to stop the thread 
	private Boolean stopAll=false;
	public static int BUFFER_SIZE=256;
	private InetAddress group;
	private  UDPCleintThread th;
	private ClientThread cth;
	private int port;
	private ArrayList<String> forbiddenWords;
	private boolean block;
	private int filteredMessageCounter=0;
	
	public  MyUDPCleint(ClientGUI gui){
		super(gui);
		 forbiddenWords = new ArrayList<String>();
		 forbiddenWords.add("framework");
		 forbiddenWords.add("fuck you");
		 forbiddenWords.add("temporallogik");
		 forbiddenWords.add("bitch");
		 forbiddenWords.add("javafx");
		 
		System.setProperty("java.net.preferIPv4Stack" , "true");
		
	}
	
	@Override
	public void sendChatMessage(String msg) {
		if(socket==null) return;
		//send message to server 
		msg=filter(msg);
		if(filteredMessageCounter>=3){
			gui.pushChatMessage("you're blocked");
			return;
		}
		msg=getUName()+" "+msg;
		byte data[] =msg.getBytes();
		DatagramPacket packet=null;
	
			packet = new DatagramPacket(data, data.length, group, port);
		
		try {
			//sending message as packet
			System.out.println("sending " +msg);
			socket.send(packet);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	private String filter(String message){
		for (String badWord : forbiddenWords) {
		
			if(message.contains(badWord)){
				filteredMessageCounter++;
				String replacement="";
				for(int i=1;i<=badWord.length();i++){
					 replacement+="*";
				}
				
				 message = message.replace(badWord,  replacement);
			}
		}
		
		return message;
	}
	
	@Override
	public void connect(String address, String p) {
		
		if (clientSocket==null){
			
			try {
				
				
				
				this.port = Integer.parseInt(p);
				
				clientSocket = new Socket(address,port);
				
				
				System.out.println("get mutli cast group");
				cth =new ClientThread(clientSocket);
				cth.start();
				
				
				
			
				
				
			   
			} catch (NumberFormatException e) {
				
				System.out.println(e.getStackTrace());
			} catch (UnknownHostException e) {
				
			
			} catch (IOException e) {
				
				System.out.println(e.getStackTrace());
			}
			
			
		}
		
	

	}
	
	
	
	
	

	@Override
	public void disconnect() {
		stopAll=true;
		try {
			socket.leaveGroup(group);
		} catch (IOException e) {
			
			System.out.println(e.getStackTrace());
		}
		socket.close();
		socket = null;
		th = null;
	}

	@Override
	public void terminate() {
		disconnect();

	}
	
	
	
	
	
class ClientThread extends Thread{
		
		private Socket clientSocket;
		private BufferedReader inp;
		public ClientThread( Socket cs){
			
			 clientSocket = cs;
			 try {
					inp = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				} catch (IOException e) {
					
					
					System.out.println(e.getStackTrace());
				}
		}
		
		public void run(){
			String inputLine;
			try {
				while(!stopAll && (inputLine = inp.readLine())!=null){
					System.out.println("group name recieved :" + inputLine);
					group = InetAddress.getByName(inputLine);
					System.out.println("port " + port);
					socket = new MulticastSocket(port);
					socket.joinGroup(group);
					th = new  UDPCleintThread();
					th.start();
					inp.close();
					clientSocket.close();
					break;
				}
			} catch (IOException e) {
				
				System.out.println("diconnected");
			}
			
		}
		
		
	}
	
	
	
	
	class UDPCleintThread extends Thread{
		public  UDPCleintThread(){
			
			
			
		}
		
		public void run(){
			while(!stopAll){
				System.out.println("running udp  client");
				byte[] buf = new byte[BUFFER_SIZE];
				//wait for packet from sever
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				try {
					socket.receive(packet);
					//convert bytes to string 
					String recievedMessage = new String(packet.getData(), 0,packet.getLength());
					System.out.println("message recieved from server " + recievedMessage);
					//show recieved message in chat 
					gui.pushChatMessage(recievedMessage);
				} catch (IOException e) {
					
					System.out.println(e.getStackTrace());
				}
				
				
			}
			
			
		}
	}

}
