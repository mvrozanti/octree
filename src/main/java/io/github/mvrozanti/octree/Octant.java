package io.github.mvrozanti.octree;

import java.util.*;
import lombok.*;

@Data
public class Octant {
    private boolean isLeaf = true;
    private Double x, y, z;
    private Double extent;
    private int start, end;
    private int size;
    @Getter(AccessLevel.NONE)
    private List<Octant> child = Arrays.asList(new Octant[8]);

    public void setChild(int index, Octant child) {
        this.child.set(index, child);
    }

    public Octant getChild(int index) {
        return child.get(index);
    }
}
