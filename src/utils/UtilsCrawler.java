package utils;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;

import abstractdt.StateVertexLevensteinEquals;
import apogen.Crawler;

import com.crawljax.browser.EmbeddedBrowser.BrowserType;
import com.crawljax.core.configuration.BrowserConfiguration;
import com.crawljax.core.configuration.CrawljaxConfiguration.CrawljaxConfigurationBuilder;
import com.crawljax.core.configuration.Form;
import com.crawljax.core.configuration.InputSpecification;
import com.crawljax.core.state.StateVertex;
import com.crawljax.core.state.StateVertexFactory;

/**
 * Utils class containing auxiliary and configuration methods
 * for the Crawler
 * @author tsigalko18
 *
 */
public class UtilsCrawler {
	
	/**
	 * set a custom StateVertexFactory for the builder Crawljax configuration
	 * @param builder
	 */
	public static void setCustomStateVertexFactory(CrawljaxConfigurationBuilder builder) {
		
		builder.setStateVertexFactory(new StateVertexFactory() {
			@Override
			public StateVertex newStateVertex(int id, String url, String name, String dom, String strippedDom) {
				return new StateVertexLevensteinEquals(id, url, name, dom, strippedDom);
			}
		});
	}
	
	/**
	 * Personalized list of Crawljax crawling rules
	 * @throws IOException 
	 */
	public static void myCrawlRules(CrawljaxConfigurationBuilder builder) throws IOException {
		
		Properties configFile = new Properties();
		
		try {
			
			configFile.load(Crawler.class.getClassLoader().getResourceAsStream("config.properties"));
			
			long timeoutEvent = Long.parseLong(configFile.getProperty("WAIT_TIME_AFTER_EVENT"));
			long timeoutReload = Long.parseLong(configFile.getProperty("WAIT_TIME_AFTER_RELOAD"));
			int numberOfBrowsers = Integer.parseInt(configFile.getProperty("NUMBER_OF_BROWSERS"));
			String out_dir = configFile.getProperty("OUT_DIR");
		
			builder.crawlRules().setInputSpec(getInputSpecification());
			//builder.crawlRules().setInputSpec(getPetClinicInputSpecification());
			
			builder.crawlRules().clickOnce(false);
			builder.crawlRules().insertRandomDataInInputForms(true);
			builder.crawlRules().clickElementsInRandomOrder(false); // it is default
			
			/** DEFAULT ELEMENTS
			click("a");
			click("button");
			click("input").withAttribute("type", "submit");
			click("input").withAttribute("type", "button");
			*/
			builder.crawlRules().clickDefaultElements();
			builder.crawlRules().crawlFrames(true);
			builder.crawlRules().crawlHiddenAnchors(true);
			
			// click these specific
			//builder.crawlRules().click("div");
			//builder.crawlRules().click("input").withAttribute("name", "new");
			//builder.crawlRules().click("a").withText("MAP");
			
			// but don't click these
			//builder.crawlRules().dontClick("div").withAttribute("id", "top");
			//builder.crawlRules().dontClick("div").withAttribute("id", "nav");
			builder.crawlRules().dontClick("a").withAttribute("href", "notes.htm");
			//builder.crawlRules().dontClick("a").withAttribute("href", "#");
			
			// set limits
			int crawl_depth = Integer.parseInt(configFile.getProperty("CRAWL_DEPTH"));
			long max_runtime = Long.parseLong(configFile.getProperty("MAX_RUNTIME"));
			int max_states = Integer.parseInt(configFile.getProperty("MAX_STATES"));
			
			if(crawl_depth != 0){
				builder.setMaximumDepth(crawl_depth);
			}
			else {
				builder.setUnlimitedCrawlDepth();
			}
			
			if(max_runtime != 0){
				builder.setMaximumRunTime(max_runtime, TimeUnit.MINUTES);
			}
			else {
				builder.setUnlimitedRuntime();
			}
			
			if(max_states != 0){
				builder.setMaximumStates(max_states);
			}
			else {
				builder.setUnlimitedStates();
			}
			
			// Set timeouts
			builder.crawlRules().waitAfterReloadUrl(timeoutReload, TimeUnit.MILLISECONDS);
			builder.crawlRules().waitAfterEvent(timeoutEvent, TimeUnit.MILLISECONDS);
			
			// set the number of browsers
			builder.setBrowserConfig(new BrowserConfiguration(BrowserType.FIREFOX, numberOfBrowsers));
			
			// This will generate a nice output in the output directory.
			File outFolder = new File(out_dir);
			if (outFolder.exists()) {
				FileUtils.deleteDirectory(outFolder);
			}
			builder.setOutputDirectory(outFolder);
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Input specification containing credentials
	 * The credentials are read from the app.properties file
	 * @return InputSpecification
	 */
	public static InputSpecification getInputSpecification() {
		
		Properties configFile = new Properties();
		InputSpecification input = new InputSpecification();
		
		try {
                    
                        // THESE CALLS MUST BE SUBSTITUTED WITH THE GUI VALUES
			
			configFile.load(Crawler.class.getClassLoader().getResourceAsStream("app.properties"));
			
			String user_field = configFile.getProperty("USERNAME_FIELD");
			String user_value = configFile.getProperty("USERNAME_VALUE");
			String password_field = configFile.getProperty("PASSWORD_FIELD");
			String password_value = configFile.getProperty("PASSWORD_VALUE");
			String submit_field = configFile.getProperty("SUBMIT_FIELD");
			String submit_text = configFile.getProperty("SUBMIT_TEXT");
			
			Form loginForm = new Form();
			loginForm.field(user_field).setValue(user_value);
			loginForm.field(password_field).setValue(password_value);
			input.setValuesInForm(loginForm).beforeClickElement(submit_field).withText(submit_text);
			
			/*
			String textarea_field = configFile.getProperty("TEXTAREA_FIELD");
			String textarea_text = configFile.getProperty("TEXTAREA_VALUE");
			String quickadd_field = configFile.getProperty("QUICKADD_FIELD");
			String quickadd_text = configFile.getProperty("QUICKADD_TEXT");
			
			Form textareaForm = new Form();
			textareaForm.field(textarea_field).setValue(textarea_text);
			input.setValuesInForm(textareaForm).beforeClickElement(quickadd_field).withText(quickadd_text);
			*/
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		return input;
	}
	
	/**
	 * Input specification containing credentials
	 * The credentials are read from the app.properties file
	 * @return InputSpecification
	 */
	public static InputSpecification getPetClinicInputSpecification() {
		
		Properties configFile = new Properties();
		InputSpecification input = new InputSpecification();
		
		try {
			
			configFile.load(Crawler.class.getClassLoader().getResourceAsStream("app.properties"));
			
			/*
			String firstname_field = configFile.getProperty("NEW_OWNER_FIRSTNAME");
			String firstname_value = configFile.getProperty("NEW_OWNER_FIRSTNAME_VALUE");
			String lastname_field = configFile.getProperty("NEW_OWNER_LASTNAME");
			String lastname_value = configFile.getProperty("NEW_OWNER_LASTNAME_VALUE");
			String address_field = configFile.getProperty("NEW_OWNER_ADDRESS");
			String address_value = configFile.getProperty("NEW_OWNER_ADDRESS_VALUE");
			String city_field = configFile.getProperty("NEW_OWNER_CITY");
			String city_value = configFile.getProperty("NEW_OWNER_CITY_VALUE");
			String telephone_field = configFile.getProperty("NEW_OWNER_TELEPHONE");
			String telephone_value = configFile.getProperty("NEW_OWNER_TELEPHONE_VALUE");
			String submit_field = configFile.getProperty("SUBMIT_FIELD");
			String submit_value = configFile.getProperty("SUBMIT_TEXT");
			
			Form newOwner = new Form();
			newOwner.field(firstname_field).setValue(firstname_value);
			newOwner.field(lastname_field).setValue(lastname_value);
			newOwner.field(address_field).setValue(address_value);
			newOwner.field(city_field).setValue(city_value);
			newOwner.field(telephone_field).setValue(telephone_value);
			input.setValuesInForm(newOwner).beforeClickElement(submit_field).withText(submit_value);
			*/
			
			String search_field = configFile.getProperty("SEARCH_FIELD");
			String search_value = configFile.getProperty("SEARCH_VALUE");
			String search_button_field = configFile.getProperty("SEARCH_BUTTON_FIELD");
			String search_button_value = configFile.getProperty("SEARCH_BUTTON_VALUE");
			
			Form search = new Form();
			search.field(search_field).setValue(search_value);
			input.setValuesInForm(search).beforeClickElement(search_button_field).withText(search_button_value);
			
			/*
			String pet_name_field = configFile.getProperty("PET_NAME_FIELD");
			String pet_name_value = configFile.getProperty("PET_NAME_VALUE");
			String pet_birthday_field = configFile.getProperty("PET_BIRTHDATE_FIELD");
			String pet_birthday_value = configFile.getProperty("PET_BIRTHDATE_VALUE");
			String pet_type_field = configFile.getProperty("PET_TYPE_FIELD");
			String pet_type_value = configFile.getProperty("PET_TYPE_VALUE");
			String pet_button_field = configFile.getProperty("PET_ADD_BUTTON_FIELD");
			String pet_button_value = configFile.getProperty("PET_ADD_BUTTON_VALUE");
			
			Form addPet = new Form();
			addPet.field(pet_name_field).setValue(pet_name_value);
			addPet.field(pet_birthday_field).setValue(pet_birthday_value);
			addPet.field(pet_type_field).setValue(pet_type_value);			
			input.setValuesInForm(addPet).beforeClickElement(pet_button_field).withText(pet_button_value);
			
			String visit_description_field = configFile.getProperty("VISIT_DESCRIPTION_FIELD");
			String visit_description_value = configFile.getProperty("VISIT_DESCRIPTION_VALUE");
			description
			*/
					
		} catch (IOException e) {
			e.printStackTrace();
		}

		return input;
	}
	
}
