package edu.uncc.aside.phpcodeannotate.actions;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
/*import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.ui.JavaUI;*/
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.php.internal.core.ast.nodes.ASTNode;
import org.eclipse.php.internal.core.ast.nodes.ASTParser;
import org.eclipse.php.internal.core.ast.nodes.ITypeBinding;
import org.eclipse.php.internal.core.ast.nodes.Program;


//import edu.uncc.aside.phpcodeannotate.Binding2JavaModel;
import edu.uncc.aside.phpcodeannotate.Constants;
import edu.uncc.aside.phpcodeannotate.NodeFinder;
import edu.uncc.aside.phpcodeannotate.Plugin;
import edu.uncc.aside.phpcodeannotate.util.Utils;
//import edu.uncc.aside.phpcodeannotate.models.NodeAllPositionInfo;
//import edu.uncc.aside.phpcodeannotate.models.NodePositionInfo;
import edu.uncc.aside.phpcodeannotate.models.AnnotationRecord;
import edu.uncc.aside.phpcodeannotate.models.AnnotationType;
import edu.uncc.aside.phpcodeannotate.models.MarkerRecord;
import edu.uncc.aside.phpcodeannotate.models.NodePositionInfo;
import edu.uncc.aside.phpcodeannotate.models.Path;
import edu.uncc.aside.phpcodeannotate.models.Point;

/**
 * 
 * @author Jun Zhu (jzhu16 at uncc dot edu)
 *
 */
public class ASIDELogicControlAnnotateHandler extends AbstractHandler {

	private IEditorPart target;
	private IScriptProject PHPProject;
	private ISourceModule iSourceModule;
//	private IResource file;
	private IResource iResource;
	private IFile iFile;
	//private CompilationUnit astRoot;
	private Program astRoot;
	private IProject project;
	private ASTNode node;
	private Shell shell;
	private Map<String, Path> map;
	private Point annotatedControlLogic;

	private Path annotationPath;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		target = HandlerUtil.getActiveEditor(event);
		shell = HandlerUtil.getActiveShell(event);
		ISelection selection = HandlerUtil.getCurrentSelection(event);

		// The current path to be annotated with a check
		annotationPath = Plugin.annotationPath;
//removed temporarily Agu 4th
	/*	if (annotationPath == null) {
			popupDialogWarning();
			return null;
		}*/

		if (selection != null && (selection instanceof ITextSelection)) {
			ITextSelection tSelection = (ITextSelection) selection;
			
			IEditorInput input = target.getEditorInput();
			if (input instanceof IFileEditorInput) {
				iFile = ((IFileEditorInput)input).getFile();
				project = iFile.getProject();
				PHPProject = DLTKCore.create(project);
				iSourceModule = (ISourceModule)DLTKCore.create(iFile);
				iResource = iSourceModule.getResource();
			}
			
			/*IJavaElement element = JavaUI.getEditorInputJavaElement(input);

			try {
				file = element.getUnderlyingResource();
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
			IJavaProject javaProject = element.getJavaProject();
			project = javaProject.getProject();*/

			process(tSelection, iSourceModule);

		}

		return selection;
	}
	
