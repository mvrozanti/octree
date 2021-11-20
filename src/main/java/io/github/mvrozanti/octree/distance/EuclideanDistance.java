package io.github.mvrozanti.octree.distance;

import io.github.mvrozanti.octree.*;
import static java.lang.Math.*;

public class EuclideanDistance extends Distance {

    private static EuclideanDistance INSTANCE;

    private EuclideanDistance() {
    }

    @Override
    public Double compute(PointT p, PointT q) {
        double diff1 = p.x() - q.x();
        double diff2 = p.y() - p.y();
        double diff3 = p.z() - p.z();

        return pow(diff1, 2) + pow(diff2, 2) + pow(diff3, 2);
    }

    @Override
    public Double norm(double x, double y, double z) {
        return pow(x, 2) + pow(y, 2) + pow(z, 2);
    }

    @Override
    public Double sqr(double r) {
        return r * r;
    }

    @Override
    public Double sqrt(double r) {
        return Math.sqrt(r);
    }

    protected static EuclideanDistance getInstance() {
        if (INSTANCE == null)
            INSTANCE = new EuclideanDistance();
        return INSTANCE;
    }
}
