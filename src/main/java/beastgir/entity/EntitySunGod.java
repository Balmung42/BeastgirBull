package beastgir.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.World;

/**
 * Created by Balmung on 10/27/2015.
 */
public class EntitySunGod extends EntityFireGod{

    public EntitySunGod(World p_i1731_1_) {
        super(p_i1731_1_);
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        return "mob.blaze.breathe";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return "mob.blaze.hit";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return "mob.blaze.death";
    }

    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender(float p_70070_1_)
    {
        return 15728880;
    }

    /**
     * Gets how bright this entity is.
     */
    public float getBrightness(float p_70013_1_)
    {
        return 1.0F;
    }

    /**
     *
     * @return The drop Item obviously, but use the other method
     */
    protected Item getDropItem()
    {
        return Items.blaze_rod;
    }

    /**
     * This is the other one
     *
     * @param actuallyDropItems
     * @param numberOfDrops
     */
    @Override
    protected void dropFewItems(boolean actuallyDropItems, int numberOfDrops) {
        // Drop shit
        if (actuallyDropItems)
        {
            // Randoms. I taught you about these years ago
            // 1 is the minimum here, 1 + numberOfDrops is the maximum
            int j = this.rand.nextInt(2 + numberOfDrops);

            // Drop j amount, one at a time
            for (int k = 0; k < j; ++k)
            {
                // Drop a single Blaze Rod
                this.dropItem(Items.blaze_rod, 1);
            }
        }
    }
}
