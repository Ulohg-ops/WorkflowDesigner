package model;

import java.awt.*;

/**
 * CompositionLink 代表組合連線，其裝飾為一個菱形。
 * 菱形的繪製利用連線方向向量及指定參數計算菱形四個頂點，
 * 然後以 Polygon 畫出菱形邊框。
 */
public class CompositionLink extends LinkObject {

    /**
     * 建構子：初始化組合連線的起點、終點及端口座標。
     *
     * @param start     起始物件
     * @param end       結束物件
     * @param startPort 起點的連線端口座標
     * @param endPort   終點的連線端口座標
     */
    public CompositionLink(BasicObject start, BasicObject end, Point startPort, Point endPort) {
        super(start, end, startPort, endPort);
    }

    /**
     * 繪製菱形裝飾：
     * 1. 設定菱形的半長度（從頂點到中心的距離）與菱形寬度（中心左右偏移）。
     * 2. 取得連線的起點與終點，並計算連線方向向量。
     * 3. 利用連線方向計算終點（上頂點）、底部頂點（延反方向延伸 2 * halfLength）
     *    以及中心點（tip 與 bottom 的中點）。
     * 4. 以中心點與與連線垂直的方向，計算菱形左右兩頂點。
     * 5. 利用 Polygon 組成菱形，並繪製出菱形邊框。
     *
     * @param g2d Graphics2D 物件，用於繪製菱形
     */
    @Override
    protected void drawDecoration(Graphics2D g2d) {
        int halfLength = 10;   // 菱形上頂點到中心點的距離
        int diamondWidth = 10; // 中心點左右各偏移的距離

        // 取得連線的起點與終點連接埠座標
        Point start = getStartPort();
        Point end = getEndPort();

        // 計算連線方向向量 (dx, dy)
        double dx = end.x - start.x;
        double dy = end.y - start.y;
        double len = Math.sqrt(dx * dx + dy * dy);
        if (len == 0) return; // 避免除以零

        // 正規化方向向量 (ux, uy)
        double ux = dx / len;
        double uy = dy / len;

        // 定義菱形的上頂點為 end（tip）
        Point tip = end;

        // 計算菱形的下頂點：以 tip 為上頂點，沿連線方向相反延伸 2 * halfLength
        int bx = tip.x - (int)(2 * halfLength * ux);
        int by = tip.y - (int)(2 * halfLength * uy);
        Point bottom = new Point(bx, by);

        // 計算中心點：tip 與 bottom 的中點
        int cx = (tip.x + bottom.x) / 2;
        int cy = (tip.y + bottom.y) / 2;

        // 利用與連線方向垂直的單位向量計算左右頂點
        double px = -uy;  // 垂直方向的 x 分量
        double py = ux;   // 垂直方向的 y 分量
        int rx = cx + (int)(diamondWidth * px); // 右頂點 x 座標
        int ry = cy + (int)(diamondWidth * py); // 右頂點 y 座標
        int lx = cx - (int)(diamondWidth * px); // 左頂點 x 座標
        int ly = cy - (int)(diamondWidth * py); // 左頂點 y 座標

        Polygon diamond = new Polygon();
        diamond.addPoint(tip.x, tip.y);       // 上頂點
        diamond.addPoint(rx, ry);             // 右頂點
        diamond.addPoint(bottom.x, bottom.y); // 下頂點
        diamond.addPoint(lx, ly);             // 左頂點

        g2d.drawPolygon(diamond);
    }
}
