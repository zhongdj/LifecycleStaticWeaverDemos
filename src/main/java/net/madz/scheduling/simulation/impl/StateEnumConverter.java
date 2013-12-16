package net.madz.scheduling.simulation.impl;

import net.madz.lifecycle.StateConverter;
import net.madz.scheduling.simulation.IResource.StateEnum;

public class StateEnumConverter implements StateConverter<StateEnum> {

	@Override
	public StateEnum fromState(String stateName) {
		return StateEnum.valueOf(stateName);
	}

	@Override
	public String toState(StateEnum state) {
		return state.name();
	}

}
