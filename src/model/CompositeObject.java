package model;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CompositeObject 代表一個群組物件，
 * 其由多個 BasicObject 組成，並會根據所有子物件計算出群組的邊界（bounding box）。
 * 此類別可用於群組操作，並提供移動整個群組與取得群組連接埠等功能。
 */
public class CompositeObject extends BasicObject {
    // 儲存所有被群組的 BasicObject 子物件
    private List<BasicObject> children = new ArrayList<>();

    /**
     * 建構子：接收一個 BasicObject 的集合，初始化 CompositeObject，
     * 並根據子物件重新計算群組邊界，同時預設不顯示連接埠。
     *
     * @param children 子物件集合
     */
    public CompositeObject(List<BasicObject> children) {
        // 初步以 (0,0,0,0) 初始化，待後續 updateBounds() 計算正確邊界
        super(0, 0, 0, 0);
        this.children.addAll(children);
        updateBounds();
        // 當物件被群組時，預設不顯示連接埠
        setShowPorts(false);
    }

    /**
     * 根據子物件的位置與尺寸重新計算群組的 bounding box，
     * 即計算所有子物件的最小 x 與 y 以及最大 x 與 y，然後設定自身的 x、y、width 與 height。
     */
    private void updateBounds() {
        // 若沒有子物件，則邊界設為 (0,0,0,0)
        if (children.isEmpty()) {
            setX(0);
            setY(0);
            setWidth(0);
            setHeight(0);
            return;
        }
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
        // 遍歷所有子物件，找出最小與最大的座標值
        for (BasicObject child : children) {
            minX = Math.min(minX, child.getX());
            minY = Math.min(minY, child.getY());
            maxX = Math.max(maxX, child.getX() + child.getWidth());
            maxY = Math.max(maxY, child.getY() + child.getHeight());
        }
        // 更新 CompositeObject 的邊界
        setX(minX);
        setY(minY);
        setWidth(maxX - minX);
        setHeight(maxY - minY);
    }

    /**
     * 取得群組中的所有子物件。
     *
     * @return 子物件的 List
     */
    public List<BasicObject> getChildren() {
        return children;
    }

    /**
     * 繪製群組物件：
     * 1. 繪製群組邊框（以 MAGENTA 顏色，可依需求修改底色）。
     * 2. 依序繪製每個子物件（子物件的座標均為全域座標）。
     *    為避免子物件各自顯示連接埠，先暫存其原始狀態，再強制關閉，
     *    繪製完成後還原原來的狀態。
     * 3. 如果群組物件本身被選取 (isShowPorts 為 true)，則繪製群組的連接埠。
     *
     * @param g Graphics 物件，用於進行繪製操作
     */
    @Override
    public void draw(Graphics g) {
        // 繪製群組邊框
        g.setColor(Color.MAGENTA);
        g.drawRect(getX(), getY(), getWidth(), getHeight());

        // 繪製每個子物件
        for (BasicObject child : children) {
            // 暫存子物件原本是否顯示連接埠的狀態
            boolean originalShowPorts = child.isShowPorts();
            // 強制關閉子物件的連接埠顯示，避免重複顯示
            child.setShowPorts(false);
            // 繪製子物件
            child.draw(g);
            // 還原子物件原來的連接埠顯示狀態
            child.setShowPorts(originalShowPorts);
        }

        // 若群組物件本身被選取，則繪製群組的連接埠
        if (isShowPorts()) {
            Point[] ports = getPorts();
            g.setColor(Color.BLACK);
            for (Point pt : ports) {
                // 在每個連接埠位置畫一個 6x6 的小方塊
                g.fillRect(pt.x - 3, pt.y - 3, 6, 6);
            }
        }
    }

    /**
     * 移動群組物件：
     * 同時移動所有子物件，然後重新計算群組邊界。
     *
     * @param dx 水平位移量
     * @param dy 垂直位移量
     */
    public void moveBy(int dx, int dy) {
        // 對所有子物件進行位移，若子物件本身是 CompositeObject，
        // 則遞迴呼叫 moveBy，否則直接更新其位置
        for (BasicObject child : children) {
            if (child instanceof CompositeObject) {
                ((CompositeObject) child).moveBy(dx, dy);
            } else {
                child.setX(child.getX() + dx);
                child.setY(child.getY() + dy);
            }
        }
        updateBounds();
    }

    /**
     * 取得群組物件的連接埠座標。
     * 這裡定義連接埠為群組 bounding box 的上下左右四個中點。
     *
     * @return Point 陣列，包含 4 個連接埠的位置
     */
    @Override
    public Point[] getPorts() {
        return new Point[] {
                new Point(getX() + getWidth() / 2, getY()),                // 上
                new Point(getX() + getWidth() / 2, getY() + getHeight()),     // 下
                new Point(getX(), getY() + getHeight() / 2),                  // 左
                new Point(getX() + getWidth(), getY() + getHeight() / 2)        // 右
        };
    }

    /**
     * 取得與參考點 p 距離最短的連接埠。
     *
     * @param p 參考點
     * @return 與 p 距離最短的連接埠座標
     */
    @Override
    public Point getClosestPort(Point p) {
        Point[] ports = getPorts();
        Point closest = ports[0];
        double minDist = p.distance(ports[0]);
        for (int i = 1; i < ports.length; i++) {
            double dist = p.distance(ports[i]);
            if (dist < minDist) {
                minDist = dist;
                closest = ports[i];
            }
        }
        return closest;
    }
}
