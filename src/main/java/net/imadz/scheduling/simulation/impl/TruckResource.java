package net.imadz.scheduling.simulation.impl;

import net.imadz.lifecycle.LifecycleContext;
import net.imadz.lifecycle.annotations.LifecycleMeta;
import net.imadz.lifecycle.annotations.StateIndicator;
import net.imadz.lifecycle.annotations.Transition;
import net.imadz.lifecycle.annotations.action.Condition;
import net.imadz.lifecycle.annotations.callback.PostStateChange;
import net.imadz.lifecycle.annotations.state.Converter;
import net.imadz.scheduling.simulation.IResource;
import net.imadz.scheduling.simulation.Id;
import net.imadz.scheduling.simulation.lifecycle.IResourceLifecycle;

@LifecycleMeta(IResourceLifecycle.class)
public class TruckResource implements IResource, IResourceLifecycle.Conditions.HistoryState {

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

    @Transition(IResourceLifecycle.Transitions.Deploy.class)
    public void doDeploy() {}

    @Transition(IResourceLifecycle.Transitions.Work.class)
    public void doWork() {}

    @Transition(IResourceLifecycle.Transitions.Undeploy.class)
    public void doUndeploy() {}

    @Transition(IResourceLifecycle.Transitions.Release.class)
    public void doRelease() {}

    @Transition(IResourceLifecycle.Transitions.Recover.class)
    public void doResume() {}

    @Transition(IResourceLifecycle.Transitions.Recycle.class)
    public void doRecycle() {}

    @Transition(IResourceLifecycle.Transitions.Fail.class)
    public void doFail() {}

    @Condition(IResourceLifecycle.Conditions.HistoryState.class)
    public IResourceLifecycle.Conditions.HistoryState getHistoryState() {
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
