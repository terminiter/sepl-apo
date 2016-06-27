package apogen;

import gui.APOGEN;

public class ApogenMain {

	/**
	 * Runs the Automatic Page Object Generator
	 * @throws Exception 
	 * 
	 * @throws JSONException 
	 */
	 public static void main(String args[]) throws Exception {
		 new ApogenMain().run();
	 }
	
	
	public void run() throws Exception {
		
            Crawler.crawl();
		
            StaticAnalyzer.start();
	
            CodeGenerator.run();
	
            System.exit(0); 
	}
     
}
