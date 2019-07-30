package com.github.victorkendy.lzcassandra.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.datastax.driver.core.Row;

public class ExecutionContext {
	private final Map<String, Object> parameters;
	private final List<Row> results = new ArrayList<>(5);

	public ExecutionContext(Map<String, Object> parameters) {
		this.parameters = parameters;
	}
	Object getParameterValue(String name) {
		return parameters.get(name);
	}
	public ExecutionContext createSubContext(Row row) {
		return null;
	}
}
