package net.lastcraft.lobby.game.npc;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;
import net.lastcraft.api.LastCraft;
import net.lastcraft.api.depend.PacketObject;
import net.lastcraft.api.entity.npc.types.HumanNPC;
import net.lastcraft.api.hologram.Hologram;
import net.lastcraft.api.hologram.HologramAPI;
import net.lastcraft.base.locale.Language;

public abstract class LobbyNPC {

    protected static final HologramAPI HOLOGRAM_API = LastCraft.getHologramAPI();

    @Getter
    protected final HumanNPC humanNPC;

    protected final TIntObjectMap<Hologram> holograms = new TIntObjectHashMap<>();

    protected LobbyNPC(HumanNPC humanNPC) {
        this.humanNPC = humanNPC;
    }

    public final Hologram getHologram(Language lang) {
        Hologram hologram = holograms.get(lang.getId());
        if (hologram != null)
            return hologram;

        return holograms.get(Language.getDefault().getId());
    }

    public final void remove() {
        holograms.valueCollection().forEach(PacketObject::remove);
        humanNPC.remove();
    }
}
