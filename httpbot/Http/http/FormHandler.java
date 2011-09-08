package http;

import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Provides interfacemethods to send GET and POST requests via HTTP to fill forms for example
 * 
 * @author Marvin
 * 
 * @version 1.00
 */
public class FormHandler {

	public static enum Method {POST, GET};
	private Method method = null;
	private List<String> attributs = new LinkedList<String>();
	private List<String> values = new LinkedList<String>();
	private final static String ENCODING = "UTF-8";
	private HttpURLConnection urlcon = null;
	private HttpConnection httpcon = null;
	private CookieContainer cookieContainer = new CookieContainer();
	
	/**
	 * Creates a new FormHandler to send HTTP requests
	 * 
	 * @param method - Either Method.POST or Method.GET depanding on your desired requesttype
	 * @param urlcon - The connection to send the request to
	 */
	public FormHandler (Method method) {
		this.method = method;
	}
	
	public void setMethod(Method m) {
		method = m;
	}
	
	public Method getMethod() {
		return method;
	}
	
	public URLConnection getURLConnection() {
		return urlcon;
	}
	
	public HttpConnection getHttpConnection() {
		return httpcon;
	}
	
	public void clear () {
		attributs.clear();
		values.clear();
	}
	
	/**
	 * Copys a given List object into a new List
	 * 
	 * @param source - The List to copy
	 * @return A List object containing the same data as the given List
	 */
	@SuppressWarnings("null")
	private List<String> stringListCopy(List<String> source) {
		List<String> copied = null;
		for (int i=0; i<=source.size(); i++) {
			copied.add(source.get(i));
		}
		return copied;
	}
	
	/**
	 *  Encodes the parameters of the request to URL format in UTF-8
	 * 
	 * @param text - The String to encode
	 * 
	 * @return The encoded String
	 */
	private String encode (String text) {
		try {
			return URLEncoder.encode(text, ENCODING);
		} 
		catch (UnsupportedEncodingException e) {
			return text;
		}
	}
	
	/**
	 * Add a pair of attribut and value to send along the request
	 * 
	 * @param attribut - The fieldname in the request
	 * @param value - The value of the field attribut
	 */
	public void addPair (String attribut, String value) {
		attributs.add(attribut);
		values.add(value);
	}
	
	/**
	 * Add a pair of attribut and value to send along the request
	 * 
	 * @param pair - Tupel of Strings where pair.first() is the fieldname and pair.second()
	 * 					is the value for the Request
	 */
	public void addPair (Tupel pair) {
		this.addPair((String)pair.first(), (String)pair.second());
	}
	
	/**
	 * Delete a pair of attribut and value already stored for the request
	 * 
	 * @param attribut - Attribut to delete
	 * @param value - Value to delete
	 */
	public void deletePair (String attribut, String value) {
		deletePair (getPairIndex(attribut, value));
	}

	/**
	 * Delete a pair of attribut and value already stored for the request
	 * 
	 * @param pair - Tupel where pair.first() is the attribut and pair.second() is the value to delete
	 */
	public void deletePair (Tupel pair) {
		this.deletePair((String)pair.first(),(String)pair.second());
	}
	
	/**
	 * Delete a pair of attribut and value already stored for the request
	 * 
	 * @param index - Index of the pair that should be deleted
	 */
	public void deletePair (int index) {
		if ((index>0) && (index<values.size())) {
			attributs.remove(index);
			values.remove(index);
		}
	}
	
	/**
	 * Finds all occurrences off a given keyword in a List and saves these positions
	 * 
	 * @param needle - The String to search for inside the List
	 * @param haystack - The List where needle should be found
	 * 
	 * @return A List of integers, containing all positions off needle in haystack
	 */
	@SuppressWarnings("null")
	private List<Integer> getIndizes(String needle, List<String> haystack) {
		List<Integer> finds = null;
		List<String> temporary = this.stringListCopy(haystack);
		List<String> storage = null;
		while (!temporary.isEmpty()) {
			if (temporary.get(0) == needle) {
				finds.add(storage.size()+1);
			}
			storage.add(temporary.get(0));
			temporary.remove(0);
		}
		return finds;
	}
	
	/**
	 * Finds a Tupel where either attribut or value matches the given searchstring (case insensitiv)
	 * 
	 * @param needle - The string that is searched for in attributs and methods
	 * @param occurrence - If there are more than 1 matches specify which should be returned
	 * 
	 * @return A Tupel of Strings (attribut and value) that matches the given searchterms or NULL 
	 *			if there isn't any
	 */
	@SuppressWarnings("null")
	public Tupel getPair (String needle, int occurrence) {
		List<String> temporary = this.stringListCopy(attributs);
		List<String> storage = null;
		int att = -1;
		int count = 0;
		while (!temporary.isEmpty()) {
			if (temporary.get(0) == needle) {
				count++;
				if (count==occurrence) {
					att = storage.size()+1;
					break;
				}
			}
			storage.add(temporary.get(0));
			temporary.remove(0);
		}
		
		temporary.clear();
		storage.clear();
		
		temporary = this.stringListCopy(values);
		int val = -1;
		count = 0;
		while (!temporary.isEmpty()) {
			if (temporary.get(0) == needle) {
				count++;
				if (count==occurrence) {
					val = storage.size()+1;
					break;
				}
			}
			storage.add(temporary.get(0));
			temporary.remove(0);
		}
		
		if ((att>-1) || (val>-1)) {
			if (att <= val) {
				return new Tupel(attributs.get(att), values.get(att));
			}
			else if (val <= att) {
				return new Tupel(attributs.get(val), values.get(val));
			}
		}
		return null;
		
	}
	
