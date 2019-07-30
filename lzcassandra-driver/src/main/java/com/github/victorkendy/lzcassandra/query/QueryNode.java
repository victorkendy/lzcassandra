package com.github.victorkendy.lzcassandra.query;

import java.util.List;
import java.util.concurrent.Executor;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

public class QueryNode {
	private final PreparedStatement rootStmt;
	private final String[] parametersOrder;
	private final int level;
	private final QueryNode[] children;
	
	public QueryNode(PreparedStatement rootStmt, String[] parametersOrder, int level, QueryNode[] children) {
		this.rootStmt = rootStmt;
		this.parametersOrder = parametersOrder;
		this.level = level;
		this.children = children;
	}

	public ListenableFuture<MyResult> execute(Session session, ExecutionContext context, Executor executor) {
		Object[] boundParameters = new Object[parametersOrder.length];
		for(int i = 0; i < parametersOrder.length; i++) {
			Object value = context.getParameterValue(parametersOrder[i]);
			boundParameters[i] = value;
		}
		BoundStatement boundStmt = rootStmt.bind(boundParameters);
		ResultSetFuture futureResult = session.executeAsync(boundStmt);
		return Futures.transform(futureResult, result -> {
			List<Row> rows = result.all();
			
			
			for(Row row : rows) {
				if(children != null && children.length > 0) {
					ExecutionContext subcontext = context.createSubContext(row);
					ListenableFuture[] futures = new ListenableFuture[children.length];
					for(int j = 0; j < children.length; j++) {
						futures[j] = children[j].execute(session, subcontext, executor);
					}
					Futures.whenAllComplete(futures).call(() -> {
						return null;
					}, executor);
				}
			}
			return null;
		}, executor);
	}
}
