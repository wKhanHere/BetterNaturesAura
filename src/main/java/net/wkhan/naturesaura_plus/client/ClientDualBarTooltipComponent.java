package net.wkhan.naturesaura_plus.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public class ClientDualBarTooltipComponent implements TooltipComponent, ClientTooltipComponent {

    private final int A, MAX_A;
    private final int B, MAX_B;
    private static final int BAR_WIDTH = 100;
    private static final int BAR_HEIGHT = 4;

    public ClientDualBarTooltipComponent(int A, int MAX_A, int B, int MAX_B) {
        this.A = A;
        this.MAX_A = MAX_A;
        this.B = B;
        this.MAX_B = MAX_B;
    }

    @Override
    public int getHeight() {
        return 13;
    }

    @Override
    public int getWidth(Font p_169952_) {
        return BAR_WIDTH;
    }

    @Override
    public void renderImage(Font font, int mouseX, int mouseY, GuiGraphics guiGraphics) {

        //A - Green
        int fillWidthA = fillWidth(A, MAX_A);
        guiGraphics.fill(mouseX, mouseY, mouseX + BAR_WIDTH, mouseY + BAR_HEIGHT, 0xFF222222); // Background
        guiGraphics.fill(mouseX, mouseY, mouseX + fillWidthA, mouseY + BAR_HEIGHT, 0xFF4CAF50); // Progress

        //B - Blue
        int bar2Y = mouseY + BAR_HEIGHT + 3; // Shift down past Bar 1 + gap
        int fillWidthB = fillWidth(B, MAX_B);
        guiGraphics.fill(mouseX, bar2Y, mouseX + BAR_WIDTH, bar2Y + BAR_HEIGHT, 0xFF222222); // Background
        guiGraphics.fill(mouseX, bar2Y, mouseX + fillWidthB, bar2Y + BAR_HEIGHT, 0xFF2196F3); // Progress

    }

    private int fillWidth(int x, int max_x) {
        return max_x > 0 ? BAR_WIDTH*x/max_x : 0;
    }
}
