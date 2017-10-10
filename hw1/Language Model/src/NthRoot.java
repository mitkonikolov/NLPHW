/**
 ** Java Program to implement Nth Root Algorithm.
 *  This code was copied from http://www.sanfoundry.com/java-program-nth-root-algorithm/
 **/

/** Class NthRoot **/
public class NthRoot
{
    public double nthroot(int n, double x)
    {
        return nthroot(n, x, .0001);
    }
    public double nthroot(int n, double x, double p)
    {
        if(x < 0)
        {
            System.err.println("Negative!");
            return -1;
        }
        if(x == 0)
            return 0;
        double x1 = x;
        double x2 = x / n;
        while (Math.abs(x1 - x2) > p)
        {
            x1 = x2;
            x2 = ((n - 1.0) * x2 + x / Math.pow(x2, n - 1.0)) / n;
        }
        return x2;
    }
}