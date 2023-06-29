package com.tiviacz.travelersbackpack.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.items.HoseItem;
import com.tiviacz.travelersbackpack.util.RenderUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.Window;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class OverlayHandledScreen extends Screen
{
    public MinecraftClient mc;
    public ItemRenderer itemRenderer;
    public Window mainWindow;

    public OverlayHandledScreen()
    {
        super(Text.literal(""));

        this.mc = MinecraftClient.getInstance();
        this.itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        this.mainWindow = MinecraftClient.getInstance().getWindow();
    }

    public void renderOverlay(DrawContext context)
    {
        PlayerEntity player = mc.player;

        int offsetX = TravelersBackpackConfig.offsetX;
        int offsetY = TravelersBackpackConfig.offsetY;
        int scaledWidth = mainWindow.getScaledWidth() - offsetX;
        int scaledHeight = mainWindow.getScaledHeight() - offsetY;

        int textureX = 10;
        int textureY = 0;

        ITravelersBackpackInventory inv = ComponentUtils.getBackpackInv(player);
        SingleVariantStorage<FluidVariant> rightFluidStorage = inv.getRightTank();
        SingleVariantStorage<FluidVariant> leftFluidStorage = inv.getLeftTank();

        if(!rightFluidStorage.getResource().isBlank())
        {
            this.drawGuiTank(context, rightFluidStorage, scaledWidth + 1, scaledHeight, 21, 8);
        }

        if(!leftFluidStorage.getResource().isBlank())
        {
            this.drawGuiTank(context, leftFluidStorage, scaledWidth - 11, scaledHeight, 21, 8);
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        if(inv.getTier() != null)
        {
            if(!inv.getInventory().getStack(inv.getTier().getSlotIndex(Tiers.SlotType.TOOL_UPPER)).isEmpty())
            {
                this.drawItemStack(context, inv.getInventory().getStack(inv.getTier().getSlotIndex(Tiers.SlotType.TOOL_UPPER)), scaledWidth - 30, scaledHeight - 4);
            }

            if(!inv.getInventory().getStack(inv.getTier().getSlotIndex(Tiers.SlotType.TOOL_LOWER)).isEmpty())
            {
                this.drawItemStack(context, inv.getInventory().getStack(inv.getTier().getSlotIndex(Tiers.SlotType.TOOL_LOWER)), scaledWidth - 30, scaledHeight + 11);
            }
        }

        Identifier id = new Identifier(TravelersBackpack.MODID, "textures/gui/travelers_backpack_overlay.png");

        if(player.getMainHandStack().getItem() instanceof HoseItem)
        {
            int tank = HoseItem.getHoseTank(player.getMainHandStack());

            int selectedTextureX = 0;
            int selectedTextureY = 0;

            if(tank == 1)
            {
                context.drawTexture(id, scaledWidth, scaledHeight, textureX, textureY, 10, 23);
                context.drawTexture(id, scaledWidth - 12, scaledHeight, selectedTextureX, selectedTextureY, 10, 23);
            }

            if(tank == 2)
            {
                context.drawTexture(id, scaledWidth, scaledHeight, selectedTextureX, selectedTextureY, 10, 23);
                context.drawTexture(id, scaledWidth - 12, scaledHeight, textureX, textureY, 10, 23);
            }

            if(tank == 0)
            {
                context.drawTexture(id, scaledWidth, scaledHeight, textureX, textureY, 10, 23);
                context.drawTexture(id, scaledWidth - 12, scaledHeight, textureX, textureY, 10, 23);
            }
        }
        else
        {
            context.drawTexture(id, scaledWidth, scaledHeight, textureX, textureY, 10, 23);
            context.drawTexture(id, scaledWidth - 12, scaledHeight, textureX, textureY, 10, 23);
        }
    }

    public void drawGuiTank(DrawContext drawContext, SingleVariantStorage<FluidVariant> fluidStorage, int startX, int startY, int height, int width)
    {
        RenderUtils.renderScreenTank(drawContext, fluidStorage, startX, startY, height, width);
    }

    private void drawItemStack(DrawContext context, ItemStack stack, int x, int y)
    {
        context.drawItemWithoutEntity(stack, x, y);
        context.drawItemInSlot(textRenderer, stack, x, y);
    }
}