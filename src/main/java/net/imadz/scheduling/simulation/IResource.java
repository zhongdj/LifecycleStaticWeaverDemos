package net.imadz.scheduling.simulation;

import net.imadz.lifecycle.annotations.Event;
import net.imadz.scheduling.simulation.lifecycle.IResourceLifecycle.Events.*;
public interface IResource {

    Id getId();

    StateEnum getState();

    public static enum StateEnum {
        Idle,
        Deploying,
        Working,
        Undeploying,
        Failing,
        Interrupting,
        Recycled
    }

    @Event(Deploy.class)
    void doDeploy();

    @Event(Work.class)
    void doWork();

    @Event(Undeploy.class)
    void doUndeploy();

    @Event(Release.class)
    void doRelease();

    @Event(Recover.class)
    void doResume();

    @Event(Recycle.class)
    void doRecycle();

    @Event(Fail.class)
    void doFail();
}
