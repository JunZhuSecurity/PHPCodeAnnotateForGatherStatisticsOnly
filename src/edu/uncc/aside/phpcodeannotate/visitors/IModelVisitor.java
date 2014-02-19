package edu.uncc.aside.phpcodeannotate.visitors;

import edu.uncc.aside.phpcodeannotate.models.Path;
import edu.uncc.aside.phpcodeannotate.models.PathCollector;
import edu.uncc.aside.phpcodeannotate.models.Point;
/**
 * 
 * @author Jing Xie (jxie2 at uncc dot edu)
 *
 */
public interface IModelVisitor {

	public void visit(PathCollector pathCollector);
	public void visit(Path path);
	public void visit(Point point);
	
}
