package model;

import java.util.ArrayList;
import java.util.List;

/**
 * CanvasModel 類別負責儲存與管理畫布上的所有物件和連線，
 * 以及目前被選取的物件。此類別提供存取這些集合的方法，
 * 方便其他元件 (例如畫布視圖或控制器) 進行資料操作。
 */
public class CanvasModel {
    // 儲存所有在畫布上繪製的 BasicObject 物件
    private List<BasicObject> objects;

    // 儲存所有連接兩個 BasicObject 物件的 LinkObject 連線
    private List<LinkObject> links;

    // 儲存目前被選取的 BasicObject 物件
    private List<BasicObject> selectedObjects;

    /**
     * 建構子：初始化 CanvasModel，
     * 建立空的集合以儲存物件、連線以及被選取物件。
     */
    public CanvasModel() {
        this.objects = new ArrayList<>();
        this.links = new ArrayList<>();
        this.selectedObjects = new ArrayList<>();
    }

    /**
     * 取得畫布上所有 BasicObject 物件的列表。
     *
     * @return 存有所有 BasicObject 物件的 List
     */
    public List<BasicObject> getObjects() {
        return objects;
    }

    /**
     * 取得畫布上所有連線物件的列表。
     *
     * @return 存有所有 LinkObject 連線物件的 List
     */
    public List<LinkObject> getLinks() {
        return links;
    }

    /**
     * 取得目前被選取的物件列表。
     *
     * @return 存有被選取 BasicObject 物件的 List
     */
    public List<BasicObject> getSelectedObjects() {
        return selectedObjects;
    }
}
