package mctmods.immersivetechnology.client.render;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.client.ClientUtils;
import mctmods.immersivetechnology.common.ITContent;
import mctmods.immersivetechnology.common.blocks.metal.tileentities.TileEntitySolarReflectorMaster;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

public class TileRenderSolarReflector extends TileEntitySpecialRenderer<TileEntitySolarReflectorMaster> {

	@SuppressWarnings("deprecation")
	@Override
	public void render(TileEntitySolarReflectorMaster te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if(!te.formed || te.isDummy() || !te.getWorld().isBlockLoaded(te.getPos(), false)) {
			return;
		}
		final BlockRendererDispatcher blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
		BlockPos blockPos = te.getPos();
		IBlockState state = getWorld().getBlockState(blockPos);
		if(state.getBlock() != ITContent.blockMetalMultiblock) {
			return;
		}
		state = state.getBlock().getActualState(state, getWorld(), blockPos);
		state = state.withProperty(IEProperties.DYNAMICRENDER, true);
		IBlockState state1 = state.withProperty(IEProperties.DYNAMICRENDER, false).withProperty(IEProperties.BOOLEANS[0], true);
		IBakedModel supportModel = blockRenderer.getBlockModelShapes().getModelForState(state);
		IBakedModel mirrorModel = blockRenderer.getBlockModelShapes().getModelForState(state1);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder worldRenderer = tessellator.getBuffer();
		ClientUtils.bindAtlas();
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.translate(.5, .5, .5);
		RenderHelper.disableStandardItemLighting();
		GlStateManager.blendFunc(770, 771);
		GlStateManager.enableBlend();
		GlStateManager.disableCull();
		if(Minecraft.isAmbientOcclusionEnabled()) {
			GlStateManager.shadeModel(7425);
		} else {
			GlStateManager.shadeModel(7424);
		}
		//Base rotation for both
		GlStateManager.rotate(te.getAnimationRotations()[0], 0, 1, 0);
		//Draw the support
		worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
		worldRenderer.setTranslation(- .5 - blockPos.getX(), - .5 - blockPos.getY(), - .5 - blockPos.getZ());
		worldRenderer.color(255, 255, 255, 255);
		blockRenderer.getBlockModelRenderer().renderModel(te.getWorld(), supportModel, state, blockPos, worldRenderer, true);
		worldRenderer.setTranslation(0.0D, 0.0D, 0.0D);
		tessellator.draw();

		//Rotation just for the mirror
		GlStateManager.rotate(te.getAnimationRotations()[1], te.getFacing().getZOffset(), 0, te.getFacing().getXOffset());
		//Draw the mirror
		worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
		worldRenderer.setTranslation(- .5 - blockPos.getX(), - .5 - blockPos.getY(), - .5 - blockPos.getZ());
		worldRenderer.color(255, 255, 255, 255);
		blockRenderer.getBlockModelRenderer().renderModel(te.getWorld(), mirrorModel, state1, blockPos, worldRenderer, true);
		worldRenderer.setTranslation(0.0D, 0.0D, 0.0D);
		tessellator.draw();
		RenderHelper.enableStandardItemLighting();
		GlStateManager.popMatrix();
	}

}