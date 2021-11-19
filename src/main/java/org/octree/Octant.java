package org.octree;

import java.util.*;
import lombok.*;

@Data
public class Octant<T> {
    private boolean isLeaf = true;
    private T x, y, z;
    private float extent;
    private int start, end;
    private int size;
    private List<Octant<T>> child = new ArrayList<>(8);
}
