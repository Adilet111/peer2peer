import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import misc.PeerQueue;
import misc.misc;

public class Peer {
    public ServerSocket serverSocket;
    private int peerId;
    private String dir;
    private int port;
    private PeerQueue<Socket> peerQueue;
    ObjectInputStream in = null;
    ObjectOutputStream out = null;
    
    public Peer(int pId, String directory) throws IOException{
		peerId = pId;
		dir=directory;
		peerQueue = new PeerQueue<Socket>();
	}
    public int getPort() {
    	return port;
    }
    
    
    
public int server() throws IOException{
    	
    	try {
    		serverSocket = new ServerSocket(0); 
    		this.port = serverSocket.getLocalPort();
    	} catch(Exception e){
    		return 0;
    	}
		
		while(true){
			Socket socket = serverSocket.accept();
			synchronized(peerQueue){
				peerQueue.add(socket);
			}
		}
		
	}
public void income() throws IOException{
	
	ExecutorService executor = Executors.newFixedThreadPool(4);

	while(true){
		if(peerQueue.peek() == null){
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			continue;
		}
		synchronized(peerQueue){
			Socket c = peerQueue.poll();
			String d = this.dir;
			
			Thread thread = new Thread() {
		    public void run(){
		        BufferedInputStream bis = null;
		        OutputStream os = null;
		        ObjectInputStream in1 = null;
		        try {
		            System.out.println("Waiting...");
		            try {
		              // send file
		            	int bytesRead;
		            	
		              in1 = new ObjectInputStream(c.getInputStream());

		              String fm = (String)in1.readObject();
		              
		              
		              
		              File myFile = new File (d+"/"+fm);
		              byte [] mybytearray  = new byte [(int)myFile.length()];
		              bis = new BufferedInputStream(new FileInputStream(myFile));
		              bis.read(mybytearray,0,mybytearray.length);
		              os = c.getOutputStream();

		              os.write(mybytearray,0,mybytearray.length);
		              os.flush();
		              System.out.println("Done.");		            }
		            finally {
		              if (in1 != null) in1.close();
		              if (bis != null) bis.close();
		              if (os != null) os.close();
		              if (c!=null) c.close();
		            }
		          
		        }
		        catch(Exception e) {
		        	return;
		        }
		    }
		};
		thread.start();
		}
	}
	
}
}
