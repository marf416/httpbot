package http;

import java.io.*;

import com.DeathByCaptcha.*;
import com.DeathByCaptcha.Exception;

/**
 * Uses APIs of different captchasolver companys to automatically solve captchas
 * 
 * @author Marvin Huber
 *
 * @version 1.00
 */
public class CaptchaSolver {
	
	private byte[] img = null;
	private Object cap = null;
	private Object connection = null;
	public static enum Solver {DEATHBYCAPTCHA};
	private Solver solver = null;
	
	/**
	 * Saves the filepath of the captcha that should be solved
	 * 
	 * @param filepath - Path of the file to decode
	 */
	public CaptchaSolver (String filepath) {
		File file = new File(filepath);
		try {
			img = getBytesFromFile(file);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
    public byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
    
        long length = file.length();
    
        if (length > Integer.MAX_VALUE) {
            // File is too large
        	return null;
        }
    
        byte[] bytes = new byte[(int)length];
    
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }
            is.close();
        return bytes;
    }
	
	/**
	 * Displays the captcha to the user and lets him solve it
	 * 
	 * @return The text on the image or NULL if the process got aborted
	 */
	public String solveByHand () {
		return null;
	}
	
	public void report () {
		if (solver==Solver.DEATHBYCAPTCHA) {
			Client cl = (Client)connection;
			Captcha ca = (Captcha)cap;
			try {
				cl.report(ca);
			} 
			catch (IOException e) {
				e.printStackTrace();
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * Uses the DeathByCaptcha API to solve the image loaded from imagepath
	 * 
	 * @return The text on the image or NULL if the image wasn't found
	 */
	public String deathByCaptcha () {
		if (img!=null) {
		    String username = "marf416";
		    String password = "ve8rDusa";
		    Captcha captcha = null;
		    Client client = (Client)new SocketClient(username, password);
		    try {
				double balance = client.getBalance();
				if (!(balance>0)) {
					//Not enough money
				}
				captcha = client.decode(img);
				cap = captcha;
				solver = Solver.DEATHBYCAPTCHA;
				connection = client;
	
		        if (captcha != null) {
		            /* The CAPTCHA was solved; captcha.id property holds its numeric ID,
		               and captcha.text holds its text. */
		            return captcha.text;
		        }
		    } 
		    catch (AccessDeniedException e) {
		        /* Access to DBC API denied, check your credentials and/or balance */
		    } 
		    catch (IOException e) {
				e.printStackTrace();
			} 
			catch (Exception e) {
				e.printStackTrace();
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * Uses the Decaptcher API to solve the image loaded from imagepath
	 * 
	 * @return The text on the image or NULL if the image wasn't found
	 */
	public String decaptcher () {
		return null;
	}
	
	/**
	 * Uses the AntiCaptcha API to solve the image loaded from imagepath
	 * 
	 * @return The text on the image or NULL if the image wasn't found
	 */
	public String antiCaptcha () {
		return null;
	}
	
	/**
	 * Uses the ExpertDecoders API to solve the image loaded from imagepath
	 * 
	 * @return The text on the image or NULL if the image wasn't found
	 */
	public String expertDecoders () {
		return null;
	}
}
