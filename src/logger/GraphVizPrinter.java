// NOTE: This is extra code, not a critical part of our Othello assignment
package logger;

import java.io.File;

import representation.Conf;

/**
 * We use this class for debugging purposes.
 * It draws and maintains a graph of a search space.
 * @author Ashoat Tevosyan
 * @since Mon Apr 18 2011
 * @version CSE 473
 */
public class GraphVizPrinter {
	
	// The GraphViz object.
	private static GraphViz gv;
	// Which turn is this? Used so we don't overwrite the same file each turn.
	private static int turn = 0;
	// What type should we save our files in?
	public static String type = "svg";
	
	static int idx = 0;
	/**
	 * A static constructor to initialize GraphViz.
	 */
	static {
		gv = new GraphViz();
		gv.addln(gv.start_graph());
		gv.addln("rankdir=BT;");
	}
	
	/**
	 * Add a Conf to the graph.
	 * @param state The Conf to add. 
	 */
	public static void setState(Conf c) {
		gv.addln(getId(c)+ " [label=\"" + c.toString().replaceAll("\n", "\\\\n") + "\", shape=box, fontname=Courier];");
	}
	
	/**
	 * Add a Conf to the graph.
	 * @param state The Conf to add. 
	 */
	public static void setCached(Conf s) {
		gv.addln(getId(s) + " [style=filled,fillcolor=green];");
	}
	
	/**
	 * Set a relationship between two Confs with the given weight.
	 * @param state The child Conf.
	 * @param value The weight (heuristic) value of this relationship.
	 * @param daddy The parent Conf.
	 */
	public static void setRelation(Conf state, float value, Conf daddy, int iter) {
		if (daddy != null) gv.addln(getId(state) + " -> " + getId(daddy) + " [label=\"" + value + "\\n" + iter + ".\"];");
	}
	
	public static void setRelation(Conf state, float value, Conf daddy) {
		setRelation(state, value, daddy, 0);
	}
	
	/**
	 * Set a relationship between two Confs with the given weight.
	 * @param state The child Conf.
	 * @param value The weight (heuristic) value of this relationship.
	 * @param daddy The parent Conf.
	 * @param alpha the alpha value passed in to this search
	 * @param alpha the beta value passed in to this search
	 */
	public static void setRelation(Conf state, float value, Conf daddy, float alpha, float beta, int iter) {
		if (daddy != null) gv.addln(getId(state) + " -> " + getId(daddy) + " [label=\"" + value + " \\n"+ iter + ". [" + alpha + ", " + beta +"]\"];");
	}
	
	public static void setRelation(Conf state, float value, Conf daddy, float alpha, float beta) {
		setRelation(state, value, daddy, alpha, beta, 0);
	}
	
	
	/**
	 * Highlight the final decision as red for easy viewing.
	 * @param state The final decision we settled on.
	 */
	public static void setDecision(Conf state) {
		gv.addln(getId(state) + " [color=red];");
	}
	
	/**
	 * Finally print the current graph to a file.
	 */
	public static void printGraphToFile() {
		gv.addln(gv.end_graph());
		gv.writeGraphToFile( gv.getGraph( gv.getDotSource(), type ), new File("turn" + turn++ + "." + type) );
		gv = new GraphViz();
		gv.addln(gv.start_graph());
	}
	
	/**
	 * Finally print the current graph to a file.
	 */
	public static void printGraphToFileWDeepening(int d) {
		gv.addln(gv.end_graph());
		gv.writeGraphToFile( gv.getGraph( gv.getDotSource(), type ), new File("turn" + turn + "." + +d+"."+type) );
		gv = new GraphViz();
		gv.addln(gv.start_graph());
	}
	
	private static String getId(Conf s) {
		StringBuilder sb = new StringBuilder();
		sb.append(s.identifier());
		if (s.getParentState() != null) {
			sb.append(s.getParentState().identifier());
		}
		return sb.toString();
	}
}