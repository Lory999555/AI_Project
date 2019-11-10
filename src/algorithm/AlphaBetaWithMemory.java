package algorithm;

public class AlphaBetaWithMemory {
	
	/**
	 * function AlphaBetaWithMemory(n : node_type; alpha , beta , d : integer) : integer;

    if retrieve(n) == OK then // Transposition table lookup
        if n.lowerbound >= beta then return n.lowerbound;
        if n.upperbound <= alpha then return n.upperbound;
        alpha := max(alpha, n.lowerbound);
        beta := min(beta, n.upperbound);
    if d == 0 then g := evaluate(n); /* leaf node 
    else if n == MAXNODE then
        g := -INFINITY; a := alpha; /* save original alpha value 
        c := firstchild(n);
        while (g < beta) and (c != NOCHILD) do
            g := max(g, AlphaBetaWithMemory(c, a, beta, d - 1));
            a := max(a, g);
            c := nextbrother(c);
    else /* n is a MINNODE 
        g := +INFINITY; b := beta; /* save original beta value 
        c := firstchild(n);
        while (g > alpha) and (c != NOCHILD) do
            g := min(g, AlphaBetaWithMemory(c, alpha, b, d - 1));
            b := min(b, g);
            c := nextbrother(c);
    /* Traditional transposition table storing of bounds 
    /* Fail low result implies an upper bound 
    if g <= alpha then n.upperbound := g; store n.upperbound;
    /* Found an accurate minimax value - will not occur if called with zero window 
    if g >  alpha and g < beta then
        n.lowerbound := g; n.upperbound := g; store n.lowerbound, n.upperbound;
    /* Fail high result implies a lower bound 
    if g >= beta then n.lowerbound := g; store n.lowerbound;
    return g; 
	 */

}
