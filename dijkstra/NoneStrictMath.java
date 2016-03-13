package dijkstra;

import java.util.Comparator;

public class NoneStrictMath {
	public static double EPSILON = 1.0E-8;

    /**
     * Compares two numbers given some amount of allowed error.
     */
    public static int compare( double x, double y, double eps )
    {
        return equals( x, y, eps ) ? 0 : x < y ? -1 : 1;
    }

    /**
     * Compares two numbers given some amount of allowed error.
     * Error given by {@link NoneStrictMath#EPSILON}
     */
    public static int compare( double x, double y )
    {
        return compare( x, y, EPSILON );
    }

    /**
     * Returns true if both arguments are equal or within the range of allowed error (inclusive)
     */
    public static boolean equals( double x, double y, double eps )
    {
        return Math.abs( x - y ) <= eps;
    }

    /**
     * Returns true if both arguments are equal or within the range of allowed error (inclusive)
     * Error given by {@link NoneStrictMath#EPSILON}
     */
    public static boolean equals( double x, double y )
    {
        return equals( x, y, EPSILON );
    }

    public static class CommonToleranceComparator implements Comparator<Double>
    {
        private final double epsilon;

        public CommonToleranceComparator( double epsilon )
        {
            this.epsilon = epsilon;
        }

        @Override
        public int compare( Double x, Double y )
        {
            return NoneStrictMath.compare( x, y, epsilon );
        }
    }
}
