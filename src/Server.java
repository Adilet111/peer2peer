import java.awt.datatransfer.SystemFlavorMap;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;






public class Server{
	public static int counter = 0;
	public static Multimap<String, List<Object>> table = ArrayListMultimap.create();
    

    public static void main (String [] args) throws Exception {
    	try{
	      InetAddress addr = InetAddress.getByName("127.0.0.1");
	      ServerSocket server=new ServerSocket(5555,0, addr );
	      System.out.println("Server Started ....");
	      while(true){
	        Socket client=server.accept();  //server accept the client connection request
	        clientThread sct = new clientThread(client); //send  the request to a separate thread
	        sct.start();
	      }
	    }catch(Exception e){
	      System.out.println(e);
	    }
    }


}
