package org.rumblefish.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.rumblefish.state.LotteryState;
import org.rumblefish.state.UserState;

import java.io.Serializable;

@Getter
@NoArgsConstructor
public abstract class UserEvent extends Event implements Serializable {
    private Integer userId;
    private Integer lotteryId;
    private Long timestamp;

    public UserEvent(String type, boolean isOutputEvent, Long timestamp, Integer userId, Integer lotteryId) {
        super(type, isOutputEvent);
        this.userId = userId;
        this.lotteryId = lotteryId;
        this.timestamp = timestamp;
    }

    @Override
    public Integer getId() {
        return userId;
    }
    public abstract UserState process(UserState currentUserState, LotteryState currentLotteryState);
}
