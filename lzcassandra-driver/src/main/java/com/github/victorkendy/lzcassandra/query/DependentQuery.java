package com.github.victorkendy.lzcassandra.query;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

public class DependentQuery {
	private final PreparedStatement preparedStmt;
	private final Session session;
	private final String alias;
	private final ParamBinder[] binders;
	private final int aliasIndex;
	
	public DependentQuery(Session session, PreparedStatement preparedStmt, String alias, ParamBinder[] binders, int aliasIndex) {
		this.session = session;
		this.preparedStmt = preparedStmt;
		this.alias = alias;
		this.binders = binders;
		this.aliasIndex = aliasIndex;
	}
	
	public ListenableFuture<List<CombinedRow>> execute(Row parentRow, Object[] resultLine) {
		Object[] parameters = Arrays.stream(binders).map(b -> b.getValue(parentRow)).collect(Collectors.toList()).toArray();
		ResultSetFuture futureResult = session.executeAsync(preparedStmt.bind(parameters));
		return Futures.transform(futureResult, resultSet -> {
			return resultSet
					.all()
					.stream()
					.map(row -> new CombinedRow(alias, row))
					.collect(Collectors.toList());
		}, MoreExecutors.directExecutor());
	}
}
