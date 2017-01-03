package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;

import fx.ClientGUI;

public class EchoClient extends AbstractChatClient {

	
	private EchoClientThread th;
	public EchoClient(ClientGUI gui){
		super(gui);
	
	}
	
	@Override
	public void sendChatMessage(String msg) {
		gui.pushChatMessage(msg);
		th.messages.add(msg);

	}

	@Override
	public void connect(String address, String port) {
		if (th==null){
		
			try {
				th =new EchoClientThread(address, port);
			} catch (NumberFormatException | IOException e) {
				
				e.printStackTrace();
			}
			th.start();
			
		}
		
	}

	@Override
	public void disconnect() {
		try {
			System.out.println("closing connection");
			th.out.close();
			th.echoSocket.close();
			th.stopCommnunication=true;
			th=null;
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}

	}

	@Override
	public void terminate() {
		
		disconnect();

	}
	
	class EchoClientThread extends Thread{
		
		PrintWriter out ;
		Socket echoSocket;
		LinkedList<String> messages;
		Boolean stopCommnunication=false;
		BufferedReader in;
		EchoClientThread(String address, String port) throws NumberFormatException, UnknownHostException, IOException{
			
			echoSocket = new Socket(address,Integer.parseInt(port));
			messages = new LinkedList<String>();
		    out=new PrintWriter(echoSocket.getOutputStream(),true);
			in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
			System.out.println("initiated");			
		}
		
		
		public void run(){
			
				while(!stopCommnunication){
					System.out.println("in while");
					if(!messages.isEmpty()){
						System.out.println("recived a message " +  messages.peek());
						out.println(messages.poll());
						try {
							gui.pushChatMessage(in.readLine());
						} catch (IOException e) {
							
							e.printStackTrace();
						}
					}
					
				}
		
			
			
			
		}
	
	}

}
