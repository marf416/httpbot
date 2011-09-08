package web20bot;

import java.io.InputStream;
import java.util.*;

import http.*;

public class Register {

	public Register() {
	
	}
	
	public void sendData (LinkedList<Tupel> data, String address, FormHandler.Method method) {
		FormHandler form = new FormHandler(method);
		for (int i=0; i<data.size(); i++) {
			form.addPair(data.get(i));
		}
		form.submitRequest(address);
	}
	
	public String password(int len, boolean lowercase, boolean uppercase, boolean numbers, String signs) {
		String low = "";
		String up = "";
		String num = "";
		if (!lowercase==false) {
			low = "abcdefghijklmnopqrstuvwxyz";
		}
		if (!uppercase==false) {
			up ="ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		}
		if (!numbers==false) {
			num = "0123456789";
		}
		if (signs == null) {
			signs = "";
		}
		String random = low+up+num+signs;
		
		Random rand = new Random();
		String password = "";
		for (int i = 0; i<len; i++) {
			password += random.charAt(rand.nextInt((random.length())-1));
		}
		return password;
	}
	
	public static boolean grabRecaptcha (String address, String filepath, ProxyData proxy, InputStream is) {
		try {
			Tupel[] ref =  {new Tupel("Referer", address)};
		
			FormHandler form = new FormHandler(FormHandler.Method.GET);
			StreamReader reader = new StreamReader(is);
			String code = reader.read();
			String key = Parser.extract(code, "api.recaptcha.net/challenge?k=", "\"");
			
			reader = new StreamReader(form.submitRequest("http://api.recaptcha.net/challenge?k="+key, proxy, ref));
			code = reader.read();
			String key2 = Parser.extract(code, "challenge : '", "',");
			
			reader = new StreamReader(form.submitRequest("http://www.google.com/recaptcha/api/image?c="+key2, proxy, ref));
			reader.download(filepath);
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}
	
}
