package com.github.victorkendy.lzcassandra.query;

import static com.google.common.util.concurrent.Futures.getUnchecked;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

public class RootQuery {

	private final Session session;
	private final PreparedStatement rootStmt;
	private final List<DependentQuery> dependents;

	public RootQuery(Session session, PreparedStatement rootStmt, List<DependentQuery> dependents) {
		this.session = session;
		this.rootStmt = rootStmt;
		this.dependents = dependents;
	}

	public ListenableFuture<List<CombinedRow>> executeAsync(Object[] objects, int fetchSize, ExecutorService executor) {
		ResultSetFuture resultSet = session.executeAsync(rootStmt.bind(objects).setFetchSize(fetchSize));
		
		return Futures.transformAsync(resultSet, rs -> processResultAsync(rs, fetchSize, executor), executor);
	}
	
	private ListenableFuture<List<CombinedRow>> processResultAsync(ResultSet resultSet, int fetchSize, ExecutorService executor) {
		List<ListenableFuture<List<CombinedRow>>> combinedRows = new ArrayList<>();
		while(!resultSet.isExhausted()) {
			List<Row> rows = nextPage(resultSet, fetchSize);
			rows.forEach(row-> {
				combinedRows.add(createCombinedResultAsync(row, executor));
			});
		}
		return Futures.whenAllComplete(combinedRows).call(() -> {
			List<CombinedRow> result = new ArrayList<>();
			combinedRows.forEach(r -> {
				result.addAll(Futures.getUnchecked(r));
			});
			return result;
		}, executor);
	}

	private ListenableFuture<List<CombinedRow>> createCombinedResultAsync(Row row, ExecutorService executor) {
		Object[] resultLine = new Object[dependents.size() + 1];
		resultLine[0] = row;
		CombinedRow rootRow = new CombinedRow("uc", row);
		ListenableFuture<List<CombinedRow>>[] results = new ListenableFuture[] {
			dependents.get(0).execute(row, resultLine),
			dependents.get(1).execute(row, resultLine)
		};
		
		return Futures.whenAllComplete(results).call(()->{
			List<CombinedRow> combinedRows = new ArrayList<>();
			List<CombinedRow> rows1 = getUnchecked(results[0]);
			List<CombinedRow> rows2 = getUnchecked(results[1]);
			
			for (CombinedRow row1 : rows1) {
				for (CombinedRow row2 : rows2) {
					combinedRows.add(rootRow.combine(row1).combine(row2));
				}
			}
			return combinedRows;
		}, executor);
	}

	private List<Row> nextPage(ResultSet resultSet, int fetchSize) {
		List<Row> rows = new ArrayList<Row>(fetchSize);
		int availableResults = resultSet.getAvailableWithoutFetching();
		for(int i = 0; i < availableResults; i++) {
			rows.add(resultSet.one());
		}
		resultSet.fetchMoreResults();
		return rows;
	}
}
