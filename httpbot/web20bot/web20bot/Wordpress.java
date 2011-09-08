package web20bot;

import java.util.Calendar;

import http.*;

public class Wordpress {

	private String address = null;
	private FormHandler form = null;

	public Wordpress (String address) {
		if (!address.startsWith("http://")) {
			address = "http://" + address;
		}
		if (!address.endsWith("/")) {
			address += "/";
		}
		this.address = address;
		form = new FormHandler (FormHandler.Method.GET);
	}
	
	public void login (String username, String password, ProxyData proxy) {
		form.setMethod (FormHandler.Method.GET);
		String code = new StreamReader(form.submitRequest(address + "wp-admin/", proxy)).read();
		
		String submit = Parser.extract(code, "name=\"wp-submit\" id=\"wp-submit\" class=\"button-primary\" value=\"", "\" tabindex");
		String redirect = Parser.extract(code, "name=\"redirect_to\" value=\"", "\" />");
		String testcookie = Parser.extract(code, "name=\"testcookie\" value=\"", "\" />");
		
		System.out.println(submit);
		System.out.println(redirect);
		System.out.println(testcookie);
		
		form.setMethod(FormHandler.Method.POST);
		form.addPair("log", username);
		form.addPair("pwd", password);
		form.addPair("wp-submit", submit);
		form.addPair("redirect_to", redirect);
		form.addPair("testcookie", testcookie);

		form.submitRequest(form.getHttpConnection().getConnection().getURL()+"", proxy);
		
		form.setMethod(FormHandler.Method.GET);
		form.submitRequest(address+"wp-admin/", proxy);
		
		/*String redirect = Parser.extract(code, "<input type=\"hidden\" name=\"redirect_to\" value=\"", "\" />");
		String testcookie = Parser.extract(code, "<input type=\"hidden\" name=\"testcookie\" value=\"", "\" />");
		
		form.submitRequest(address+"wp-admin/images/button-grad-active.png", proxy);
		
		form.setMethod(FormHandler.Method.POST);
		form.addPair("log", username);
		form.addPair("pwd", password);
		form.addPair("wp-submit", "Log In");
		form.addPair("redirect_to", redirect);
		form.addPair("testcookie", testcookie);
		
		form.submitRequest(address + "wp-login.php", proxy);
		
		form.setMethod(FormHandler.Method.GET);
		form.submitRequest(address+"wp-admin/", proxy);*/
	}
	
