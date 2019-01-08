package com.github.victorkendy.lzcassandra;

import java.util.Collections;
import java.util.List;

class QueryStringProcessor {

	public static ProcessedQuery process(String query) {
		extractParameters(query);
		return null;
	}

	private static List<String> extractParameters(String query) {
		return null;
	}

	public static class ProcessedQuery {

		public List<String> getNamedParameters() {
			return Collections.emptyList();
		}
	}
}
