package io.github.mvrozanti.octree.distance;

import io.github.mvrozanti.octree.*;
import static java.lang.Math.*;

public class ManhattanDistanceType extends DistanceType {

    private static ManhattanDistanceType INSTANCE;

    private ManhattanDistanceType() {
    }

    @Override
    public Double compute(PointT p, PointT q) {
        double diff1 = p.x() - q.x();
        double diff2 = p.y() - q.y();
        double diff3 = p.z() - q.z();
        return abs(diff1) + abs(diff2) + abs(diff3);
    }

    @Override
    public Double norm(double x, double y, double z) {
        return abs(x) + abs(y) + abs(z);
    }

    @Override
    public Double sqr(double r) {
        return r;
    }

    @Override
    public Double sqrt(double r) {
        return r;
    }

    protected static ManhattanDistanceType getInstance() {
        if (INSTANCE == null)
            INSTANCE = new ManhattanDistanceType();
        return INSTANCE;
    }
}