	/**
	 * Finds the Tupel at a given position
	 * 
	 * @param index - The position of the Tupel that is searched for
	 * 
	 * @return A Tupel of Strings (attribut and value) on the given position, or NULL if
	 * 			index > length of the FormHandler object
	 */
	public Tupel getPair (int index) {
		return new Tupel(attributs.get(index), values.get(index));
	}
	
	/**
	 * Determines the position of a pair of attribut and value
	 * 
	 * @param attribut - The searched attribut
	 * @param value - The searched value
	 * 
	 * @return Position of the given pair or -1 if no such pair exists
	 */
	public int getPairIndex (String attribut, String value) {
		List<Integer> attIndex = getIndizes(attribut, attributs);
		List<Integer> valIndex = getIndizes(value, values);
		for (int i=0; i<=attIndex.size(); i++) {
			for (int j=0; j<=valIndex.size(); j++) {
				if (attributs.get(attIndex.get(i)) == values.get(valIndex.get(j))) {
					return attIndex.get(i);
				}
			}
		}
		return -1;
	}
	
	/**
	 * Determines the position of a pair of attribut and value stored as Tupel
	 * 
	 * @param pair - Tupel of Strings where pair.first() is the searched attribut and pair.second()
	 *					is the searched value
	 * 
	 * @return Position of the given pair or NULL if no such pair exists
	 */
	public int getPairIndex (Tupel pair) {
		return getPairIndex((String) pair.first(), (String) pair.second());
	}
	
	/**
	 * Determines the position of a stored pair of attribut and value, where one of those matches
	 *		the search arguments
	 * 
	 * @param needle - The searched String that matches either the attribut or the value of the
	 *					searched pair (case insensitiv)
	 * @param occurrence - If there are more pairs than one matching the given search String
							specify which one is returned
	 * 
	 * @return Position of the pair where needle first occurres as attribut or value or -1 if no 
				such pair exists
	 */
	public int getPairIndex (String needle, int occurrence) {
		List<Integer> att = getIndizes(needle, attributs);
		List<Integer> val = getIndizes(needle, values);
		if (att.get(0)<=val.get(0)) {
			return att.get(0);
		}
		else if (val.get(0)<=att.get(0)) {
			return val.get(0);
		}
		return -1;
	}
	
	/**
	 * Getting the attributs already set for the request
	 * 
	 * @return List of attributs that stores the given attributs for the request
	 */
	public List<String> getAttributs () {
		return attributs;
	}
	
	/**
	 * Getting the values already set for the request
	 * 
	 * @return List of values that stores the given values for the request
	 */
	public List<String> getValues () {
		return values;
	}
	
	/**
	 * Counting the attribut-value pairs given as request parameters
	 * 
	 * @return Number of requestattributs/requestvalues (which is the same amount)
	 */
	public int length () {
		return attributs.size();
	}
	
	public InputStream submitRequest (String address) {
		return submitRequest(address, null, 0, null, true);
	}
	public InputStream submitRequest (String address, ProxyData proxy) {
		return submitRequest(address, proxy, 0, null, true);
	}
	public InputStream submitRequest (String address, int profil) {
		return submitRequest(address, null, profil, null, true);
	}
	public InputStream submitRequest (String address, Tupel[] headerdata) {
		return submitRequest(address, null, 0, headerdata, true);
	}
	public InputStream submitRequest (String address, boolean setCookies) {
		return submitRequest(address, null, 0, null, setCookies);
	}
	public InputStream submitRequest (String address, ProxyData proxy, int profil) {
		return submitRequest (address, proxy, profil, null, true);
	}
	public InputStream submitRequest (String address, ProxyData proxy, Tupel[] headerdata) {
		return submitRequest (address, proxy, 0, headerdata, true);
	}
	public InputStream submitRequest (String address, ProxyData proxy, boolean setCookies) {
		return submitRequest (address, proxy, 0, null, setCookies);
	}
	public InputStream submitRequest (String address, int profil, Tupel[] headerdata) {
		return submitRequest (address, null, profil, headerdata, true);
	}	
	public InputStream submitRequest (String address, int profil, boolean setCookies) {
		return submitRequest (address, null, profil, null, setCookies);
	}
	public InputStream submitRequest (String address, Tupel[] headerdata, boolean setCookies) {
		return submitRequest (address, null, 0, headerdata, setCookies);
	}
	public InputStream submitRequest (String address, ProxyData proxy, int profil, Tupel[] headerdata) {
		return submitRequest(address, proxy, profil, headerdata, true);
	}
	public InputStream submitRequest (String address, ProxyData proxy, int profil, boolean setCookies) {
		return submitRequest(address, proxy, profil, null, setCookies);
	}
	public InputStream submitRequest (String address, ProxyData proxy, Tupel[] headerdata, boolean setCookies) {
		return submitRequest(address, proxy, 0, headerdata, setCookies);
	}
	public InputStream submitRequest (String address, int profil, Tupel[] headerdata, boolean setCookies) {
		return submitRequest(address, null, profil, headerdata, setCookies);
	}

