package net.lastcraft.lobby;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class WrapperPlayServerMap {

    @Getter
    private PacketContainer handle;

    public WrapperPlayServerMap() {
        this.handle = new PacketContainer(PacketType.Play.Server.MAP);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerMap(PacketContainer packet) {
        this.handle = packet;
    }

    public void sendPacket(Player receiver) {
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(receiver, this.getHandle());
        }
        catch (InvocationTargetException e) {
            throw new RuntimeException("Cannot send packet.", e);
        }
    }

    public int getItemDamage() {
        return this.handle.getIntegers().read(0);
    }

    public void setItemDamage(int value) {
        this.handle.getIntegers().write(0, value);
    }

    public byte getScale() {
        return this.handle.getBytes().read(0);
    }

    public void setScale(byte value) {
        this.handle.getBytes().write(0, value);
    }

    public boolean getTrackingPosition() {
        return this.handle.getBooleans().read(0);
    }

    public void setTrackingPosition(boolean value) {
        this.handle.getBooleans().write(0, value);
    }

    public Object[] getMapIcons() {
        return (Object[]) this.handle.getModifier().read(3);
    }

    public void setMapIcons(Object[] value) {
        this.handle.getModifier().write(3, value);
    }

    public int getColumns() {
        return this.handle.getIntegers().read(3);
    }

    public void setColumns(int value) {
        this.handle.getIntegers().write(3, value);
    }

    public int getRows() {
        return this.handle.getIntegers().read(4);
    }

    public void setRows(int value) {
        this.handle.getIntegers().write(4, value);
    }

    public int getX() {
        return this.handle.getIntegers().read(1);
    }

    public void setX(int value) {
        this.handle.getIntegers().write(1, value);
    }

    public int getZ() {
        return this.handle.getIntegers().read(2);
    }

    public void setZ(int value) {
        this.handle.getIntegers().write(2, value);
    }

    public byte[] getData() {
        return this.handle.getByteArrays().read(0);
    }

    public void setData(byte[] value) {
        this.handle.getByteArrays().write(0, value);
    }

    public void broadcastPacket() {
        ProtocolLibrary.getProtocolManager().broadcastServerPacket(this.getHandle());
    }

    public void receivePacket(Player sender) {
        try {
            ProtocolLibrary.getProtocolManager().recieveClientPacket(sender, this.getHandle());
        }
        catch (Exception e) {
            throw new RuntimeException("Cannot receive packet.", e);
        }
    }

    @Override
    public WrapperPlayServerMap clone() {
        return new WrapperPlayServerMap(handle);
    }

    @Override
    public String toString() {
        return getItemDamage()
                + ", x = " + getX()
                + ", z = " + getZ()
                + ", colums = " + getColumns()
                + ", rows = " + getRows()
                + ", scale = " + getScale()
                + ", track = " + getTrackingPosition();
                //+ ", data = " + getData();
    }
}
