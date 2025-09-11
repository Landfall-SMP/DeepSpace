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

    public record RocketForward() implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<RocketForward> TYPE = new Type<>(
                Deepspace.path("rocket_forward")
        );
        public static final StreamCodec<FriendlyByteBuf, RocketForward> STREAM_CODEC = StreamCodec.ofMember(
            RocketForward::encode,
            RocketForward::decode
        );

        public static RocketForward create() {
            return new RocketForward();
        }
        public static void encode(@NotNull RocketForward packet, @NotNull FriendlyByteBuf buffer) {}
        public static @NotNull RocketForward decode(FriendlyByteBuf buf) {
            return new RocketForward();
        }
        public static void handle(@NotNull RocketForward packet, IPayloadContext ctx) {
            Objects.requireNonNull(packet, "Packet cannot be null!");
            Objects.requireNonNull(ctx, "Context cannot be null!");
            var player = ctx.player();
            if (player.level().isClientSide) return;
            if (!player.hasData(ModAttatchments.IS_FLYING_JETPACK)) return;

            var isFlying = player.getData(ModAttatchments.IS_FLYING_JETPACK);
            if (!isFlying) return;
            player.setData(ModAttatchments.IS_ROCKETING_FORWARD, true);
        }
        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
    public record BeginFlying() implements CustomPacketPayload {
        public static final Type<BeginFlying> TYPE = new Type<>(
                Deepspace.path("begin_flying")
        );

        public static final StreamCodec<FriendlyByteBuf, BeginFlying> STREAM_CODEC = StreamCodec.ofMember(
                BeginFlying::encode,
                BeginFlying::decode
        );
        public static void encode(@NotNull BeginFlying packet, @NotNull FriendlyByteBuf buffer) {}
        public static @NotNull BeginFlying decode(FriendlyByteBuf buf) {return new BeginFlying();}
        public static void handle(@NotNull BeginFlying packet, @NotNull IPayloadContext ctx) {
            Objects.requireNonNull(packet, "Packet cannot be null!");
            Objects.requireNonNull(ctx, "Context cannot be null!");
            var player = ctx.player();
            if (player.level().isClientSide ||
                player.getData(ModAttatchments.IS_FLYING_JETPACK) ||
                player.onGround()) return;
            player.setData(ModAttatchments.IS_FLYING_JETPACK, true);
            LOGGER.info("made it");
        }
        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
