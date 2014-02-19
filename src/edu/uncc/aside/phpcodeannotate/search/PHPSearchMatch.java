/*******************************************************************************
 * Copyright (c) 2009, 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package edu.uncc.aside.phpcodeannotate.search;

import org.eclipse.core.resources.IResource;
import org.eclipse.dltk.internal.core.SourceType;

import edu.uncc.aside.phpcodeannotate.util.PHPToolkitUtil;

@SuppressWarnings("restriction")
public class PHPSearchMatch {
	private SourceType element;
	private IResource resource;

	public PHPSearchMatch(SourceType element, IResource resource) {
		this.element = element;
		this.resource = resource;
	}

	public SourceType getElement() {
		return element;
	}

	public IResource getResource() {
		return resource;
	}

	public String toString() {
		String name = PHPToolkitUtil.getClassNameWithNamespace(element
				.getSourceModule());
		name += " - " + resource.getFullPath().toOSString();
		return name;
	}
}
