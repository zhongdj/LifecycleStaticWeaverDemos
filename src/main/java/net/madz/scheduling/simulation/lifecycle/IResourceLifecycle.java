package net.madz.scheduling.simulation.lifecycle;

import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.annotations.Functions;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.TransitionSet;
import net.madz.lifecycle.annotations.action.ConditionSet;
import net.madz.lifecycle.annotations.action.Conditional;
import net.madz.lifecycle.annotations.action.ConditionalTransition;
import net.madz.lifecycle.annotations.state.End;
import net.madz.lifecycle.annotations.state.Initial;
import net.madz.scheduling.simulation.lifecycle.IResourceLifecycle.Conditions.HistoryState;
import net.madz.scheduling.simulation.lifecycle.IResourceLifecycle.Transitions.Deploy;
import net.madz.scheduling.simulation.lifecycle.IResourceLifecycle.Transitions.Fail;
import net.madz.scheduling.simulation.lifecycle.IResourceLifecycle.Transitions.Recover;
import net.madz.scheduling.simulation.lifecycle.IResourceLifecycle.Transitions.Recycle;
import net.madz.scheduling.simulation.lifecycle.IResourceLifecycle.Transitions.Release;
import net.madz.scheduling.simulation.lifecycle.IResourceLifecycle.Transitions.Undeploy;
import net.madz.scheduling.simulation.lifecycle.IResourceLifecycle.Transitions.Work;
import net.madz.scheduling.simulation.lifecycle.IResourceLifecycle.Utils.JustUseHistoryState;

@StateMachine
public interface IResourceLifecycle {

    @StateSet
    static interface States {

        @Initial
        @Functions({ @Function(transition = Deploy.class, value = Deploying.class), @Function(transition = Recycle.class, value = Recycled.class), })
        static interface Idle {}
        @Functions({ @Function(transition = Deploy.class, value = Deploying.class), @Function(transition = Work.class, value = Working.class),
                @Function(transition = Fail.class, value = Failing.class) })
        static interface Deploying {}
        @Functions({ @Function(transition = Undeploy.class, value = Undeploying.class), @Function(transition = Work.class, value = Working.class),
                @Function(transition = Fail.class, value = Failing.class) })
        static interface Working {}
        @Functions({ @Function(transition = Undeploy.class, value = Undeploying.class), @Function(transition = Release.class, value = Idle.class),
                @Function(transition = Fail.class, value = Failing.class) })
        static interface Undeploying {}
        @Function(transition = Recover.class, value = { Deploying.class, Working.class, Undeploying.class })
        static interface Failing {}
        @End
        static interface Recycled {}
    }
    @TransitionSet
    static interface Transitions {

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

        public static class JustUseHistoryState implements ConditionalTransition<HistoryState> {

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
