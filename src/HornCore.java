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

    HornCore(List<CNFImplClause> cnfClauses, int numVariables)
    {
        CONCLUSION = new HashMap<CNFImplClause, Proposition>();
        COUNT = new HashMap<CNFImplClause, Integer>();
        TRUTH = new HashMap<Proposition, Boolean>();
        LAST = new HashMap<Proposition, Hypothesis>();
        CLAUSE = new HashMap<Hypothesis, CNFImplClause>();
        PREV = new HashMap<Hypothesis, Hypothesis>();

        clauses = cnfClauses;
        for(CNFImplClause c : clauses)
        {
            System.out.println(c.toString());
        }
        for (int i=1; i <= numVariables; i++)
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
            return "EMPTY";
        }

        List<String> coreStrs = new ArrayList<String>();

        for(Proposition coreClause : coreClauses)
        {
            coreStrs.add(coreClause.toString());
        }

        return String.join(", ", coreStrs);
    }

    private void parseDimacsFile()
    {

    }

    public static void main(String[] args)
    {
        Scanner in = new Scanner(System.in);

        boolean isHeaderLine = true;
        Proposition consequent = null;
        List<Proposition> antecedentProps = new ArrayList<Proposition>();
        List<CNFImplClause> fileClauses = new ArrayList<CNFImplClause>();
        int numVariables = 0;
        int numClauses = 0;
        String element;
        List<String> elements = new ArrayList<String>();
        while(in.hasNext())
        {
            element = in.next();
            System.out.println("element: "+element);

            if(isHeaderLine)
            {
                if(!element.equals("p") && !element.equals("cnf"))
                {
                    numVariables = Integer.parseInt(element);
                    numClauses = Integer.parseInt(in.next());
                    isHeaderLine = false;
                }
                continue;
            }

            // if we've found a '0', end of clause
            if(element.equals("0"))
            {
                // once we found a '0', the last element stored is the consequent
                consequent = new Proposition(Integer.parseInt(elements.get(elements.size()-1)));
                CNFImplClause newClause;
                if(elements.size() == 1)
                {
                    newClause = new CNFImplClause(new ArrayList<Proposition>(), consequent);
                }else
                {
                    newClause = new CNFImplClause(antecedentProps, consequent);
                }

                System.out.println("Adding clause: "+newClause.toString());
                fileClauses.add(newClause);

                elements = new ArrayList<String>();
                antecedentProps = new ArrayList<Proposition>();
                continue;
            }

            // if we've found a '0p', end of dimacs file
            if(element.equals("0p"))
            {
                // once we found a '0p', the last element stored is the consequent
                consequent = new Proposition(Integer.parseInt(elements.get(elements.size()-1)));

                // create new HornCore instance found the file found so far
                fileClauses.add(new CNFImplClause(antecedentProps, consequent));
                HornCore hornCore = new HornCore(fileClauses, numVariables);
                String core = hornCore.getCore();
                System.out.println(String.format("Core = %s", core));
                isHeaderLine = true;
                elements = new ArrayList<String>();
                fileClauses = new ArrayList<CNFImplClause>();
                antecedentProps = new ArrayList<Proposition>();
                continue;
            }

            antecedentProps.add(new Proposition(Integer.parseInt(element)));

            // add the element to this list
            elements.add(element);
        }

        HornCore hornCore = new HornCore(fileClauses, numVariables);
        String core = hornCore.getCore();
        System.out.println(String.format("Core = %s", core));

        /*
        DimacsFile dimacsFile = new DimacsFile(in);
        HornCore hornCore = new HornCore(dimacsFile);
        String core = hornCore.getCore();
        System.out.println(String.format("Core = %s", core));


        DimacsFile dimacsFile2 = new DimacsFile(in);
        HornCore hornCore2 = new HornCore(dimacsFile2);
        String core2 = hornCore2.getCore();
        System.out.println(String.format("Core = %s" , core2));
        */

    }
}
