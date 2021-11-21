package io.github.mvrozanti.octree.distance;

import io.github.mvrozanti.octree.*;
import static java.lang.Math.*;

public class MaxDistanceType extends DistanceType {

    private static MaxDistanceType INSTANCE;

    private MaxDistanceType() {
    }

    @Override
    public Double compute(PointT p, PointT q) {
        double diff1 = abs(p.x() - q.x());
        double diff2 = abs(p.y() - q.y());
        double diff3 = abs(p.z() - q.z());

        double maximum = diff1;
        if (diff2 > maximum) maximum = diff2;
        if (diff3 > maximum) maximum = diff3;

        return maximum;
    }

    @Override
    public Double norm(double x, double y, double z) {
        double maximum = x;
        if (y > maximum) maximum = y;
        if (z > maximum) maximum = z;
        return maximum;
    }

    @Override
    public Double sqr(double r) {
        return r;
    }

    @Override
    public Double sqrt(double r) {
        return r;
    }

    protected static MaxDistanceType getInstance() {
        if (INSTANCE == null)
            INSTANCE = new MaxDistanceType();
        return INSTANCE;
    }
}
