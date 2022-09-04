package org.jenkinsci.plugins.parameterizedscheduler;

import hudson.triggers.TimerTrigger;

import java.util.Map;
import java.util.Objects;

public class ParameterizedTimerTriggerCause extends TimerTrigger.TimerTriggerCause {

	private final String description;

	public ParameterizedTimerTriggerCause(Map<String, String> parameterValues) {
		this.description = Messages.ParameterizedTimerTrigger_TimerTriggerCause_ShortDescription(parameterValues
				.toString());
	}

	@Override
	public String getShortDescription() {
		return description;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		ParameterizedTimerTriggerCause that = (ParameterizedTimerTriggerCause) o;
		return Objects.equals(description, that.description);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), description);
	}
}
