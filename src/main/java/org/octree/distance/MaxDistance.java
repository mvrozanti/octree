package org.octree.distance;

import org.octree.*;
import static java.lang.Math.*;

public class MaxDistance extends Distance {

    private static MaxDistance INSTANCE;

    private MaxDistance() {
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

    protected static MaxDistance getInstance() {
        if (INSTANCE == null)
            INSTANCE = new MaxDistance();
        return INSTANCE;
    }
}
