package com.github.victorkendy.lzcassandra;

import com.datastax.driver.core.ResultSetFuture;
import org.junit.Test;

public class AccessorFactoryTest {
	@EnhancedAccessor
	interface TestAccessor {
		@Query("select * from table")
		ResultSetFuture execute();
	}
}