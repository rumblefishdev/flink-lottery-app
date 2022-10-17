package org.rumblefish.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.rumblefish.state.LotteryState;

import java.io.Serializable;

@Getter
@NoArgsConstructor
public abstract class LotteryEvent extends Event implements Serializable {
    private int lotteryId;

    public LotteryEvent(String type, boolean isOutputEvent, int lotteryId) {
        super(type, isOutputEvent);
        this.lotteryId = lotteryId;
    }

    @Override
    public Integer getId() {
        return lotteryId;
    }

    public abstract LotteryState process(LotteryState currentLotteryState);

}
