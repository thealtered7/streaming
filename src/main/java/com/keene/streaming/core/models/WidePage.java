package com.keene.streaming.core.models;

import java.util.List;

public class WidePage {

    private List<Wide> wides;
    private int offset;
    private int count;

    public WidePage() {
    }

    public WidePage(List<Wide> wides, int offset, int count) {
        this.wides = wides;
        this.offset = offset;
        this.count = count;
    }

    public List<Wide> getWides() {
        return wides;
    }

    public void setWides(List<Wide> wides) {
        this.wides = wides;
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
