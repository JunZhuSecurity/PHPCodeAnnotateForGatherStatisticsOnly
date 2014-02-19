package edu.uncc.aside.phpcodeannotate.presentations;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.php.internal.core.ast.nodes.ASTNode;
import org.eclipse.php.internal.core.ast.nodes.Program;
import org.eclipse.php.internal.core.ast.nodes.Identifier;
import org.eclipse.php.internal.core.ast.visitor.ApplyAll;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.ISharedImages;

import edu.uncc.aside.phpcodeannotate.Constants;
import edu.uncc.aside.phpcodeannotate.NodeFinder;
import edu.uncc.aside.phpcodeannotate.Plugin;
import edu.uncc.aside.phpcodeannotate.util.Utils;
import edu.uncc.aside.phpcodeannotate.models.ModelRegistry;
import edu.uncc.aside.phpcodeannotate.models.Path;
import edu.uncc.aside.phpcodeannotate.models.PathCollector;
import edu.uncc.aside.phpcodeannotate.models.Point;
/**
 * 
 * @author Jing Xie (jxie2 at uncc dot edu)
 *
 */
public class CheckUndoResolution implements IMarkerResolution2 {

	private IProject project;
	private IResource resource;
	private Path path = null; 
	private Point check = null;
	
	private final static String LABEL = "1. Click me to undo this check";
	
	@Override
	public String getLabel() {	
		return LABEL;
	}

	@Override
	public void run(IMarker marker) {
		
		resource = marker.getResource();
		project = resource.getProject();
		
		retrievePathAndCheckFromMarker(marker);
		
		if(path != null && check != null){
			replaceAccessorMarkerOnPath(path);
			path.removeCheck(check);
		}
		
		try {
			marker.delete();
		} catch (CoreException e) {
			e.printStackTrace();
		}

	}

	private void retrievePathAndCheckFromMarker(IMarker marker) {
		
		IFile file = (IFile) resource.getAdapter(IFile.class);
		ISourceModule sourceModule = (ISourceModule)DLTKCore.create(file);
		
		if(sourceModule == null){
			System.err.println("sourceModule == null in retrievePathAndCheckFromMarker");
		}
		Program astRoot = null;
		try {
			astRoot = Utils.getCompilationUnit(sourceModule);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(astRoot == null){
			System.err.println("astRoot == null in retrievePathAndCheckFromMarker");
			return;
		}

		int charStart = marker.getAttribute(IMarker.CHAR_START, -1);
		int charEnd = marker.getAttribute(IMarker.CHAR_END, -1);
		int length = charEnd - charStart;

		NodeFinder finder = new NodeFinder(charStart, length);
		ASTNode node = finder.perform(astRoot, charStart, length);

		Point nodePoint = new Point(node, astRoot, resource);
		
		PathCollector pathCollector = ModelRegistry
				.getPathCollectorForProject(project);
		List<Path> paths = pathCollector.getAllPaths();
		List<Point> checks = null;
		for (Path _path : paths) {
			checks = _path.getChecks();
			for(Point _check : checks){
				if(_check.equalsTo(nodePoint)){
					check = _check;
					path = _path;
					return;
				}
			}
		}

	}

	private void replaceAccessorMarkerOnPath(Path path) {
		Point accessor = path.getAccessor();
		ASTNode node = accessor.getNode();
		IResource resource = accessor.getResource();
		
		Program unit = accessor.getUnit();
		
		try {
			// First, gotta check whether there is a marker for the accessor node
			int char_start, length;
			
			IMarker[] questionMarkers = resource.findMarkers(Plugin.ANNOTATION_QUESTION,
					false, IResource.DEPTH_ONE);
			for (IMarker marker : questionMarkers) {
				char_start = marker.getAttribute(IMarker.CHAR_START, -1);
				length = marker.getAttribute(IMarker.CHAR_END, -1) - char_start;
				// Second, if there is one, then move on; if not, create one.
				if (char_start == node.getStart()
						&& length == node.getLength()){
					return;
				}
					
			}
			
			IMarker[] markers = resource.findMarkers(Plugin.ANNOTATION_QUESTION_CHECKED,
					false, IResource.DEPTH_ONE);
			
			for (IMarker marker : markers) {
				char_start = marker.getAttribute(IMarker.CHAR_START, -1);
				length = marker.getAttribute(IMarker.CHAR_END, -1) - char_start;
				// Second, if there is one, then move on; if not, create one.
				if (char_start == node.getStart()
						&& length == node.getLength()){
					marker.delete();
					Utils.createQuestionMarker(node, resource, unit);
					return;
				}
					
			}

		} catch (CoreException e) {
			e.printStackTrace();
		}

	}

	@Override
	public String getDescription() {
		
		return Constants.CHECK_UNDO_RESOLUTION_DESC;
	}

	@Override
	public Image getImage() {
		
		return Plugin.getDefault().getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_UNDO);
	}
}
