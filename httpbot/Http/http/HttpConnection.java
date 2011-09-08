package http;

import java.io.IOException;
import java.net.*;
import java.util.List;

/**
 * Creates a simple HttpConnection and allows to choose between given sets of HTTP headers
 * 
 * @author Marvin Huber
 * 
 * @version 1.00
 */
public class HttpConnection {

	private HttpURLConnection connection = null;
	private ProxyData proxy = null;
	
	/**
	 * Opens a simple URLConnection w/ or w/o Proxy
	 * 
	 * @param profil - Lets you choose between different headerprofiles given in the setProfil-Method
	 * @param url - Server to establish a connection to
	 * @param proxy - Proxy to use, if you don't want to use a proxy assign NULL
	 */
	public HttpConnection (String url, ProxyData proxy, int profil) {
		try {
			this.proxy = proxy;
			if (proxy==null) {
				connection = (HttpURLConnection) new URL(url).openConnection();
			}
			else {
				connection = (HttpURLConnection) new URL(url).openConnection(proxy.getProxy());
			}
			connection.setInstanceFollowRedirects(false);
			setProfil(connection, profil);
		} 
		catch (MalformedURLException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getAddress() {
		return connection.getURL()+"";
	}
	
	public void setProxy (ProxyData proxy) {
		this.proxy = proxy;
	}
	
	/**
	 * Lets other objects use the connection
	 * 
	 * @return connection - Returns the already established connection with its profil
	 */
	public HttpURLConnection getConnection () {
		return connection;
	}
	
	/**
	 * Provides different sets of preset headers
	 * 
	 * @param u - Connection to apply the profil to
	 * @param profil - Number to choose between different headerprofiles
	 */
	public HttpURLConnection setProfil (HttpURLConnection con, int profil) {
		if (profil==0) {
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:6.0) Gecko/20100101 Firefox/6.0");
			if (proxy!=null) {
				if ((proxy.getUsername()!=null) && (proxy.getPassword()!=null)) {
					connection.setRequestProperty( "Proxy-Authorization","Basic " +
					Base64.encode(proxy.getUsername() + ":" + proxy.getPassword()));
				}
			}
		}
		return null;
	}
	
	public List<String> getCookies() {
		return connection.getHeaderFields().get("Set-Cookie");
	}
	
	public CookieContainer addCookies(String url, CookieContainer cookieContainer) {
		String cookiestring = "";
		List<Cookie> cookies = cookieContainer.getCookieList(url);
		
		if (cookies.size()>1) {
			for (int i=0; i<(cookies.size()-1); i++) {
				cookiestring += cookies.get(i).getName() + "=" + cookies.get(i).getValue() + "; ";
			}
		}
		if (cookies.size()>0) {
			int i = cookies.size()-1;
			cookiestring += cookies.get(i).getName() + "=" + cookies.get(i).getValue();
		}

		connection.setRequestProperty("Cookie", cookiestring);
		return cookieContainer;
	}
}
