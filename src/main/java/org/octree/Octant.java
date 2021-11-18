package org.octree;

import java.util.*;
import java.util.function.*;

public class Octant<T> {
    boolean isLeaf;
    T x, y, z;
    float extent;
    int start, end;
    int size;
    Octant<T>[] child = new Octant[8];


}
