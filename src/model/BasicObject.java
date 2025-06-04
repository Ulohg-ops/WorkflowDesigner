package model;

import enums.LabelShape;
import java.util.List;
import java.util.Collections;

import java.awt.*;

public abstract class BasicObject extends DisplayObject {

    private int x;
    private int y;
    private int width;
    private int height;
    private boolean showPorts = false;

    private String label = "";
    private LabelShape labelShape = LabelShape.RECTANGLE;
    private Color labelColor = Color.WHITE;
    private int fontSize = 12;

    public BasicObject(int x, int y, int width, int height) {
        super();  // 呼叫 DisplayObject 建構子，設定 depth
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // Getter 與 Setter
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }

    public void setShowPorts(boolean show) { this.showPorts = show; }
    public boolean isShowPorts() { return showPorts; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public LabelShape getLabelShape() { return labelShape; }
    public void setLabelShape(LabelShape labelShape) { this.labelShape = labelShape; }

    public Color getLabelColor() { return labelColor; }
    public void setLabelColor(Color labelColor) { this.labelColor = labelColor; }

    public int getFontSize() { return fontSize; }
    public void setFontSize(int fontSize) { this.fontSize = fontSize; }

    /**
     * 判斷點是否在此物件範圍內
     */
    public boolean contains(Point p) {
        return (p.x >= x && p.x <= x + width && p.y >= y && p.y <= y + height);
    }

    /**
     * 繪製物件，由子類別實作
     */
    public abstract void draw(Graphics g);

    /**
     * 取得此物件的連線端口，由子類別定義
     */
    public abstract Point[] getPorts();

    /**
     * 取得與參考點距離最近的端口
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
    
    public void moveBy(int dx, int dy) {
        setX(getX() + dx);
        setY(getY() + dy);
    }

    public boolean isGroup() {
        return false;
    }

    public List<BasicObject> getChildren() {
        return Collections.emptyList();
    }

    public void ungroupTo(List<BasicObject> output) {
    }

}
