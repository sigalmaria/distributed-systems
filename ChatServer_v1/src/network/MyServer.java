package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.concurrent.ConcurrentHashMap;

import fx.ServerGUI;

public class MyServer extends AbstractChatServer{

	private ConcurrentHashMap<Integer, ClientServer> threads;
	private ConcurrentHashMap<String, Integer> cleintNames;
	private Server server;
	
	private static int COUNTER=0;
	public MyServer(ServerGUI gui) {
		super(gui);
		threads = new ConcurrentHashMap<Integer, ClientServer>();
		cleintNames = new ConcurrentHashMap<String,Integer>();
	}

	@Override
	public void receiveConsoleCommand(String command, String msg) {
		
		
	}

	@Override
	public void start(String port) {
	    server= new Server(Integer.parseInt(port));
		server.start();
		
	}

	@Override
	public void stop() {
	
		for ( ClientServer th : threads.values()) {
			
			if(th.writer!=null)
			th.writer.close();
		}
	
		if(server!=null && server.server!=null && !server.server.isClosed()){
			try {
				server.server.close();
			} catch (IOException e) {
				
				
				System.out.println("disconted");
			}
		}
		
		
	}

	@Override
	public void terminate() {
		stop();
		
	}
	
	
	class Server extends Thread{
		
		int port;
		ServerSocket server;
		public Server(int port){
			this.port= port;
		}
		
		
		public void run(){
			
			try{
				server=new ServerSocket(port);
				System.out.println("server established");
				while(true){
					Socket client=server.accept();
					ClientServer cleintTh = new ClientServer(client, COUNTER++);
					cleintTh.start();
				}
		
				
			} catch (NumberFormatException e) {
				
				System.out.println("disconcted");
			} catch (IOException e) {
				
				System.out.println("disconcted");
			}finally {
				if(server!=null && !server.isClosed())
				{		
					try {
						server.close();
					} catch (IOException e) {
						
						System.out.println("disconnected");
					}
				}	
			}
			
			
		}
		
		
	}
	
	class ClientServer extends Thread{
		
		Socket client;
		BufferedReader reader;
		 PrintWriter writer;
		String name;
		int id;
		
		ClientServer(Socket client, int id){
			this.client = client;
			this.id=id;
			threads.put(id, this);
		}
		
		public void run(){
			writer=null;
			
			try{
				 reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
				 writer = new PrintWriter(client.getOutputStream(), true);
				
				
				
				 String inputLine;
				 while( (inputLine = reader.readLine()) != null){
					 
					int index = inputLine.indexOf("/name");
					 if(index!=-1){
						 String clientName = inputLine.substring("/name".length()+1); //+1 for space
						 String message;
						 if(name==null){
							 message=clientName+" joined the chat !";
						 }else{
							message = name + " changes his name to "+clientName; 
							cleintNames.remove(this.name);
							gui.removeClient(this.id);
						 }
						 this.name =  clientName;
						 gui.addClient(this.id, name);
						 cleintNames.put(name, id);
						 for ( ClientServer th : threads.values()) {
							   
							th.writer.println(message);
						}
					 }else{
						 gui.pushConsoleMessage(inputLine);
						 int commandIndex;
						 commandIndex = inputLine.indexOf("/private");
						 System.out.println("command index " + commandIndex);
						 String reciever;
						 String message;
								 if(commandIndex!=-1){
									 System.out.println("command private");
									 int  commandEnd = commandIndex+"/private".length()+1;
									 reciever= inputLine.substring( commandEnd,inputLine.indexOf(" ",  commandEnd));
									 message = inputLine.substring(inputLine.indexOf(reciever));
									 System.out.println("reciever " + reciever);
									 System.out.println("message " + message);
									 System.out.println(cleintNames);
									 ClientServer th = threads.get(cleintNames.get(reciever));
									 if( th!=null){
										 System.out.println("found reciever" );
										 th.writer.println(message);
									 }
									 
									 
									
								 }else{
								//send to all clients
								 for ( ClientServer th : threads.values()) {
										System.out.println("sending  to th " + th.id);
										th.writer.println(inputLine);
									}
								 }
					 }
					 
					 
					 
					 
				 }
				
				
			}catch (Exception e) {
			   System.out.println("disconnected");
			}finally {
				if(writer!=null){
					gui.removeClient(id);
					threads.remove(id);
					writer.close();
				}
			}
			
		}
	}
	
	

}
