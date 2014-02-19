package edu.uncc.aside.phpcodeannotate.visitors;

/*
 Refactor4PDT an Eclipse PDT plugin to refactor php code.
 Copyright (C) 2008-2009  Vivek Narendra


 This program is free software: you can redistribute it and/or modify 
 it under the terms of the GNU General Public License as published by 
 the Free Software Foundation, either version 3 of the License, or 
 at your option any later version. 

 This program is distributed in the hope that it will be useful, 
 but WITHOUT ANY WARRANTY; without even the implied warranty of 
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 GNU General Public License for more details. 

 You should have received a copy of the GNU General Public License 
 along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 */

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import edu.uncc.aside.phpcodeannotate.log.Log;
import org.eclipse.php.internal.core.ast.nodes.ASTNode;
import org.eclipse.php.internal.core.ast.nodes.FunctionDeclaration;
import org.eclipse.php.internal.core.ast.nodes.FunctionInvocation;
import org.eclipse.php.internal.core.ast.nodes.Identifier;
import org.eclipse.php.internal.core.ast.visitor.ApplyAll;

public class PhpVisitor extends ApplyAll {

	public PhpVisitor() {
		checkFunction = false;
		dangerousFunctionCall = new HashMap<Integer, Identifier>();
	}

	public boolean hasDangerousFunctionCall() {
		return dangerousFunctionCall.size() != 0;
	}

	public Collection<Identifier> getDangerousFunctionCalls() {
		return dangerousFunctionCall.values();
	}

	@SuppressWarnings("restriction")
	protected void checkFunction(FunctionInvocation node) {
		if (node.getFunctionName().getName().getType() == 33) {
			Identifier funcName = (Identifier) node.getFunctionName().getName();
			if (dangerousFunctions.contains(funcName.getName())) {
				dangerousFunctionCall.put(Integer.valueOf(funcName.getStart()),
						funcName);

			}
		} else {

		}
	}

	public void enableFunctionCheck() {
		checkFunction = true;
		functionDeclaration = new HashMap<Integer, FunctionDeclaration>();
	}

	@Override
	public boolean visit(FunctionDeclaration node) {
		if (checkFunction && node.getParent() != null
				&& node.getParent().getType() != 42)
			functionDeclaration.put(Integer.valueOf(node.getStart()), node);
		return checkFunction;
	}

	public FunctionDeclaration getFunctionDeclaration(String name) {
		if (checkFunction) {
			for (Iterator<FunctionDeclaration> iterator = functionDeclaration
					.values().iterator(); iterator.hasNext();) {
				FunctionDeclaration currentNode = iterator.next();
				if (currentNode.getFunctionName().getName().equals(name))
					return currentNode;
			}

		}
		return null;
	}

	protected static HashSet<String> dangerousFunctions;
	private HashMap<Integer, Identifier> dangerousFunctionCall;
	private HashMap<Integer, FunctionDeclaration> functionDeclaration;
	private boolean checkFunction;

	static {
		dangerousFunctions = new HashSet<String>();
		dangerousFunctions.add("eval");
	}

	@Override
	protected boolean apply(ASTNode node) {
		return false;
	}
}
