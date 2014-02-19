package edu.uncc.aside.phpcodeannotate.log;

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

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.RefactoringStatusEntry;

public class Log {
	public enum Level {

		FATAL_ERROR, ERROR, WARNING, INFO_L1, INFO_L2, INFO_L3, INFO_L4, UNKNOWN
	}

	public Log() {
	}

	@SuppressWarnings("unchecked")
	public static void write(Level level, Class refClass, String method,
			String message) {
		printMessage((new StringBuilder("[")).append(getClassName(refClass))
				.append("::").append(method).append("] ").append(message)
				.toString(), level);
	}

	@SuppressWarnings("unchecked")
	public static void write(Class refClass, String method,
			RefactoringStatus status) {
		write(Level.INFO_L4, refClass, method, status);
	}

	@SuppressWarnings("unchecked")
	public static void write(Level level, Class refClass, String method,
			RefactoringStatus status) {
		if (!status.isOK()) {
			RefactoringStatusEntry arefactoringstatusentry[];
			int j = (arefactoringstatusentry = status.getEntries()).length;
			for (int i = 0; i < j; i++) {
				RefactoringStatusEntry entry = arefactoringstatusentry[i];
				printMessage(
						(new StringBuilder("["))
								.append(getClassName(refClass))
								.append("::")
								.append(method)
								.append("] ")
								.append(getRefactoringStatusSeverity(entry
										.getSeverity())).append(" ")
								.append(entry.getMessage()).toString(),
						level != null ? level
								: getRefactoringStatusSeverity(entry
										.getSeverity()));
			}

		} else {
			printMessage((new StringBuilder("["))
					.append(getClassName(refClass)).append("::").append(method)
					.append("] ").append("status is ok").toString(), level);
		}
	}

	@SuppressWarnings("unchecked")
	public static void write(Level level, Class refClass, String method,
			String message, int astType) {
		printMessage((new StringBuilder("[")).append(getClassName(refClass))
				.append("::").append(method).append("] ").append(message)
				.append(": ").append(getRealASTNodeTypeName(astType))
				.toString(), level);
	}

	@SuppressWarnings("unchecked")
	public static void write(Level level, Class refClass, String method,
			String message, Exception e) {
		printMessage((new StringBuilder("[")).append(getClassName(refClass))
				.append("::").append(method).append("] ").append(message)
				.toString(), level);
		e.printStackTrace();
	}

	@SuppressWarnings("unchecked")
	private static String getClassName(Class refClass) {
		String className = refClass.getName();
		return className.substring(className.lastIndexOf('.') + 1);
	}

	private static Level getRefactoringStatusSeverity(int severity) {
		Level level;
		switch (severity) {
		case 0: // '\0'
			level = Level.INFO_L2;
			break;

		case 1: // '\001'
			level = Level.INFO_L1;
			break;

		case 2: // '\002'
			level = Level.WARNING;
			break;

		case 3: // '\003'
			level = Level.ERROR;
			break;

		case 4: // '\004'
			level = Level.FATAL_ERROR;
			break;

		default:
			level = Level.UNKNOWN;
			break;
		}
		return level;
	}

	private static void printMessage(String message, Level level) {
		message = (new StringBuilder("["))
				.append(dateFormat.format(new Date())).append("][")
				.append(level).append("]").append(message).toString();
		if (enableDebug && stdLevel.compareTo(level) >= 0)
			if (errorLevel.compareTo(level) >= 0)
				psError.println(message);
			else
				psStd.println(message);
	}

	private static String getRealASTNodeTypeName(int number) {
		return (new StringBuilder(String.valueOf(number >= 10 ? "" : "0")))
				.append(number).append(" (").append(astNodeType[number])
				.append(")").toString();
	}

	private static DateFormat dateFormat = new SimpleDateFormat(
			"yyyy.MM.dd HH:mm:ss");
	private static PrintStream psStd;
	private static PrintStream psError;
	private static Level stdLevel;
	private static Level errorLevel;
	private static boolean enableDebug = false;
	private static String astNodeType[] = { "ARRAY_ACCESS", "ARRAY_CREATION",
			"ARRAY_ELEMENT", "ASSIGNMENT", "AST_ERROR", "BACK_TICK_EXPRESSION",
			"BLOCK", "BREAK_STATEMENT", "CAST_EXPRESSION", "CATCH_CLAUSE",
			"STATIC_CONSTANT_ACCESS", "CLASS_CONSTANT_DECLARATION",
			"CLASS_DECLARATION", "CLASS_INSTANCE_CREATION", "CLASS_NAME",
			"CLONE_EXPRESSION", "COMMENT", "CONDITIONAL_EXPRESSION",
			"CONTINUE_STATEMENT", "DECLARE_STATEMENT", "DO_STATEMENT",
			"ECHO_STATEMENT", "EMPTY_STATEMENT", "EXPRESSION_STATEMENT",
			"FIELD_ACCESS", "FIELD_DECLARATION", "FOR_EACH_STATEMENT",
			"FORMAL_PARAMETER", "FOR_STATEMENT", "FUNCTION_DECLARATION",
			"FUNCTION_INVOCATION", "FUNCTION_NAME", "GLOBAL_STATEMENT",
			"IDENTIFIER", "IF_STATEMENT", "IGNORE_ERROR", "INCLUDE",
			"INFIX_EXPRESSION", "IN_LINE_HTML", "INSTANCE_OF_EXPRESSION",
			"INTERFACE_DECLARATION", "LIST_VARIABLE", "METHOD_DECLARATION",
			"METHOD_INVOCATION", "POSTFIX_EXPRESSION", "PREFIX_EXPRESSION",
			"PROGRAM", "QUOTE", "REFERENCE", "REFLECTION_VARIABLE",
			"RETURN_STATEMENT", "SCALAR", "STATIC_FIELD_ACCESS",
			"STATIC_METHOD_INVOCATION", "STATIC_STATEMENT", "SWITCH_CASE",
			"SWITCH_STATEMENT", "THROW_STATEMENT", "TRY_STATEMENT",
			"UNARY_OPERATION", "VARIABLE", "WHILE_STATEMENT",
			"PARENTHESIS_EXPRESSION" };

	static {
		psStd = System.out;
		psError = System.err;
		stdLevel = Level.INFO_L4;
		errorLevel = Level.ERROR;
	}
}
