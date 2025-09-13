package world.landfall.deepspace.network;

import com.mojang.logging.LogUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import world.landfall.deepspace.Deepspace;
import world.landfall.deepspace.ModAttatchments;

import java.util.Objects;

public class JetpackPacket {
    private static Logger LOGGER = LogUtils.getLogger();

    public record RocketForward(boolean rocketing) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<RocketForward> TYPE = new Type<>(
                Deepspace.path("rocket_forward")
        );
        public static final StreamCodec<FriendlyByteBuf, RocketForward> STREAM_CODEC = StreamCodec.ofMember(
            RocketForward::encode,
            RocketForward::decode
        );

        public static void encode(@NotNull RocketForward packet, @NotNull FriendlyByteBuf buffer) {buffer.writeBoolean(packet.rocketing);}
        public static @NotNull RocketForward decode(FriendlyByteBuf buf) {
            return new RocketForward(buf.readBoolean());
        }
        public static void handle(@NotNull RocketForward packet, IPayloadContext ctx) {
            Objects.requireNonNull(packet, "Packet cannot be null!");
            Objects.requireNonNull(ctx, "Context cannot be null!");
            var player = ctx.player();
            if (player.level().isClientSide) return;
            if (!player.hasData(ModAttatchments.IS_FLYING_JETPACK)) {
                player.setData(ModAttatchments.IS_ROCKETING_FORWARD, false);
                return;
            }

            var isFlying = player.getData(ModAttatchments.IS_FLYING_JETPACK);
            if (!isFlying) return;
            player.setData(ModAttatchments.IS_ROCKETING_FORWARD, packet.rocketing);
        }
        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
    public record BeginFlying(boolean flying) implements CustomPacketPayload {
        public static final Type<BeginFlying> TYPE = new Type<>(
                Deepspace.path("begin_flying")
        );

        public static final StreamCodec<FriendlyByteBuf, BeginFlying> STREAM_CODEC = StreamCodec.ofMember(
                BeginFlying::encode,
                BeginFlying::decode
        );
        public static void encode(@NotNull BeginFlying packet, @NotNull FriendlyByteBuf buffer) {
            Objects.requireNonNull(packet, "Packet cannot be null");
            Objects.requireNonNull(buffer, "Buffer cannot be null");

            buffer.writeBoolean(packet.flying);
        }
        public static @NotNull BeginFlying decode(FriendlyByteBuf buf) {return new BeginFlying(buf.readBoolean());}
        public static void handle(@NotNull BeginFlying packet, @NotNull IPayloadContext ctx) {
            Objects.requireNonNull(packet, "Packet cannot be null!");
            Objects.requireNonNull(ctx, "Context cannot be null!");
            var player = ctx.player();
            if (player.level().isClientSide) return;
            if (player.onGround()) {
                player.setData(ModAttatchments.IS_FLYING_JETPACK, false);
                player.setData(ModAttatchments.IS_ROCKETING_FORWARD, false);
            } else
                player.setData(ModAttatchments.IS_FLYING_JETPACK, packet.flying);
        }
        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
