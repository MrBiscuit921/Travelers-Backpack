package com.tiviacz.travelersbackpack.inventory.menu.slot;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.CraftingContainerImproved;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.event.EventHooks;

public class ResultSlotExt extends ResultSlot
{
    protected final ResultContainer inv;
    protected final ITravelersBackpackContainer container;

    public ResultSlotExt(ITravelersBackpackContainer container, Player player, CraftingContainerImproved matrix, ResultContainer inv, int slotIndex, int xPosition, int yPosition)
    {
        super(player, matrix, inv, slotIndex, xPosition, yPosition);
        this.inv = inv;
        this.container = container;
    }

    @Override
    public boolean mayPickup(Player player)
    {
        return !TravelersBackpackConfig.disableCrafting;
    }

    @Override
    public boolean isActive()
    {
        if(TravelersBackpackConfig.disableCrafting)
        {
            return false;
        }

        if(this.container.getTier().getOrdinal() <= 0)
        {
            return this.container.getFluidSlotsHandler().getStackInSlot(this.container.getTier().getSlotIndex(Tiers.SlotType.BUCKET_IN_RIGHT)).isEmpty() && this.container.getFluidSlotsHandler().getStackInSlot(this.container.getTier().getSlotIndex(Tiers.SlotType.BUCKET_OUT_RIGHT)).isEmpty();
        }
        return true;
    }

    @Override
    public ItemStack remove(int amount)
    {
        if(this.hasItem())
        {
            this.removeCount += Math.min(amount, this.getItem().getCount());
        }
        return this.getItem().copy();
    }

    @Override
    protected void onSwapCraft(int numItemsCrafted)
    {
        super.onSwapCraft(numItemsCrafted);
        this.inv.setItem(0, this.getItem().copy()); // https://github.com/Shadows-of-Fire/FastWorkbench/issues/62 - Vanilla's SWAP action will leak this stack here.
    }

    @Override
    public void set(ItemStack stack) {}

    @Override
    protected void checkTakeAchievements(ItemStack stack)
    {
        if(this.removeCount > 0)
        {
            stack.onCraftedBy(this.player.level(), this.player, this.removeCount);
            EventHooks.firePlayerCraftingEvent(this.player, stack, this.craftSlots);
        }
        this.removeCount = 0;
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    public void onTake(Player player, ItemStack stack)
    {
        this.checkTakeAchievements(stack);
        CommonHooks.setCraftingPlayer(player);
        NonNullList<ItemStack> list;
        Recipe<CraftingContainer> recipe = (Recipe<CraftingContainer>) this.inv.getRecipeUsed().value();
        if(recipe != null && recipe.matches(this.craftSlots, player.level())) list = recipe.getRemainingItems(this.craftSlots);
        else list = ((CraftingContainerImproved)this.craftSlots).getStackList();
        CommonHooks.setCraftingPlayer(null);

        for(int i = 0; i < list.size(); ++i)
        {
            ItemStack itemstack = this.craftSlots.getItem(i);
            ItemStack itemstack1 = list.get(i);

            if(!itemstack.isEmpty())
            {
                this.craftSlots.removeItem(i, 1);
                itemstack = this.craftSlots.getItem(i);
            }

            if(!itemstack1.isEmpty())
            {
                if(itemstack.isEmpty())
                {
                    this.craftSlots.setItem(i, itemstack1);
                }
                else if (ItemStack.isSameItemSameTags(itemstack, itemstack1))
                {
                    itemstack1.grow(itemstack.getCount());
                    this.craftSlots.setItem(i, itemstack1);
                }
                else if (!this.player.getInventory().add(itemstack1))
                {
                    this.player.drop(itemstack1, false);
                }
            }
        }
    }

 /*   @Override
    public ItemStack getItem()
    {
        // Crafting Tweaks fakes 64x right click operations to right-click craft a stack to the "held" item, so we need to verify the recipe here.
        Recipe<CraftingContainer> recipe = (Recipe<CraftingContainer>)this.inv.m_40158_().f_291008_();
        if (recipe != null && recipe.matches(this.craftSlots, player.level())) return super.getItem();
        return ItemStack.EMPTY;
    } */
}