package model;

import enums.LabelShape;
import java.awt.*;

/**
 * OvalObject 繼承自 BasicObject，代表橢圓形的基本物件，
 * 負責繪製橢圓形狀、連接埠（ports）以及物件內部的標籤。
 */
public class OvalObject extends BasicObject {

    /**
     * 建構子：初始化橢圓物件的位置與尺寸。
     *
     * @param x      橢圓的 x 座標
     * @param y      橢圓的 y 座標
     * @param width  橢圓的寬度
     * @param height 橢圓的高度
     */
    public OvalObject(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    /**
     * 繪製橢圓物件，包括填充背景、外框、連接埠與標籤。
     *
     * @param g Graphics 物件，用於繪製
     */
    @Override
    public void draw(Graphics g) {
        // 1. 繪製橢圓背景，使用淡色填滿
        g.setColor(Color.decode("#F6F0F0"));
        g.fillOval(getX(), getY(), getWidth(), getHeight());

        // 2. 繪製橢圓外框
        g.setColor(Color.BLACK);
        g.drawOval(getX(), getY(), getWidth(), getHeight());

        // 3. 如果需要顯示連接埠（ports），則繪製位於橢圓四邊中點的小方塊
        if (isShowPorts()) {
            g.setColor(Color.BLACK);
            Point[] ports = getPorts();
            for (Point pt : ports) {
                g.fillRect(pt.x - 3, pt.y - 3, 6, 6);
            }
        }

        // 4. 若物件有標籤文字，則在橢圓內部置中繪製標籤
        if (!getLabel().isEmpty()) {
            Graphics2D g2d = (Graphics2D) g;
            // 設定字型與大小
            g2d.setFont(new Font("SansSerif", Font.PLAIN, getFontSize()));
            FontMetrics fm = g2d.getFontMetrics();

            String text = getLabel();
            int textWidth = fm.stringWidth(text);  // 文字寬度
            int textHeight = fm.getAscent();         // 文字高度（以字體上升高度計算）

            // 計算橢圓外框的中心點
            int centerX = getX() + getWidth() / 2;
            int centerY = getY() + getHeight() / 2;

            // 計算文字置中的座標
            int labelX = centerX - textWidth / 2;
            // 基線位置微調：使文字垂直置中
            int labelY = centerY + textHeight / 4;

            // 先繪製標籤背景：根據使用者選擇的形狀繪製背景（矩形或橢圓）
            g2d.setColor(getLabelColor());
            if (getLabelShape() == LabelShape.RECTANGLE) {
                g2d.fillRect(labelX, labelY - textHeight, textWidth, textHeight);
            } else if (getLabelShape() == LabelShape.OVAL) {
                g2d.fillOval(labelX, labelY - textHeight, textWidth, textHeight);
            }

            // 再繪製標籤文字，使用黑色
            g2d.setColor(Color.BLACK);
            g2d.drawString(text, labelX, labelY);
        }
    }

    /**
     * 取得橢圓物件的連接埠座標。
     * 4 個連接埠，分別位於橢圓上、下、左、右中點。
     *
     * @return 連接埠的 Point 陣列
     */
    @Override
    public Point[] getPorts() {
        return new Point[] {
                new Point(getX() + getWidth() / 2, getY()),                   // 上端中點
                new Point(getX() + getWidth() / 2, getY() + getHeight()),         // 下端中點
                new Point(getX(), getY() + getHeight() / 2),                      // 左端中點
                new Point(getX() + getWidth(), getY() + getHeight() / 2)            // 右端中點
        };
    }
}
