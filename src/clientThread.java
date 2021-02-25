import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class clientThread extends Thread  {
		
	Socket serverClient;
	public clientThread(Socket s) {
		this.serverClient = s;
	}
	 
	 static synchronized void incrementCounter(){
        	Server.counter++;
     }
	
	public void run() {

	    String extension;
	    SimpleDateFormat date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	    System.out.println("server started");
	    ObjectInputStream in = null;
	    ObjectOutputStream out = null;
	    String inputLine = "";
	   try {
	    in = new ObjectInputStream(serverClient.getInputStream());
	    out = new ObjectOutputStream(serverClient.getOutputStream());
	   }catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    while (true) {
	    	
	        
	        try {
	        	inputLine = (String)in.readObject();
	        	if(inputLine.equals("empty")) {
	        		continue;
	        	}
		        if (inputLine.equals("Hello") == true) {
		
		            out.writeObject("Hi");
		            out.flush();
		            
		            
		            System.out.println(Server.table);
		    	    incrementCounter();
		    	    synchronized(this) {
		    	    	out.writeObject(Server.counter);
		    	    	out.flush();
		    	    }
		
		    	   int port = (int) in.readObject();

		            
		            
		            File[] files = (File[]) in.readObject();
		        
		            for (File file: files) {
		        		List<Object> temp = new ArrayList<>();
	                    String nameExt = file.getName().replaceFirst("[.][^.]+$", "");
	                    int i = file.getName().lastIndexOf('.');
	                    extension = file.getName().substring(i+1);
	                    temp.add(extension);
	                    temp.add(file.length());
	                    temp.add(date.format(file.lastModified()));
	                    temp.add(serverClient.getInetAddress());
	                    temp.add(serverClient.getLocalPort());
	                    temp.add(port);
	                    System.out.println(temp);
	                    Server.table.put(nameExt, temp);
	                    System.out.println("File added");
		            }
			    	   System.out.println("after adding port");
			    	   System.out.println(Server.table);


		    	    	
		            continue;
		        }
	        }catch (IOException e) {
				e.printStackTrace();
				break;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
	        
	        System.out.println(inputLine);
	        if (inputLine.equals("Search") == true) {
	        	List result = new ArrayList();
	            String fileName = "";
				try {
					fileName = (String)in.readObject();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            System.out.println(fileName);
	            if(Server.table.containsKey(fileName)) {
                   for(String name_file: Server.table.keySet()) {
                       if(name_file.equals(fileName)) {
                           result.add(Server.table.get(fileName) + "\n");
                       }
                   }
		               try {
						out.writeObject(result);
						out.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}  
                }
	        }
	        
	        
	        
	        if (inputLine.equals("BYE!!") == true) {
                try {
                        int closingPort = (int)in.readObject();
                        System.out.println(closingPort);
                        //String a = Integer.toString(closingPort);
                        Object smth;

                        for(String searchking_key: Server.table.keySet()) {
                            List<Object> temporali = new ArrayList<>();
                            int i = 0;
                            smth = Server.table.get(searchking_key);
                            List values = (List) smth;
                            System.out.println(values);
                            System.out.println("After first values");
                            for(Object ob: values) {
                                List checker = (List)ob;
                                if (checker.contains(closingPort) == true) {

                                } else {
                                    temporali.add(ob);
                                }
                            }
                            System.out.println(values);
                            System.out.println(Server.table);
                            Server.table.replaceValues(searchking_key, Collections.singleton(temporali));
                        }

                        System.out.println(Server.table);
                        if (in != null) in.close();
                        serverClient.close();
                        break;

                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
	        
	    }
	
	}
}
