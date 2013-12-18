package net.imadz.scheduling.simulation;

import net.imadz.lifecycle.annotations.Transition;
import net.imadz.scheduling.simulation.lifecycle.IResourceLifecycle;

public interface IResource {

	Id getId();

	StateEnum getState();

	public static enum StateEnum {
		Idle, Deploying, Working, Undeploying, Failing, Interrupting, Recycled
	}

	@Transition(IResourceLifecycle.Transitions.Deploy.class)
	void doDeploy();

	@Transition(IResourceLifecycle.Transitions.Work.class)
	void doWork();

	@Transition(IResourceLifecycle.Transitions.Undeploy.class)
	void doUndeploy();

	@Transition(IResourceLifecycle.Transitions.Release.class)
	void doRelease();

	@Transition(IResourceLifecycle.Transitions.Recover.class)
	void doResume();

	@Transition(IResourceLifecycle.Transitions.Recycle.class)
	void doRecycle();

	@Transition(IResourceLifecycle.Transitions.Fail.class)
	void doFail();

}
