import java.util.*;

public class HornCore
{
    private HashMap<CNFImplClause, Proposition> CONCLUSION;
    private HashMap<CNFImplClause, Integer>     COUNT;
    private HashMap<Proposition, Boolean>       TRUTH;
    private HashMap<Proposition, Hypothesis>    LAST;
    private HashMap<Hypothesis, CNFImplClause>  CLAUSE;
    private HashMap<Hypothesis, Hypothesis>     PREV;

    private Stack<Proposition> stack = new Stack<Proposition>();

    private List<CNFImplClause> clauses = new ArrayList<CNFImplClause>();
    private List<Proposition> uniquePropositions = new ArrayList<Proposition>();
    private List<Proposition> coreClauses = new ArrayList<Proposition>();

    HornCore(DimacsFile dimacsFile)
    {
        CONCLUSION = new HashMap<CNFImplClause, Proposition>();
        COUNT = new HashMap<CNFImplClause, Integer>();
        TRUTH = new HashMap<Proposition, Boolean>();
        LAST = new HashMap<Proposition, Hypothesis>();
        CLAUSE = new HashMap<Hypothesis, CNFImplClause>();
        PREV = new HashMap<Hypothesis, Hypothesis>();

        clauses = dimacsFile.cnfClauses;
        for (int i=1; i <= dimacsFile.numVariables; i++)
        {
            uniquePropositions.add(new Proposition(i));
        }
    }

    private void computeCoreClauses()
    {
        // used in LAST to signal that we are done with the hypothesis
        final Hypothesis lambda = new Hypothesis(new Proposition(-99));

        // for each possible proposition p, set LAST(p) = lambda, TRUTH(P) = 0
        for(Proposition p : uniquePropositions)
        {
            LAST.put(p, lambda);
            TRUTH.put(p, Boolean.FALSE);
        }

        // for each clause
        for(CNFImplClause c : clauses)
        {
            // set CONCLUSION(c) = v, COUNT(c) = k,
            // where v is the consequent and k is the number of literals in the antecedent
            CONCLUSION.put(c, c.consequent);
            COUNT.put(c, c.antecedentProps.size());

            // if k = 0 and TRUTH(v) = 0
            if(c.antecedentProps.size() == 0 && TRUTH.get(c.consequent) == Boolean.FALSE)
            {
                // set TRUTH(v) = 1, S[s] = v, s = s+1
                TRUTH.put(c.consequent, Boolean.TRUE);
                stack.push(c.consequent);
            }

            // for each literal in the antecedent, u_i
            for(Proposition p : c.antecedentProps)
            {
                // create hypothesis h
                Hypothesis h = new Hypothesis(p);

                // set CLAUSE(h) = c, PREV(h) = LAST(u_i), LAST(u_i) = h
                CLAUSE.put(h, c);
                PREV.put(h, LAST.get(p));
                LAST.put(p, h);
            }
        }

        // while s != 0
        while(!stack.empty())
        {
            // s = s-1, p = S[s], h = LAST(p)
            Proposition p = stack.pop();
            Hypothesis h = LAST.get(p);

            // while h != lambda
            while(!h.equals(lambda))
            {
                // c = CLAUSE(h), COUNT(c) = COUNT(c) - 1
                CNFImplClause c = CLAUSE.get(h);
                COUNT.put(c, COUNT.get(c) - 1);

                // if COUNT(c) == 0
                if(COUNT.get(c) == 0)
                {
                    // p = CONCLUSION(c)
                    p = CONCLUSION.get(c);

                    // if TRUTH(p) == 0
                    if(TRUTH.get(p) == Boolean.FALSE)
                    {
                        // TRUTH(p) = 1, S[s] = p, s = s+1
                        TRUTH.put(p, Boolean.TRUE);
                        stack.push(p);
                    }
                }

                // h = PREV(h)
                h = PREV.get(h);
            }
        }

        // values in the truth map that are true are in the core set
        for(Map.Entry<Proposition, Boolean> entry : TRUTH.entrySet())
        {
            if(entry.getValue())
            {
                coreClauses.add(entry.getKey());
            }
        }
    }

    public String getCore()
    {
        computeCoreClauses();

        if(coreClauses.isEmpty())
        {
            return "UNSAT";
        }

        List<String> coreStrs = new ArrayList<String>();

        for(Proposition coreClause : coreClauses)
        {
            coreStrs.add(coreClause.toString());
        }

        return String.join(", ", coreStrs);
    }


    public static void main(String[] args)
    {
        for(String fileName : args)
        {
            DimacsFile dimacsFile = new DimacsFile(fileName);
            HornCore hornCore = new HornCore(dimacsFile);
            String core = hornCore.getCore();
            System.out.println(String.format("%s: %s", dimacsFile.fileName, core));
        }
    }
}
