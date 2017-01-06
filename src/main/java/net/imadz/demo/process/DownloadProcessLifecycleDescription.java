package net.imadz.demo.process;

import net.imadz.lifecycle.annotations.Transition;
import net.imadz.lifecycle.annotations.Transitions;
import net.imadz.lifecycle.annotations.StateMachine;
import net.imadz.lifecycle.annotations.StateSet;
import net.imadz.lifecycle.annotations.EventSet;
import net.imadz.lifecycle.annotations.action.Corrupt;
import net.imadz.lifecycle.annotations.action.Fail;
import net.imadz.lifecycle.annotations.action.Recover;
import net.imadz.lifecycle.annotations.action.Redo;
import net.imadz.lifecycle.annotations.action.Timeout;
import net.imadz.lifecycle.annotations.state.Corrupted;
import net.imadz.lifecycle.annotations.state.Final;
import net.imadz.lifecycle.annotations.state.Initial;
import net.imadz.lifecycle.annotations.state.Running;
import net.imadz.lifecycle.annotations.state.Stopped;

import net.imadz.demo.process.DownloadProcessLifecycleDescription.Events.*;

@StateMachine
public interface DownloadProcessLifecycleDescription {

    @StateSet
    static class States {

        @Initial
        @Transitions({
                @Transition(event = Prepare.class, value = Queued.class),
                @Transition(event = Remove.class, value = Removed.class)
        })
        static class New {}

        @Running
        @Transitions({
                @Transition(event = Pause.class, value = Paused.class),
                @Transition(event = Start.class, value = Started.class),
                @Transition(event = Deactive.class, value = InactiveQueued.class),
                @Transition(event = Remove.class, value = Removed.class)
        })
        static class Queued {}

        @Running
        @Transitions({
                @Transition(event = Pause.class, value = Paused.class),
                @Transition(event = Receive.class, value = Started.class),
                @Transition(event = Deactive.class, value = InactiveStarted.class),
                @Transition(event = Err.class, value = Failed.class),
                @Transition(event = Finish.class, value = Finished.class),
                @Transition(event = Remove.class, value = Removed.class)
        })
        static class Started {}

        @Corrupted(recoverPriority = 1)
        @Transitions({
                @Transition(event = ActivateInactiveQueued.class, value = Queued.class),
                @Transition(event = Remove.class, value = Removed.class)
        })
        static class InactiveQueued {}

        @Corrupted(recoverPriority = 0)
        @Transitions({
                @Transition(event = ActivateInactiveStarted.class, value = Queued.class),
                @Transition(event = Remove.class, value = Removed.class)
        })
        static class InactiveStarted {}

        @Stopped
        @Transitions({
                @Transition(event = Resume.class, value = New.class),
                @Transition(event = Restart.class, value = New.class),
                @Transition(event = Remove.class, value = Removed.class)
        })
        static class Paused {}

        @Stopped
        @Transitions({
                @Transition(event = Restart.class, value = New.class),
                @Transition(event = Resume.class, value = New.class),
                @Transition(event = Remove.class, value = Removed.class)
        })
        static class Failed {}

        @Stopped
        @Transitions({
                @Transition(event = Restart.class, value = New.class),
                @Transition(event = Remove.class, value = Removed.class)
        })
        static class Finished {}

        @Final
        static class Removed {}
    }

    @EventSet
    static class Events {

        @Recover
        @Timeout(3000L)
        static class ActivateInactiveQueued {}

        @Recover
        @Timeout(3000L)
        static class ActivateInactiveStarted {}

        @Corrupt
        @Timeout(3000L)
        static class Deactive {}

        @Fail
        @Timeout(3000L)
        static class Err {}

        static class Prepare {}

        static class Start {}

        static class Resume {}

        static class Pause {}

        static class Finish {}

        static class Receive {}

        @Redo
        @Timeout(3000L)
        static class Restart {}

        static class Remove {}
    }
}