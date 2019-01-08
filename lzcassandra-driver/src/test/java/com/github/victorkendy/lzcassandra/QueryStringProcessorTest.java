package com.github.victorkendy.lzcassandra;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class QueryStringProcessorTest {
	@Test
	public void shouldExtractQueryParametersInCorrectOrder() {
		String query = "select * from table where partition1=:p1 and partition2=:p2 and clustering1=:p1";
		QueryStringProcessor.ProcessedQuery processedQuery = QueryStringProcessor.process(query);

		assertEquals(Arrays.asList("p1", "p2", "p1"), processedQuery.getNamedParameters());
	}

}