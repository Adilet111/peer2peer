import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import misc.misc;
import misc.PeerQueue;

import static java.lang.Thread.sleep;

public class Client extends JFrame{

    public Socket sock;
    public ObjectOutputStream out;
    public ObjectInputStream in;
    public Hashtable<String, List> contentTable = new Hashtable<String, List>();
    private Peer pr;
    private String dir;


    
    
    private JPanel mainPanel;
    //buttons
    private JButton searchButton;
    private JButton downloadButton;
    private JButton helloButton;
    private JButton exitButton;
    //labels for buttons
    private JLabel searchLabel;
    private JLabel helloLabel;
    private JLabel downloadLabel;
    private JLabel errorLabel;
    //fields
    private JTextField SearchFileField;
    private JTextField DownloadFile;
    //others
    private JScrollPane ScrollingOfFiles;
    private JFrame frame;
    public File[] files;
    DefaultListModel listModel;
    private JList jl;
    String str[]={"Info1","Info2","Info3","Info4","Info5"};
    
    

    public Client() throws IOException {
    	
    	//-----------
        super("Client");
        
        setLayout(null);
        setSize(800,500);
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //buttons setting
        this.helloButton = new JButton("hello");
        this.helloButton.setBounds(80,440,130,20);
        add(this.helloButton);
        
        this.searchButton = new JButton("search");
        this.searchButton.setBounds(390,50,110,20);
   	 	searchButton.setEnabled(false);
        add(this.searchButton);
        
        
        this.downloadButton = new JButton("download");
	    downloadButton.setEnabled(false);
        this.downloadButton.setBounds(250,440,130,20);
        add(downloadButton);
        
        this.exitButton = new JButton("exit");
        this.exitButton.setBounds(530, 350, 110, 20);
        exitButton.setEnabled(false);
        add(exitButton);
        
        //field setting
//        this.responseField = new JTextField("");
//        responseField.setBounds(380,100,130,20);
//        add(this.responseField);
        
        SearchFileField=new JTextField();
        SearchFileField.setBounds(520,200,130,20);
        add(SearchFileField);
        
        
        //label setting 
        helloLabel = new JLabel();
        helloLabel.setBounds(530,100,300,20);
        add(this.helloLabel);
        
        errorLabel = new JLabel();
        errorLabel.setBounds(530,440,200,20);
        add(this.errorLabel);
        
        searchLabel = new JLabel("For search");
        searchLabel.setBounds(530,160,130,20);
        add(this.searchLabel);
        
        
        
        listModel = new DefaultListModel();
        jl=new JList(listModel);
        
        JScrollPane listScroller = new JScrollPane(jl);
        listScroller.setBounds(50, 80,400,300);
        add(listScroller);
        
      

        helloButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                	 searchButton.setEnabled(true);
                	 exitButton.setEnabled(true);
                    //connecting to the server and sending HELLO message

                    
                    

                    //sending data about files in the directory
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setMultiSelectionEnabled(true);
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    int option = fileChooser.showOpenDialog(frame);
                    if (option == JFileChooser.APPROVE_OPTION) {
                        files = fileChooser.getSelectedFiles();
                    }
                    if (files.length ==0) {
                    	System.out.println("Choose file");
                    }else {
                    	
  		        	  sock = new Socket("localhost", 5555);
  		        	  out = new ObjectOutputStream(sock.getOutputStream());
  		              in = new ObjectInputStream(sock.getInputStream());

                      out.writeObject("Hello");
                      out.flush();
                      
                      String response = (String)in.readObject();
                      helloLabel.setText("Response from client: " + response);
                      
                      int pId = (Integer)in.readObject();
                      
                      dir = System.getProperty("user.dir");
                      dir += "/Client" +Integer.toString(pId);
                      pr = new Peer(pId, dir );
                      new Thread(){
                  		public void run(){
                  			try {
              					pr.server();
              				} catch (IOException e) {
              					e.printStackTrace();
              				}
                  		}
                  	}.start();
                  	
                  	new Thread(){
                  		public void run(){
                  			try {
              					pr.income();
              				} catch (IOException e) {
              					e.printStackTrace();
              				}
                  		}
                  	}.start();
                  while(pr.getPort()==0) {
                  	Thread.sleep(100);
                  }	
                  out.writeObject(pr.getPort());
                  out.flush();
                      
                      
                      
                      

                      out.writeObject(files);
                      out.flush();
                      System.out.println("files send");

                      
                  	

                      File file = new File(dir);
                      if(file.mkdirs()) {
                      	System.out.println("File succesfully created");
                      }
                      
                      for (File f : files) {
                      	misc.copyFiles(f.getAbsolutePath(), dir+"/"+f.getName());
                      }
                      
                      

                      
                      
                    }
                    
                    
                
                } catch (IOException | ClassNotFoundException e) {
                	
                    e.printStackTrace();
                } catch (InterruptedException e) {
					
					e.printStackTrace();
				}
            }
        });
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
//                    String search = "Search";
//                    String fileName = SearchFileField.getText();
//                    
//
//                    out.writeObject(search);
//                    out.flush();
//                    System.out.println("Sent");
//
//                    out.writeObject(fileName);
//                    out.flush();
//                    System.out.println("Sent fileName");
                	downloadButton.setEnabled(true);
                	  List result = new ArrayList();
                      String search = "Search";
                      String fileName = SearchFileField.getText();
                      int i = 0;

                      out.writeObject(search);
                      out.flush();
                      System.out.println("Sent");

                      out.writeObject(fileName);
                      out.flush();
                      System.out.println("Sent fileName");

                      result = (List) in.readObject();

                      String[] p = result.toString().split("], ", 0);
                      for(int j = 0; j< p.length; j ++) {
                          p[j] = p[j].replaceAll("]", "");
                          p[j] = p[j].replaceAll("\\[","");
                      }
                      for (int j = 0; j < p.length; j++) {
                          System.out.println("Element:");
                          System.out.println(p[j]);
                          listModel.insertElementAt(p[j],j);
                      }
                    
                    
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


            }
        });
        
        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
            	String filename = SearchFileField.getText();
            	String filename1 = jl.getSelectedValue().toString();
                String[] splittedFileName = filename1.toString().split(", ", 0);
                System.out.println(splittedFileName[splittedFileName.length - 1]);
                splittedFileName[splittedFileName.length-1] = splittedFileName[splittedFileName.length-1].replaceAll("\n", "");
                int portnum = Integer.parseInt(splittedFileName[splittedFileName.length - 1]);
            	if(filename.isEmpty()) {
					try {
						errorLabel.setText("Please provide filename");
						out.writeObject("empty");
						out.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            		System.out.println("Please provide a filename");
            	}else {
            	
	               
	            	
	                try {
	                	out.writeObject("download");
						out.flush();
	                	Socket sock2 = new Socket("localhost", portnum );
	                    filename = filename + "." + splittedFileName[0];
	                    
	                	int bytesRead;
	                    int current = 0;
	                    FileOutputStream fos = null;
	                    BufferedOutputStream bos = null;
	                    
	                    ObjectOutputStream out1 = new ObjectOutputStream(sock2.getOutputStream());
	                    
	                    try {
	                    	

	                      out1.writeObject(filename);
	                      out1.flush();
	  		        
	                    	
	                    	
	                      byte [] mybytearray  = new byte [600000];
	                      InputStream is = sock2.getInputStream();
	
	                      bos = new BufferedOutputStream(new FileOutputStream(dir+"/"+filename));
	                      
	                      
	                      bytesRead = is.read(mybytearray,0,mybytearray.length);
	                      current = bytesRead;
	                      bos.write(mybytearray, 0 , current);
	                      bos.flush();
	                      
	                      do {
	                         bytesRead = is.read(mybytearray, 0, mybytearray.length);
	                         bos.write(mybytearray, 0 , bytesRead);
		                     bos.flush();
	                      } while(bytesRead > -1);

	                      
	                    }
	                    finally {
	                      if (fos != null) fos.close();
	                      if (bos != null) bos.close();
	                      if (sock2 != null) sock2.close();
	                    }
	                	
	                	
	                	
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	
	
	            }
            }
        });
        
        
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    out.writeObject("BYE!!");
                    out.flush();

                    System.out.println(pr.getPort());
                    out.writeObject(pr.getPort());

                    out.flush();
                    sock.close();
                   System.exit(0);
                    
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        
        
    }



    public static void main(String args[]) throws IOException {
       Client window = new Client();
       window.setVisible(true);
    }
}
