package edu.uncc.aside.phpcodeannotate.util;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.dltk.ast.references.VariableReference;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.builder.IBuildContext;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.php.internal.core.PHPVersion;
import org.eclipse.php.internal.core.ast.nodes.ASTNode;
import org.eclipse.php.internal.core.ast.nodes.ASTParser;
import org.eclipse.php.internal.core.ast.nodes.FieldAccess;
import org.eclipse.php.internal.core.ast.nodes.Identifier;
import org.eclipse.php.internal.core.ast.nodes.MethodInvocation;
import org.eclipse.php.internal.core.ast.nodes.Program;
import org.eclipse.php.internal.core.ast.nodes.Variable;
import org.eclipse.php.internal.core.compiler.ast.nodes.PHPCallExpression;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.texteditor.ITextEditor;

import edu.uncc.aside.phpcodeannotate.Constants;
import edu.uncc.aside.phpcodeannotate.Plugin;
import edu.uncc.aside.phpcodeannotate.models.AnnotationRecord;
import edu.uncc.aside.phpcodeannotate.models.MarkerRecord;
import edu.uncc.aside.phpcodeannotate.models.NodePositionInfo;
import edu.uncc.aside.phpcodeannotate.models.Path;
import edu.uncc.aside.phpcodeannotate.models.Point;

/*
 * Very useful utility class, currently copied from LapsePlus
 */
public class Utils {

	// this method is what I need now --Sept 20th
	public static ISourceModule compilationUnitOfInterest(IResource resource) {
		IFile file = (IFile) resource.getAdapter(IFile.class);
		return (ISourceModule) DLTKCore.create(file);
	}

	public static void removeMarkersOnPath(Path path) {
		if (path == null)
			return;
		Point accessor = path.getAccessor();
		removeMarkerOnPoint(accessor);
		List<Point> checks = path.getChecks();
		for (Point check : checks) {
			removeMarkerOnPoint(check);
		}

	}

	public static void removeMarkerOnPoint(Point point) {
		ASTNode node = point.getNode();
		IResource resource = point.getResource();

		try {
			int char_start, length;

			IMarker[] questionMarkers = resource.findMarkers(
					Plugin.ANNOTATION_QUESTION, false, IResource.DEPTH_ONE);
			IMarker[] checkedMarkers = resource.findMarkers(
					Plugin.ANNOTATION_QUESTION_CHECKED, false,
					IResource.DEPTH_ONE);
			IMarker[] answerMarkers = resource.findMarkers(
					Plugin.ANNOTATION_ANSWER, false, IResource.DEPTH_ONE);

			for (IMarker marker : questionMarkers) {
				char_start = marker.getAttribute(IMarker.CHAR_START, -1);
				length = marker.getAttribute(IMarker.CHAR_END, -1) - char_start;

				if (char_start == node.getStart() && length == node.getLength()) {
					marker.delete();
				}

			}

			for (IMarker marker : checkedMarkers) {
				char_start = marker.getAttribute(IMarker.CHAR_START, -1);
				length = marker.getAttribute(IMarker.CHAR_END, -1) - char_start;
				if (char_start == node.getStart() && length == node.getLength()) {
					marker.delete();
				}

			}

			for (IMarker marker : answerMarkers) {
				char_start = marker.getAttribute(IMarker.CHAR_START, -1);
				length = marker.getAttribute(IMarker.CHAR_END, -1) - char_start;
				if (char_start == node.getStart() && length == node.getLength()) {
					marker.delete();
				}

			}

		} catch (CoreException e) {
			e.printStackTrace();
		}

	}

	public static boolean isAssociatedWithMarker(ASTNode node,
			IResource resource) {

		try {
			int char_start, length;

			IMarker[] questionMarkers = resource.findMarkers(
					Plugin.ANNOTATION_QUESTION, false, IResource.DEPTH_ONE);
			IMarker[] checkedMarkers = resource.findMarkers(
					Plugin.ANNOTATION_QUESTION_CHECKED, false,
					IResource.DEPTH_ONE);

			for (IMarker marker : questionMarkers) {
				char_start = marker.getAttribute(IMarker.CHAR_START, -1);
				length = marker.getAttribute(IMarker.CHAR_END, -1) - char_start;

				if (char_start == node.getStart() && length == node.getLength()) {
					return true;
				}

			}

			for (IMarker marker : checkedMarkers) {
				char_start = marker.getAttribute(IMarker.CHAR_START, -1);
				length = marker.getAttribute(IMarker.CHAR_END, -1) - char_start;
				if (char_start == node.getStart() && length == node.getLength()) {
					return true;
				}

			}

		} catch (CoreException e) {
			e.printStackTrace();
		}

		return false;
	}

