package com.skybird.create_jp_signal.client.gui.widget; // widgetsパッケージを新設

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class DropdownWidget<T> extends AbstractWidget {

    private final List<T> options;
    private int selectedIndex = 0;
    private boolean isExpanded = false;

    // T型のオブジェクトを、表示用のComponentに変換する関数
    private final Function<T, Component> displayStringFunction;
    // 新しいTが選択されたときに呼び出されるコールバック関数
    private final Consumer<T> onSelectCallback;

    public DropdownWidget(int x, int y, int width, int height, List<T> options, Function<T, Component> displayFunc, Consumer<T> onSelect) {
        super(x, y, width, height, Component.empty());
        this.options = options;
        this.displayStringFunction = displayFunc;
        this.onSelectCallback = onSelect;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;
        // 背景を描画
        guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0xFF000000);
        guiGraphics.renderOutline(this.getX(), this.getY(), this.width, this.height, 0xFFFFFFFF);

        // 現在選択されている項目を表示
        Component selectedStr = displayStringFunction.apply(options.get(selectedIndex));
        guiGraphics.drawString(font, selectedStr, this.getX() + 5, this.getY() + (this.height - 8) / 2, 0xFFFFFF);

    }

    public void renderExpandedList(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (!this.isExpanded) {
            return;
        }
        Font font = Minecraft.getInstance().font;
        int yOffset = this.getY() + this.height;

        // 背景
        guiGraphics.fill(this.getX(), yOffset, this.getX() + this.width, yOffset + options.size() * this.height, 0xFF3B3B3B);

        for (int i = 0; i < options.size(); i++) {
            int itemY = yOffset + i * this.height;
            // マウスが乗っている項目をハイライト
            if (mouseX >= this.getX() && mouseX < this.getX() + this.width &&
                mouseY >= itemY && mouseY < itemY + this.height) {
                guiGraphics.fill(this.getX(), itemY, this.getX() + this.width, itemY + this.height, 0xFF555555);
            }
            guiGraphics.drawString(font, displayStringFunction.apply(options.get(i)), this.getX() + 5, itemY + (this.height - 8) / 2, 0xFFFFFFFF);
        }
        // リストに枠線を追加
        guiGraphics.renderOutline(this.getX(), yOffset, this.width, options.size() * this.height, 0xFF000000);
    }

    public boolean isExpanded() {
        return this.isExpanded;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.visible) {
            return false;
        }

        // 1. メインボックスがクリックされたか判定
        if (this.clicked(mouseX, mouseY)) {
            this.isExpanded = !this.isExpanded;
            return true; // クリックを消費
        }

        // 2. ドロップダウンが開いている場合、リスト部分がクリックされたか判定
        if (this.isExpanded) {
            int yOffset = this.getY() + this.height;
            int listHeight = options.size() * this.height;

            // マウスカーソルが展開リストの範囲内にあるかチェック
            boolean mouseOverList = mouseX >= this.getX() && mouseX < this.getX() + this.width &&
                                    mouseY >= yOffset && mouseY < yOffset + listHeight;

            if (mouseOverList) {
                // リスト範囲内のクリックであれば、どの項目か計算
                int clickedIndex = (int) (mouseY - yOffset) / this.height;
                if (clickedIndex >= 0 && clickedIndex < options.size()) {
                    this.setSelectedIndex(clickedIndex);
                }
                // 項目が選択されてもされなくても、ドロップダウンを閉じる
                this.isExpanded = false;
                
                // ★重要★ リスト内であれば必ずクリックを消費(true)し、後ろのボタンにイベントを渡さない
                return true;
            } else {
                // リスト範囲外のクリックであれば、ドロップダウンを閉じるだけ
                // イベントは消費しない(false)ので、他のウィジェットがクリックを受け取れる
                this.isExpanded = false;
                return false;
            }
        }

        // どにも該当しない場合は、クリックを消費しない
        return false;
    }

    public T getSelectedOption() {
        return this.options.get(this.selectedIndex);
    }

    public void setSelectedIndex(int index) {
        if (index >= 0 && index < options.size()) {
            this.selectedIndex = index;
            this.onSelectCallback.accept(options.get(index));
        }
    }

    public void setCurrentOption(T option) {
        int index = options.indexOf(option);
        if (index != -1) {
            this.selectedIndex = index;
        }
    }
    
    @Override protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {  }
}