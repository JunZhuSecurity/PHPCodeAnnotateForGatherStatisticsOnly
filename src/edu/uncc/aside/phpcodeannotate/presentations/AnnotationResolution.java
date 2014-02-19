package edu.uncc.aside.phpcodeannotate.presentations;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.php.internal.core.ast.nodes.ASTNode;
import org.eclipse.php.internal.core.ast.nodes.Program;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMarkerResolution2;

import edu.uncc.aside.phpcodeannotate.Constants;
import edu.uncc.aside.phpcodeannotate.NodeFinder;
import edu.uncc.aside.phpcodeannotate.Plugin;
import edu.uncc.aside.phpcodeannotate.models.ModelRegistry;
import edu.uncc.aside.phpcodeannotate.models.NodePositionInfo;
import edu.uncc.aside.phpcodeannotate.models.Path;
import edu.uncc.aside.phpcodeannotate.models.PathCollector;
import edu.uncc.aside.phpcodeannotate.models.Point;
import edu.uncc.aside.phpcodeannotate.util.Utils;

/**
 * 
 * @author Jun Zhu (jzhu16 AT uncc dot edu)
 * 
 */
public class AnnotationResolution implements IMarkerResolution2 {

	public final static String LABEL = "2. Click me to annotate a control logic";

	private IProject project;
	private IResource resource;

	@Override
	public String getLabel() {
		return LABEL;
	}

	@Override
	public void run(IMarker marker) {
		changeCursor();
		resource = marker.getResource();
		project = resource.getProject();

		Path path = retrivePathFromMarker(marker);

		switchAnnotationPath(path);

		setCurrentMarkerWorkedOn(marker);

	}

	private void setCurrentMarkerWorkedOn(IMarker marker) {
		// set this marker as the current marker worked on of the plugin, which
		// will be used when an annotation is selected and made

		String markerFileDir = marker.getResource().getFullPath().toString();

		// System.out.println("currentMarkerWorkedOn.getResource().getFullPath().toString() = "
		// + marker.getResource().getFullPath().toString());
		int charStart = marker.getAttribute(IMarker.CHAR_START, -1);
		int charEnd = marker.getAttribute(IMarker.CHAR_END, -1);
		int charLength = charEnd - charStart;
		NodePositionInfo nodePositionInfo = new NodePositionInfo(markerFileDir,
				charStart, charLength);
		Plugin.currentMarkerWorkedOnPositionInfo = nodePositionInfo;
	}

	@Override
	public String getDescription() {

		return Constants.ANNOTATION_RESOLUTION_DESC;
	}

	@Override
	public Image getImage() {
		ImageDescriptor descriptor = Plugin
				.getImageDescriptor("annotation.jpeg");
		Image image = Plugin.imageCache.get(descriptor);

		if (image == null) {
			image = descriptor.createImage();
			Plugin.imageCache.put(descriptor, image);
		}

		return image;
	}

	/**
	 * Retrieves the Path that is identified by the marker.
	 * 
	 * @param marker
	 * @return
	 */
	private Path retrivePathFromMarker(IMarker marker) {

		IFile file = (IFile) resource.getAdapter(IFile.class);
		ISourceModule unit = (ISourceModule) DLTKCore.create(file);
		// ICompilationUnit unit = JavaCore.createCompilationUnitFrom(file);

		if (unit == null) {
			return null;
		}

		Program astRoot = null;
		try {
			astRoot = Utils.getCompilationUnit(unit);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (astRoot == null)
			return null;
		int charStart = marker.getAttribute(IMarker.CHAR_START, -1);
		int charEnd = marker.getAttribute(IMarker.CHAR_END, -1);
		int length = charEnd - charStart;
		// test ues
		System.out.println("Actual marker start=" + charStart + ", Length = "
				+ length);

		// //

		ASTNode node = NodeFinder.perform(astRoot, charStart, length);
		if (node == null) {
			System.err.println("node == null in AnnotationResolution.java");
		}
		System.out.println("node in AnnotationResolution is = "
				+ node.toString());
		Point nodePoint = new Point(node, astRoot, resource);

		return getPathByAccessor(nodePoint);

	}

	private Path getPathByAccessor(Point nodePoint) {

		if (nodePoint == null) {
			System.out.println("nodePoint == null in getPathByAccessor!");
			return null;
		}
		System.out.println("getPathByAccessor iproject name = "
				+ project.getName());
		Collection<PathCollector> pathCollectorss = ModelRegistry
				.getAllRegisteredChildren();
		System.out.println("Actual project in the collection is = "
				+ pathCollectorss.iterator().next().getProject().getName());
		PathCollector pathCollector = ModelRegistry
				.getPathCollectorForProject(project);
		System.out.println("PathCollector has project name "
				+ pathCollector.getProject().getName());
		// test code
		if (pathCollector == null)
			System.out.println("pathCollector == null");

		// above is test code
		List<Path> paths = pathCollector.getAllPaths();
		System.out.println("path size = " + paths.size());
		Point accessor = null;
		for (Path path : paths) {
			accessor = path.getAccessor();
			System.out.println("accessor = " + accessor.getNode().toString());
			if (accessor.equalsTo(nodePoint)) {
				return path;
			}
		}

		return null;
	}

	private void switchAnnotationPath(Path path) {
		if (path == null) {
			System.err.println("switchAnnotationPath(Path path)  is null");
		}
		Plugin.annotationPath = path;
	}

	private void changeCursor() {

		Display display = Plugin.getDefault().getWorkbench().getDisplay();
		Shell shell = display.getActiveShell();
		// doesn't work ahhh
		Cursor cursor = new Cursor(display, SWT.CURSOR_CROSS);
		System.err.println("just created a new cursor");
		shell.setCursor(cursor);

	}
}
