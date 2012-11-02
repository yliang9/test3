package com.github.oozie.oozie2dot;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class DotNode {
	private List<DotNode> children;
	private String name;
	
	public DotNode(String pid) {
		this.children = new ArrayList<DotNode>();
		this.name = pid;
	}
	
	public void addChild(DotNode node) {
		if(!this.children.contains(node))
			this.children.add(node);
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	public List<DotNode> getChildren() {
		return this.children;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof DotNode == false)
			return false;
		if(obj == this)
			return true;
		DotNode rhs = (DotNode)obj;
		return new EqualsBuilder().appendSuper(super.equals(obj)).append(this.name, rhs.name).isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17,37).append(this.name).toHashCode();
	}
	
	public String getName() {
		return this.toString();
	}
}
