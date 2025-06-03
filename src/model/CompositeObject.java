package model;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CompositeObject 代表一個群組物件，由多個 BasicObject 組成，
 * 用於群組操作（例如移動、群組繪製等），
 * 並不允許直接連線：透過覆寫 getPorts() 與 getConnectableChild() 達到此效果。
 */
public class CompositeObject extends BasicObject {
    private List<BasicObject> children = new ArrayList<>();

    public CompositeObject(List<BasicObject> children) {
        // 初步以 (0,0,0,0) 初始化，待 updateBounds 計算正確邊界
        super(0, 0, 0, 0);
        this.children.addAll(children);
        updateBounds();
        // 群組物件本身不顯示連接埠
        setShowPorts(false);
    }

    /**
     * 根據所有子物件計算群組的邊界
     */
    private void updateBounds() {
        if (children.isEmpty()) {
            setX(0);
            setY(0);
            setWidth(0);
            setHeight(0);
            return;
        }
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
        for (BasicObject child : children) {
            minX = Math.min(minX, child.getX());
            minY = Math.min(minY, child.getY());
            maxX = Math.max(maxX, child.getX() + child.getWidth());
            maxY = Math.max(maxY, child.getY() + child.getHeight());
        }
        setX(minX);
        setY(minY);
        setWidth(maxX - minX);
        setHeight(maxY - minY);
    }

    /**
     * 繪製群組：先畫群組邊框，再依序繪製各子物件（避免重複顯示連接埠）
     */
    @Override
    public void draw(Graphics g) {
        // 畫出群組邊界（以品紅色）
        g.setColor(Color.MAGENTA);
        g.drawRect(getX(), getY(), getWidth(), getHeight());
        for (BasicObject child : children) {
            boolean originalShowPorts = child.isShowPorts();
            child.setShowPorts(false);
            child.draw(g);
            child.setShowPorts(originalShowPorts);
        }
        // CompositeObject 本身不提供有效連接埠
        if (isShowPorts()) {
            Point[] ports = getPorts();
            g.setColor(Color.BLACK);
            for (Point pt : ports) {
                g.fillRect(pt.x - 3, pt.y - 3, 6, 6);
            }
        }
    }

    /**
     * 覆寫連接埠為空陣列，強制 CompositeObject 不作為連線端點
     */
    @Override
    public Point[] getPorts() {
        return new Point[0];
    }

    @Override
    public Point getClosestPort(Point p) {
        // 因為沒有有效連接埠，所以直接回傳 null 或拋出例外（視需求調整）
        return null;
    }

    /**
     * 這裡修改：群組物件內部物件不允許直接連線，
     * 故回傳 null 表示無可連線的子物件。
     */
    public BasicObject getConnectableChild() {
        // 修改前可能回傳 children.get(0)，但現在群組後的子物件不可被連線
        return null;
    }

    /**
     * 移動群組：所有子物件同時移動，再重新計算邊界
     */
    public void moveBy(int dx, int dy) {
        for (BasicObject child : children) {
            child.moveBy(dx, dy);
        }
        updateBounds();
    }
    
    @Override
    public boolean isGroup() {
        return true;
    }

    @Override
    public List<BasicObject> getChildren() {
        return children;
    }


    @Override
    public void ungroupTo(List<BasicObject> output) {
        output.addAll(children);
    }

}