package beastgir.Proxy;

import beastgir.core.ModbeastgirBull;
import beastgir.entity.EntityPuppies;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.registry.EntityRegistry;

/**
 * Created by Balmung on 7/14/2015.
 */
public class ServerProxy
{

    @SidedProxy(clientSide = "beastgir.Proxy.ClientProxy", serverSide = "beastgir.Proxy.ServerProxy")
    public static ServerProxy	proxy;

    public void registerEntities() {
        EntityRegistry.registerModEntity(EntityPuppies.class,"Puppies",1, ModbeastgirBull.instance,80,3,true);
        //EntityRegistry.registerModEntity(Class<extends EntityFireGod>,"NameOfIt",2, ModbeastgirBull.instance,80,3,true);
    }

}
