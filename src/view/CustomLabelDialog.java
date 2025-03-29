package view;

import enums.LabelShape;

import javax.swing.*;
import java.awt.*;

/**
 * CustomLabelDialog 用於自訂 Label 樣式，包括標籤文字、形狀、背景顏色與字體大小。
 * 此對話框會根據傳入的初始值初始化各個元件，並在使用者按下 OK 或 Cancel 後結束對話。
 */
public class CustomLabelDialog extends JDialog {
    private JTextField labelNameField;
    private JRadioButton rectButton, ovalButton;
    private JButton colorButton;
    private JSpinner fontSizeSpinner;
    private JButton okButton, cancelButton;
    private Color chosenColor = Color.WHITE;
    private boolean confirmed = false;

    /**
     * 建構子，初始化對話框內各項元件與版面配置。
     *
     * @param owner         父視窗
     * @param currentLabel  當前標籤文字
     * @param currentShape  當前標籤形狀
     * @param currentColor  當前背景色
     * @param currentFontSize 當前字體大小
     */
    public CustomLabelDialog(Frame owner, String currentLabel, LabelShape currentShape, Color currentColor, int currentFontSize) {
        super(owner, "Customize Label Style", true);

        // 建立標籤名稱文字欄位
        labelNameField = new JTextField(currentLabel, 20);

        // 建立標籤形狀的選擇按鈕，並組成 ButtonGroup 保證互斥
        rectButton = new JRadioButton("Rectangle");
        ovalButton = new JRadioButton("Oval");
        ButtonGroup shapeGroup = new ButtonGroup();
        shapeGroup.add(rectButton);
        shapeGroup.add(ovalButton);
        if (currentShape == LabelShape.RECTANGLE) {
            rectButton.setSelected(true);
        } else {
            ovalButton.setSelected(true);
        }

        // 建立背景色選擇按鈕，初始顏色為 currentColor；按下後透過 JColorChooser 選擇新顏色
        colorButton = new JButton("Choose Color");
        colorButton.setBackground(currentColor);
        chosenColor = currentColor;
        colorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Choose Label Background Color", chosenColor);
            if (newColor != null) {
                chosenColor = newColor;
                colorButton.setBackground(chosenColor);
            }
        });

        // 建立字體大小的數值選擇器，最小 8、最大 72、間隔 1
        fontSizeSpinner = new JSpinner(new SpinnerNumberModel(currentFontSize, 8, 72, 1));

        // 建立 OK 與 Cancel 按鈕，並設定按下後關閉對話框
        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");
        okButton.addActionListener(e -> {
            confirmed = true;
            setVisible(false);
        });
        cancelButton.addActionListener(e -> {
            confirmed = false;
            setVisible(false);
        });

        // 使用 GridBagLayout 來排列標籤與對應元件
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // 各元件之間的間距
        gbc.anchor = GridBagConstraints.WEST;

        // 標籤名稱
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Label Name:"), gbc);
        gbc.gridx = 1;
        panel.add(labelNameField, gbc);

        // 標籤形狀選擇
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Label Shape:"), gbc);
        gbc.gridx = 1;
        JPanel shapePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        shapePanel.add(rectButton);
        shapePanel.add(ovalButton);
        panel.add(shapePanel, gbc);

        // 背景色選擇
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Background Color:"), gbc);
        gbc.gridx = 1;
        panel.add(colorButton, gbc);

        // 字體大小選擇
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Font Size:"), gbc);
        gbc.gridx = 1;
        panel.add(fontSizeSpinner, gbc);

        // 建立底部按鈕區，使用 FlowLayout 右對齊
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        // 將主面板與按鈕區域分別加入對話框
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(owner);
    }

    /**
     * 回傳是否按下 OK 以確認自訂設定。
     *
     * @return true 表示確認，false 表示取消
     */
    public boolean isConfirmed() {
        return confirmed;
    }

    /**
     * 取得使用者輸入的標籤文字。
     *
     * @return 標籤文字
     */
    public String getLabelName() {
        return labelNameField.getText();
    }

    /**
     * 取得使用者選擇的標籤形狀。
     *
     * @return LabelShape.RECTANGLE 或 LabelShape.OVAL
     */
    public LabelShape getLabelShape() {
        return rectButton.isSelected() ? LabelShape.RECTANGLE : LabelShape.OVAL;
    }

    /**
     * 取得使用者選擇的背景顏色。
     *
     * @return 背景顏色
     */
    public Color getChosenColor() {
        return chosenColor;
    }

    /**
     * 取得使用者選擇的字體大小。
     *
     * @return 字體大小
     */
    public int getFontSize() {
        return (Integer) fontSizeSpinner.getValue();
    }
}
