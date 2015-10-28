package beastgir.core;

import beastgir.Proxy.ServerProxy;
import beastgir.item.Puppies;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

// Note on compile, all comments are removed. If I decompiled it, I wouldn't have any of the nice notes.
// This means I can write 4 pages worth of comments if I want to, and it won't make the mod any larger to download.

// This is needed. It just is, okay. It defines the properties of the mod.
@Mod(modid = "beastgirBull", name = "Beastgir's Bull", version = "v1.0", acceptedMinecraftVersions = "[1.7.2,1.8)", dependencies = "required-after:Forge@[10.13.2.1208,)")
public class ModbeastgirBull {

    // Anything that starts with an @ is called an annotation. It is placed before anything is defined to give it
    // a special property or handling
    // Common ones are @Suppress, @Override, etc.
    // This is the mod instance.
    @Instance("beastgirBull")
    public static ModbeastgirBull instance;

    public static Item puppies;

    // These are handled automatically by forge using the Event Handler Annotation
    // preinit is for declaring instances (blocks, items, world types, dimensions, etc) and registering handlers
    @EventHandler
    public void preinit(FMLPreInitializationEvent event) {

        // Initialize puppies so that it is not null
        puppies=new Puppies();

        // Give it an entry in the localization file (en_US.lang)
        puppies.setUnlocalizedName("puppies");

        // Register entities in the game system. see the actual function for more.
        ServerProxy.proxy.registerEntities();

        // Register an item for use in the game. It now has an ID and a name
        GameRegistry.registerItem(puppies,"Puppies");

        // This is an event bus, if you don't register the class with the SubscribeEvent annotation (@), they won't do anything.
        MinecraftForge.EVENT_BUS.register(this);
    }

    // init is for using the declared instances (recipes, rendering, gui, etc)
    @EventHandler
    public void init(FMLInitializationEvent event) {

    }

    // Runs after the above. This is not commonly used.
    @EventHandler
    public void postinit(FMLPostInitializationEvent event) {

    }

    // called every time any entity is updated
    // SubscribeEvent is used to tell the Event Bus what to send to this class
    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        // Only do stuff if it is on the host side. Clients can't set shit on fire
        if(event.entity.worldObj.isRemote) return;

        Entity entity = event.entity;
        // instanceof already checks for null, but I'm used to null checking everything.
        if (entity != null && entity instanceof EntityWolf) {
            if (((EntityWolf) entity).isTamed() && ((EntityWolf) entity).getOwner() != null && ((EntityWolf) entity).getOwner().getCommandSenderName().equalsIgnoreCase("balmung42")) {
                // Remember ! is not or the opposite of that.
                // isImmuneToFire is normally protected, but I made an access transformer to make it public so we
                // can change it.
                if (!entity.isImmuneToFire) entity.isImmuneToFire = true;
                // If it's on fire, make it not be
                if(entity.fire > 0) entity.fire = 0;
            } else {
                // make sure any wolves that aren't yours didn't suddenly become fireproof
                if (entity.isImmuneToFire) entity.isImmuneToFire = false;
                // is it a chicken, and has it been aroumd for 20 minutes. 20 ticks / second. 20 ticks/s * 60 s/min * 20 min
                if(entity instanceof EntityChicken && entity.ticksExisted >= 24000) {
                    // if it has less than a second left of being on fire, add another second of it being on fire
                    if(entity.fire < 20) entity.fire += 20;
                    // if it's resisting, stomp the resistance
                    if(entity.fireResistance > 0) entity.fireResistance = 0;
                }
            }
        }

        // Remember your squiggly brackets. Clicking on them will show what is encompassed by them
        if (entity instanceof EntityPlayer) {
            if(entity.getCommandSenderName().equalsIgnoreCase("balmung42")) {
                // if not immune to fire, make it immune
                if (!entity.isImmuneToFire) {
                    entity.isImmuneToFire = true;
                    // if on fire, make it not on fire
                    if (entity.fire > 0) entity.fire = 0;
                }
            } else {
                // if not balmung42, and immune to fire, make it not immune to fire
                if (entity.isImmuneToFire) entity.isImmuneToFire = false;
            }
        }

        if(entity instanceof EntityPlayer && entity.getCommandSenderName().equalsIgnoreCase("balmung42")) {

            if(entity.ridingEntity != null) {
                // Every tick (0.05s) the entity's speed will double
                entity.ridingEntity.motionX *= 2D;
                entity.ridingEntity.motionZ *= 2D;
                // Prevent things from moving too fast. 2 is about 90 mph or 40 m/s.
                // Math: Everything is per tick. A tick is 50ms. 2 * 20 = 40 m/s
                entity.ridingEntity.motionX = MathHelper.clamp_double(entity.ridingEntity.motionX, -2, 2);
                entity.ridingEntity.motionZ = MathHelper.clamp_double(entity.ridingEntity.motionZ, -2, 2);
            }
        }
    }

    public void onPlayerRightClick(PlayerInteractEvent evt) {
        // Only do stuff if it is on the host side. Clients can't set shit on fire
        if(evt.world.isRemote) return;
        // Only if right clicking in air
        if(evt.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR) {
            // is it a player, is the player holding something, is the player holding dye, is it purple
            if((evt.entity instanceof EntityPlayer) && ((EntityPlayer) evt.entity).inventory.getCurrentItem() != null && (((EntityPlayer) evt.entity).inventory.getCurrentItem().getItem() instanceof ItemDye) && (((EntityPlayer) evt.entity).inventory.getCurrentItem().getItemDamage() == 5)) {
                // Vector math shit you'll never bother learning, but will probably copy and paste a lot.
                // BTW this a raycast
                double range = 150D;
                Vec3 vec3 = Vec3.createVectorHelper(evt.entityPlayer.posX, evt.entityPlayer.posY + evt.entityPlayer.getEyeHeight(), evt.entityPlayer.posZ);
                Vec3 vec31 = evt.entityPlayer.getLookVec();
                Vec3 vec32 = vec3.addVector(vec31.xCoord * range, vec31.yCoord * range, vec31.zCoord * range);
                // the boolean makes it hit liquids
                MovingObjectPosition hit = evt.world.rayTraceBlocks(vec3, vec32, false);
                // did it hit something
                if(hit != null) {
                    // was it a block
                    if(hit.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                        // HIT IT WITH LIGHTNING
                        evt.world.spawnEntityInWorld(new EntityLightningBolt(evt.world,hit.blockX + Facing.offsetsXForSide[hit.sideHit], hit.blockY + Facing.offsetsYForSide[hit.sideHit], hit.blockZ + Facing.offsetsZForSide[hit.sideHit]));
                        // if it wasn't a block, then it was an entity
                    } else {
                        // HIT IT WITH LIGHTNING
                        evt.world.spawnEntityInWorld(new EntityLightningBolt(evt.world, hit.entityHit.posX, hit.entityHit.posY + hit.entityHit.getEyeHeight(), hit.entityHit.posZ));
                    }
                }
            }
        }
    }



}