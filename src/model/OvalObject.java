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
        g.setColor(Color.decode("#F6F0F0"));
        g.fillOval(getX(), getY(), getWidth(), getHeight());

        g.setColor(Color.BLACK);
        g.drawOval(getX(), getY(), getWidth(), getHeight());

        if (isShowPorts()) {
            g.setColor(Color.BLACK);
            Point[] ports = getPorts();
            for (Point pt : ports) {
                g.fillRect(pt.x - 3, pt.y - 3, 6, 6);
            }
        }

        if (!getLabel().isEmpty()) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setFont(new Font("SansSerif", Font.PLAIN, getFontSize()));
            FontMetrics fm = g2d.getFontMetrics();

            String text = getLabel();
            int textWidth = fm.stringWidth(text);  // 文字寬度
            int textHeight = fm.getAscent();         // 文字高度（以字體上升高度計算）

            int centerX = getX() + getWidth() / 2;
            int centerY = getY() + getHeight() / 2;

            int labelX = centerX - textWidth / 2;
            int labelY = centerY + textHeight / 4;

            g2d.setColor(getLabelColor());
            if (getLabelShape() == LabelShape.RECTANGLE) {
                g2d.fillRect(labelX, labelY - textHeight, textWidth, textHeight);
            } else if (getLabelShape() == LabelShape.OVAL) {
                g2d.fillOval(labelX, labelY - textHeight, textWidth, textHeight);
            }

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
