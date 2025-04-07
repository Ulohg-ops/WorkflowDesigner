package model;

import java.awt.*;

public abstract class LinkObject extends DisplayObject {
    private BasicObject startObject;
    private BasicObject endObject;
    private Point startPort;
    private Point endPort;

    // 記錄端口相對於物件左上角的偏移量
    private int startPortOffsetX, startPortOffsetY;
    private int endPortOffsetX, endPortOffsetY;

    public LinkObject(BasicObject start, BasicObject end, Point startPort, Point endPort) {
        super();
        this.startObject = start;
        this.endObject = end;
        this.startPort = startPort;
        this.endPort = endPort;
        this.startPortOffsetX = startPort.x - start.getX();
        this.startPortOffsetY = startPort.y - start.getY();
        this.endPortOffsetX = endPort.x - end.getX();
        this.endPortOffsetY = endPort.y - end.getY();
        reCalcDepth();
    }

    public BasicObject getStartObject() { return startObject; }
    public void setStartObject(BasicObject startObject) { this.startObject = startObject; }

    public BasicObject getEndObject() { return endObject; }
    public void setEndObject(BasicObject endObject) { this.endObject = endObject; }

    public Point getStartPort() { return startPort; }
    public void setStartPort(Point startPort) { this.startPort = startPort; }

    public Point getEndPort() { return endPort; }
    public void setEndPort(Point endPort) { this.endPort = endPort; }

    public int getStartPortOffsetX() { return startPortOffsetX; }
    public int getStartPortOffsetY() { return startPortOffsetY; }
    public int getEndPortOffsetX() { return endPortOffsetX; }
    public int getEndPortOffsetY() { return endPortOffsetY; }

    /**
     * 依據連線兩端物件的 depth 重新計算連線的 depth，這裡直接取兩者中的較小值
     */
    public void reCalcDepth() {
        depth = Math.min(startObject.getDepth(), endObject.getDepth());
    }

    /**
     * 更新端口位置，並重新計算 depth
     */
    public void updatePorts() {
        startPort = new Point(startObject.getX() + startPortOffsetX, startObject.getY() + startPortOffsetY);
        endPort = new Point(endObject.getX() + endPortOffsetX, endObject.getY() + endPortOffsetY);
        reCalcDepth();
    }

    public void draw(Graphics g) {
        updatePorts();
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLACK);
        g2d.drawLine(startPort.x, startPort.y, endPort.x, endPort.y);
        drawDecoration(g2d);
    }

    /**
     * 由子類別實作連線的裝飾，例如箭頭、菱形等。
     */
    protected abstract void drawDecoration(Graphics2D g2d);
}
