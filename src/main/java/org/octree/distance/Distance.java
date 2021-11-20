package org.octree.distance;

import org.octree.*;

public abstract class Distance {

    public static final EuclideanDistance EUCLIDEAN;

    public static final MaxDistance MAXIMUM;

    public static final ManhattanDistance MANHATTAN;

    static {
        EUCLIDEAN = EuclideanDistance.getInstance();
        MAXIMUM = MaxDistance.getInstance();
        MANHATTAN = ManhattanDistance.getInstance();
    }

    public abstract Double compute(PointT p, PointT q);

    public abstract Double norm(double x, double y, double z);

    public abstract Double sqr(double r);

    public abstract Double sqrt(double r);

    protected static Distance getInstance() throws NoSuchMethodException { throw new NoSuchMethodException(); }
}
