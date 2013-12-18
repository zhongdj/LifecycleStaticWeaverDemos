package net.imadz.scheduling.simulation.impl;

import static org.junit.Assert.assertEquals;
import net.imadz.scheduling.simulation.IResource.StateEnum;
import net.imadz.scheduling.simulation.impl.TruckResource;

import org.junit.Test;

public class TruckResourceTest {

    @Test
    public void truckResource_should_automatic_change_state_and_correctly_resume_to_undeploying_state_with_conditional_transition_recover() {
        TruckResource t = new TruckResource();
        t.doDeploy();
        assertEquals(StateEnum.Deploying, t.getState());
        t.doWork();
        assertEquals(StateEnum.Working, t.getState());
        t.doUndeploy();
        assertEquals(StateEnum.Undeploying, t.getState());
        t.doFail();
        assertEquals(StateEnum.Failing, t.getState());
        t.doResume();
        assertEquals(StateEnum.Undeploying, t.getState());
    }
}
