package io.github.mvrozanti.octree;

import io.github.mvrozanti.octree.distance.*;
import java.util.*;

public class NaiveNeighborSearch {

    private List<PointT> data;

    public void initialize(List<PointT> points) {
        data = points;
    }

    public int findNeighbor(PointT query, Distance distance) {
        return findNeighbor(query, -1, distance);
    }

    public int findNeighbor(PointT query, double minDistance, Distance distance) {
        List<PointT> pts = data;
        if (pts.size() == 0) return -1;

        double maxDistance = Double.POSITIVE_INFINITY;
        double sqrMinDistance = (minDistance < 0) ? minDistance : distance.sqr(minDistance);
        int resultIndex = -1;
        for (int i = 0; i < pts.size(); i++) {
            double dist = distance.compute(query, pts.get(i));
            if ((dist > sqrMinDistance) && (dist < maxDistance)) {
                maxDistance = dist;
                resultIndex = i;
            }
        }

        return resultIndex;
    }

    public void radiusNeighbors(PointT query, double radius, List<Integer> resultIndices, Distance distance) {
        List<PointT> pts = data;
        resultIndices.clear();
        double sqrRadius = distance.sqr(radius);
        for (int i = 0; i < pts.size(); i++)
            if (distance.compute(query, pts.get(i)) < sqrRadius)
                resultIndices.add(i);
    }
}
