// Generated from Ecql.g4 by ANTLR 4.7.2
package com.github.victorkendy.lzcassandra.query.parser;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link EcqlParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface EcqlVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link EcqlParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(EcqlParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link EcqlParser#select}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect(EcqlParser.SelectContext ctx);
	/**
	 * Visit a parse tree produced by {@link EcqlParser#select_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_list(EcqlParser.Select_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link EcqlParser#from_specification}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFrom_specification(EcqlParser.From_specificationContext ctx);
}