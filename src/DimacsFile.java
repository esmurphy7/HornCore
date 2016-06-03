import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evan on 5/31/2016.
 */
public class DimacsFile
{
    public int numVariables;
    public int numClauses;

    public String fileName;
    public List<CNFImplClause> cnfClauses = new ArrayList<CNFImplClause>();

    public DimacsFile(String fileName)
    {
        this.fileName = fileName;

        ClassLoader classLoader = this.getClass().getClassLoader();

        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(classLoader.getResourceAsStream(fileName)));
            boolean headerLine = true;
            for(String line; (line = reader.readLine()) != null; )
            {
                String[] elements = line.split(" ");
                if(headerLine)
                {
                    numVariables = new Integer(elements[2]);
                    numClauses = new Integer(elements[3]);
                    headerLine = false;
                }
                else
                {
                    Proposition consequent = null;
                    List<Proposition> antecedentProps = new ArrayList<Proposition>();
                    for(int i=0; i<elements.length; i++)
                    {
                        // check if next element is end-of-line (0)
                        if(i+1 < elements.length && Integer.parseInt(elements[i+1]) == 0)
                        {
                            consequent = new Proposition(Integer.parseInt(elements[i]));
                            break;
                        }
                        else
                        {
                            antecedentProps.add(new Proposition(Integer.parseInt(elements[i])));
                        }
                    }

                    if(consequent != null)
                    {
                        cnfClauses.add(new CNFImplClause(antecedentProps, consequent));
                    }
                }
            }

            reader.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e ) {
            e.printStackTrace();
        }

    }
}
