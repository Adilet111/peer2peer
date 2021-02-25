package misc;
 
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class misc {

	public static void copyFiles(String source, String dest) {
		File src_file = new File(source);
		File dest_file = new File(dest);
		
		try (FileInputStream fis = new FileInputStream(src_file);
		     FileOutputStream fos = new FileOutputStream(dest_file)) {
		
		    byte[] buffer = new byte[1024];
		    int length;
		
		    while ((length = fis.read(buffer)) > 0) {
		
		        fos.write(buffer, 0, length);
		    }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int count = 0;
        while ((count = in.read(buffer)) != -1) {
            out.write(buffer, 0, count);
        }
    }
	
	
}
