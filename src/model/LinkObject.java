package model;

import view.Mode;
import java.awt.*;

public class LinkObject {
    private BasicObject startObject;
    private BasicObject endObject;
    private Point startPort;
    private Point endPort;
    private Mode linkType;

    // 新增：存放連接埠相對於物件左上角的偏移量
    private int startPortOffsetX, startPortOffsetY;
    private int endPortOffsetX, endPortOffsetY;

    public LinkObject(BasicObject start, BasicObject end, Point startPort, Point endPort, Mode linkType) {
        this.startObject = start;
        this.endObject = end;
        this.startPort = startPort;
        this.endPort = endPort;
        this.linkType = linkType;

        // 計算偏移量：在連線建立時記錄下來
        this.startPortOffsetX = startPort.x - start.x;
        this.startPortOffsetY = startPort.y - start.y;
        this.endPortOffsetX = endPort.x - end.x;
        this.endPortOffsetY = endPort.y - end.y;
    }

    // Getter 用於更新連線時使用
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

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLACK);
        g2d.drawLine(startPort.x, startPort.y, endPort.x, endPort.y);
        switch (linkType) {
            case ASSOCIATION:
                drawArrowHead(g2d, endPort, startPort, true);
                break;
            case GENERALIZATION:
                drawArrowHead(g2d, endPort, startPort, false);
                break;
            case COMPOSITION:
                drawDiamondHead(g2d, endPort, startPort);
                break;
            default:
                break;
        }
    }

    private void drawArrowHead(Graphics2D g2d, Point tip, Point tail, boolean filled) {
        int barb = 15;
        double phi = Math.toRadians(40);
        double theta = Math.atan2(tip.y - tail.y, tip.x - tail.x);
        double x1 = tip.x - barb * Math.cos(theta + phi);
        double y1 = tip.y - barb * Math.sin(theta + phi);
        double x2 = tip.x - barb * Math.cos(theta - phi);
        double y2 = tip.y - barb * Math.sin(theta - phi);
        Polygon arrowHead = new Polygon();
        arrowHead.addPoint(tip.x, tip.y);
        arrowHead.addPoint((int) x1, (int) y1);
        arrowHead.addPoint((int) x2, (int) y2);
        if (filled) {
            g2d.fill(arrowHead);
        } else {
            g2d.draw(arrowHead);
        }
    }

    private void drawDiamondHead(Graphics2D g2d, Point tip, Point tail) {
        int diamondLength = 15;
        int diamondWidth = 10;
        double dx = tip.x - tail.x;
        double dy = tip.y - tail.y;
        double len = Math.sqrt(dx * dx + dy * dy);
        double ux = dx / len;
        double uy = dy / len;
        int bx = tip.x - (int) (diamondLength * ux);
        int by = tip.y - (int) (diamondLength * uy);
        double px = -uy;
        double py = ux;
        int rx = bx + (int) (diamondWidth / 2 * px);
        int ry = by + (int) (diamondWidth / 2 * py);
        int lx = bx - (int) (diamondWidth / 2 * px);
        int ly = by - (int) (diamondWidth / 2 * py);
        Polygon diamond = new Polygon();
        diamond.addPoint(tip.x, tip.y);
        diamond.addPoint(rx, ry);
        diamond.addPoint(bx, by);
        diamond.addPoint(lx, ly);
        // Composition 顯示空心菱形：僅繪製邊框
        g2d.draw(diamond);
    }
}
