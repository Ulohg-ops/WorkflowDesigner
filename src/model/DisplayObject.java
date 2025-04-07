package model;

public abstract class DisplayObject {
    protected static final int MIN_DEPTH = 0;
    protected static final int MAX_DEPTH = 99;
    protected static int nextDepth = MAX_DEPTH;

    protected int depth;

    public DisplayObject() {
        this.depth = nextDepth--;
    }

    public int getDepth() {
        return depth;
    }

    /**
     * 設定 depth，並限制在 MIN_DEPTH 與 MAX_DEPTH 之間
     */
    public void setDepth(int depth) {
        if (depth < MIN_DEPTH) {
            depth = MIN_DEPTH;
        }
        if (depth > MAX_DEPTH) {
            depth = MAX_DEPTH;
        }
        this.depth = depth;
    }
}
