package io.github.mvrozanti.octree.distance;

import io.github.mvrozanti.octree.*;

public abstract class DistanceType {

    public static final EuclideanDistanceType EUCLIDEAN;

    public static final MaxDistanceType MAXIMUM;

    public static final ManhattanDistanceType MANHATTAN;

    static {
        EUCLIDEAN = EuclideanDistanceType.getInstance();
        MAXIMUM = MaxDistanceType.getInstance();
        MANHATTAN = ManhattanDistanceType.getInstance();
    }

    public abstract Double compute(PointT p, PointT q);

    public abstract Double norm(double x, double y, double z);

    public abstract Double sqr(double r);

    public abstract Double sqrt(double r);

    protected static DistanceType getInstance() throws NoSuchMethodException { throw new NoSuchMethodException(); }
}
