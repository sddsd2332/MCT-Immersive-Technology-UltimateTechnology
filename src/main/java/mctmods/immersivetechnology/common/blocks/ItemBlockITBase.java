package mctmods.immersivetechnology.common.blocks;

import blusunrize.immersiveengineering.client.ClientProxy;
import blusunrize.immersiveengineering.common.util.ItemNBTHelper;
import mctmods.immersivetechnology.ImmersiveTechnology;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Locale;

	/*
	* @author BluSunrize
	*/
public class ItemBlockITBase extends ItemBlock {
	public ItemBlockITBase(Block b) {
		super(b);
		if(((BlockITBase<?>) b).enumValues.length > 1) setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int damageValue) {
		return damageValue;
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> itemList) {
		if(this.isInCreativeTab(tab)) this.block.getSubBlocks(tab, itemList);
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		return ((BlockITBase<?>) this.block).getTranslationKey(stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public FontRenderer getFontRenderer(ItemStack stack) {
		return ClientProxy.itemFont;
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> list, ITooltipFlag advInfo) {
		if(((BlockITBase<?>) block).hasFlavour(stack)) {
			String subName = ((BlockITBase<?>) this.block).getStateFromMeta(stack.getItemDamage()).getValue(((BlockITBase<?>) this.block).property).toString().toLowerCase(Locale.US);
			String flavourKey = "desc." + ImmersiveTechnology.MODID + ".flavor." + ((BlockITBase<?>) this.block).name + "." + subName;
			list.add(TextFormatting.GRAY + I18n.format(flavourKey));
		}
		super.addInformation(stack, worldIn, list, advInfo);

		if(ItemNBTHelper.hasKey(stack, "energyStorage")) list.add(I18n.format("desc.immersiveengineering.info.energyStored", ItemNBTHelper.getInt(stack, "energyStorage")));
		if(ItemNBTHelper.hasKey(stack, "tank")) {FluidStack fs = FluidStack.loadFluidStackFromNBT(ItemNBTHelper.getTagCompound(stack, "tank"));
		if(fs != null) list.add(fs.getLocalizedName() + ": " + fs.amount + "mB");
		}
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
		if(!((BlockITBase<?>) this.block).canITBlockBePlaced(world, pos, newState, side, hitX, hitY, hitZ, player, stack)) return false;

		boolean ret = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);

		if(ret)	((BlockITBase<?>) this.block).onITBlockPlacedBy(world, pos, newState, side, hitX, hitY, hitZ, player, stack);
		return ret;
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		IBlockState iblockstate = world.getBlockState(pos);
		Block block = iblockstate.getBlock();

		if(!block.isReplaceable(world, pos)) pos = pos.offset(side);

		if(stack.getCount() > 0 && player.canPlayerEdit(pos, side, stack) && canBlockBePlaced(world, pos, side, stack)) {
			int i = this.getMetadata(stack.getMetadata());
			@SuppressWarnings("deprecation")
			IBlockState iblockstate1 = this.block.getStateForPlacement(world, pos, side, hitX, hitY, hitZ, i, player);

			if(placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, iblockstate1)) {
				SoundType soundtype = world.getBlockState(pos).getBlock().getSoundType(world.getBlockState(pos), world, 	pos, player);
				world.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
				if(!player.capabilities.isCreativeMode) stack.shrink(1);
			}
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.FAIL;
	}

	@Override
	public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack) {
		Block block = worldIn.getBlockState(pos).getBlock();

		if(block == Blocks.SNOW_LAYER && block.isReplaceable(worldIn, pos)) {
			side = EnumFacing.UP;
		} else if(!block.isReplaceable(worldIn, pos)) {
			pos = pos.offset(side);
		}
		return canBlockBePlaced(worldIn, pos, side, stack);
	}

	private boolean canBlockBePlaced(World w, BlockPos pos, EnumFacing side, ItemStack stack) {
		BlockITBase<?> blockIn = (BlockITBase<?>) this.block;
		Block block = w.getBlockState(pos).getBlock();
		@SuppressWarnings("deprecation")
		AxisAlignedBB axisalignedbb = blockIn.getCollisionBoundingBox(blockIn.getStateFromMeta(stack.getItemDamage()), w, pos);
		if(axisalignedbb != null && !w.checkNoEntityCollision(axisalignedbb.offset(pos), null)) return false;
		return block.isReplaceable(w, pos) && blockIn.canPlaceBlockOnSide(w, pos, side);
	}

}