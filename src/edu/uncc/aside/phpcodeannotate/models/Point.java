package edu.uncc.aside.phpcodeannotate.models;

import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.php.internal.core.ast.nodes.ASTNode;
import org.eclipse.php.internal.core.ast.nodes.Program;

import edu.uncc.aside.phpcodeannotate.Plugin;
import edu.uncc.aside.phpcodeannotate.visitors.IModelVisitor;
/**
 * 
 * @author Jing Xie (jxie2 at uncc dot edu)
 *
 */
public class Point extends Model {
	private ASTNode node;
	private Program unit;
	private IResource resource;

	private Path parent;

	public Point(ASTNode node, Program unit, IResource resource) {

		if (node == null || unit == null || resource == null) {
			System.err.println("NULL for Constructing a Point");
		}

		this.setNode(node);
		this.setUnit(unit);
		this.setResource(resource);
		
		int startOffset = -1;
		Object startProperty = node.getProperty(Plugin.ASIDE_NODE_PROP_START);
		if(startProperty == null)
		{
		startOffset = node.getStart();	
		}else{
			startOffset = Integer.parseInt(startProperty.toString());
			node.setProperty(Plugin.ASIDE_NODE_PROP_START, startOffset);
		}
	}

	public void setParent(Path path) {
		parent = path;
	}

	public ASTNode getNode() {
		return node;
	}

	public void setNode(ASTNode node) {
		this.node = node;
	}

	public Program getUnit() {
		return unit;
	}

	public void setUnit(Program unit) {
		this.unit = unit;
	}

	public IResource getResource() {
		return resource;
	}

	public void setResource(IResource resource) {
		this.resource = resource;
	}

	public boolean equalsTo(Object object) {
		if (!(object instanceof Point))
			return false;
		Point another = (Point) object;
		ASTNode anotherNode = another.getNode();
		Program anotherUnit = another.getUnit();
		IResource anotherResource = another.getResource();
		if (anotherResource.getFullPath().toString()
				.equals(resource.getFullPath().toString())) {
			if (anotherUnit.getSourceModule().getElementName().equals(unit.getSourceModule().getElementName())) {
				if (anotherNode.getType() == node.getType()) { //changed Sept 23th
					if (anotherNode.getStart() == node
							.getStart()
							&& anotherNode.getEnd() == node.getEnd()) {
						return true;
					}
				}
			}
		}

		return false;
	}

	
	
	public String toString() {
		int startOffset = -1;
		Object startProperty = node.getProperty(Plugin.ASIDE_NODE_PROP_START);
		if(startProperty == null)
		{
		startOffset = node.getStart();	
		}else{
			startOffset = Integer.parseInt(startProperty.toString());
			node.setProperty(Plugin.ASIDE_NODE_PROP_START, startOffset);
		}
		return "\nPartial/Full access control check is done through \n\n   " + node.toString() + "\n\nin " + resource.getName() + " at Line "
				+ unit.getLineNumber(startOffset);
	}

	public String getPointID() {
		int startOffset = -1;
		Object startProperty = node.getProperty(Plugin.ASIDE_NODE_PROP_START);
		if(startProperty == null)
		{
		    startOffset = node.getStart();	
		}else{
			startOffset = Integer.parseInt(startProperty.toString());
			node.setProperty(Plugin.ASIDE_NODE_PROP_START, startOffset);
		}
		
		return "Line " + unit.getLineNumber(startOffset) + " in "
				+ resource.getName();
	}

	@Override
	public void accept(IModelVisitor visitor) {
		// This does nothing for Point
		visitor.visit(this);
	}

	@Override
	public Model getParent() {
		return parent;
	}

	@Override
	protected List<Model> buildChildren() {
		return null;
	}
}