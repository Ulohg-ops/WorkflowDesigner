package model;

import java.awt.*;

/**
 * LinkObject 抽象類別，代表連結兩個 BasicObject 的連線。
 * 負責記錄起點、終點、連線端口的偏移量及深度計算。
 */
public abstract class LinkObject {

    // 連線所連接的兩個物件
    private BasicObject startObject;
    private BasicObject endObject;

    // 連線端點在畫面上的實際座標
    private Point startPort;
    private Point endPort;

    // 端口相對於物件左上角的偏移量
    private int startPortOffsetX, startPortOffsetY;
    private int endPortOffsetX, endPortOffsetY;

    // 連線的深度，0 代表被選取
    private int depth;

    /**
     * 建構子，初始化連線的起點、終點及端口座標，同時計算偏移量及深度。
     *
     * @param start 起始物件
     * @param end 結束物件
     * @param startPort 起點的連線端口座標
     * @param endPort 終點的連線端口座標
     */
    public LinkObject(BasicObject start, BasicObject end, Point startPort, Point endPort) {
        this.startObject = start;
        this.endObject = end;
        this.startPort = startPort;
        this.endPort = endPort;

        // 計算端口相對於物件左上角的偏移量
        this.startPortOffsetX = startPort.x - start.getX();
        this.startPortOffsetY = startPort.y - start.getY();
        this.endPortOffsetX = endPort.x - end.getX();
        this.endPortOffsetY = endPort.y - end.getY();

        reCalcDepth();
    }

    public BasicObject getStartObject() {
        return startObject;
    }

    public void setStartObject(BasicObject startObject) {
        this.startObject = startObject;
    }

    public BasicObject getEndObject() {
        return endObject;
    }

    public void setEndObject(BasicObject endObject) {
        this.endObject = endObject;
    }

    public Point getStartPort() {
        return startPort;
    }

    public void setStartPort(Point startPort) {
        this.startPort = startPort;
    }

    public Point getEndPort() {
        return endPort;
    }

    public void setEndPort(Point endPort) {
        this.endPort = endPort;
    }

    public int getStartPortOffsetX() {
        return startPortOffsetX;
    }

    public void setStartPortOffsetX(int startPortOffsetX) {
        this.startPortOffsetX = startPortOffsetX;
    }

    public int getStartPortOffsetY() {
        return startPortOffsetY;
    }

    public void setStartPortOffsetY(int startPortOffsetY) {
        this.startPortOffsetY = startPortOffsetY;
    }

    public int getEndPortOffsetX() {
        return endPortOffsetX;
    }

    public void setEndPortOffsetX(int endPortOffsetX) {
        this.endPortOffsetX = endPortOffsetX;
    }

    public int getEndPortOffsetY() {
        return endPortOffsetY;
    }

    public void setEndPortOffsetY(int endPortOffsetY) {
        this.endPortOffsetY = endPortOffsetY;
    }

    public int getDepth() {
        return depth;
    }

    /**
     * 設定連線的深度，並限制在 0 與 99 之間。
     *
     * @param depth 深度值
     */
    public void setDepth(int depth) {
        if (depth < 0) {
            depth = 0;
        }
        if (depth > 99) {
            depth = 99;
        }
        this.depth = depth;
    }

    /**
     * 重新計算連線的深度：<br>
     * - 若任一端的物件深度為 0（代表被選取），則連線深度設為 0；<br>
     * - 否則，取兩端物件深度的較大值。
     */
    public void reCalcDepth() {
        if (startObject.getDepth() == 0 || endObject.getDepth() == 0) {
            depth = 0;
        } else {
            depth = Math.min(startObject.getDepth(), endObject.getDepth());
        }
    }

    /**
     * 更新連線端點的座標，根據物件移動後的新位置以及原先的偏移量，
     * 並重新計算連線的深度。
     */
    public void updatePorts() {
        startPort = new Point(startObject.getX() + startPortOffsetX, startObject.getY() + startPortOffsetY);
        endPort = new Point(endObject.getX() + endPortOffsetX, endObject.getY() + endPortOffsetY);
        reCalcDepth();
    }

    /**
     * 畫出連線：先更新端點位置及深度，再繪製連線及裝飾（由子類別實作）。
     *
     * @param g Graphics 物件
     */
    public void draw(Graphics g) {
        updatePorts();
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLACK);
        g2d.drawLine(startPort.x, startPort.y, endPort.x, endPort.y);
        drawDecoration(g2d);
    }

    /**
     * 抽象方法，由子類別實作連線的裝飾繪製（例如箭頭或菱形）。
     *
     * @param g2d Graphics2D 物件
     */
    protected abstract void drawDecoration(Graphics2D g2d);
}
