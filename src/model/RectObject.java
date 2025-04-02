package model;

import enums.LabelShape;
import java.awt.*;

/**
 * RectObject 代表矩形物件，繼承自 BasicObject。
 * 此物件除了繪製基本的矩形背景與外框外，
 * 當需要時也會顯示連接埠以及置中繪製標籤。
 */
public class RectObject extends BasicObject {

    /**
     * 建構子：根據傳入的 x、y、width 與 height 初始化矩形物件。
     *
     * @param x      矩形左上角的 x 座標
     * @param y      矩形左上角的 y 座標
     * @param width  矩形的寬度
     * @param height 矩形的高度
     */
    public RectObject(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    /**
     * 繪製矩形物件：
     * 1. 填充矩形背景色
     * 2. 繪製矩形外框
     * 3. 繪製兩條水平分隔線（將矩形分成三部分）
     * 4. 根據 isShowPorts() 判斷是否繪製連接埠（8 個小方塊）
     * 5. 如果 label 不為空，計算文字置中位置後，先繪製標籤背景，
     *    再繪製標籤文字
     *
     * @param g Graphics 物件，用於進行繪製操作
     */
    @Override
    public void draw(Graphics g) {
        g.setColor(Color.decode("#F6F0F0"));
        g.fillRect(getX(), getY(), getWidth(), getHeight());

        g.setColor(Color.BLACK);
        g.drawRect(getX(), getY(), getWidth(), getHeight());

        int line1Y = getY() + getHeight() / 3;
        int line2Y = getY() + (2 * getHeight() / 3);
        g.drawLine(getX(), line1Y, getX() + getWidth(), line1Y);
        g.drawLine(getX(), line2Y, getX() + getWidth(), line2Y);

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
            int textHeight = fm.getAscent();         // 文字上升高度

            int labelX = getX() + (getWidth() - textWidth) / 2;
            int labelY = getY() + (getHeight() - textHeight) / 2 + fm.getAscent();

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
     * 取得矩形物件的連接埠座標。
     * 8 個連接埠：分別位於矩形的四角以及四邊中點。
     *
     * @return Point 陣列，包含 8 個連接埠的位置
     */
    @Override
    public Point[] getPorts() {
        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getHeight();
        return new Point[] {
                new Point(x, y),                     // 左上
                new Point(x + width / 2, y),           // 上中
                new Point(x + width, y),               // 右上
                new Point(x, y + height / 2),          // 左中
                new Point(x + width, y + height / 2),  // 右中
                new Point(x, y + height),              // 左下
                new Point(x + width / 2, y + height),  // 下中
                new Point(x + width, y + height)       // 右下
        };
    }
}
