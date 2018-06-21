package com.samvasta.common.helpers;

public class ComparisonHelper
{
    public static final double EPSILON = 1e-7;

    /**
     * Calls {@link #isEqual(double, double, double)}, using {@link #EPSILON} as the tolerance.
     * @param o1 Comparison operand 1
     * @param o2 Comparison operand 2
     */
    public static boolean isEqual(double o1, double o2){
        return isEqual(o1, o2, EPSILON);
    }

    /**
     * Fuzzy comparison between two floating point numbers
     * @param o1 Comparison operand 1
     * @param o2 Comparison operand 2
     * @param tolerance Acceptable distance between o1 and o2
     * @return true if o1 and o2 are within <code>tolerance</code>
     */
    public static boolean isEqual(double o1, double o2, double tolerance){
        if(tolerance < 0){
            throw new ArithmeticException("Floating point comparison tolerance must be non-negative");
        }
        return Math.abs(o1 - o2) < tolerance;
    }


    /**
     * Calls {@link #isLessThan(double, double, double)}, using {@link #EPSILON} as the tolerance.
     * @param o1 Comparison operand 1
     * @param o2 Comparison operand 2
     */
    public static boolean isLessThan(double o1, double o2){
        return isLessThan(o1, o2, EPSILON);
    }
    /**
     * Fuzzy comparison between two floating point numbers (o1 < o2)
     * @param o1 Comparison operand 1
     * @param o2 Comparison operand 2
     * @param tolerance Acceptable distance between o1 and o2
     * @return true if o1 < o2 or if o1 and o2 are within <code>tolerance</code>
     */
    public static boolean isLessThan(double o1, double o2, double tolerance){
        if(tolerance < 0){
            throw new ArithmeticException("Floating point comparison tolerance must be non-negative");
        }
        return (o2 - o1) >= tolerance;
    }

    /**
     * Calls {@link #isGreaterThan(double, double, double)}, using {@link #EPSILON} as the tolerance.
     * @param o1 Comparison operand 1
     * @param o2 Comparison operand 2
     */
    public static boolean isGreaterThan(double o1, double o2){
        return isGreaterThan(o1, o2, EPSILON);
    }
    /**
     * Fuzzy comparison between two floating point numbers (o1 > o2)
     * @param o1 Comparison operand 1
     * @param o2 Comparison operand 2
     * @param tolerance Acceptable distance between o1 and o2
     * @return true if o1 > o2 or if o1 and o2 are within <code>tolerance</code>
     */
    public static boolean isGreaterThan(double o1, double o2, double tolerance){
        if(tolerance < 0){
            throw new ArithmeticException("Floating point comparison tolerance must be non-negative");
        }
        return (o1 - o2) > tolerance;
    }
}
