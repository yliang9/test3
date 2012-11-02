package com.github.oozie.oozie2dot;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

public class Oozie2dot {
	private static Namespace NAMESPACE = Namespace.getNamespace("uri:oozie:workflow:0.2");
	public static void main(String args[]) {
		Oozie2dot oo = new Oozie2dot();
		if(args.length != 2) {
			System.exit(1);
		}
		try {
			oo.parseOozie(args[0], args[1]);
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void parseOozie(String input, String output) throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(input);
        Element root = doc.getRootElement();
        //start
        Element start = root.getChild("start", NAMESPACE);
        DotNode rootNode = new DotNode(start.getAttributeValue("to"));
        this.addSubNode(rootNode, root);
        
        //construct dot file
		
      	FileWriter fw = new FileWriter(output);
      	fw.write("digraph dag {\n");
      	//avoid duplicated
      	Set<String> allEdge = new HashSet<String>();
      	this.visitNode(rootNode, fw, allEdge);
      	fw.write("}\n");
      	fw.close();
	}
	
	private Element findElementByName(String name, Element root) {
		List<Element> actionelements = root.getChildren("action", NAMESPACE);
		for(Element element: actionelements) {
			if(element.getAttributeValue("name").equals(name))
				return element;
		}
		
		List<Element> forkElements = root.getChildren("fork", NAMESPACE);
		for(Element element: forkElements) {
			if(element.getAttributeValue("name").equals(name))
				return element;
		}	
		
		List<Element> joinElements = root.getChildren("join", NAMESPACE);
		for(Element element: joinElements) {
			if(element.getAttributeValue("name").equals(name))
				return element;
		}	
		if(name.equals("end"))
			return root.getChild("end", NAMESPACE);
		return null;
	}
	
	private void addSubNode(DotNode parent, Element root) {
		Element element = this.findElementByName(parent.getName(), root);
		if(element.getName().equals("action")) {
			Element child = element.getChild("ok", NAMESPACE);
			String okTo = child.getAttributeValue("to");
			DotNode childNode = new DotNode(okTo);
			parent.addChild(childNode);
			this.addSubNode(childNode, root);
		} else if(element.getName().equals("fork")) {
			List<Element> forks = element.getChildren();
			for(Element fork: forks) {
				DotNode childNode = new DotNode(fork.getAttributeValue("start"));
				parent.addChild(childNode);
				this.addSubNode(childNode, root);
			}
		} else if(element.getName().equals("join")) {
			DotNode childNode = new DotNode(element.getAttributeValue("to"));
			parent.addChild(childNode);
			this.addSubNode(childNode, root);
		}
	}
	
	private void visitNode(DotNode node, FileWriter fw, Collection<String> all) throws IOException {
		for(DotNode child: node.getChildren()) {
			String w = node.toString() + "  ->  " + child.toString();
			if(all.contains(w))
				continue;
			all.add(w);
			fw.write(w + "\n");
			this.visitNode(child, fw, all);
		}
	}
}