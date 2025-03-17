package model;

import java.awt.*;

public abstract class LinkObject {
    protected BasicObject startObject;
    protected BasicObject endObject;
    protected Point startPort;
    protected Point endPort;

    // 記錄連接埠相對於物件左上角的偏移量
    protected int startPortOffsetX, startPortOffsetY;
    protected int endPortOffsetX, endPortOffsetY;
    protected int depth;    // 數值範圍 0-99 (數字越小表示越在上層)


    public LinkObject(BasicObject start, BasicObject end, Point startPort, Point endPort) {
        this.startObject = start;
        this.endObject = end;
        this.startPort = startPort;
        this.endPort = endPort;

        // 在連線建立時記錄偏移量
        this.startPortOffsetX = startPort.x - start.x;
        this.startPortOffsetY = startPort.y - start.y;
        this.endPortOffsetX = endPort.x - end.x;
        this.endPortOffsetY = endPort.y - end.y;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        if (depth < 0) depth = 0;
        if (depth > 99) depth = 99;
        this.depth = depth;
    }

    public BasicObject getStartObject() {
        return startObject;
    }

    public BasicObject getEndObject() {
        return endObject;
    }

    public void setStartPort(Point p) {
        startPort = p;
    }

    public void setEndPort(Point p) {
        endPort = p;
    }

    public int getStartPortOffsetX() {
        return startPortOffsetX;
    }

    public int getStartPortOffsetY() {
        return startPortOffsetY;
    }

    public int getEndPortOffsetX() {
        return endPortOffsetX;
    }

    public int getEndPortOffsetY() {
        return endPortOffsetY;
    }

    // 當物件移動時，依據記錄的偏移量更新連線端點
    public void updatePorts() {
        startPort = new Point(startObject.x + startPortOffsetX, startObject.y + startPortOffsetY);
        endPort = new Point(endObject.x + endPortOffsetX, endObject.y + endPortOffsetY);
    }

    public void draw(Graphics g) {
        updatePorts();  // 先更新連線端點
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLACK);
        g2d.drawLine(startPort.x, startPort.y, endPort.x, endPort.y);
        drawDecoration(g2d);
    }

    // 子類別實作此方法來繪製不同的連線裝飾（箭頭、菱形）
    protected abstract void drawDecoration(Graphics2D g2d);
}
