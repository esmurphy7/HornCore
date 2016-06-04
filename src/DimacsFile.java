import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Evan on 5/31/2016.
 */
public class DimacsFile
{
    public int numVariables;
    public int numClauses;

    public List<CNFImplClause> cnfClauses = new ArrayList<CNFImplClause>();

    public DimacsFile(Scanner in)
    {
        try
        {
            String headerLine = in.nextLine();

            System.out.println("Header line: " + headerLine);

            // go to the first line of the DIMACS cnf format
            while( !headerLine.startsWith( "p" ) )
            {
                headerLine = in.nextLine();
                System.out.println("Header line: "+headerLine);
            }

            String[] headerToks = headerLine.split(" ");

            numVariables = new Integer(headerToks[2]);
            numClauses = new Integer(headerToks[3]);

            Proposition consequent = null;
            List<Proposition> antecedentProps = new ArrayList<Proposition>();
            for(int i=0; i<numClauses; i++)
            {
                // store each proposition (integer) from the dimacs file into element set
                List<Integer> elements = new ArrayList<Integer>();
                int propVal = Integer.parseInt(in.next());
                while(propVal != 0 && in.hasNext())
                {
                    System.out.println("Adding proposition: " + propVal);
                    elements.add(propVal);
                    try
                    {
                        propVal = Integer.parseInt(in.next());
                    }
                    catch (NumberFormatException e)
                    {

                    }

                }

                // create new proposition for each element
                int j=0;
                boolean done = false;
                while(!done)
                {
                    // if its the last element, it's the consequent
                    if(j == elements.size()-1)
                    {
                        consequent = new Proposition(elements.get(j));
                        done = true;
                        continue;
                    }

                    antecedentProps.add(new Proposition(elements.get(j)));
                    j++;
                }
            }

            if(consequent != null)
            {
                cnfClauses.add(new CNFImplClause(antecedentProps, consequent));
            }

        } catch (IndexOutOfBoundsException e ) {
            e.printStackTrace();
        }

    }
}
