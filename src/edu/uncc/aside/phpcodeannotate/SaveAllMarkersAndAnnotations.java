package edu.uncc.aside.phpcodeannotate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.core.ScriptProject;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.php.internal.core.ast.nodes.Program;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.handlers.HandlerUtil;

//import edu.uncc.aside.codeannotate.PathFinder;
import edu.uncc.aside.phpcodeannotate.Constants;
import edu.uncc.aside.phpcodeannotate.Plugin;
//import edu.uncc.aside.codeannotate.listeners.CodeAnnotateDocumentEditListener;
import edu.uncc.aside.phpcodeannotate.models.ModelRegistry;
import edu.uncc.aside.phpcodeannotate.models.Path;
import edu.uncc.aside.phpcodeannotate.models.PathCollector;
import edu.uncc.aside.phpcodeannotate.util.Utils;
import edu.uncc.aside.phpcodeannotate.visitors.SensitiveOperationVisitor;

public class SaveAllMarkersAndAnnotations {

	private IWorkbenchPart targetPart;
	IProject selectProject = null;

	private PathCollector pathCollector;
	private List<Path> paths;

	public Object saveAll(IScriptProject scriptProject) {
	//	Plugin.isManuallyStarted = true;

       

		System.out
				.println("start saving annotations -----");
	
		if(scriptProject == null){
			System.out.println("scirpt project == null");
			return null;
	}
		IScriptFolder[] folders = null;
		try {
			folders = scriptProject.getScriptFolders();
		} catch (ModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("number of folders ==" + folders.length);
		//Utils.removeAllQuestionMarkers(iRes);
		//the directory is, projectName/folderName/fileName
		String projectName = scriptProject.getElementName();
		
		for (IScriptFolder folder : folders) {
			String folderName = folder.getElementName();
			System.out.println("folder name = " + folderName);
			if(!Constants.PHPLibraryFolders.contains(folderName)){
			
			ISourceModule[] sourceModules = null;
			try {
				sourceModules = folder.getSourceModules();
			} catch (ModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			for (ISourceModule tmpSourceModule : sourceModules) {
				String fileName = tmpSourceModule.getElementName();
				String fileDir = projectName + "/" + folderName + "/" + fileName;
				System.out.println();
				
				System.out.println("isourcemodule being built = "
						+ tmpSourceModule.getElementName().toLowerCase());
				SensitiveOperationVisitor visitor = new SensitiveOperationVisitor(
						tmpSourceModule);
				Program root = null;

				try {
					root = Utils.getCompilationUnit(tmpSourceModule);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				System.out.println("begin of traverseTopDown");
				root.traverseTopDown(visitor);
				System.out.println("end of traverseTopDown");
			}
			}
		}

		return null;
	}

}

