package web20bot;

import java.io.InputStream;

import http.*;

public class BlogsplashOrg {

	public BlogsplashOrg () {
	
	}
	
	public void register (String username, String password, String eMail, ProxyData proxy) {
		FormHandler form = new FormHandler(FormHandler.Method.GET);
		InputStream is = null;
		String url = "http://blogsplash.org/";
		form.submitRequest(url, proxy);
		url = "http://blogsplash.org/wp-signup.php";
		is = form.submitRequest(url, proxy);
		
		String code = new StreamReader(is).read();
		String signupId =  Parser.extract(code, "name='signup_form_id' value='", "' />");
		String signupForm =  Parser.extract(code, "name=\"_signup_form\" value=\"", "\" />");
				
		form.setMethod(FormHandler.Method.POST);
		form.addPair("stage", "validate-user-signup");
		form.addPair("signup_form_id", signupId);
		form.addPair("_signup_form", signupForm);
		form.addPair("user_name", username);
		form.addPair("user_email", eMail);
		form.addPair("signup_for", "blog");
		form.addPair("submit", "Next");
		is = form.submitRequest("http://blogsplash.org/wp-signup.php", proxy);
		form.clear();
		
		code = new StreamReader(is).read();
		signupId =  Parser.extract(code, "name='signup_form_id' value='", "' />");
		signupForm =  Parser.extract(code, "name=\"_signup_form\" value=\"", "\" />");
		
		form.setMethod(FormHandler.Method.POST);
		form.addPair("stage", "validate-blog-signup");
		form.addPair("user_name", username);
		form.addPair("user_email", eMail);
		form.addPair("signup_form_id", signupId);
		form.addPair("_signup_form", signupForm);
		form.addPair("blogname", username+"sblog");
		form.addPair("blog_title", username+"sblog");
		form.addPair("blog_public", "1");
		form.addPair("submit", "Signup");
		form.submitRequest("http://blogsplash.org/wp-signup.php", proxy);
	}
	
}
