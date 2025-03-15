package model;

import view.LabelShape;

import java.awt.*;

public class RectObject extends BasicObject {

    public RectObject(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void draw(Graphics g) {
        // 繪製矩形
        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);

        int line1Y = y + height / 3;
        int line2Y = y + (2 * height / 3);

        g.drawLine(x, line1Y, x + width, line1Y);
        g.drawLine(x, line2Y, x + width, line2Y);

        // 若要顯示 ports，畫出 8 個小方塊
        if (showPorts) {
            g.setColor(Color.BLACK);
            Point[] ports = getPorts();
            for (Point pt : ports) {
                g.fillRect(pt.x - 3, pt.y - 3, 6, 6);
            }
        }

        // 繪製 label
        if (!label.isEmpty()) {
            g.setFont(new Font("SansSerif", Font.PLAIN, fontSize));
            g.setColor(labelColor);
            // 根據 labelShape 繪製不同背景
            if (labelShape == LabelShape.RECTANGLE) {
                g.fillRect(x, y - fontSize, g.getFontMetrics().stringWidth(label), fontSize);
            } else if (labelShape == LabelShape.OVAL) {
                g.fillOval(x, y - fontSize, g.getFontMetrics().stringWidth(label), fontSize);
            }
            g.setColor(Color.BLACK);
            g.drawString(label, x, y - 2);
        }


    }

    @Override
    public Point[] getPorts() {
        // 8 個連接埠：四角 + 四邊中點
        return new Point[] {
                new Point(x, y),                     // 左上
                new Point(x + width/2, y),           // 上中
                new Point(x + width, y),             // 右上
                new Point(x, y + height/2),          // 左中
                new Point(x + width, y + height/2),  // 右中
                new Point(x, y + height),            // 左下
                new Point(x + width/2, y + height),  // 下中
                new Point(x + width, y + height)     // 右下
        };
    }
}
