package apogen;

import abstractdt.State;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.xml.parsers.ParserConfigurationException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.xml.sax.SAXException;
import utils.UtilsClustering;
import utils.UtilsDataset;
import utils.UtilsStaticAnalyzer;

/**
 * This class is intended to analyze the output info provided by Crawljax and
 * create opportune State classes
 * 
 * @author Andrea Stocco
 *
 */
public class StaticAnalyzer {

	private static List<State> statesList;
	private static String gen_po_dir;
	private static String out_dir;
	private static String clustering;
	private static String threshold;
	private static String strategy;
	private static String doms_dir;
	private static String algorithm;
	private static String number_of_clusters;
	private static LinkedHashMap<Integer, LinkedList<String>> clustersMap;

	/**
	 * initializes the properties and runs the static analyzer
	 * 
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws ParseException
	 */
	public static void start() throws IOException,
			ParserConfigurationException, SAXException, ParseException {
		init();
		run();
	}

	/**
	 * returns the list of the states
	 * 
	 * @return the statesList
	 */
	public static List<State> getStatesList() {
		return statesList;
	}

	/**
	 * returns the property variable indexing the folder in which the generated
	 * Page Objects will be saved
	 * 
	 * @return the gen_po_dir
	 */
	public static String getGen_po_dir() {
		return gen_po_dir;
	}

	/**
	 * returns the property variable indexing the folder containing the outputs
	 * of Crawljax
	 * 
	 * @return the out_dir
	 */
	public String getOut_dir() {
		return out_dir;
	}

	/**
	 * @return the threshold
	 */
	public static String getThreshold() {
		return threshold;
	}

	/**
	 * @return the strategy
	 */
	public static String getStrategy() {
		return strategy;
	}

	/**
	 * @return the algorithm
	 */
	public static String getAlgorithm() {
		return algorithm;
	}

	/**
	 * @return the number_of_clusters
	 */
	public static String getNumber_of_clusters() {
		return number_of_clusters;
	}

	/**
	 * initialize the static analyzer, i.e. reads the opportune properties
	 * 
	 * @throws ParseException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 */
	private static void init() throws IOException,
			ParserConfigurationException, SAXException, ParseException {

		statesList = new LinkedList<State>();
		Properties configFile = new Properties();

		try {
			configFile.load(Crawler.class.getClassLoader().getResourceAsStream(
					"config.properties"));
			gen_po_dir = configFile.getProperty("GEN_PO_DIR");

			if (!gen_po_dir.endsWith("/"))
				gen_po_dir = gen_po_dir.concat("/");

			out_dir = configFile.getProperty("OUT_DIR");
			doms_dir = configFile.getProperty("DOMS_DIR");
			algorithm = configFile.getProperty("ALGORITHM");
			doms_dir = configFile.getProperty("DOMS_DIR");
			number_of_clusters = configFile.getProperty("NUMBER_OF_CLUSTERS");

		} catch (IOException e) {
			e.printStackTrace();
		}

		// default icon, custom title
		int n = JOptionPane.showConfirmDialog(null,
				"Would you like to run clustering over the model?\n",
				"Clustering", JOptionPane.YES_NO_OPTION);

		if (n == JOptionPane.YES_OPTION) {
			clustering = "y";
			calculateClusters(configFile);
		} else {
			clustering = "n";
		}
	}

