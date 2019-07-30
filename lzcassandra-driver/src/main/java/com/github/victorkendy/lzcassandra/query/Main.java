package com.github.victorkendy.lzcassandra.query;

import static com.google.common.util.concurrent.Futures.getUnchecked;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.google.common.util.concurrent.ListenableFuture;

public class Main {
	static ExecutorService executor = Executors.newFixedThreadPool(10);
	public static void main(String[] args) {
		Cluster cluster = Cluster.builder().addContactPoint("localhost").withPort(9042).withCredentials("cassandra", "").build();
		Session session = cluster.connect("tyrion");
		// select * from user_collection uc 
		//   join user u on uc.user_key=u.key
		//   join collection c on uc.collection_key=c.key
		// where
		//   uc.user_key=?
		String userKey="887EE";
		int fetchSize = 1;
		List<DependentQuery> dependents = Arrays.asList(
			new DependentQuery(session, session.prepare("select * from collection where key=?"), "c",
				new ParamBinder[] { row -> row.getObject("collection_key") }, 1),
			new DependentQuery(session, session.prepare("select * from user where key=?"), "u",
				new ParamBinder[] { row -> row.getObject("user_key") }, 2)
		);
		
		PreparedStatement rootStmt = session.prepare("select * from user_collection where user_key=?");
		RootQuery rootQuery = new RootQuery(session, rootStmt, dependents);
		ListenableFuture<List<CombinedRow>> futureRows = rootQuery.executeAsync(new Object[] {userKey}, fetchSize, executor);
		
		Instant t1 = Instant.now();
		List<CombinedRow> result = getUnchecked(futureRows);
		System.out.println(Duration.between(t1, Instant.now()).toMillis());
		result.forEach(row -> {
			System.out.println(row.get("u", "name") + " --- " + row.get("c", "name"));
		});
		session.close();
		cluster.close();
		executor.shutdown();
	}
}
