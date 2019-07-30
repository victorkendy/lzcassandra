package com.github.victorkendy.lzcassandra.query;

import com.datastax.driver.core.Row;

public interface ParamBinder {
	Object getValue(Row row);
}
