package apogen;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import utils.UtilsCrawler;

import com.crawljax.core.CrawljaxRunner;
import com.crawljax.core.configuration.CrawljaxConfiguration;
import com.crawljax.core.configuration.CrawljaxConfiguration.CrawljaxConfigurationBuilder;
import com.crawljax.plugins.crawloverview.CrawlOverview;

public class Crawler {

	protected static String url; 
	protected static String out_dir;
	protected static Properties configFile;
	protected static Properties appFile;
	protected static String diff_dir;

	/**
	 * set the appConfigFile
	 * @param appConfigFile
	 */
	public static void setAppConfigFile(Properties appConfigFile) {
		Crawler.appFile = appConfigFile;
	}
	
	/**
	 * Initialise and Run the crawler
	 */
	public static void crawl() {
		
		CrawljaxConfigurationBuilder b = Crawler.initCrawljax();
		Crawler.runCrawljax(b);
		
	}

	/**
	 * initializes the properties and the crawler
	 * @return CrawljaxConfigurationBuilder
	 */
	public static CrawljaxConfigurationBuilder initCrawljax(){
		
		System.out.println("[LOG] CRAWLER SETUP ... ");
		
		configFile = new Properties();
		
		try {
			configFile.load(Crawler.class.getClassLoader().getResourceAsStream("config.properties"));
			
			if(appFile == null){
				appFile = new Properties();
				appFile.load(Crawler.class.getClassLoader().getResourceAsStream("app.properties"));
			}
			
			url = appFile.getProperty("URL");
			
			out_dir = configFile.getProperty("OUT_DIR");
			
			//FileUtils.deleteDirectory(new File(out_dir));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		CrawljaxConfigurationBuilder builder = CrawljaxConfiguration.builderFor(url);
		
		try {
			UtilsCrawler.myCrawlRules(builder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		UtilsCrawler.getInputSpecification();
			
		builder.addPlugin(new CrawlOverview());
		
		System.out.println("[LOG] CRAWLER SETUP COMPLETED SUCCESSFULLY");
		
		return builder;
	}
	
	
	/**
	 * Run Crawljax with the configurations in input
	 * @param CrawljaxConfigurationBuilder builder
	 */
	static void runCrawljax(CrawljaxConfigurationBuilder builder) {
		System.out.println("[LOG] STARTED CRAWLING OF: " + url);
		
		CrawljaxRunner crawljax = new CrawljaxRunner(builder.build());
		crawljax.call();
		
		System.out.println("[LOG] CRAWLING ENDED WITH STATUS: " + crawljax.getReason());
		System.out.println("[LOG] CRAWLING OF " + url + " FINISHED");
	}
	
}

