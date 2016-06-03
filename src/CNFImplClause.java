import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evan on 5/31/2016.
 */
public class CNFImplClause
{
    private final String OR_OPERATOR            = "\u2228";
    private final String IMPLICATION_OPERATOR   = "\u2192";

    public List<Proposition> antecedentProps;
    public Proposition consequent;

    public CNFImplClause(List<Proposition> antecedentProps, Proposition consequent)
    {
        this.antecedentProps = antecedentProps;
        this.consequent = consequent;
    }

    public String toString()
    {
        List<String> propStrs = new ArrayList<String>();

        for(Proposition p : antecedentProps)
        {
            propStrs.add(p.toString());
        }

        String clauseStr = "(";
        clauseStr += String.join(OR_OPERATOR, propStrs) + ")";
        clauseStr += IMPLICATION_OPERATOR;
        clauseStr += consequent.toString();
        return clauseStr;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        for( Proposition p : antecedentProps)
        {
            result = result * prime + p.hashCode();
        }
        result = result * prime + consequent.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        CNFImplClause other = (CNFImplClause) obj;
        if(consequent != other.consequent)
        {
            return false;
        }
        if(!antecedentProps.equals(other.antecedentProps))
        {
            return false;
        }

        return true;
    }
}
