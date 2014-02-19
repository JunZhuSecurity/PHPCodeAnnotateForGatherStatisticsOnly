/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Zend Technologies
 *     Modified by: Jun Zhu, jzhu16 AT uncc.edu
 *******************************************************************************/

package edu.uncc.aside.phpcodeannotate.build;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.SourceParserUtil;
import org.eclipse.dltk.core.builder.IBuildContext;
import org.eclipse.dltk.core.builder.IBuildParticipant;
import org.eclipse.php.internal.core.ast.nodes.Program;

import edu.uncc.aside.phpcodeannotate.Plugin;
import edu.uncc.aside.phpcodeannotate.util.Utils;
import edu.uncc.aside.phpcodeannotate.visitors.SensitiveOperationVisitor;

/**
 * This build participant is invoked just after the PHP builder
 * It validates the PHP code for places where XSS can be applied
 * (http://en.wikipedia.org/wiki/Cross-site_scripting)
 */
public class DetectionParticipant implements IBuildParticipant {

	public void build(IBuildContext context) throws CoreException {
		// Current file is being built:
		ISourceModule sourceModule = context.getSourceModule();
		/*sourceModule.get
		for (IScriptFolder folder : folders) {
			String folderName = folder.getElementName();
			System.out.println("folder name = " + folderName);
			//if(!Constants.PHPLibraryFolders.contains(folderName)){
			
		if(){*/
		System.out.println("isourcemodule being built = " + sourceModule.getElementName().toLowerCase());
		SensitiveOperationVisitor visitor = new SensitiveOperationVisitor(sourceModule);
		Program root = null;
		try {
			root = Utils.getCompilationUnit(sourceModule);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("begin of traverseTopDown");
		root.traverseTopDown(visitor);
		System.out.println("end of traverseTopDown");
	
		//visitor.apply(root);
		
		// Get file AST:
		/*ModuleDeclaration moduleDeclaration = SourceParserUtil.getModuleDeclaration(sourceModule);
		// Run the validation visitor:
		try {
			moduleDeclaration.traverse(new SensitiveOperationVisitor(context, sourceModule));
		} catch (Exception e) {
			throw new CoreException(new Status(
				IStatus.ERROR, Plugin.PLUGIN_ID, "An error has occurred while invoking SensitiveOperationVisitor", e));
		}*/
	}
}

