package http;

import java.net.*;

public class ProxyData {

	private Proxy proxy = null;
	private String ip = null;
	private int port = 0;
	private String username = null;
	private String password = null;

	public ProxyData (String ip, int port, String username, String password) {
		proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
		this.ip = ip;
		this.port = port;
		this.username = username;
		this.password = password;
	}
	
	public void reconnect() {
		proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
	}
	
	public Proxy getProxy () {
		return proxy;
	}
	
	public String getIp () {
		return ip;
	}
	
	public int getPort () {
		return port;
	}
	
	public String getUsername () {
		return username;
	}
	
	public String getPassword () {
		return password;
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public void setUsername (String username) {
		this.username = username;
	}
	
	public void setPassword (String password) {
		this.password = password;
	}
	
}
