package net.imadz.scheduling.simulation.lifecycle;

import net.imadz.lifecycle.annotations.EventSet;
import net.imadz.lifecycle.annotations.StateMachine;
import net.imadz.lifecycle.annotations.StateSet;
import net.imadz.lifecycle.annotations.Transition;
import net.imadz.lifecycle.annotations.Transitions;
import net.imadz.lifecycle.annotations.action.ConditionSet;
import net.imadz.lifecycle.annotations.action.Conditional;
import net.imadz.lifecycle.annotations.action.ConditionalEvent;
import net.imadz.lifecycle.annotations.state.Final;
import net.imadz.lifecycle.annotations.state.Initial;
import net.imadz.scheduling.simulation.lifecycle.IResourceLifecycle.Conditions.HistoryState;
import net.imadz.scheduling.simulation.lifecycle.IResourceLifecycle.Events.Deploy;
import net.imadz.scheduling.simulation.lifecycle.IResourceLifecycle.Events.Fail;
import net.imadz.scheduling.simulation.lifecycle.IResourceLifecycle.Events.Recover;
import net.imadz.scheduling.simulation.lifecycle.IResourceLifecycle.Events.Recycle;
import net.imadz.scheduling.simulation.lifecycle.IResourceLifecycle.Events.Release;
import net.imadz.scheduling.simulation.lifecycle.IResourceLifecycle.Events.Undeploy;
import net.imadz.scheduling.simulation.lifecycle.IResourceLifecycle.Events.Work;
import net.imadz.scheduling.simulation.lifecycle.IResourceLifecycle.Utils.JustUseHistoryState;

@StateMachine
public interface IResourceLifecycle {

    @StateSet
    static interface States {

        @Initial
        @Transitions({ @Transition(event = Deploy.class, value = Deploying.class), @Transition(event = Recycle.class, value = Recycled.class), })
        static interface Idle {}
        @Transitions({ @Transition(event = Deploy.class, value = Deploying.class), @Transition(event = Work.class, value = Working.class),
                @Transition(event = Fail.class, value = Failing.class) })
        static interface Deploying {}
        @Transitions({ @Transition(event = Undeploy.class, value = Undeploying.class), @Transition(event = Work.class, value = Working.class),
                @Transition(event = Fail.class, value = Failing.class) })
        static interface Working {}
        @Transitions({ @Transition(event = Undeploy.class, value = Undeploying.class), @Transition(event = Release.class, value = Idle.class),
                @Transition(event = Fail.class, value = Failing.class) })
        static interface Undeploying {}
        @Transition(event = Recover.class, value = { Deploying.class, Working.class, Undeploying.class })
        static interface Failing {}
        @Final
        static interface Recycled {}
    }
    @EventSet
    static interface Events {

        @Conditional(condition = HistoryState.class, judger = JustUseHistoryState.class)
        static interface Recover {}
        static interface Deploy {}
        static interface Work {}
        static interface Undeploy {}
        static interface Release {}
        static interface Fail {}
        static interface Recycle {}
    }
    @ConditionSet
    static interface Conditions {

        static interface HistoryState {

            String getLastState();
        }
    }
    public static class Utils {

        public static class JustUseHistoryState implements ConditionalEvent<HistoryState> {

            @Override
            public Class<?> doConditionJudge(HistoryState t) {
                String lastState = t.getLastState();
                for ( Class<?> cls : IResourceLifecycle.States.class.getDeclaredClasses() ) {
                    if ( cls.getSimpleName().equalsIgnoreCase(lastState) ) return cls;
                }
                throw new IllegalStateException("Cannot find state: " + t.getLastState());
            }
        }
    }
}
