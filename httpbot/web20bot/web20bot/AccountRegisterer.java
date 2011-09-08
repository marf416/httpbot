package web20bot;

public class AccountRegisterer {

	public static void main(String[] args) {
		//ProxyData proxy = new ProxyData("46.137.128.202", 3128, null, null);
		//new BlogsplashOrg().register("muffleed1987", "agiixe123", "pbruner92@yahoo.com", null);
		
		
		Wordpress wp =  new Wordpress("muffleed1987sblog.blogsplash.org");
		
		wp.login("muffleed1987", "jfzHLg8QXj3h", null);
		
		
		//wp.postArticle(null, "Automated", "This is supposed <b>to</b> work");
		//new CookieContainer().add("wordpress_cbe81e2f3a2bd8136c13443ad70aab9e=+; expires=Sun, 05-Sep-2010 12:41:09 GMT; path=/wp-admin; domain=.blogsplash.org");
		//System.out.println(new URL("http://www.vogella.de/articles/JavaRegularExpressions/article.html").getHost());
	}
}
