package view;

import javax.swing.*;
import java.awt.*;

public class ToolPanel extends JPanel {

    private Mode currentMode = Mode.SELECT;
    private JButton selectBtn;
    private JButton rectBtn;
    private JButton ovalBtn;
    private JButton assocBtn;
    private JButton genBtn;
    private JButton compBtn;

    public ToolPanel() {
        setLayout(new GridLayout(6, 1));

        initializeButtons();
        addButtonsToPanel();
        applyDefaultStyles();
        addActionListeners();
    }

    /**
     * 初始化按鈕與圖示
     */
    private void initializeButtons() {
        selectBtn = new JButton(loadIcon("/resources/select.png"));
        assocBtn  = new JButton(loadIcon("/resources/association_line.png"));
        genBtn    = new JButton(loadIcon("/resources/generalization_line.png"));
        compBtn   = new JButton(loadIcon("/resources/composition_line.png"));
        rectBtn   = new JButton(loadIcon("/resources/rect.png"));
        ovalBtn   = new JButton(loadIcon("/resources/oval.png"));

        customizeButton(selectBtn);
        customizeButton(assocBtn);
        customizeButton(genBtn);
        customizeButton(compBtn);
        customizeButton(rectBtn);
        customizeButton(ovalBtn);
    }

    /**
     * 將按鈕添加到面板
     */
    private void addButtonsToPanel() {
        add(selectBtn);
        add(assocBtn);
        add(genBtn);
        add(compBtn);
        add(rectBtn);
        add(ovalBtn);
    }

    /**
     * 設定按鈕預設樣式
     */
    private void applyDefaultStyles() {
        Color defaultColor = Color.decode("#FFF7F3");
        selectBtn.setBackground(defaultColor);
        assocBtn.setBackground(defaultColor);
        genBtn.setBackground(defaultColor);
        compBtn.setBackground(defaultColor);
        rectBtn.setBackground(defaultColor);
        ovalBtn.setBackground(defaultColor);
    }

    /**
     * 註冊按鈕點擊事件
     */
    private void addActionListeners() {
        selectBtn.addActionListener(e -> setMode(Mode.SELECT));
        assocBtn.addActionListener(e -> setMode(Mode.ASSOCIATION));
        genBtn.addActionListener(e -> setMode(Mode.GENERALIZATION));
        compBtn.addActionListener(e -> setMode(Mode.COMPOSITION));
        rectBtn.addActionListener(e -> setMode(Mode.RECT));
        ovalBtn.addActionListener(e -> setMode(Mode.OVAL));
    }

    /**
     * 設定按鈕的外觀
     */
    private void customizeButton(JButton button) {
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
    }

    /**
     * 嘗試載入圖示，若失敗則回傳空白圖片
     */
    private ImageIcon loadIcon(String path) {
        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Can't load icon image: " + path);
            return null;

        }
    }

    /**
     * 設定目前模式並更新 UI
     */
    private void setMode(Mode mode) {
        this.currentMode = mode;
        updateButtonColors();
        System.out.println("Current Mode: " + currentMode);
    }

    public Mode getCurrentMode() {
        return currentMode;
    }

    /**
     * 更新按鈕背景顏色
     */
    private void updateButtonColors() {
        Color defaultColor = Color.decode("#FFF7F3");
        Color selectedColor = Color.decode("#FAD0C4");

        // 先恢復所有按鈕的預設顏色
        selectBtn.setBackground(defaultColor);
        assocBtn.setBackground(defaultColor);
        genBtn.setBackground(defaultColor);
        compBtn.setBackground(defaultColor);
        rectBtn.setBackground(defaultColor);
        ovalBtn.setBackground(defaultColor);

        // 根據當前模式變更選中按鈕的顏色
        switch (currentMode) {
            case SELECT:
                selectBtn.setBackground(selectedColor);
                break;
            case ASSOCIATION:
                assocBtn.setBackground(selectedColor);
                break;
            case GENERALIZATION:
                genBtn.setBackground(selectedColor);
                break;
            case COMPOSITION:
                compBtn.setBackground(selectedColor);
                break;
            case RECT:
                rectBtn.setBackground(selectedColor);
                break;
            case OVAL:
                ovalBtn.setBackground(selectedColor);
                break;
            default:
                break;
        }
    }
}
