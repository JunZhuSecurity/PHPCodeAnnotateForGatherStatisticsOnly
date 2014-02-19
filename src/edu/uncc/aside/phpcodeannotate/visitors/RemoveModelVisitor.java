package edu.uncc.aside.phpcodeannotate.visitors;

import edu.uncc.aside.phpcodeannotate.Plugin;
import edu.uncc.aside.phpcodeannotate.models.ModelRegistry;
import edu.uncc.aside.phpcodeannotate.models.Path;
import edu.uncc.aside.phpcodeannotate.models.PathCollector;
import edu.uncc.aside.phpcodeannotate.models.Point;
/**
 * 
 * @author Jing Xie (jxie2 at uncc dot edu)
 *
 */
public class RemoveModelVisitor implements IModelVisitor {
	
	private static RemoveModelVisitor instance;
	private RemoveModelVisitor(){
		
	}
	
public static RemoveModelVisitor getInstance(){
	if(instance == null)
		instance = new RemoveModelVisitor();
	return instance;
}
	
	@Override
	public void visit(PathCollector pathCollector) {
//		pathCollector.removePropertyChangeListener(Plugin.getDefault().getAnnotationView());
		pathCollector.removePropertyChangeListener(ModelRegistry.getInstance());

	}

	@Override
	public void visit(Path path) {
//		path.removePropertyChangeListener(Plugin.getDefault().getAnnotationView());
		path.removePropertyChangeListener(ModelRegistry.getInstance());

	}

	@Override
	public void visit(Point point) {
//		point.removePropertyChangeListener(Plugin.getDefault().getAnnotationView());
		point.removePropertyChangeListener(ModelRegistry.getInstance());
	}

}