	public void postArticle (ProxyData proxy, String postTitle, String postText) {
		String category = "New Category Name";
		/*
			Any other could be choosen, depending on the desired category
		*/
		
		String postFormat = "0";
		/*
			Choose from one of the following:
				aside
				link
				gallery
				status
				quote
				image
		*/
	
		form.setMethod(FormHandler.Method.GET);
		
		form.submitRequest(address+"wp-admin/edit.php", proxy);
		String code = new StreamReader(form.submitRequest(address+"wp-admin/post-new.php", proxy)).read();
		form.setMethod(FormHandler.Method.POST);
		
		form.addPair("_wpnonce", formExtract(code, "_wpnonce", "\""));
		form.addPair("_wp_http_referer", formExtract(code, "", "_wp_http_referer", "\""));
		form.addPair("user_ID", formExtract(code, "user-id", "user_ID", "\""));
		form.addPair("action", formExtract(code, "hiddenaction", "action", "\""));
		form.addPair("originalaction", formExtract(code, "originalaction", "\""));
		form.addPair("post_author", formExtract(code, "post_author", "\""));
		form.addPair("post_type", "post");
		form.addPair("original_post_status", formExtract(code, "original_post_status", "\""));
		form.addPair("referredby", formExtract(code, "referredby", "\""));
		form.addPair("_wp_original_http_referer", formExtract(code, "", "_wp_original_http_referer", "\""));
		form.addPair("auto_draft", formExtract(code, "auto_draft", "'"));
		form.addPair("post_ID", formExtract(code, "post_ID", "'"));
		form.addPair("autosavenonce", formExtract(code, "autosavenonce", "\""));
		form.addPair("meta-box-order-nonce", formExtract(code, "meta-box-order-nonce", "\""));
		form.addPair("closedpostboxesnonce", formExtract(code, "closedpostboxesnonce", "\""));
		form.addPair("wp-preview", formExtract(code, "wp-preview", "\""));
		form.addPair("hidden_post_status", formExtract(code, "hidden_post_status", "\""));
		form.addPair("post_status", formExtract(code, "post_status", "'"));
		form.addPair("hidden_post_password", Parser.extract(code, "id=\"hidden-post-password\" value=\"", "\" />"));
		form.addPair("hidden_post_visibility", Parser.extract(code, "id=\"hidden-post-visibility\" value=\"", "\" />"));
		form.addPair("visibility", "public");
		form.addPair("post_password", "");
		
		String[] date = getDate();
		
		form.addPair("mm", date[0]);
		form.addPair("jj", date[1]);
		form.addPair("aa", date[2]);
		form.addPair("hh", date[3]);
		form.addPair("mn", date[4]);
		form.addPair("ss", date[5]);
		form.addPair("hidden_mm", date[0]);
		form.addPair("cur_mm", date[0]);
		form.addPair("hidden_jj", date[1]);
		form.addPair("cur_jj", date[1]);
		form.addPair("hidden_aa", date[2]);
		form.addPair("cur_aa", date[2]);
		form.addPair("hidden_hh", date[3]);
		form.addPair("cur_hh", date[3]);
		form.addPair("hidden_mn", date[4]);
		form.addPair("cur_mn", date[4]);
		form.addPair("original_publish", Parser.extract(code, "id=\"original_publish\" value=\"", "\" />"));
		form.addPair("publish", Parser.extract(code, "id=\"publish\" class=\"button-primary\" value=\"", "\" "));
		form.addPair("post_format", postFormat);
		form.addPair("post_category%5B%5D", Parser.extract(code, "name='post_category[]' value='", "' />"));
		form.addPair("newcategory", category);
		form.addPair("newcategory_parent", "-1");
		form.addPair("_ajax_nonce-add-category", formExtract(code, "", "_ajax_nonce-add-category", "\""));
		form.addPair("tax_input%5Bpost_tag%5D", "");
		form.addPair("newtag%5Bpost_tag%5D", "");
		form.addPair("post_title", postTitle);
		form.addPair("samplepermalinknonce", formExtract(code, "", "samplepermalinknonce" , "\""));
		form.addPair("content", postText);
		form.addPair("excerpt", "");
		form.addPair("trackback_url", Parser.extract(code, "id=\"trackback_url\" class=\"code\" tabindex=\"7\" value=\"", "\" />"));
		form.addPair("metakeyinput", Parser.extract(code, "name=\"metakeyinput\" tabindex=\"7\" value=\"", "\" />"));
		form.addPair("metavalue", "");
		form.addPair("_ajax_nonce-add-meta", formExtract(code, "", "_ajax_nonce-add-meta", "\""));
		form.addPair("advanced_view", Parser.extract(code, "name=\"advanced_view\" type=\"hidden\" value=\"", "\" />"));
		form.addPair("comment_status", "open");
		form.addPair("ping_status", "open");
		form.addPair("post_name", "");
		form.addPair("post_author_override", Parser.extract(code, "id='post_author_override' class=''><option value='", "' "));
	}
	
	private String formExtract(String haystack, String nameId, String quote) {
		return (Parser.extract(haystack, "id=" + quote + nameId + quote + 
				" name=" + quote + nameId + quote + " value=" + quote, quote + " />"));
	}
	
	private String formExtract(String haystack, String id, String name, String quote) {
		String seperator = "";
		if ((id!=null) && (id!="")) {
			id = "id=" + quote + id + quote;
		}
		else {
			id = "";
		}
		if ((name!=null) && (name!="")) {
			name = "name=" + quote + name + quote;
		}
		else {
			name = "";
		}
		if ((id!="") && (name!="")) {
			seperator = " ";
		}
		return(Parser.extract(haystack, id + seperator + name + " value=" + quote, quote + " />"));	
	}
	
	private String[] getDate() {
		String date[] = new String[6];
	 	Calendar cal = Calendar.getInstance();
		
		date[0] = formatDate(cal.get(Calendar.MONTH) + 1);
		date[1] = formatDate(cal.get(Calendar.DAY_OF_MONTH));
		date[2] = formatDate(cal.get(Calendar.YEAR));
		date[3] = formatDate(cal.get(Calendar.HOUR));
		date[4] = formatDate(cal.get(Calendar.MINUTE));
		date[5] = formatDate(cal.get(Calendar.SECOND));
		return date;
	}
	
	private String formatDate(int date) {
		if (date<10) {
			return "0"+date;
		}
		return date+"";
	}
}
