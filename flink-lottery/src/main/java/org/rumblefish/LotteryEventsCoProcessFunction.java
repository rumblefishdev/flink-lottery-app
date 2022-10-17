package org.rumblefish;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.state.BroadcastState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.functions.co.KeyedCoProcessFunction;
import org.apache.flink.util.Collector;
import org.rumblefish.event.LotteryEvent;
import org.rumblefish.event.UserEvent;
import org.rumblefish.state.LotteryState;
import org.rumblefish.state.UserState;

import java.io.Serializable;

@Slf4j
@Getter
public class LotteryEventsCoProcessFunction
        extends KeyedCoProcessFunction<Integer, LotteryEvent, UserEvent, EventOutput> implements CheckpointedFunction, Serializable {

    private transient BroadcastState<Integer, UserState> userState;
    private transient BroadcastState<Integer, LotteryState> lotteryState;

    @Override
    public void processElement1(LotteryEvent lotteryEvent, Context ctx, Collector<EventOutput> out) throws Exception {
        log.info("Processing LotteryEvent is started: " + lotteryEvent);
        log.info("Current LotteryState: " + getLotteryState());

        LotteryState currentLotteryState = getCurrentLotteryState(lotteryEvent.getLotteryId());
        LotteryState updatedLotteryState = lotteryEvent.process(currentLotteryState);
        lotteryState.put(updatedLotteryState.getLotteryId(), updatedLotteryState);

        log.info("Processing LotteryEvent is done: " + lotteryEvent);
        log.info("updated LotteryState: " + getLotteryState());
    }

    @Override
    public void processElement2(UserEvent userEvent, Context ctx, Collector<EventOutput> out) throws Exception {
        log.info("Processing UserEvent is started: " + userEvent);
        log.info("Current UserState: " + getUserState());

        UserState currentUserState = getCurrentUserState(userEvent.getUserId());
        LotteryState currentLotteryState = getCurrentLotteryState(userEvent.getLotteryId());

        UserState updatedUserState = userEvent.process(currentUserState, currentLotteryState);
        userState.put(updatedUserState.getUserId(), updatedUserState);

        if (userEvent.isOutputEvent())
            out.collect(new EventOutput(userEvent.getLotteryId(), userEvent.getUserId(), userEvent.getTimestamp()));

        log.info("Processing UserEvent is done: " + userEvent);
        log.info("updated UserState: " + getUserState());
    }

    private UserState getCurrentUserState(Integer userId) throws Exception {
        UserState currentUserState = userState.get(userId);
        return currentUserState == null ? UserState.createNew(userId) : currentUserState;
    }

    private LotteryState getCurrentLotteryState(Integer lotteryId) throws Exception {
        LotteryState currentLotteryState = lotteryState.get(lotteryId);
        return currentLotteryState == null ? LotteryState.createNew(lotteryId) : currentLotteryState;
    }

    @Override
    public void onTimer(long timestamp,
                        KeyedCoProcessFunction<Integer, LotteryEvent, UserEvent, EventOutput>.OnTimerContext ctx,
                        Collector<EventOutput> out) {
        log.info("Time window is ended for current lottery, lotteryId" + ctx.getCurrentKey());
    }

    @Override
    public void initializeState(FunctionInitializationContext context) throws Exception {
        MapStateDescriptor<Integer, UserState> userDescriptor = new MapStateDescriptor<>(
                "userState",
                TypeInformation.of(new TypeHint<>() {
                }),
                TypeInformation.of(new TypeHint<>() {
                }));
        userState = context.getOperatorStateStore().getBroadcastState(userDescriptor);

        MapStateDescriptor<Integer, LotteryState> lotteryStateMapDescriptor = new MapStateDescriptor<>(
                "lotteryState",
                TypeInformation.of(new TypeHint<>() {
                }),
                TypeInformation.of(new TypeHint<>() {
                }));
        lotteryState = context.getOperatorStateStore().getBroadcastState(lotteryStateMapDescriptor);
    }

    @Override
    public void snapshotState(FunctionSnapshotContext context) {
    }

}
