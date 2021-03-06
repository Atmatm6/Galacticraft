package micdoodle8.mods.galacticraft.core.tile;

import java.util.EnumSet;

import micdoodle8.mods.galacticraft.api.transmission.core.item.IItemElectric;
import micdoodle8.mods.galacticraft.core.items.ItemOxygenTank;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityOxygenCompressor extends TileEntityOxygen implements IInventory, ISidedInventory
{
	private ItemStack[] containingItems = new ItemStack[2];

	public static final int WATTS_PER_TICK = 1;
	public static final int TANK_TRANSFER_SPEED = 1;

	public TileEntityOxygenCompressor()
	{
		super(1200, 16);
		this.storage.setMaxExtract(200);
		this.storage.setCapacity(50000);
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		if (!this.worldObj.isRemote)
		{
			if (this.storedOxygen > 0 && this.hasEnoughEnergyToRun)
			{
				if (!this.worldObj.isRemote)
				{
					if (this.containingItems[0] != null)
					{
						ItemStack tank0 = this.containingItems[0];

						if (tank0.getItem() instanceof ItemOxygenTank && tank0.getItemDamage() > 0)
						{
							tank0.setItemDamage(tank0.getItemDamage() - TileEntityOxygenCompressor.TANK_TRANSFER_SPEED);
							this.storedOxygen -= TileEntityOxygenCompressor.TANK_TRANSFER_SPEED;
						}
					}
				}
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readFromNBT(par1NBTTagCompound);

		final NBTTagList var2 = par1NBTTagCompound.getTagList("Items", 10);
		this.containingItems = new ItemStack[this.getSizeInventory()];

		for (int var3 = 0; var3 < var2.tagCount(); ++var3)
		{
			final NBTTagCompound var4 = var2.getCompoundTagAt(var3);
			final byte var5 = var4.getByte("Slot");

			if (var5 >= 0 && var5 < this.containingItems.length)
			{
				this.containingItems[var5] = ItemStack.loadItemStackFromNBT(var4);
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);

		final NBTTagList list = new NBTTagList();

		for (int var3 = 0; var3 < this.containingItems.length; ++var3)
		{
			if (this.containingItems[var3] != null)
			{
				final NBTTagCompound var4 = new NBTTagCompound();
				var4.setByte("Slot", (byte) var3);
				this.containingItems[var3].writeToNBT(var4);
				list.appendTag(var4);
			}
		}

		par1NBTTagCompound.setTag("Items", list);
	}

	@Override
	public int getSizeInventory()
	{
		return this.containingItems.length;
	}

	@Override
	public ItemStack getStackInSlot(int par1)
	{
		return this.containingItems[par1];
	}

	@Override
	public ItemStack decrStackSize(int par1, int par2)
	{
		if (this.containingItems[par1] != null)
		{
			ItemStack var3;

			if (this.containingItems[par1].stackSize <= par2)
			{
				var3 = this.containingItems[par1];
				this.containingItems[par1] = null;
				return var3;
			}
			else
			{
				var3 = this.containingItems[par1].splitStack(par2);

				if (this.containingItems[par1].stackSize == 0)
				{
					this.containingItems[par1] = null;
				}

				return var3;
			}
		}
		else
		{
			return null;
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int par1)
	{
		if (this.containingItems[par1] != null)
		{
			final ItemStack var2 = this.containingItems[par1];
			this.containingItems[par1] = null;
			return var2;
		}
		else
		{
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
	{
		this.containingItems[par1] = par2ItemStack;

		if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit())
		{
			par2ItemStack.stackSize = this.getInventoryStackLimit();
		}
	}

	@Override
	public String getInventoryName()
	{
		return GCCoreUtil.translate("container.oxygencompressor.name");
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 1;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
	{
		return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : par1EntityPlayer.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory()
	{
	}

	@Override
	public void closeInventory()
	{
	}

	// ISidedInventory Implementation:

	@Override
	public int[] getAccessibleSlotsFromSide(int side)
	{
		return new int[] { 0, 1 };
	}

	@Override
	public boolean canInsertItem(int slotID, ItemStack itemstack, int side)
	{
		if (this.isItemValidForSlot(slotID, itemstack))
		{
			switch (slotID)
			{
			case 0:
				return itemstack.getItemDamage() > 1;
			case 1:
				return ((IItemElectric) itemstack.getItem()).getElectricityStored(itemstack) > 0;
			default:
				return false;
			}
		}
		return false;
	}

	@Override
	public boolean canExtractItem(int slotID, ItemStack itemstack, int side)
	{
		if (this.isItemValidForSlot(slotID, itemstack))
		{
			switch (slotID)
			{
			case 0:
				return itemstack.getItemDamage() == 0;
			case 1:
				return ((IItemElectric) itemstack.getItem()).getElectricityStored(itemstack) <= 0;
			default:
				return false;
			}
		}
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int slotID, ItemStack itemstack)
	{
		switch (slotID)
		{
		case 0:
			return itemstack.getItem() instanceof ItemOxygenTank;
		case 1:
			return itemstack.getItem() instanceof IItemElectric;
		}

		return false;
	}

	@Override
	public boolean hasCustomInventoryName()
	{
		return true;
	}

	@Override
	public boolean shouldUseEnergy()
	{
		return TileEntityOxygen.timeSinceOxygenRequest > 0 && this.getStackInSlot(0) != null;
	}

	@Override
	public ForgeDirection getElectricInputDirection()
	{
		return ForgeDirection.getOrientation(this.getBlockMetadata() + 2);
	}

	@Override
	public ItemStack getBatteryInSlot()
	{
		return this.getStackInSlot(1);
	}

	@Override
	public boolean shouldPullOxygen()
	{
		return this.hasEnoughEnergyToRun;
	}

	@Override
	public boolean shouldUseOxygen()
	{
		return false;
	}

	@Override
	public EnumSet<ForgeDirection> getOxygenInputDirections()
	{
		return EnumSet.of(this.getElectricInputDirection().getOpposite());
	}

	@Override
	public EnumSet<ForgeDirection> getOxygenOutputDirections()
	{
		return EnumSet.noneOf(ForgeDirection.class);
	}
}