	public static IMarker getAssociatedMarker(ASTNode node, IResource resource) {

		try {
			int char_start, length;

			IMarker[] questionMarkers = resource.findMarkers(
					Plugin.ANNOTATION_QUESTION, false, IResource.DEPTH_ONE);
			IMarker[] checkedMarkers = resource.findMarkers(
					Plugin.ANNOTATION_QUESTION_CHECKED, false,
					IResource.DEPTH_ONE);

			for (IMarker marker : questionMarkers) {
				char_start = marker.getAttribute(IMarker.CHAR_START, -1);
				length = marker.getAttribute(IMarker.CHAR_END, -1) - char_start;

				if (char_start == node.getStart() && length == node.getLength()) {
					return marker;
				}

			}

			for (IMarker marker : checkedMarkers) {
				char_start = marker.getAttribute(IMarker.CHAR_START, -1);
				length = marker.getAttribute(IMarker.CHAR_END, -1) - char_start;
				if (char_start == node.getStart() && length == node.getLength()) {
					return marker;
				}

			}

		} catch (CoreException e) {
			e.printStackTrace();
		}

		return null;
	}
	public static void removeAllQuestionMarkers(IResource resource) {
		
			// First, gotta check whether there is a marker for the method
			// invocation
		
			IMarker[] markers = null;
			try {
				markers = resource.findMarkers(
						Plugin.ANNOTATION_QUESTION, false, IResource.DEPTH_ONE);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int char_start, length;
			System.out.println("number of markers removed = " + markers.length);
			for (IMarker marker : markers) {
				try {
					marker.delete();
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/*char_start = marker.getAttribute(IMarker.CHAR_START, -1);
				length = marker.getAttribute(IMarker.CHAR_END, -1) - char_start;
				// Second, if there is one, then move on; if not, create one.
				if (char_start == mi.getStart() && length == mi.getLength()){
					System.out.println("char_start == mi.getStart() && length == mi.getLength() in markAccessor");
					return;
				}*/
			}
		
	}
	
	@SuppressWarnings("restriction")
	public static void markAccessor(MethodInvocation mi, IResource resource) {
		try {
			// First, gotta check whether there is a marker for the method
			// invocation
			Program root = mi.getProgramRoot();
			if(root == null){
				System.err.println("root = null");
			}
			IMarker[] markers = resource.findMarkers(
					Plugin.ANNOTATION_QUESTION, false, IResource.DEPTH_ONE);
			int char_start, length;
			for (IMarker marker : markers) {
				char_start = marker.getAttribute(IMarker.CHAR_START, -1);
				length = marker.getAttribute(IMarker.CHAR_END, -1) - char_start;
				// Second, if there is one, then move on; if not, create one.
				if (char_start == mi.getStart() && length == mi.getLength()){
					System.out.println("char_start == mi.getStart() && length == mi.getLength() in markAccessor");
					return;
				}
			}
			

			IMarker questionMarker = resource
					.createMarker(Plugin.ANNOTATION_QUESTION);

			questionMarker.setAttribute(IMarker.CHAR_START, mi.getStart());
			questionMarker.setAttribute(IMarker.CHAR_END,
					mi.getStart() + mi.getLength());
			System.out.println("method invocation start=" + mi.getStart() + " , length =" + mi.getLength());
			questionMarker.setAttribute(IMarker.MESSAGE,
					"Where is the corresponding authentication process?");
			System.out.println("line number = " +root.getLineNumber(mi.getStart()));
			questionMarker.setAttribute(IMarker.LINE_NUMBER, root.getLineNumber(mi.getStart()));
			questionMarker.setAttribute(IMarker.SEVERITY,
					IMarker.SEVERITY_WARNING);
			questionMarker
					.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
			//test use
			IMarker[] omarkers = resource.findMarkers(
					Plugin.ANNOTATION_QUESTION, false, IResource.DEPTH_ONE);
			System.out.println("omarker size = " + omarkers.length);
			
		} catch (CoreException e) {
			e.printStackTrace();
		}

	}
	
	public static void createMarkerForAccessors(ISourceModule sourceModule, boolean isAnnotated, int markerStart, int markerLength){
		try {
			// First, gotta check whether there is a marker for the method
			// invocation
			IResource resource = sourceModule.getResource();
			Program root = null;
			try {
				root = Utils.getCompilationUnit(sourceModule);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(root == null){
				System.err.println("root = null");
			}
		/*	IMarker[] markers = resource.findMarkers(
					Plugin.ANNOTATION_QUESTION, false, IResource.DEPTH_ONE);
			int char_start = 0, length = 0;
			for (IMarker marker : markers) {
				char_start = marker.getAttribute(IMarker.CHAR_START, -1);
				length = marker.getAttribute(IMarker.CHAR_END, -1) - char_start;
				// Second, if there is one, then move on; if not, create one.
				if (char_start == markerStart && length == markerLength){
					System.out.println("char_start == markerStart && length == markerLength");
					return;
				}
			}*/
			
if(isAnnotated == false){//not annotated yet, show red warnings with questions
	createAMarker(resource, root, markerStart, markerLength, Plugin.ANNOTATION_QUESTION);
}else{//annotated, then show yellow warnings
	createAMarker(resource, root, markerStart, markerLength, Plugin.ANNOTATION_QUESTION_CHECKED);
}
			
			//test use
			/*IMarker[] omarkers = resource.findMarkers(
					Plugin.ANNOTATION_QUESTION, false, IResource.DEPTH_ONE);
			System.out.println("omarker size = " + omarkers.length);*/
			
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	public static void createAnnotations(ISourceModule sourceModule, int markerStart, int markerLength){
		try {
			// First, gotta check whether there is a marker for the method
			// invocation
			IResource resource = sourceModule.getResource();
			Program root = null;
			try {
				root = Utils.getCompilationUnit(sourceModule);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(root == null){
				System.err.println("root = null");
			}
	
	createAMarker(resource, root, markerStart, markerLength, Plugin.ANNOTATION_ANSWER);
	
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	public static void createAMarker(IResource resource, Program root, int start, int length, String markerType) throws CoreException{
		if(markerType.equals(Plugin.ANNOTATION_QUESTION)){
			IMarker questionMarker = resource
					.createMarker(Plugin.ANNOTATION_QUESTION);

			questionMarker.setAttribute(IMarker.CHAR_START, start);
			questionMarker.setAttribute(IMarker.CHAR_END,
					start + length);
			questionMarker.setAttribute(IMarker.MESSAGE,
					Constants.QUESTION_MESSAGE);
			questionMarker.setAttribute(IMarker.LINE_NUMBER, root.getLineNumber(start));
			
			questionMarker.setAttribute(IMarker.SEVERITY,
					IMarker.SEVERITY_WARNING);
			questionMarker
					.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
		}else if(markerType.equals(Plugin.ANNOTATION_QUESTION_CHECKED)){
			String message = "Access control checks are at "
					+ root.getLineNumber(start);

			IMarker questionCheckedMarker = resource
					.createMarker(Plugin.ANNOTATION_QUESTION_CHECKED);

			questionCheckedMarker.setAttribute(IMarker.CHAR_START,
					start);
			questionCheckedMarker.setAttribute(IMarker.CHAR_END,
					start + length);
			questionCheckedMarker.setAttribute(IMarker.MESSAGE, message);
			questionCheckedMarker.setAttribute(IMarker.LINE_NUMBER,
					root.getLineNumber(start));
			questionCheckedMarker.setAttribute(IMarker.SEVERITY,
					IMarker.SEVERITY_INFO);
			questionCheckedMarker.setAttribute(IMarker.PRIORITY,
					IMarker.PRIORITY_HIGH);
		}else if(markerType.equals(Plugin.ANNOTATION_ANSWER)){
			IMarker answerMarker = resource.createMarker(Plugin.ANNOTATION_ANSWER);

			answerMarker.setAttribute(IMarker.CHAR_START,
					start);
			answerMarker.setAttribute(IMarker.CHAR_END, start + length);
			answerMarker.setAttribute(IMarker.MESSAGE,
					Constants.LOGIC_MARKER_MESSAGE);
			answerMarker.setAttribute(IMarker.LINE_NUMBER,
					root.getLineNumber(start));
			answerMarker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
			answerMarker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
		}
	}
	
	public static void markAccessor(PHPCallExpression node, IResource resource,
			IBuildContext context) {
		try {
			// First, gotta check whether there is a marker for the method
			// invocation
			IMarker[] markers = resource.findMarkers(
					Plugin.ANNOTATION_QUESTION, false, IResource.DEPTH_ONE);
			int char_start, char_end;
			for (IMarker marker : markers) {
				char_start = marker.getAttribute(IMarker.CHAR_START, -1);
				char_end = marker.getAttribute(IMarker.CHAR_END, -1);
				// Second, if there is one, then move on; if not, create one.
				if (char_start == node.sourceStart()
						&& char_end == node.sourceEnd())
					return;
			}

			IMarker questionMarker = resource
					.createMarker(Plugin.ANNOTATION_QUESTION);

			questionMarker.setAttribute(IMarker.CHAR_START, node.sourceStart());
			questionMarker.setAttribute(IMarker.CHAR_END, node.sourceEnd());
			questionMarker.setAttribute(IMarker.MESSAGE,
					"Where is the corresponding authentication process?");
			questionMarker
					.setAttribute(IMarker.LINE_NUMBER, context.getLineTracker()
							.getLineNumberOfOffset(node.sourceStart()));
			questionMarker.setAttribute(IMarker.SEVERITY,
					IMarker.SEVERITY_WARNING);
			questionMarker
					.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);

		} catch (CoreException e) {
			e.printStackTrace();
		}

	}

	public static void createQuestionMarker(ASTNode node, IResource resource,
			Program unit) throws CoreException {
		String message = "What is the corresponding authentication?";

		IMarker questionCheckedMarker = resource
				.createMarker(Plugin.ANNOTATION_QUESTION);
		questionCheckedMarker.setAttribute(IMarker.CHAR_START, node.getStart());
		questionCheckedMarker.setAttribute(IMarker.CHAR_END, node.getStart()
				+ node.getLength());
		questionCheckedMarker.setAttribute(IMarker.MESSAGE, message);
		questionCheckedMarker.setAttribute(IMarker.LINE_NUMBER,
				unit.getLineNumber(node.getStart()));

		questionCheckedMarker.setAttribute(IMarker.SEVERITY,
				IMarker.SEVERITY_WARNING);
		questionCheckedMarker.setAttribute(IMarker.PRIORITY,
				IMarker.PRIORITY_HIGH);
	}

	/**
	 * Checks whether this variable is a reference to the URL parameter.
	 * 
	 * @param s
	 *            Variable reference node
	 * @return
	 */
	public boolean isURLParemeterVariable(VariableReference s) {
		String name = s.getName();
		return ("$_GET".equals(name) || "$_POST".equals(name) || "$_REQUEST"
				.equals(name));
	}

	public boolean isSensitiveOperation(VariableReference s) {
		String name = s.getName();
		return ("$db->query".equals(name) || "db->exec".equals(name));
	}
//phpVersion string, such as php4, php5
	public static Program getCompilationUnit(ISourceModule unit) throws Exception {
		StringReader st = new StringReader (unit.getBuffer().getContents());
        ASTParser parser = ASTParser.newParser(st, PHPVersion.byAlias(Constants.PHP_VERSION),  false, unit);
        return (Program)parser.createAST(null);
	}
	
	
	public static class EditorUtility {
		private EditorUtility() {
			super();
		}

		public static IEditorPart getActiveEditor() {
			IWorkbenchWindow window= Plugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
			if (window != null) {
				IWorkbenchPage page= window.getActivePage();
				if (page != null) {
					return page.getActiveEditor();
				}
			}
			return null;
		}
		
		public static ISourceModule getPhpInput(IEditorPart part) {
			IEditorInput editorInput= part.getEditorInput();
			if (editorInput != null) {
				ISourceModule input= (ISourceModule) DLTKUIPlugin.getEditorInputModelElement(editorInput);
				return input;
			}
			return null;	
		}

		public static void selectInEditor(ITextEditor editor, int offset, int length) {
			IEditorPart active = getActiveEditor();
			if (active != editor) {
				editor.getSite().getPage().activate(editor);
			}
			editor.selectAndReveal(offset, length);
		}
	}
	// ////////////above are what I need now
	/*
	 * public static class ExprUnitResource { Expression expr; CompilationUnit
	 * cu; IResource resource;
	 * 
	 * public ExprUnitResource(Expression expr, CompilationUnit cu, IResource
	 * resource) { this.expr = expr; this.cu = cu; this.resource = resource; }
	 * 
	 * public CompilationUnit getCompilationUnit() { return cu; }
	 *//**
	 * This is either a MethodInvocation or a ClassInstanceCreation.
	 * */
	/*
	 * public Expression getExpression() { return expr; }
	 * 
	 * public IResource getResource() { return resource; }
	 * 
	 * public String toString() { if (expr != null && resource != null && cu !=
	 * null) return expr.toString() + " in " + resource.getName() + " at Line "
	 * + cu.getLineNumber(expr.getStartPosition()); else return ""; } }
	 * 
	 * public static class ExprUnitResourceMember extends ExprUnitResource {
	 * private IMember member;
	 * 
	 * public ExprUnitResourceMember(Expression expr, CompilationUnit cu,
	 * IResource resource, IMember member) { super(expr, cu, resource);
	 * this.member = member; }
	 * 
	 * public IMember getMember() { return member; }
	 * 
	 * public String toString() { return super.toString(); } }
	 * 
	 * public static class MethodDeclarationUnitPair { MethodDeclaration method;
	 * CompilationUnit cu; IResource resource; IMember member;
	 * 
	 * public MethodDeclarationUnitPair(MethodDeclaration method,
	 * CompilationUnit cu, IResource resource, IMember member) { this.method =
	 * method; this.cu = cu; this.resource = resource; this.member = member; }
	 * 
	 * public CompilationUnit getCompilationUnit() { return cu; }
	 * 
	 * public MethodDeclaration getMethod() { return method; }
	 * 
	 * public IResource getResource() { return resource; }
	 * 
	 * public IMember getMember() { return member; }
	 * 
	 * public String toString() { if (method != null && member != null &&
	 * resource != null) return method.toString() + " in " + resource.getName()
	 * + " at Line " + cu.getLineNumber(method.getStartPosition()); else return
	 * ""; } }
	 * 
	 * public static MethodDeclaration getParentMethodDeclaration(ASTNode node)
	 * { if (node == null) return null; ASTNode parent = node.getParent(); if
	 * (parent == null || parent.getNodeType() == ASTNode.COMPILATION_UNIT)
	 * return null;
	 * 
	 * if (parent.getNodeType() == ASTNode.METHOD_DECLARATION) return
	 * (MethodDeclaration) parent; else return
	 * getParentMethodDeclaration(parent);
	 * 
	 * }
	 * 
	 * 
	 * public static boolean isMethodInvocationOfInterest(MethodInvocation node,
	 * Collection<SinkDescription> accessors) {
	 * 
	 * IMethodBinding binding = node.resolveMethodBinding(); if (binding ==
	 * null) return false; ITypeBinding typeBinding =
	 * binding.getDeclaringClass(); if (typeBinding == null) return false;
	 * String methodName = binding.getName(); String className =
	 * typeBinding.getQualifiedName(); for (SinkDescription accessor :
	 * accessors) { if (accessor.getMethodName().equals(className + "." +
	 * methodName) && accessor.getTypeName().equals(className)) { return true; }
	 * }
	 * 
	 * return false; }
	 *//**
	 * 
	 * @param target
	 *            the accessor node of a path stored in a path collector.
	 * @return whether the target is still in the compilation unit as it was.
	 */
	/*
	 * 
	 * 
	 * public static ICompilationUnit compilationUnitOfInterest(IResource
	 * resource) { IFile file = (IFile) resource.getAdapter(IFile.class);
	 * 
	 * return (ICompilationUnit) JavaCore.create(file); }
	 * 
	 * /////////////above are what I need now - sept 20
	 * 
	 * public static IMethod convertMethodDecl2IMethod( MethodDeclaration
	 * methodDecl, IResource resource) {
	 * 
	 * try { ICompilationUnit iCompilationUnit = JavaCore
	 * .createCompilationUnitFrom((IFile) resource); int startPos =
	 * methodDecl.getStartPosition(); IJavaElement element =
	 * iCompilationUnit.getElementAt(startPos); if (element instanceof IMethod)
	 * { return (IMethod) element; } return null; } catch (JavaModelException e)
	 * { e.printStackTrace(); } return null; }
	 * 
	 * public static CompilationUnit getCompilationUnit(ICompilationUnit unit) {
	 * 
	 * ASTParser parser = ASTParser.newParser(AST.JLS3);
	 * parser.setKind(ASTParser.K_COMPILATION_UNIT); parser.setSource(unit);
	 * parser.setResolveBindings(true);
	 * 
	 * return (CompilationUnit) parser.createAST(null); }
	 * 
	 * // TODO: hard coded for now public static boolean isEntranceMethod(String
	 * new_accessor_id) { if (new_accessor_id.equals("execute") ||
	 * new_accessor_id.equals("doPost") || new_accessor_id.equals("doGet") ||
	 * new_accessor_id.equals("processRequest") ||
	 * new_accessor_id.equals("onSubmit") ||
	 * new_accessor_id.equals("referenceData")) { return true; } return false; }
	 * 
	 * // TODO: this is a simplified version public static boolean
	 * isEntranceMethodDeclaration(MethodDeclaration node) { String name =
	 * node.getName().getIdentifier(); return isEntranceMethod(name); }
	 * 
	 * 
	 * 
	 * 
	 * public static void removeMarkersOnPath(Path path) { if(path == null)
	 * return; Point accessor = path.getAccessor();
	 * removeMarkerOnPoint(accessor); List<Point> checks = path.getChecks(); for
	 * (Point check : checks) { removeMarkerOnPoint(check); }
	 * 
	 * }
	 * 
	 * public static void removeMarkerOnPoint(Point point) { ASTNode node =
	 * point.getNode(); IResource resource = point.getResource();
	 * 
	 * try { int char_start, length;
	 * 
	 * IMarker[] questionMarkers = resource.findMarkers(
	 * Plugin.ANNOTATION_QUESTION, false, IResource.DEPTH_ONE); IMarker[]
	 * checkedMarkers = resource.findMarkers(
	 * Plugin.ANNOTATION_QUESTION_CHECKED, false, IResource.DEPTH_ONE);
	 * IMarker[] answerMarkers = resource.findMarkers( Plugin.ANNOTATION_ANSWER,
	 * false, IResource.DEPTH_ONE);
	 * 
	 * for (IMarker marker : questionMarkers) { char_start =
	 * marker.getAttribute(IMarker.CHAR_START, -1); length =
	 * marker.getAttribute(IMarker.CHAR_END, -1) - char_start;
	 * 
	 * if (char_start == node.getStartPosition() && length == node.getLength())
	 * { marker.delete(); }
	 * 
	 * }
	 * 
	 * for (IMarker marker : checkedMarkers) { char_start =
	 * marker.getAttribute(IMarker.CHAR_START, -1); length =
	 * marker.getAttribute(IMarker.CHAR_END, -1) - char_start; if (char_start ==
	 * node.getStartPosition() && length == node.getLength()) { marker.delete();
	 * }
	 * 
	 * }
	 * 
	 * for (IMarker marker : answerMarkers) { char_start =
	 * marker.getAttribute(IMarker.CHAR_START, -1); length =
	 * marker.getAttribute(IMarker.CHAR_END, -1) - char_start; if (char_start ==
	 * node.getStartPosition() && length == node.getLength()) { marker.delete();
	 * }
	 * 
	 * }
	 * 
	 * } catch (CoreException e) { e.printStackTrace(); }
	 * 
	 * }
	 * 
	 * public static boolean isAssociatedWithMarker(ASTNode node, IResource
	 * resource) {
	 * 
	 * try { int char_start, length;
	 * 
	 * IMarker[] questionMarkers = resource.findMarkers(
	 * Plugin.ANNOTATION_QUESTION, false, IResource.DEPTH_ONE); IMarker[]
	 * checkedMarkers = resource.findMarkers(
	 * Plugin.ANNOTATION_QUESTION_CHECKED, false, IResource.DEPTH_ONE);
	 * 
	 * for (IMarker marker : questionMarkers) { char_start =
	 * marker.getAttribute(IMarker.CHAR_START, -1); length =
	 * marker.getAttribute(IMarker.CHAR_END, -1) - char_start;
	 * 
	 * if (char_start == node.getStartPosition() && length == node.getLength())
	 * { return true; }
	 * 
	 * }
	 * 
	 * for (IMarker marker : checkedMarkers) { char_start =
	 * marker.getAttribute(IMarker.CHAR_START, -1); length =
	 * marker.getAttribute(IMarker.CHAR_END, -1) - char_start; if (char_start ==
	 * node.getStartPosition() && length == node.getLength()) { return true; }
	 * 
	 * }
	 * 
	 * } catch (CoreException e) { e.printStackTrace(); }
	 * 
	 * return false; }
	 * 
	 * public static IMarker getAssociatedMarker(ASTNode node, IResource
	 * resource) {
	 * 
	 * try { int char_start, length;
	 * 
	 * IMarker[] questionMarkers = resource.findMarkers(
	 * Plugin.ANNOTATION_QUESTION, false, IResource.DEPTH_ONE); IMarker[]
	 * checkedMarkers = resource.findMarkers(
	 * Plugin.ANNOTATION_QUESTION_CHECKED, false, IResource.DEPTH_ONE);
	 * 
	 * for (IMarker marker : questionMarkers) { char_start =
	 * marker.getAttribute(IMarker.CHAR_START, -1); length =
	 * marker.getAttribute(IMarker.CHAR_END, -1) - char_start;
	 * 
	 * if (char_start == node.getStartPosition() && length == node.getLength())
	 * { return marker; }
	 * 
	 * }
	 * 
	 * for (IMarker marker : checkedMarkers) { char_start =
	 * marker.getAttribute(IMarker.CHAR_START, -1); length =
	 * marker.getAttribute(IMarker.CHAR_END, -1) - char_start; if (char_start ==
	 * node.getStartPosition() && length == node.getLength()) { return marker; }
	 * 
	 * }
	 * 
	 * } catch (CoreException e) { e.printStackTrace(); }
	 * 
	 * return null; }
	 */

	public static boolean isSecurityInterest(MethodInvocation node) {
		Identifier methodIdentifier = null;
		
		if (node.getMethod().getFunctionName().getFunctionName().getType() == 60) {
			Variable varNode = (Variable) node.getMethod().getFunctionName()
					.getFunctionName();
			if (varNode.getVariableName().getType() == 33){
				methodIdentifier = (Identifier) varNode.getVariableName();
				String methodName = methodIdentifier.getName();
				System.out.println("methodName = " + methodName);
		if(isSensitiveOperation(methodName)){
		
		if (node.getDispatcher().getType() == 60) {
			ASTNode dispatcherVarName = ((Variable) node.getDispatcher()).getName();
		
			if (dispatcherVarName.getType() == 33){
				Identifier dispatcherIden = (Identifier) dispatcherVarName;
				String dispatcherStr = dispatcherIden.getName();
				System.out.println("dispatcherStr = " +dispatcherStr);
//				System.out.println("first branch: identifier = " + currentNode.toString());
				if(dispatcherStr.equals("db")){ // should create a method testing the type binding of the dispatcher, together with the functiona invocation test
				return true;
			}
				
		} else if (node.getDispatcher().getType() == 24) {
			FieldAccess field = (FieldAccess) node.getDispatcher();
			if (field.getDispatcher().getType() == 60) {
				System.out.println("in field dispatcher");
				ASTNode fieldObjName = ((Variable) field.getDispatcher())
						.getName();
				System.out.println("(Identifier) fieldObjName).getName()="+ ((Identifier) fieldObjName).getName());
				ASTNode fieldName = null;
				if (fieldObjName.getType() == 33
						&& "this".equals(((Identifier) fieldObjName).getName())
						&& field.getMember().getType() == 60){ // I changed "
					fieldName = ((Variable) field.getMember())
							.getName();
				System.out.println("this fieldName = " + fieldName);	
				}
				
				if (fieldObjName.getType() == 33
						&& "db".equals(((Identifier) fieldObjName).getName())
						&& field.getMember().getType() == 60){ // I changed "
					fieldName = ((Variable) field.getMember())
							.getVariableName();
				System.out.println("db fieldName = " + fieldName);	
				}
				/*if (fieldName != null && fieldName.getType() == 33)
					instanceIdentifier = new VariableIdentifier(
							net.sourceforge.refactor4pdt.core.variablescope.VariableIdentifier.Types.FIELD,
							net.sourceforge.refactor4pdt.core.variablescope.VariableIdentifier.Calls.DECLARATION,
							(Identifier) fieldName);*/
			}
		}
			}
			}
		
		
	}
		}
		return false;
	}
	

	private static boolean isSensitiveOperation(String methodName) {
	    Set<String> sensitiveOperations = new HashSet();
	    sensitiveOperations.add("query");
	    sensitiveOperations.add("exec");
		if(sensitiveOperations.contains(methodName))
			return true;
		return false;
	}

	public static HashSet<MarkerRecord> getMarkerRecordsForSingleFile(
			HashSet<MarkerRecord> allMarkerRecords, String fileDir) {
		HashSet<MarkerRecord> records = new HashSet<MarkerRecord>();
		Iterator<MarkerRecord> iter = allMarkerRecords.iterator();
		MarkerRecord record = null;

		while(iter.hasNext()){
			record = iter.next();
			if(record.getNodePositionInfo().getFileDir().equals(fileDir)){
				records.add(record);
			}
		}
		return records;
	}

	public static void createMarkersForSingleFile(
			HashSet<MarkerRecord> markerRecordsInSingleFile, ISourceModule sourceModule) {
		IResource resource = sourceModule.getResource();
		Iterator<MarkerRecord> iter = markerRecordsInSingleFile.iterator();
		MarkerRecord record = null;
		while(iter.hasNext()){
	    record = iter.next();
	    createMarkerForAccessors(sourceModule, record.isAnnotated(), record.getNodePositionInfo().getStartPosition(), record.getNodePositionInfo().getLength());
	}
	}

	public static MarkerRecord getMarkerRecordByPositionInfo(
			NodePositionInfo markerPositionInfo,
			HashSet<MarkerRecord> allMarkerRecords) {
		MarkerRecord record = null;
		Iterator<MarkerRecord> iter = allMarkerRecords.iterator();
		while(iter.hasNext()){
			record = iter.next();
			if(record.getNodePositionInfo().equals(markerPositionInfo)){
				System.out.println("one marker record is matched!");
				return record;
			}
		}
		return null;
	}

	public static AnnotationRecord getAnnotationRecordByPositionInfo(
			NodePositionInfo annotationPositionInfo,
			HashSet<AnnotationRecord> allAnnotationRecords) {
		AnnotationRecord record = null;
		Iterator<AnnotationRecord> iter = allAnnotationRecords.iterator();
		while(iter.hasNext()){
			record = iter.next();
			if(record.getNodePositionInfo().equals(annotationPositionInfo)){
				System.out.println("one annotation record is matched!");
				return record;
			}
		}
		return null;
	}

	public static HashSet<AnnotationRecord> getAnnotationRecordsForSingleFile(
			HashSet<AnnotationRecord> allAnnotationRecords, String fileDir) {
		HashSet<AnnotationRecord> records = new HashSet<AnnotationRecord>();
		Iterator<AnnotationRecord> iter = allAnnotationRecords.iterator();
		AnnotationRecord record = null;

		while(iter.hasNext()){
			record = iter.next();
			if(record.getNodePositionInfo().getFileDir().equals(fileDir)){
				records.add(record);
			}
		}
		return records;
	}

	public static void createAnnotationsForSingleFile(
			HashSet<AnnotationRecord> annotationRecordsInSingleFile,
			ISourceModule sourceModule) {
		IResource resource = sourceModule.getResource();
		Iterator<AnnotationRecord> iter = annotationRecordsInSingleFile.iterator();
		AnnotationRecord record = null;
		while(iter.hasNext()){
	    record = iter.next();
	    createAnnotations(sourceModule, record.getNodePositionInfo().getStartPosition(), record.getNodePositionInfo().getLength());
	}
		
	}
	public static String getPlugingBasePath(){
Plugin plugin = Plugin.getDefault();
URL base = plugin.getBundle().getEntry("/");

try {
return FileLocator.toFileURL(base).getFile() + "/";
} catch (IOException e) {	
e.printStackTrace();
return "";
}	
}
}
