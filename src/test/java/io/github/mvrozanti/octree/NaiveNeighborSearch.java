package io.github.mvrozanti.octree;

import io.github.mvrozanti.octree.distance.*;
import java.util.*;

public class NaiveNeighborSearch {

    private List<PointT> data;

    public void initialize(List<PointT> points) {
        data = points;
    }

    public int findNeighbor(PointT query, DistanceType distanceType) {
        return findNeighbor(query, -1, distanceType);
    }

    public int findNeighbor(PointT query, double minDistance, DistanceType distanceType) {
        List<PointT> pts = data;
        if (pts.size() == 0) return -1;

        double maxDistance = Double.POSITIVE_INFINITY;
        double sqrMinDistance = (minDistance < 0) ? minDistance : distanceType.sqr(minDistance);
        int resultIndex = -1;
        for (int i = 0; i < pts.size(); i++) {
            double dist = distanceType.compute(query, pts.get(i));
            if ((dist > sqrMinDistance) && (dist < maxDistance)) {
                maxDistance = dist;
                resultIndex = i;
            }
        }

        return resultIndex;
    }

    public void radiusNeighbors(PointT query, double radius, List<Integer> resultIndices, DistanceType distanceType) {
        List<PointT> pts = data;
        resultIndices.clear();
        double sqrRadius = distanceType.sqr(radius);
        for (int i = 0; i < pts.size(); i++)
            if (distanceType.compute(query, pts.get(i)) < sqrRadius)
                resultIndices.add(i);
    }
}
