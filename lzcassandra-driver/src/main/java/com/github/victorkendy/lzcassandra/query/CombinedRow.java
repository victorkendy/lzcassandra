package com.github.victorkendy.lzcassandra.query;

import java.util.HashMap;

import com.datastax.driver.core.Row;

public class CombinedRow {
	private HashMap<String, Row> namedRows = new HashMap<>();
	private CombinedRow() {
	}
	
	public CombinedRow(String alias, Row row) {
		namedRows.put(alias, row);
	}
	
	public CombinedRow combine(CombinedRow other) {
		CombinedRow result = new CombinedRow();
		result.namedRows.putAll(namedRows);
		result.namedRows.putAll(other.namedRows);
		return result;
	}

	public Object get(String alias, String column) {
		return namedRows.get(alias).getObject(column);
	}
}
