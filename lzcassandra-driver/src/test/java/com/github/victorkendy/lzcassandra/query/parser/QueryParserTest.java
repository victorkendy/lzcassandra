package com.github.victorkendy.lzcassandra.query.parser;

import static org.junit.Assert.*;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;

public class QueryParserTest {
	@Test
	public void shouldExecuteSimpleSelect() throws Exception {
		String query = "SELECT a.key FROM tablea a";
		CharStream stream = CharStreams.fromString(query);
		EcqlLexer lexer = new EcqlLexer(stream);
		CommonTokenStream tokenStream = new CommonTokenStream(lexer);
		EcqlParser parser = new EcqlParser(tokenStream);
		ParseTree tree = parser.statement();
	}
}
