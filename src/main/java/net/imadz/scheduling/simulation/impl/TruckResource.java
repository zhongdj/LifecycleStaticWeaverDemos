package net.imadz.scheduling.simulation.impl;

import net.imadz.lifecycle.LifecycleContext;
import net.imadz.lifecycle.annotations.LifecycleMeta;
import net.imadz.lifecycle.annotations.StateIndicator;
import net.imadz.lifecycle.annotations.Event;
import net.imadz.lifecycle.annotations.action.Condition;
import net.imadz.lifecycle.annotations.callback.PostStateChange;
import net.imadz.lifecycle.annotations.state.Converter;
import net.imadz.scheduling.simulation.IResource;
import net.imadz.scheduling.simulation.Id;
import net.imadz.scheduling.simulation.lifecycle.IResourceLifecycle;
import net.imadz.scheduling.simulation.lifecycle.IResourceLifecycle.Conditions.*;
import net.imadz.scheduling.simulation.lifecycle.IResourceLifecycle.Events.*;

@LifecycleMeta(IResourceLifecycle.class)
public class TruckResource implements IResource, HistoryState {

    @StateIndicator
    @Converter(StateEnumConverter.class)
    private StateEnum state = StateEnum.Idle;
    private StateEnum lastState = StateEnum.Idle;

    @Override
    public Id getId() {
        return null;
    }

    @Override
    public StateEnum getState() {
        return state;
    }

    @Event(Deploy.class)
    public void doDeploy() {}

    @Event(Work.class)
    public void doWork() {}

    @Event(Undeploy.class)
    public void doUndeploy() {}

    @Event(Release.class)
    public void doRelease() {}

    @Event(Recover.class)
    public void doResume() {}

    @Event(Recycle.class)
    public void doRecycle() {}

    @Event(Fail.class)
    public void doFail() {}

    @Condition(HistoryState.class)
    public HistoryState getHistoryState() {
        return this;
    }

    @Override
    public String getLastState() {
        return lastState.name();
    }

    @PostStateChange
    public void recordOldState(LifecycleContext<TruckResource, StateEnum> context) {
        lastState = context.getFromState();
    }
}
