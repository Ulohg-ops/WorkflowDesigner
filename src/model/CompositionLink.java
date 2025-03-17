package model;

import java.awt.*;

public class CompositionLink extends LinkObject {

    public CompositionLink(BasicObject start, BasicObject end, Point startPort, Point endPort) {
        super(start, end, startPort, endPort);
    }

    @Override
    protected void drawDecoration(Graphics2D g2d) {
        // 設定參數：沿著連線方向的半長度與菱形的寬度
        int halfLength = 10;  // 這裡代表菱形上頂點到中心點的距離
        int diamondWidth = 10;  // 中心點左右各偏移的距離

        // 計算連線方向向量（從 startPort 指向 endPort）
        double dx = endPort.x - startPort.x;
        double dy = endPort.y - startPort.y;
        double len = Math.sqrt(dx * dx + dy * dy);
        if (len == 0) return; // 避免除以零

        double ux = dx / len;
        double uy = dy / len;

        // 定義菱形四個頂點：
        // 1. 上頂點：endPort 本身
        Point tip = endPort;

        // 2. 下頂點：以 endPort 為上頂點，沿連線方向相反延伸 2 * halfLength
        int bx = tip.x - (int)(2 * halfLength * ux);
        int by = tip.y - (int)(2 * halfLength * uy);
        Point bottom = new Point(bx, by);

        // 3. 中心點：tip 與 bottom 的中點
        int cx = (tip.x + bottom.x) / 2;
        int cy = (tip.y + bottom.y) / 2;
        Point center = new Point(cx, cy);

        // 4. 左右頂點：以中心點為基準，沿著與連線方向垂直的方向偏移
        double px = -uy;  // 垂直單位向量
        double py = ux;
        int rx = cx + (int)(diamondWidth * px);
        int ry = cy + (int)(diamondWidth * py);
        int lx = cx - (int)(diamondWidth * px);
        int ly = cy - (int)(diamondWidth * py);

        Polygon diamond = new Polygon();
        diamond.addPoint(tip.x, tip.y);       // 上頂點
        diamond.addPoint(rx, ry);             // 右頂點
        diamond.addPoint(bottom.x, bottom.y); // 下頂點
        diamond.addPoint(lx, ly);             // 左頂點

        g2d.drawPolygon(diamond);

    }
}
