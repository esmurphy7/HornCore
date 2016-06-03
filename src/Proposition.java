/**
 * Created by Evan on 5/31/2016.
 */
public class Proposition
{
    public int value;

    public Proposition(int v)
    {
        value = v;
    }

    public String toString()
    {
        return Integer.toString(value);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + value;
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
        Proposition other = (Proposition) obj;
        if (value != other.value)
            return false;
        return true;
    }
}