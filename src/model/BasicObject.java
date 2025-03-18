package model;

import enums.LabelShape;
import java.awt.*;

/**
 * BasicObject 抽象類別，代表圖形物件的基本屬性與行為。
 * 包含位置、尺寸、層次、Label 屬性，以及連線端口的抽象方法。
 */
public abstract class BasicObject {

// =================== 基本屬性 ===================
    public int x;
    public int y;
    public int width;
    public int height;
    // 物件的層次值；數值越小代表位於上層，選取時我們將其設為 -1
    protected int depth;
    protected boolean showPorts = false;

// =================== Label屬性 ===================
    protected String label = "";
    protected LabelShape labelShape = LabelShape.RECTANGLE;
    protected Color labelColor = Color.WHITE;
    protected int fontSize = 12;

    // 靜態計數器：每建立一個新物件，就給它一個遞減的 depth 值
    private static int nextDepth = 99;

// =================== Constructor ===================

    /**
     * 建構子，初始化物件位置、尺寸及層次。
     *
     * @param x 物件的 x 座標
     * @param y 物件的 y 座標
     * @param width 物件的寬度
     * @param height 物件的高度
     */
    public BasicObject(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        // 給新建立的物件一個較大的 depth 值
        this.depth = nextDepth--;
        System.out.printf("depth = %d\n", depth);

    }

// =================== Getters and Setters ===================

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * 設定全域靜態計數器的下一個深度值。
     *
     * @param nextDepth 下一個深度值
     */
    public static void setNextDepth(int nextDepth) {
        BasicObject.nextDepth = nextDepth;
    }

    /**
     * 當物件取消選取時，取得全域靜態計數器的下一個深度值。
     *
     * @return 下一個深度值
     */
    public static int getNextDepth() {
        return nextDepth--;
    }

    public int getDepth() {
        return depth;
    }

    /**
     * 設定物件的層次值，範圍限定在 -1（代表選取）到 99 之間。
     *
     * @param depth 層次值
     */
    public void setDepth(int depth) {
        if (depth < -1) depth = -1;
        if (depth > 99) depth = 99;
        this.depth = depth;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public LabelShape getLabelShape() {
        return labelShape;
    }

    public void setLabelShape(LabelShape labelShape) {
        this.labelShape = labelShape;
    }

    public Color getLabelColor() {
        return labelColor;
    }

    public void setLabelColor(Color labelColor) {
        this.labelColor = labelColor;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public void setShowPorts(boolean show) {
        this.showPorts = show;
    }

    public boolean isShowPorts() {
        return showPorts;
    }

// =================== 其他抽象和具體方法 ===================

    /**
     * 繪製物件，具體的繪製方法由子類別實作。
     *
     * @param g Graphics 物件
     */
    public abstract void draw(Graphics g);

    /**
     * 取得物件連線使用的端口點，具體實現由子類別定義。
     *
     * @return 端口點陣列
     */
    public abstract Point[] getPorts();

    /**
     * 判斷給定的點是否位於物件內。
     *
     * @param p 檢查點
     * @return 若點在物件內則回傳 true，否則回傳 false
     */
    public boolean contains(Point p) {
        return (p.x >= x && p.x <= x + width && p.y >= y && p.y <= y + height);
    }

    /**
     * 根據給定的參考點 p，找出與其距離最近的端口點。
     *
     * @param p 參考點
     * @return 與 p 距離最短的端口點
     */
    public Point getClosestPort(Point p) {
        Point[] ports = getPorts();
        Point closest = ports[0];
        double minDist = p.distance(ports[0]);
        for (int i = 1; i < ports.length; i++) {
            double dist = p.distance(ports[i]);
            if (dist < minDist) {
                minDist = dist;
                closest = ports[i];
            }
        }
        return closest;
    }
}