	public InputStream submitRequest (String address, ProxyData proxy, int profil, Tupel[] headerdata, boolean setCookies) {
		if (method == Method.GET) {
			return submitGet(address, proxy, profil, headerdata, setCookies, true);
		}
		return submitPost(address, proxy, profil, headerdata, setCookies, true);
	}
	
	/**
	 * Merges attributs and values to fit the representation needed for GET or POST request
	 * 
	 * @return
	 */
	private String buildArgumentString ()
	{
		if (!attributs.isEmpty()) {
			String result = null;
			if (method == Method.GET) {
				result = "?" + encode((String)getPair(0).first())+"="+encode((String)getPair(0).second());
			}
			else {
				result = encode((String)getPair(0).first())+"="+encode((String)getPair(0).second());
			}
			for (int i=1; i<attributs.size(); i++) {
				result += "&" + encode((String)getPair(i).first())+"="+encode((String)getPair(i).second());
			}
			return result;
		}
		return "";
	}
	
	private String initiateConnection(String address, ProxyData proxy, int profil, boolean setCookies) {
		String ref = null;
		if (httpcon!=null) {
			ref = httpcon.getAddress();
		}
		httpcon = new HttpConnection(address, proxy, profil);
		cookieContainer = httpcon.addCookies(address, cookieContainer);
		urlcon = httpcon.getConnection();
		return ref;
	}
	
	private void applyHeader(Tupel[] headerdata, String ref) {
		boolean hasref = false;
		if (headerdata!=null) {
			for (int i=0; i<headerdata.length; i++) {
				urlcon.addRequestProperty((String)headerdata[i].first(), (String)headerdata[i].second());
				if ((String)headerdata[i].first() == "Referer") {
					hasref = true;
				}
			}
		}
		
		if ((hasref!=true) && (ref!=null) && (ref!="")) {
			urlcon.addRequestProperty("Referer", ref);
		}
	}
	
	private String checkRedirect () {
		int stat = 400;
		try {
			stat = urlcon.getResponseCode();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		if ((stat>=300) && (stat<=307) && (stat!=306) && (stat!=HttpURLConnection.HTTP_NOT_MODIFIED)) {
			return urlcon.getHeaderField("Location");
		}
		return null;
	}
	
	private InputStream submitGet (String address, ProxyData proxy, int profil, Tupel[] headerdata, boolean setCookies, boolean handleRedirect) {
		String args = buildArgumentString();
		String ref = initiateConnection((address+args), proxy, profil, setCookies);
		
		System.out.println("GET: "+address);
		
		urlcon.setDoOutput(false);
		applyHeader(headerdata, ref);
		String redirect = checkRedirect();
		cookieContainer.add(urlcon.getHeaderFields().get("Set-Cookie"));
		
		if ((redirect!=null) && (redirect!="") && (handleRedirect==true)) {
			this.clear();
			return submitGet(redirect, proxy, profil, headerdata, setCookies, true);
		}
		return getStream();
	}
	
	private InputStream submitPost (String address, ProxyData proxy, int profil, Tupel[] headerdata, boolean setCookies, boolean handleRedirect) {
		String ref = initiateConnection(address, proxy, profil, setCookies);
		
		System.out.println("POST: "+address);
		
		urlcon.setDoOutput(true);
		urlcon.setRequestProperty("Accept-Charset", ENCODING);
		urlcon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + ENCODING);
		applyHeader(headerdata, ref);
		sendPostData();
		String redirect = checkRedirect();
		cookieContainer.add(urlcon.getHeaderFields().get("Set-Cookie"));
		
		if ((redirect!=null) && (redirect!="") && (handleRedirect==true)) {
			this.clear();
			return submitGet(redirect, proxy, profil, headerdata, setCookies, true);
		}
		return getStream();
	}
	
	private InputStream getStream() {
		try {
			return urlcon.getInputStream();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void sendPostData() {
		OutputStream output = null;
		try {
			output = urlcon.getOutputStream();
			String query = buildArgumentString();
			output.write(query.getBytes(ENCODING));
			output.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
