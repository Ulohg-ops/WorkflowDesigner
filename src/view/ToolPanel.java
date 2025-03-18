package view;

import enums.Mode;

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

    private void addButtonsToPanel() {
        add(selectBtn);
        add(assocBtn);
        add(genBtn);
        add(compBtn);
        add(rectBtn);
        add(ovalBtn);
    }

    private void applyDefaultStyles() {
        Color defaultColor = Color.decode("#FFF7F3");
        selectBtn.setBackground(defaultColor);
        assocBtn.setBackground(defaultColor);
        genBtn.setBackground(defaultColor);
        compBtn.setBackground(defaultColor);
        rectBtn.setBackground(defaultColor);
        ovalBtn.setBackground(defaultColor);
    }

    private void addActionListeners() {
        selectBtn.addActionListener(e -> setMode(Mode.SELECT));
        assocBtn.addActionListener(e -> setMode(Mode.ASSOCIATION));
        genBtn.addActionListener(e -> setMode(Mode.GENERALIZATION));
        compBtn.addActionListener(e -> setMode(Mode.COMPOSITION));
        rectBtn.addActionListener(e -> setMode(Mode.RECT));
        ovalBtn.addActionListener(e -> setMode(Mode.OVAL));
    }

    private void customizeButton(JButton button) {
        button.setFocusPainted(false);

        // 允許繪製邊框與背景
        button.setBorderPainted(true);
        button.setContentAreaFilled(true);
        button.setOpaque(true);

        // 建立外層的黑色線條邊框 (3px 粗)，再加上內邊距
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    }
    private ImageIcon loadIcon(String path) {
        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Can't load icon image: " + path);
            return null;

        }
    }

    private void setMode(Mode mode) {
        this.currentMode = mode;
        updateButtonColors();
        System.out.println("Current Mode: " + currentMode);
    }

    public Mode getCurrentMode() {
        return currentMode;
    }

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
