package apogen;

import japa.parser.ast.CompilationUnit;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import abstractdt.State;
import utils.UtilsCodeGenerator;

public class CodeGenerator {
	
	/**
	 * run the code generation from the
	 * info gathered by the static analysis
	 * @throws Exception 
	 */
	static void run() throws Exception {
		System.out.println("[LOG] STARTED PAGE OBJECTS GENERATION");
		
		createPageObjects();
		
		savePageObjectsOnFileSystem();
		
		System.out.println("[LOG] ENDED PAGE OBJECTS GENERATION");
	}
	
	/**
	 * creates the source code for each state
	 * with creation of compilation units and
	 * dynamic AST modifications
	 * TODO: only java language support
	 * @throws Exception 
	 */
	private static void createPageObjects() throws Exception {
		
		for (State s : StaticAnalyzer.getStatesList()) {
			
			CompilationUnit c = UtilsCodeGenerator.createEnhancedCompilationUnit();
			
			UtilsCodeGenerator.setTypeDeclaration(c, s.getName());
			
			UtilsCodeGenerator.setVariables(c, s.getWebElements());
			
			UtilsCodeGenerator.setDefaultConstructor(c, s);
			
			UtilsCodeGenerator.setLinkMethods(c, s);
			
			UtilsCodeGenerator.setFormMethods(c, s);
			
			UtilsCodeGenerator.setGettersMethodsForAssertions(c, s);
			
			s.setSourceCode(c.toString());
		}
		
	}

	/**
	 * makes the source code of each state persistent 
	 * TODO: implements the persistence
	 * for different backends
	 * @throws IOException
	 */
	static void savePageObjectsOnFileSystem() throws IOException {
		
		for (State s : StaticAnalyzer.getStatesList()) {
			
			String poName = s.getName();
			String fileNameToCreate = StaticAnalyzer.getGen_po_dir() + poName;
			
			File f = new File(fileNameToCreate+".java");
			
			FileUtils.writeStringToFile(f, s.getSourceCode());
		}
		
	}
	
}
