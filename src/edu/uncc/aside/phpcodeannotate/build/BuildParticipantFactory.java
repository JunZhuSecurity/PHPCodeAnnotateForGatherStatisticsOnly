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
 *******************************************************************************/
package edu.uncc.aside.phpcodeannotate.build;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.builder.IBuildParticipant;
import org.eclipse.dltk.core.builder.IBuildParticipantFactory;

import edu.uncc.aside.phpcodeannotate.Plugin;

public class BuildParticipantFactory implements IBuildParticipantFactory {

	public IBuildParticipant createBuildParticipant(IScriptProject project) throws CoreException {
	    //right now, we don't need the always scanning, we just need one scanning, since we won't modify code
		return null; //new DetectionParticipant();
	}
}
