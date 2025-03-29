package model;

import java.awt.*;

/**
 * AssociationLink 代表關聯連線，其裝飾為一個箭頭，
 * 用來指示連線的方向。
 */
public class AssociationLink extends LinkObject {

    /**
     * 建構子：初始化連線的起點、終點以及端點座標。
     *
     * @param start     起始物件
     * @param end       結束物件
     * @param startPort 起點連線端口座標
     * @param endPort   終點連線端口座標
     */
    public AssociationLink(BasicObject start, BasicObject end, Point startPort, Point endPort) {
        super(start, end, startPort, endPort);
    }

    /**
     * 繪製連線的箭頭裝飾：
     * 1. 設定箭頭的尺寸與角度參數。
     * 2. 利用 getStartPort() 與 getEndPort() 取得連線的起點與終點座標。
     * 3. 根據起點與終點計算連線的方向角 theta。
     * 4. 計算箭頭兩側頂點的位置，分別位於終點延伸一段距離的方向上。
     * 5. 利用 Polygon 組成箭頭形狀並填滿。
     *
     * @param g2d Graphics2D 物件，用於繪製箭頭裝飾
     */
    @Override
    protected void drawDecoration(Graphics2D g2d) {
        int barb = 15; // 箭頭邊長：決定箭頭的大小
        double phi = Math.toRadians(40); // 箭頭兩側與連線方向的夾角，40 度轉換成弧度

        // 取得連線的起點與終點連接埠座標
        Point start = getStartPort();
        Point end = getEndPort();

        // 計算連線方向角 theta，利用 atan2(y, x) 計算角度
        double theta = Math.atan2(end.y - start.y, end.x - start.x);

        // 根據 theta 與 phi 計算箭頭兩側頂點的座標
        // 計算右側頂點：終點減去一段距離 (barb) 在 (theta + phi) 方向上的分量
        double x1 = end.x - barb * Math.cos(theta + phi);
        double y1 = end.y - barb * Math.sin(theta + phi);
        // 計算左側頂點：終點減去一段距離在 (theta - phi) 方向上的分量
        double x2 = end.x - barb * Math.cos(theta - phi);
        double y2 = end.y - barb * Math.sin(theta - phi);

        // 利用 Polygon 組成箭頭形狀，順序依次為終點、右側頂點、左側頂點
        Polygon arrowHead = new Polygon();
        arrowHead.addPoint(end.x, end.y);            // 箭頭尖端（終點）
        arrowHead.addPoint((int) x1, (int) y1);        // 箭頭右側頂點
        arrowHead.addPoint((int) x2, (int) y2);        // 箭頭左側頂點

        // 填充箭頭，繪製出裝飾效果
        g2d.fill(arrowHead);
    }
}
