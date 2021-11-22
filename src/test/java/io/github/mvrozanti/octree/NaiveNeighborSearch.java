package io.github.mvrozanti.octree;

import io.github.mvrozanti.octree.distance.*;
import java.util.*;
import lombok.*;

public class NaiveNeighborSearch {

    private List<PointT> data;
    @Getter
    @Setter
    private DistanceType distanceType;

    public NaiveNeighborSearch(DistanceType distanceType) {
        this.distanceType = distanceType;
    }

    public void initialize(List<PointT> points) {
        data = points;
    }

    public int findNeighbor(PointT query) {
        return findNeighbor(query, -1);
    }

    public int findNeighbor(PointT query, double minDistance) {
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

    public void radiusNeighbors(PointT query, double radius, List<Integer> resultIndices) {
        List<PointT> pts = data;
        resultIndices.clear();
        double sqrRadius = distanceType.sqr(radius);
        for (int i = 0; i < pts.size(); i++)
            if (distanceType.compute(query, pts.get(i)) < sqrRadius)
                resultIndices.add(i);
    }
}
