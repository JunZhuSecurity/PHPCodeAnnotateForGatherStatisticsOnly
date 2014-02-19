package edu.uncc.aside.phpcodeannotate.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.php.internal.core.ast.nodes.Program;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.uncc.aside.phpcodeannotate.Constants;
import edu.uncc.aside.phpcodeannotate.Plugin;
import edu.uncc.aside.phpcodeannotate.models.AnnotationRecord;
import edu.uncc.aside.phpcodeannotate.models.MarkerRecord;
import edu.uncc.aside.phpcodeannotate.models.ModelRegistry;
import edu.uncc.aside.phpcodeannotate.models.Path;
import edu.uncc.aside.phpcodeannotate.models.PathCollector;
import edu.uncc.aside.phpcodeannotate.util.Utils;
import edu.uncc.aside.phpcodeannotate.visitors.SensitiveOperationVisitor;

/**
 * 
 * @author Jing Xie (jxie2 at uncc dot edu)
 * 
 */
public class ASIDECodeAnnotateHandler extends AbstractHandler {

	private IWorkbenchPart targetPart;
	IProject selectProject = null;

	private PathCollector pathCollector;
	private List<Path> paths;

	@Override
	public Object execute(ExecutionEvent event) {
		
	//	Plugin.isManuallyStarted = true;

		System.out
				.println("ASIDECodeAnnotateHandler.java is ran ---first line");
		targetPart = HandlerUtil.getActivePart(event);

		IWorkbenchPartSite site = targetPart.getSite();
		ISelectionProvider selectionProvider = site.getSelectionProvider();
		if (selectionProvider == null) {
			return null;
		}
		ISelection selection = selectionProvider.getSelection();
		if (selection == null) {
			System.out.println("selectProject = ");
			return null;
		}
		IResource iRes = extractSelection(selection);
		if (iRes == null) {
			System.out.println("test == null");
			return null;
		}
		selectProject = iRes.getProject();
		if (selectProject == null) {
			System.out.println("selectProject == null");
			return null;
		}
		System.out.println("selectProject = " + selectProject.getName());

		// the following is temporarily added here
		pathCollector = ModelRegistry.getPathCollectorForProject(selectProject);

		if (pathCollector == null) {
			pathCollector = new PathCollector(selectProject);
		}

		paths = pathCollector.getAllPaths();

		if (paths == null)
			paths = Collections.synchronizedList(new ArrayList<Path>());

		System.out
				.println("ASIDECodeAnnotateHandler.java is ran -- start iterating files of the project");
		IScriptProject scriptProject = DLTKCore.create(selectProject);
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
		Utils.removeAllQuestionMarkers(iRes);
		Plugin.projectResource = iRes;
		
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
				
				// if it is not the first time to run CodeAnnotate on this project, then disable the scan function,
				//we have already pop the marker records from the file and displayed in Eclipse already. 
				
				if(true){//simply display the markers based on the marker records in the file 
					String fileDir = tmpSourceModule.getResource().getFullPath().toString();
					//System.out.println("all markers size = " + Plugin.allMarkerRecords.size());
					HashSet<MarkerRecord> markerRecordsInSingleFile = Utils.getMarkerRecordsForSingleFile(Plugin.allMarkerRecords, fileDir);
					Utils.createMarkersForSingleFile(markerRecordsInSingleFile, tmpSourceModule);
					
					HashSet<AnnotationRecord> annotationRecordsInSingleFile = Utils.getAnnotationRecordsForSingleFile(Plugin.allAnnotationRecords, fileDir);
					Utils.createAnnotationsForSingleFile(annotationRecordsInSingleFile, tmpSourceModule);
					if(markerRecordsInSingleFile.size() > 0)
					System.out.println("finished creating markers for fileDir = " + fileDir + ", markerRecordsInSingleFile size = " + markerRecordsInSingleFile.size());
				}
			else{ //start scanning the files for sensitive operations
				System.out.println("isourcemodule being built = "
						+ tmpSourceModule.getElementName().toLowerCase());
				System.out.println("full path of the source module is ---" + tmpSourceModule.getResource().getFullPath().toString());
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
		}
		// above is temporarily added.

		/*
		 * Use a Job to attach a {@link CodeAnnotateDocumentEditListener} to
		 * each and every IDocument that is related to a ICompilationUnit in the
		 * selected project
		 */
		/*
		 * Job job = new MountListenerJob("Mount listener to Java file",
		 * JavaCore.create(selectProject)); job.setPriority(Job.INTERACTIVE);
		 * job.schedule();
		 */

		/* Delegates all heavy lifting to {@link PathFinder} */
		/*Job heavy_job = new Job("Finding paths in Project: "
				+ selectProject.getName()) {

			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				try {
					Plugin.getDefault().getWorkbench().getDisplay()
							.asyncExec(new Runnable() {

								@Override
								public void run() {
									// PathFinder.getInstance(selectProject).run(monitor);

								}

							});

				} finally {
					monitor.done();
				}
				return Status.OK_STATUS;
			}

		};
		heavy_job.setPriority(Job.LONG);
		heavy_job.schedule();
*/
		return null;
	}

	public IResource extractSelection(ISelection sel) {
		if (!(sel instanceof IStructuredSelection))
			return null;
		IStructuredSelection ss = (IStructuredSelection) sel;
		Object element = ss.getFirstElement();
		if (element instanceof IResource)
			return (IResource) element;
		if (!(element instanceof IAdaptable))
			return null;
		IAdaptable adaptable = (IAdaptable) element;
		Object adapter = adaptable.getAdapter(IResource.class);
		return (IResource) adapter;
	}
	/*
	 * class MountListenerJob extends Job {
	 * 
	 * IJavaProject projectOfInterest; IBufferChangedListener listener;
	 * ArrayList<ICompilationUnit> totalUnits;
	 * 
	 * public MountListenerJob(String name, IJavaProject project) { super(name);
	 * projectOfInterest = project; listener = new
	 * CodeAnnotateDocumentEditListener(); totalUnits = new
	 * ArrayList<ICompilationUnit>(); }
	 * 
	 * @Override protected IStatus run(IProgressMonitor monitor) { try {
	 * monitor.beginTask(
	 * "Mounting a CodeAnnotateDocumentEditListener to a Java file",
	 * numberOfJavaFiles(projectOfInterest));
	 * 
	 * for (ICompilationUnit unit : totalUnits) {
	 * 
	 * if (unit == null || !unit.exists()) continue;
	 * 
	 * if (!unit.isOpen()) { unit.open(monitor); }
	 * 
	 * unit.becomeWorkingCopy(monitor);
	 * 
	 * IBuffer buffer = (unit).getBuffer(); if (buffer != null) {
	 * buffer.addBufferChangedListener(listener); } if (monitor.isCanceled()) {
	 * return Status.CANCEL_STATUS; } }
	 * 
	 * } catch (JavaModelException e) { e.printStackTrace(); return
	 * Status.CANCEL_STATUS; } finally { monitor.done(); }
	 * 
	 * return Status.OK_STATUS; }
	 * 
	 * private int numberOfJavaFiles(IJavaProject project) throws
	 * JavaModelException {
	 * 
	 * int count = 0; IPackageFragment[] fragments = projectOfInterest
	 * .getPackageFragments(); for (IPackageFragment fragment : fragments) {
	 * ICompilationUnit[] units = fragment.getCompilationUnits(); for
	 * (ICompilationUnit unit : units) { totalUnits.add(unit); count++; } }
	 * 
	 * return count; } }
	 */
}
