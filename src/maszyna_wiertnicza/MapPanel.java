package maszyna_wiertnicza;


import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.math.BigDecimal;
import java.net.*;
import java.util.ArrayList;

import javax.swing.*;

import att.grappa.*;

public class MapPanel extends JPanel
	implements GrappaConstants
{

    public JPanel routeMap;
    public GrappaPanel grappa;
    JScrollPane scroll;
    ArrayList<Node> nodes;
    ArrayList<Edge> edges;
    ArrayList<NodeRecord> noderecords;
    ArrayList<EdgeRecord> edgerecords;
    double scale;
    Graph graph;


    
   

    MapPanel() {
    	initGraph();
    	initPanel();

		setLayout(null);
		add(routeMap);
		scroll = new JScrollPane(routeMap);
		scroll.setBounds(10, 10, 775, 455);
		add(scroll);
		scale=1d;
		
		 nodes= new ArrayList<Node>();
		 edges= new ArrayList<Edge>();
		 noderecords= new ArrayList<NodeRecord>();
		 edgerecords= new ArrayList<EdgeRecord>();
    	
    }
    
    static class NodeRecord {
    	int id;
		double x;
		double y;
		String color;

		public NodeRecord(int id,double x, double y,String color) {
			super();
			this.id=id;
			this.x = x;
			this.y = y;
			this.color=color;
		}
		
		void scale(double s){
			this.x*=s;
			this.y*=s;
		}
	}
    static class EdgeRecord {
		int n1;
		int n2;
		String color;
		public EdgeRecord(int n1,int n2, String color) {
			super();
			this.n1=n1;
			this.n2=n2;
			this.color=color;
		}
	}
    
    
    public double calculateDistance(NodeRecord r1,NodeRecord r2) {
		return Math.abs((Math.sqrt(Math.pow(r2.x - r1.x, 2) + Math.pow(r2.y - r1.y, 2))));
	}
    
    void setAutoScale(){
    	grappa.removeGrappaListener();
    	grappa.setScaleToFit(true);
    	double prefferedminDistance = 50d;
    	double prefferedmaxDistance = 100d;
    	double minDistance=Double.MAX_VALUE;
    	//System.out.println("mindistance="+minDistance);
    	if(!noderecords.isEmpty()){
    		for(NodeRecord r1: noderecords){
    			for(NodeRecord r2:noderecords){
    				if(r1.id!=r2.id){
    				double distance = calculateDistance(r1,r2);
    				//System.out.println("distance="+distance+" mindistance="+minDistance);
    				if(distance<minDistance){
    					
    					minDistance = distance;
    				}
    			}
    			}
    		}
    		//end fors
    		
    		
    		
    		while((minDistance*scale)>prefferedmaxDistance){
    			scale-=0.1d;
    			//System.out.println("mindistance="+minDistance+"tempDistance="+tempDistance+"auto scale= "+scale);
    		}
    		
    		while((minDistance*scale)<prefferedminDistance){
    			scale+=0.1d;
    			//System.out.println("mindistance="+minDistance+"tempDistance="+tempDistance+"auto scale= "+scale);
    		}
    		//set scale & create nodes
    		for(NodeRecord r:noderecords){
    			int orig_x = (int)r.x;
    			int orig_y = (int)r.y;
    			r.scale(scale);
    			Node newnode = new Node(graph);
    			newnode.setName(""+r.id);
    			if(r.color!=null){
    				newnode.setAttribute("color",r.color);
    			}
    			int x = (int) r.x;
    			int y = (int) r.y;
    			newnode.setAttribute(GrappaConstants.POS_ATTR, x+","+y);
    			newnode.setAttribute("tip","Punkt "+r.id +" ("+orig_x+" ; "+orig_y+")");
    			nodes.add(newnode);
    		}
    		
    	} 
    	
    }
    
    Node getNode(int id){
    	for(Node n:nodes){
    		int nodeid=Integer.parseInt(n.getName());
    		if(id==nodeid){
    			return n;
    		}
    	}
    	System.err.println("Nie znaleziono noda: "+id);
    	return null;
    }
    
    void plotEdges(){

    	for(EdgeRecord e: edgerecords){
    		Node n1=getNode(e.n1);
    		Node n2=getNode(e.n2);
    		Edge newedge = new Edge(graph,n1,n2);
    		if(e.color!=null){
    			newedge.setAttribute("color",e.color);
    		}
    		edges.add(newedge);
    	}
    	//grappa.setScaleToFit(false);
    	//gp.addGrappaListener(new GrappaAdapter());
    	repaint();
    	
    }
    
    void finishedSimulation(){
    	grappa.setScaleToFit(false);
    	grappa.addGrappaListener(new GrappaAdapter());
    	repaint();
    }
    
    void addNode(int id,double x, double y,String c){
    	noderecords.add(new NodeRecord(id,x,y,c));
    }
    
    void addEdge(int n1,int n2,String c){
    	edgerecords.add(new EdgeRecord(n1, n2,c));
    }
    
    void clearNodes(){
    	Node[] nodearray=graph.nodeElementsAsArray();
    	for(Node n:nodearray){
    		graph.removeNode(n.getName());
    	}
    	 nodes= new ArrayList<Node>();
		 noderecords= new ArrayList<NodeRecord>();
    }
    
    void clearAll(){
    	//todo clear nodes
    	clearEdges();
    	clearNodes();
    }
    
    void clearEdges(){
    	Edge[] edgearray=graph.edgeElementsAsArray();
    	for(Edge e:edgearray){
    		graph.removeEdge(e.getName());
    	}
    	edges= new ArrayList<Edge>();
    	edgerecords= new ArrayList<EdgeRecord>();
    }

    void initGraph() {
    	

	graph = new Graph("Mapa trasy");
	
	
	graph.setAttribute("rankdir","LR");
	graph.setAttribute("ranksep", "0.3333");
	//graph.setAttribute("label","\\n\\n \\n \\n \\n");
	graph.setAttribute("fontstyle","bold");
	graph.setAttribute("fontsize","24");
	graph.setAttribute("font","Helvetica");

	graph.setNodeAttribute("shape","ellipse");
	graph.setNodeAttribute("style","filled");
	graph.setNodeAttribute("color","beige");


	graph.setEdgeAttribute("color","darkgreen");
	graph.setErrorWriter(new PrintWriter(System.err,true));
	graph.setEditable(false);

    }
    
    void initPanel(){
    	 GrappaPanel gp = new GrappaPanel(graph);
    	 
    	 grappa=gp;
    	 routeMap=gp;
    }

	public double round(double d,int pos){
		return new BigDecimal(d).setScale(pos, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
  
    
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.black);
	}
    
}