	/**
	 * Merges the similar states accordingly to a the clustering result This
	 * method modifies Crawljax outputs in order to provide cluster and diff
	 * information
	 * 
	 * @param configFile
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws ParseException
	 */
	public static void calculateClusters(Properties configFile) throws IOException,
			ParserConfigurationException, SAXException, ParseException {

		System.out.println("[LOG] STARTED STATES MERGE");

		JFrame parent = new JFrame();

		int n = 1;
		while (n == 1) {

			String a = null, f = null;
			
			// ask for algorithm
			
			Object[] algo_possibilities = {"Hierarchical", "K-means"};//, "K-medoids"};
			
			String s = (String) JOptionPane.showInputDialog(
			                    null,
			                    "Choose the clustering algorithm",
			                    "Enter algorithm",
			                    JOptionPane.INFORMATION_MESSAGE,
			                    null,
			                    algo_possibilities,
			                    "Hierarchical");
			
			if ((s != null) && (s.length() > 0)) {
			    if(s.equals("Hierarchical")){
			    	a = "0";
			    } else if(s.equals("K-means")){
			    	a = "1";
			    }
			}
			
			// ask for feature
			
			Object[] feat_possibilities = {"DOM-RTED", "DOM-Lev", "Tag Frequency", "URL-Lev"};
			
			s = (String) JOptionPane.showInputDialog(
                    null,
                    "Choose the feature",
                    "Enter feature",
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    feat_possibilities,
                    "DOM-RTED");

			if ((s != null) && (s.length() > 0)) {
				if(s.equals("DOM-RTED")){
					f = "0";
				} else if(s.equals("DOM-Lev")){
					f = "1";
				} else if(s.equals("Tag Frequency")){
					f = "2";
				} else if(s.equals("URL-Lev")){
					f = "3";
				}
			}

			String d = configFile.getProperty("OUT_DIR");

			UtilsDataset ud = new UtilsDataset(d);
			ud.createDatasets(f);

			try {

				SpinnerNumberModel sModel = new SpinnerNumberModel(2, 2, 30, 1);
				JSpinner spinner = new JSpinner(sModel);

				int option = JOptionPane.showOptionDialog(parent, spinner,
						"Enter a number of clusters",
						JOptionPane.DEFAULT_OPTION,
						JOptionPane.QUESTION_MESSAGE, null, null, null);
				
				if (option == JOptionPane.CANCEL_OPTION) {
					// user hit cancel
				} else if (option == JOptionPane.OK_OPTION) {
					// user entered a number
					number_of_clusters = spinner.getValue().toString();
				}

				clustersMap = UtilsClustering.runClustering(a, f,
						number_of_clusters);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			JSONArray list = new JSONArray();

			for (int i = 0; i < clustersMap.size(); i++) {
				for (int j = 0; j < clustersMap.get(i).size(); j++) {
					JSONObject obj = new JSONObject();
					obj.put("stateId", clustersMap.get(i).get(j));
					obj.put("clusterId", i);
					obj.put("clusterSize", clustersMap.get(i).size());
					list.add(obj);
				}
			}

			try {
				FileWriter file = new FileWriter("cve/clusters.json");
				file.write(list.toJSONString());
				file.flush();
				file.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// launch Clusters Visual Editor
			WebDriver cve = new FirefoxDriver();
			String cve_path = "file://" + System.getProperty("user.dir")
					+ "/cve/index.html";
			cve.manage().window().setPosition(new Point(0, 0));
			cve.manage().window().setSize(new Dimension(1024, 800));
			cve.get(cve_path);

			n = JOptionPane.showConfirmDialog(null,
					"Would you like to proceed?", "Clustering correct",
					JOptionPane.YES_NO_OPTION);
			if (n == 0)
				break;
		}

		System.gc();

		Runnable r = new Runnable() {

			@Override
			public void run() {
				JFileChooser jfc = new JFileChooser();
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				// if(jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
				// String file = jfc.getSelectedFile().getAbsolutePath();
				// }
			}
		};

		SwingUtilities.invokeLater(r);

		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

		int returnVal = fc.showOpenDialog(parent);
		String res = null;

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			res = fc.getSelectedFile().getAbsolutePath();
		}

		UtilsClustering.readClusteringResult(res);

		// System.exit(1);

		UtilsClustering.createCrawljaxResultCopy();
		UtilsClustering.modifyCrawljaxResultAccordingToClusters();

		System.out.println("[LOG] ENDED STATES MERGE");
	}

	/**
	 * runs the static analyzer: it parses the information contained in the
	 * Document Object Model retrieved by Crawljax and gets what is necessary
	 * for the page object's code generation
	 */
	private static void run() {
		System.out.println("[LOG] STARTED STATIC ANALYSIS");

		statesList = UtilsStaticAnalyzer.createMergedStateObjects(statesList,
				gen_po_dir, out_dir, clustering);

		System.out.println("[LOG] ENDED STATIC ANALYSIS");
	}

}
