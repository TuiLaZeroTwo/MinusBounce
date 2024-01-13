/*
 * MinusBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/MinusMC/MinusBounce
 */
package net.minusmc.minusbounce.ui.client.hud.element.elements.targets.impl

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.opengl.GL11
import java.awt.Color
import javax.vecmath.Vector2d
import kotlin.math.*

class TLZTarget(inst: Target): TargetStyle("TLZ", inst, false) {

   private var lastUpdate = System.currentTimeMillis()
   private var target: EntityPlayer? = null
   private var distance: Double = 0.0
   private var winOrLoseStatus: String = ""

   @SubscribeEvent
   fun onClientTick(event: TickEvent.ClientTickEvent) {
       if (event.phase != TickEvent.Phase.END || target == null) {
           return
       }

       val world = Minecraft.getMinecraft().world
       val pos = BlockPos(target!!.posX, target!!.posY, target!!.posZ)
       val playerPos = Minecraft.getMinecraft().player.position
       val distance = playerPos.distanceTo(pos)

       if (System.currentTimeMillis() - lastUpdate > 1000) {
           lastUpdate = System.currentTimeMillis()
           this.distance = distance
           winOrLoseStatus = if (distance <= Minecraft.getMinecraft().player.attackReachDistance) {
               I18n.format("gui.win")
           } else {
               I18n.format("gui.lose")
           }
       }
   }

   override fun drawTarget(entity: EntityPlayer) {
       target = entity

       GlStateManager.pushMatrix()
       GlStateManager.translate(-150.0, 50.0, 0.0)

       // Draw the round angle
       GlStateManager.color(0.0f, 0.0f, 0.0f, 0.5f)
       GlStateManager.enableBlend()
       RenderUtils.drawRect(0F, 0F, 160F, 60F)

       // Draw the target's head skin
       val skinTexture = Minecraft.getMinecraft().skinManager.loadSkinFromCache(entity.uniqueID)
       GlStateManager.bindTexture(skinTexture.getGlTextureId())
       RenderUtils.drawTexturedModalRect(5F, 5F, 8, 8, 8, 8)

       // Draw the target's name
       GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
       Fonts.minecraftFont.drawStringWithShadow(entity.name, 6F, 40F, -1)

       // Draw the target's distance
       Fonts.minecraftFont.drawStringWithShadow("${decimalFormat2.format(distance)} m", 6F, 30F, -1)

       // Draw the target's health
       val healthPercentage = entity.health / entity.maxHealth
       RenderUtils.drawRect(5F, 54F, 5F + (healthPercentage * 155), 55F, Color.RED.rgb)

       // Draw the win or lose status
       Fonts.minecraftFont.drawStringWithShadow(winOrLoseStatus, 155F, 55F, -1)

       GlStateManager.popMatrix()
   }

   override fun getBorder(entity: EntityPlayer?): Border {
       return Border(0F, 0F, 160F, 60F)
   }
}
