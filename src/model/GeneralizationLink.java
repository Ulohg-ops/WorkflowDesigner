package model;

import java.awt.*;

/**
 * GeneralizationLink 代表一般化連線，其裝飾為一個箭頭。
 * 箭頭的繪製利用端點、指定角度與箭頭尺寸計算三個頂點，
 * 並用 Polygon 畫出箭頭形狀。
 */
public class GeneralizationLink extends LinkObject {

    /**
     * 建構子：初始化一般化連線的起點、終點以及各端口座標。
     *
     * @param start     起始物件
     * @param end       結束物件
     * @param startPort 起點的連線端口座標
     * @param endPort   終點的連線端口座標
     */
    public GeneralizationLink(BasicObject start, BasicObject end, Point startPort, Point endPort) {
        super(start, end, startPort, endPort);
    }

    /**
     * 繪製箭頭裝飾：
     * 1. 取得連線的起點與終點。
     * 2. 根據終點與起點計算連線方向，並利用反向角度算出箭頭兩側頂點。
     * 3. 用 Polygon 將終點與兩側頂點組成箭頭形狀，並繪製出來。
     *
     * @param g2d Graphics2D 物件，用於繪製箭頭
     */
    @Override
    protected void drawDecoration(Graphics2D g2d) {
        int barb = 15; // 箭頭邊長
        double phi = Math.toRadians(40); // 箭頭角度 (40 度轉為弧度)

        // 透過 getter 取得連線起點與終點的連接埠座標
        Point start = getStartPort();
        Point end = getEndPort();

        // 計算箭頭所依據的角度
        // 注意：atan2 的參數順序為 (y, x)
        double theta = Math.atan2(-start.y, end.x - start.x);

        // 計算箭頭左右兩側頂點座標
        double x1 = end.x - barb * Math.cos(theta + phi);
        double y1 = end.y - barb * Math.sin(theta + phi);
        double x2 = end.x - barb * Math.cos(theta - phi);
        double y2 = end.y - barb * Math.sin(theta - phi);

        // 利用 Polygon 組成箭頭形狀：頂點順序為 終點、右側頂點、左側頂點
        Polygon arrowHead = new Polygon();
        arrowHead.addPoint(end.x, end.y);
        arrowHead.addPoint((int)x1, (int)y1);
        arrowHead.addPoint((int)x2, (int)y2);

        // 繪製箭頭
        g2d.draw(arrowHead);
    }
}
