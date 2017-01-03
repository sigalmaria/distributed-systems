package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import fx.ClientGUI;


public class MyClient extends AbstractChatClient{

	private Socket clientSocket;
	private ClientThread th;
	private PrintWriter out;
	
	public MyClient(ClientGUI gui) {
		super(gui);
	}

	@Override
	public void sendChatMessage(String msg) {
		
		out.println(getUName()+" "+msg);
		
	}
	
	@Override
	public void setUName(String uName) {
	
		super.setUName(uName);
		out.println("/name "+getUName());//we parse it in server accordinly 
	}

	@Override
	public void connect(String address, String port) {
		if (clientSocket==null){
			
			try {
				clientSocket = new Socket(address,Integer.parseInt(port));
			    out=new PrintWriter(clientSocket.getOutputStream(),true);
			
				
				th =new ClientThread(clientSocket);
				th.start();
				
			    out.println("/name "+getUName());
			} catch (NumberFormatException | IOException e) {
				
				e.printStackTrace();
			}
			
			
		}
		
	}

	@Override
	public void disconnect(){
	
	 if(clientSocket!=null){
	
		if(out!=null)	
	    out.close();
		
		System.out.println("out stream closed");
		try {
			if(!clientSocket.isClosed()){
			 clientSocket.close();
			}
		} catch (IOException e) {
		
			System.out.println("error closing socket");
		}
	 }
		System.out.println("socket closed");
	 th=null;
	 clientSocket=null;
	}

	@Override
	public void terminate() {
		disconnect();
		
	}
	
	class ClientThread extends Thread{
		    Socket cleintSocket;
			BufferedReader inp;
		
	ClientThread(Socket s){
		clientSocket=s;
		try {
			inp = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch (IOException e) {
			
			e.printStackTrace();
			System.out.println(e.getStackTrace());
		}
	}
	
	@Override
	public void run() {
		String inputLine;
		try {
			while( true ){
				
				
				inputLine = inp.readLine();
				if(inputLine!=null)
				gui.pushChatMessage(inputLine);
				
			}
		} catch (IOException e) {
			
			System.out.println("disconected");
			if(inp!=null){
				try {
					inp.close();
				} catch (IOException e1) {
					
					System.out.println("cannot close client stream");
				}
			}
			
			if(cleintSocket!=null && !clientSocket.isClosed()){
				try {
					clientSocket.close();
				} catch (IOException e1) {
					System.out.println("error closing socket");
				}
			}
		}
		
		
		
			
	}
	
	
   
	}
	


}
