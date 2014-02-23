package edu.uncc.aside.phpcodeannotate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Constants {
	public final static String ContentTypeID_PHP = "org.eclipse.php.core.phpsource";
	public final static String PHPNatureID = "org.eclipse.php.core.PHPNature";
	
	public final static String ANNOTATION_RESOLUTION_DESC =  "<p>This warning identifies a method invocation accessing/modifying some sensitive/critical data of the application under development, the selected option allows you to select a piece of code that enforces the entity that accesses the sensitive data actually possesses the right/privilege to do so. </p><br/>   <p>If you already have code that does the proper check, please annotate the code that can be evaluated to be true or false (e.g. the condition test of a if statement) by first selecting and then pressing Ctrl+0; </p><br/>" +
				"<p>If you do not yet have such code checking the right to access the sensitive data, it is STRONGLY encouraged that you take the time to make up the miss.</p>";
    public final static String CHECK_UNDO_RESOLUTION_DESC = "This option allows you to remove this access control check and restore the state of the path on which this check is annotated. \r\n\r\nThis option is useful in cases where you decide that this check is not annotated on the correct path, or this is a wrong check.";
    public final static String IGNORE_MARKER_RESOLUTION_DESC = "This option allows you to remove the warning from the editor. \r\n\r\nYou should use this option when you are confident that this is not a real issue. For instance, this method invocation is accessing common data that does NOT require access control checks.";
    public final static String READ_MORE_RESOLUTION_DESC = "Click this option will make your browser presenting you a web page that has more detailed information explaining vulnerabilities related to the identified code. \r\n\r\nYou are strongly ENCOURAGED to take this action before others (#2 and #3), unless you already know what this warning actually indicates.";
    public final static String PHP_VERSION = "php5";
	public static final Set<String> PHPLibraryFolders = new HashSet<String>(Arrays.asList("PHP Include Path", "PHP Language Library", ""));
	public static final Object QUESTION_MESSAGE = "Where is the corresponding authentication process?";
	public static final Object LOGIC_MARKER_MESSAGE = "This is an annotated access control logic.";
	public static final String isPureFunction = "isPureFunction";
	public static final Set<String> INSENSITIVE_OPERATIONS = new HashSet<String>(Arrays.asList(
			"require_login",
			"require_course_login",
			"admin_externalpage_setup",
			"require_capability",
			"has_capability", 
			"get_context_instance", 
			"get_context_instance_by_id",
			"print_html", "get_system_context", 
			"format_text"));
	
}
