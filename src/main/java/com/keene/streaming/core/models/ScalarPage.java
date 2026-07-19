package com.keene.streaming.core.models;

import java.util.List;

public class ScalarPage {

    private List<Scalar> scalars;
    private int offset;
    private int count;

    public ScalarPage() {
    }

    public ScalarPage(List<Scalar> scalars, int offset, int count) {
        this.scalars = scalars;
        this.offset = offset;
        this.count = count;
    }

    public List<Scalar> getScalars() {
        return scalars;
    }

    public void setScalars(List<Scalar> scalars) {
        this.scalars = scalars;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
