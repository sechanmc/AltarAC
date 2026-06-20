package ac.altarac.predictionengine.movementtick;

import ac.altarac.player.AltarACPlayer;
import ac.altarac.predictionengine.predictions.input.Input;
import ac.altarac.predictionengine.predictions.rideable.PredictionEngineNautilusWater;
import ac.altarac.utils.data.packetentity.PacketEntityNautilus;
import com.github.retrooper.packetevents.protocol.attribute.Attributes;

public class MovementTickerNautilus extends MovementTickerLivingVehicle {

    public MovementTickerNautilus(AltarACPlayer player) {
        super(player);

        PacketEntityNautilus nautilus = (PacketEntityNautilus) player.compensatedEntities.self.getRiding();
        if (!nautilus.hasSaddle()) return;

        player.speed = getRiddenSpeed(player);

        // Setup player inputs
        float sideways = player.vehicleData.vehicleHorizontal;
        float forward = 0.0F;
        float upAndDown = 0.0F;
        if (player.vehicleData.vehicleForward != 0.0F) {
            float xRot = player.pitch * 2F;
            float calcForward = player.trigHandler.cos(xRot * (float) (Math.PI / 180.0));
            float calcUpAndDown = -player.trigHandler.sin(xRot * (float) (Math.PI / 180.0));
            if (player.vehicleData.vehicleForward < 0.0F) {
                calcForward *= -0.5F;
                calcUpAndDown *= -0.5F;
            }

            upAndDown = calcUpAndDown;
            forward = calcForward;
        }

        this.movementInput = Input.createInput(player, sideways, upAndDown, forward).normalize(player);
    }

    @Override
    public void doWaterMove(float swimSpeed, boolean isFalling, float swimFriction) {
        new PredictionEngineNautilusWater(this.movementInput, 0.9).guessBestMovement(getRiddenSpeed(player), player);
    }

    public float getRiddenSpeed(AltarACPlayer player) {
        PacketEntityNautilus nautilus = (PacketEntityNautilus) player.compensatedEntities.self.getRiding();
        return player.wasTouchingWater
                ? 0.0325F * (float) nautilus.getAttributeValue(Attributes.MOVEMENT_SPEED)
                : 0.02F * (float) nautilus.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }

}
