package net.glowstone.block.blocktype;

import net.glowstone.GlowChunk;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.TECommandBlock;
import net.glowstone.block.entity.TileEntity;
import net.glowstone.block.state.GlowCommandBlock;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.message.play.game.UpdateBlockEntityMessage;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.GameMode;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CommandBlockType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Command;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

public class BlockCommandBlock extends BlockType {

    private CommandBlockType type;

    public BlockCommandBlock() {
        this(CommandBlockType.REDSTONE);
    }

    public BlockCommandBlock(CommandBlockType type) {
        this.type = type;
        setDrops(new ItemStack(type.getMaterial()));
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);

        MaterialData data = state.getData();
        if (data instanceof Command) {
            Command cmd = (Command) data;
            cmd.setDirection(face);
            state.setData(data);
        } else {
            warnMaterialData(Command.class, data);
        }
    }

    @Override
    public boolean blockInteract(GlowPlayer player, GlowBlock block, BlockFace face, Vector clickedLoc) {
        if (!player.isOp() || player.getGameMode() != GameMode.CREATIVE) {
            player.sendMessage("Command blocks can only be used by oped players in creative mode.");
            return false;
        }
        //Send the message
        GlowCommandBlock cmd = (GlowCommandBlock) block.getState();
        CompoundTag tag = new CompoundTag();
        cmd.getTileEntity().saveNbt(tag);

        UpdateBlockEntityMessage message = new UpdateBlockEntityMessage(block.getX(), block.getY(), block.getZ(), 2, tag);
        player.getSession().send(message);
        return true;
    }

    @Override
    public TileEntity createTileEntity(GlowChunk chunk, int cx, int cy, int cz) {
        return new TECommandBlock(chunk.getBlock(cx, cy, cz));
    }

    public CommandBlockType getType() {
        return type;
    }
}