/*	public HashMap<NodeAllPositionInfo, Point> obtainDeclarationRelateToAnnotationDB(ASTNode node, HashMap<NodeAllPositionInfo, Point> orginDeclarationMap){
		HashMap<NodeAllPositionInfo, Point> tmp = orginDeclarationMap;
		NodePositionInfo tmpPositionInfo = new NodePositionInfo(node.getStartPosition(), node.getLength());
		MethodDeclaration enclosingMethodDeclaration = Utils.getParentMethodDeclaration(node);
		boolean isConstructor = false;
		
		if(enclosingMethodDeclaration == null){
			System.out.println("enclosingMethodDeclaration == null");
		}
		IMethodBinding methodBinding = enclosingMethodDeclaration
				.resolveBinding();
		if(methodBinding.isConstructor() == true){
//			System.out.println("isConstructor == " + enclosingMethodDeclaration);
			isConstructor = true;
		}
		if (methodBinding == null) {
			System.err.println("bindings cannot be resolved");
			return null;
		}
		ITypeBinding typeBinding = methodBinding.getDeclaringClass();
		String qualifiedName = Binding2JavaModel
				.getFullyQualifiedName(typeBinding);
		IMethod method = Utils.convertMethodDecl2IMethod(
				enclosingMethodDeclaration, file);   //file is iResource, 
		IResource mResource;
		try {
			mResource = method.getUnderlyingResource();
			if (mResource == null) {
				System.err.println("Check here...");
				return null;
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
			return null;
		}
        //keep the information about the enclosing method declaration, they are the method declarations for which we need to build CFG
		String filePath = mResource.getFullPath().toString();
		NodeAllPositionInfo tmpNodeAllPositionInfo = new NodeAllPositionInfo(new NodePositionInfo(enclosingMethodDeclaration.getStartPosition(), enclosingMethodDeclaration.getLength()), filePath);
		Point methodDeclarationPoint = new Point(enclosingMethodDeclaration, Utils.getCompilationUnit(Utils.compilationUnitOfInterest(mResource)), mResource);
		
		tmp.put(tmpNodeAllPositionInfo, methodDeclarationPoint);
		
		
		return tmp;

	}
	private HashMap<String, Boolean> obtainMethodIDRelateAnnotation(
			ASTNode node, HashMap<String, Boolean> methodIDRelateAnnotation) {
		HashMap<String, Boolean> tmp = methodIDRelateAnnotation;
		MethodDeclaration enclosingMethodDeclaration = Utils.getParentMethodDeclaration(node);
		boolean isConstructor = false;
		String methodID = null;
		
		if(enclosingMethodDeclaration == null){
			System.err.println("enclosingMethodDeclaration == null");
		}
		IMethodBinding methodBinding = enclosingMethodDeclaration
				.resolveBinding();
		if(methodBinding.isConstructor() == true){
//			System.out.println("isConstructor == " + enclosingMethodDeclaration);
			isConstructor = true;
		}
		if (methodBinding == null) {
			System.err.println("bindings cannot be resolved");
			return null;
		}
		ITypeBinding typeBinding = methodBinding.getDeclaringClass();
		String qualifiedName = Binding2JavaModel
				.getFullyQualifiedName(typeBinding);
		IMethod method = Utils.convertMethodDecl2IMethod(
				enclosingMethodDeclaration, file);   //file is iResource, 
		IResource mResource;
		try {
			mResource = method.getUnderlyingResource();
			if (mResource == null) {
				System.err.println("Check here...");
				return null;
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
			return null;
		}
		int numOfParameters = method.getNumberOfParameters();
		String[] parameterTypes = method.getParameterTypes();
		String paraTypesStrings = null;
		if(numOfParameters == 0){
			paraTypesStrings = "()";
		}else if(numOfParameters > 0){
			paraTypesStrings = "";
			for(int t = 0; t < numOfParameters - 1; t++){
				paraTypesStrings = paraTypesStrings + Signature.getSignatureSimpleName(parameterTypes[t]) + ",";
				
			}
			paraTypesStrings = "(" + paraTypesStrings + Signature.getSignatureSimpleName(parameterTypes[numOfParameters - 1]) + ")";
			
		}
		if(isConstructor == false)
		methodID = qualifiedName + "."
				+ method.getElementName() + paraTypesStrings;
		else
			methodID = qualifiedName + paraTypesStrings;
		
		tmp.put(methodID, isConstructor);
		return tmp;
	}

	public List<NodeAllPositionInfo> obtainAnnotatedNodeAllPositionInfoList(
			ASTNode node,
			List<NodeAllPositionInfo> annotatedNodeAllPositionInfoList) {
		List<NodeAllPositionInfo> tmp = annotatedNodeAllPositionInfoList;
		NodePositionInfo nodePositionInfo = new NodePositionInfo(node.getStartPosition(), node.getLength());
		NodeAllPositionInfo nodeAllPositionInfo = new NodeAllPositionInfo(nodePositionInfo, file.getFullPath().toString());
		tmp.add(nodeAllPositionInfo);
		return tmp;
	}*/
	

	private void process(ITextSelection tSelection, ISourceModule iSourceModule) {

		int char_start = tSelection.getOffset();
		int length = tSelection.getLength();
		IResource iResource = iSourceModule.getResource();  ////////////not sure if this API should be used here?
		try {
			astRoot = Utils.getCompilationUnit(iSourceModule);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		node = NodeFinder.perform(astRoot, char_start, length);

		if (node == null)
			return;
		int nodeType = node.getType();
		
		ITypeBinding binding = null;
		
		String fullyQualifiedName = null;
		
		switch (nodeType) {
		//newly added Sept 24th
		case ASTNode.FUNCTION_INVOCATION:
			System.out.println("one FUNCTION_INVOCATION selected and annotated: " + node.toString());
			Plugin.annotatedASTNodes.add(node);
			annotatedControlLogic = new Point(node, astRoot, iResource);
			if(annotatedControlLogic == null)
			{
				System.err.println("annotatedControlLogic == null");
			}
			annotatePath();
			
			//in the process, the corresponding MarkerRecord is changed, the value of attributes isAnnotated, corresponding annotations set, numOfAnnotations
			
			NodePositionInfo markerPositionInfo = Plugin.currentMarkerWorkedOnPositionInfo;
			
			System.out.println("Plugin.allMarkerRecords size =" + Plugin.allMarkerRecords.size());
			MarkerRecord markerRecord = Utils.getMarkerRecordByPositionInfo(markerPositionInfo, Plugin.allMarkerRecords);
			if(markerRecord == null)
				System.err.println("markerRecord == null in ASIDELogicControlAnnotateHandler.java");
			//in the process, the corresponding AnnotationRecord is added/changed, the value of attributes numOfCorrespondingMarkers, correspondingMarkerRecords
			String annotationFileDir = iResource.getFullPath().toString();
			NodePositionInfo annotationPositionInfo = new NodePositionInfo(annotationFileDir, node.getStart(), node.getLength());
			//if the position of the annotation is annotated the first time, then create the record object, or else, just find the annotation record, and add the marker
		    AnnotationRecord annotationRecord = null;
		    annotationRecord = Utils.getAnnotationRecordByPositionInfo(annotationPositionInfo, Plugin.allAnnotationRecords);
		    if(annotationRecord != null){ //there exist such annotation in this position, then retrieve from the annotationRecord set
		    	
		    	annotationRecord.addMarker(markerRecord);
		    }else{//or else, create a annotation record, and add to plugin.allAnnotationRecords
		    	annotationRecord = new AnnotationRecord(annotationPositionInfo, AnnotationType.ACCESS_CONTROL, markerRecord);
		    	Plugin.allAnnotationRecords.add(annotationRecord);
		    }
		    //add annotation record to the marker record
		    markerRecord.addAnnotation(annotationRecord);
		    
			break;
			
		/*case ASTNode.ASSERT_STATEMENT:
			// read articles about Java assertion and Spring security assertion
			System.err.println("You selected assert statement!");
			break;
		case ASTNode.SIMPLE_NAME:
			SimpleName sn = (SimpleName) node;
			binding = sn.resolveTypeBinding();
			fullyQualifiedName = binding.getQualifiedName();
			//added Aug 4th
			System.out.println("one expression selected " + node.toString());
			Plugin.annotatedASTNodes.add(node);
			Plugin.annotatedNodeAllPositionInfoList = obtainAnnotatedNodeAllPositionInfoList(node, Plugin.annotatedNodeAllPositionInfoList);
			Plugin.methodDeclarationRelateToAnnotationDB = obtainDeclarationRelateToAnnotationDB(node, Plugin.methodDeclarationRelateToAnnotationDB);
			Plugin.methodIDRelateAnnotation = obtainMethodIDRelateAnnotation(node, Plugin.methodIDRelateAnnotation);
			
			//
			if (fullyQualifiedName.equals("boolean")) {
				annotatedControlLogic = new Point(node, astRoot, file);
				annotatePath();
			}
			break;
		case ASTNode.METHOD_INVOCATION:
			MethodInvocation mi = (MethodInvocation) node;
			IMethodBinding mBinding = mi.resolveMethodBinding();
			binding = mBinding.getReturnType();
			fullyQualifiedName = binding.getQualifiedName();
			//added Aug 4th
			System.out.println("one expression selected " + node.toString());
			Plugin.annotatedASTNodes.add(node);
			Plugin.annotatedNodeAllPositionInfoList = obtainAnnotatedNodeAllPositionInfoList(node, Plugin.annotatedNodeAllPositionInfoList);
			Plugin.methodDeclarationRelateToAnnotationDB = obtainDeclarationRelateToAnnotationDB(node, Plugin.methodDeclarationRelateToAnnotationDB);
			Plugin.methodIDRelateAnnotation = obtainMethodIDRelateAnnotation(node, Plugin.methodIDRelateAnnotation);
			//
			if (fullyQualifiedName.equals("boolean")) {
				annotatedControlLogic = new Point(node, astRoot, file);
				annotatePath();
			}
			break;
		case ASTNode.INFIX_EXPRESSION:
			InfixExpression infixExpression = (InfixExpression) node;
			binding = infixExpression.resolveTypeBinding();
			fullyQualifiedName = binding.getQualifiedName();
			//added Aug 4th
			System.out.println("one expression selected " + node.toString());
			Plugin.annotatedASTNodes.add(node);
			Plugin.annotatedNodeAllPositionInfoList = obtainAnnotatedNodeAllPositionInfoList(node, Plugin.annotatedNodeAllPositionInfoList);
			Plugin.methodDeclarationRelateToAnnotationDB = obtainDeclarationRelateToAnnotationDB(node, Plugin.methodDeclarationRelateToAnnotationDB);
			Plugin.methodIDRelateAnnotation = obtainMethodIDRelateAnnotation(node, Plugin.methodIDRelateAnnotation);
			//
			if (fullyQualifiedName.equals("boolean")) {
				annotatedControlLogic = new Point(node, astRoot, file);
				annotatePath();
			}
			break;*/
		default:
			System.err
					.println("the selected code piece is not recognized by CodeAnnotate...");
			break;

		}
	}


	private void annotatePath() {
		if (annotationPath.containsCheck(annotatedControlLogic))
			return;
		annotationPath.addCheck(annotatedControlLogic);
		annotatedControlLogic.setParent(annotationPath);

		markControlLogic();
		replaceAccessorMarkerOnPath(annotationPath); 
		
		
		
		// TODO: how to manage path and its control logic annotations: UI->View,
		// background infrastructure?
	}

	private void markControlLogic() {

		try {
			// First, gotta check whether there is a marker for the node
			if (iResource == null){
				System.err.println("iResource == null in markControlLogic");
				return;
			}
			IMarker[] markers = iResource.findMarkers(Plugin.ANNOTATION_ANSWER,
					false, IResource.DEPTH_ONE);
			int char_start = -1, char_end = -1;
			for (IMarker marker : markers) {
				char_start = marker.getAttribute(IMarker.CHAR_START, -1);
				char_end = marker.getAttribute(IMarker.CHAR_END, -1);
				// Second, if there is one, then move on; if not, create one.
				if (char_start == node.getStart()
						&& char_end == node.getLength())
					return;
			}

			IMarker answerMarker = iResource.createMarker(Plugin.ANNOTATION_ANSWER);

			answerMarker.setAttribute(IMarker.CHAR_START,
					node.getStart());
			answerMarker.setAttribute(IMarker.CHAR_END, node.getEnd());
			answerMarker.setAttribute(IMarker.MESSAGE,
					"This is an annotated access control logic.");
			answerMarker.setAttribute(IMarker.LINE_NUMBER,
					astRoot.getLineNumber(node.getStart()));
			answerMarker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
			answerMarker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
			System.out.println("control logic was put in the marker");
//to be added, store the information in the persistent storage
			
		} catch (CoreException e) {
			e.printStackTrace();
		}

//		changeCursorToNormal();
	}

	private void replaceAccessorMarkerOnPath(Path path) {
		Point accessor = path.getAccessor();
		ASTNode node = accessor.getNode();
		IResource resource = accessor.getResource();
		Program unit = accessor.getUnit();

		try {
			// First, gotta check whether there is a marker for the accessor
			// node
			int char_start, length;

			IMarker[] checkedMarkers = resource.findMarkers(
					Plugin.ANNOTATION_QUESTION_CHECKED, false,
					IResource.DEPTH_ONE);
			for (IMarker marker : checkedMarkers) {
				char_start = marker.getAttribute(IMarker.CHAR_START, -1);
				length = marker.getAttribute(IMarker.CHAR_END, -1) - char_start;
				// Second, if there is one, then move on; if not, create one.
				if (char_start == node.getStart()
						&& length == node.getLength()) {
					return;
				}

			}

			IMarker[] markers = resource.findMarkers(
					Plugin.ANNOTATION_QUESTION, false, IResource.DEPTH_ONE);

			for (IMarker marker : markers) {
				char_start = marker.getAttribute(IMarker.CHAR_START, -1);
				length = marker.getAttribute(IMarker.CHAR_END, -1) - char_start;
				// Second, if there is one, then move on; if not, create one.
				if (char_start == node.getStart()
						&& length == node.getLength()) {
					marker.delete();
					createCheckedMarker(node, resource, unit, path.getChecks());
					return;
				}

			}

		} catch (CoreException e) {
			e.printStackTrace();
		}

	}

	private void createCheckedMarker(ASTNode node, IResource resource,
			Program unit, List<Point> checks) throws CoreException {
		String message = "Access control checks are at "
				+ unit.getLineNumber(node.getStart());

		IMarker questionCheckedMarker = resource
				.createMarker(Plugin.ANNOTATION_QUESTION_CHECKED);

		questionCheckedMarker.setAttribute(IMarker.CHAR_START,
				node.getStart());
		questionCheckedMarker.setAttribute(IMarker.CHAR_END,
				node.getStart() + node.getLength());
		questionCheckedMarker.setAttribute(IMarker.MESSAGE, message);
		questionCheckedMarker.setAttribute(IMarker.LINE_NUMBER,
				unit.getLineNumber(node.getStart()));
		questionCheckedMarker.setAttribute(IMarker.SEVERITY,
				IMarker.SEVERITY_INFO);
		questionCheckedMarker.setAttribute(IMarker.PRIORITY,
				IMarker.PRIORITY_HIGH);

	}

	private void popupDialogWarning() {
		MessageDialog
				.openInformation(
						shell,
						"ASIDE Code Annotation",
						"Please choose a sensitive information access point that is marked by a red flag.");

	}

	private void changeCursorToNormal(){
		
		Plugin.getDefault().getWorkbench().getDisplay().getActiveShell().setCursor(null);
	}
}
