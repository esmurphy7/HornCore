/**
 * Created by Evan on 5/31/2016.
 */
public class Hypothesis
{
    public Proposition proposition;

    public Hypothesis(Proposition p)
    {
        proposition = p;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + proposition.value;
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
        Hypothesis other = (Hypothesis) obj;
        if (proposition.value != other.proposition.value)
            return false;
        return true;
    }
}
