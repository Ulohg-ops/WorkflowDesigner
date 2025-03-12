package model;

import java.awt.*;

public abstract class BasicObject {
    public int x;
    public int y;
    public int width;
    public int height;
    protected boolean showPorts = false;  // 是否顯示連接埠

    public BasicObject(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // 物件繪製（由子類實作：Rect、Oval）
    public abstract void draw(Graphics g);

    // 回傳該物件的所有連接埠座標 (Rect 有 8 個, Oval 有 4 個)
    public abstract Point[] getPorts();

    // 設定/取得是否顯示連接埠
    public void setShowPorts(boolean show) {
        this.showPorts = show;
    }

    public boolean isShowPorts() {
        return showPorts;
    }

    // 判斷點 (p) 是否落在物件範圍內
    public boolean contains(Point p) {
        return (p.x >= x && p.x <= x + width && p.y >= y && p.y <= y + height);
    }

    // 取得最接近 p 的連接埠
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
