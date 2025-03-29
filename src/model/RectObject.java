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
        // 1. 填充矩形背景
        g.setColor(Color.decode("#F6F0F0"));
        g.fillRect(getX(), getY(), getWidth(), getHeight());

        // 2. 繪製矩形外框
        g.setColor(Color.BLACK);
        g.drawRect(getX(), getY(), getWidth(), getHeight());

        // 3. 繪製水平分隔線：將矩形分成三個區域
        int line1Y = getY() + getHeight() / 3;
        int line2Y = getY() + (2 * getHeight() / 3);
        g.drawLine(getX(), line1Y, getX() + getWidth(), line1Y);
        g.drawLine(getX(), line2Y, getX() + getWidth(), line2Y);

        // 4. 如果設定顯示連接埠，則繪製 8 個連接埠（位於四角及四邊中點）
        if (isShowPorts()) {
            g.setColor(Color.BLACK);
            Point[] ports = getPorts();
            for (Point pt : ports) {
                // 在每個連接埠位置繪製一個 6x6 的小方塊
                g.fillRect(pt.x - 3, pt.y - 3, 6, 6);
            }
        }

        // 5. 如果 label 不為空，則在矩形內部置中繪製標籤
        if (!getLabel().isEmpty()) {
            Graphics2D g2d = (Graphics2D) g;
            // 設定字型與字體大小
            g2d.setFont(new Font("SansSerif", Font.PLAIN, getFontSize()));
            FontMetrics fm = g2d.getFontMetrics();

            // 取得標籤文字與其尺寸資訊
            String text = getLabel();
            int textWidth = fm.stringWidth(text);  // 文字寬度
            int textHeight = fm.getAscent();         // 文字上升高度

            // 計算文字置中位置：矩形中心減去文字寬度/高度的一半
            int labelX = getX() + (getWidth() - textWidth) / 2;
            // labelY 需微調以便文字基線正確對齊（這裡使用 fm.getAscent()）
            int labelY = getY() + (getHeight() - textHeight) / 2 + fm.getAscent();

            // 先繪製標籤背景：根據使用者選擇的標籤形狀繪製背景
            g2d.setColor(getLabelColor());
            if (getLabelShape() == LabelShape.RECTANGLE) {
                g2d.fillRect(labelX, labelY - textHeight, textWidth, textHeight);
            } else if (getLabelShape() == LabelShape.OVAL) {
                g2d.fillOval(labelX, labelY - textHeight, textWidth, textHeight);
            }

            // 再以黑色繪製標籤文字
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
