package graphene.walker.dao.impl;

import graphene.dao.IdTypeDAO;
import graphene.dao.sql.AbstractIdTypeDAO;
import graphene.walker.model.sql.walker.WalkerIdentifierType100;
import graphene.walker.model.sql.walker.QWalkerIdentifierType100;
import graphene.model.idl.G_CanonicalPropertyType;
import graphene.model.query.StringQuery;
import graphene.model.view.entities.IdType;
import graphene.util.validator.ValidationUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.sql.HSQLDBTemplates;
import com.mysema.query.sql.SQLQuery;
import com.mysema.query.sql.SQLTemplates;
import com.mysema.query.types.EntityPath;

/**
 * All implementations requested from the IOC registry are singletons by
 * default, therefore we don't need static members. If for some reason we needed
 * to reload the singleton service, it would refresh the values for us.
 * 
 * @author djue
 * 
 */
public class IdTypeDAOSQLImpl extends AbstractIdTypeDAO<WalkerIdentifierType100>
		implements IdTypeDAO<WalkerIdentifierType100, StringQuery> {
	private SQLQuery buildQuery(StringQuery q, QWalkerIdentifierType100 t,
			Connection conn) throws Exception {
		BooleanBuilder builder = new BooleanBuilder();

		if (ValidationUtils.isValid(q)) {
			builder.and(t.shortName.eq(q.getValue()));
		}

		SQLQuery sq = from(conn, t).where(builder);
		return sq;
	}

	@Override
	public long count(StringQuery q) throws Exception {
		long results = 0;
		QWalkerIdentifierType100 t = new QWalkerIdentifierType100("t");
		Connection conn;
		conn = getConnection();
		SQLQuery sq = buildQuery(q, t, conn).orderBy(t.idtypeId.asc());
		results = sq.count();
		conn.close();
		logger.debug("Counted " + results + " entries");
		return results;
	}

	@Override
	public List<WalkerIdentifierType100> findByQuery(StringQuery q)
			throws Exception {
		List<WalkerIdentifierType100> results = new ArrayList<WalkerIdentifierType100>();

		QWalkerIdentifierType100 t = new QWalkerIdentifierType100("t");
		Connection conn;
		conn = getConnection();
		SQLQuery sq = buildQuery(q, t, conn);
		sq = setOffsetAndLimit(q, sq);
		if (sq != null) {
			sq = sq.orderBy(t.idtypeId.asc());
			results = sq.list(t);
		}
		conn.close();
		if (results != null) {
			logger.debug("Returning " + results.size() + " entries");
		}
		return results;
	}

	@Override
	protected SQLQuery from(Connection conn, EntityPath<?>... o) {
		SQLTemplates dialect = new HSQLDBTemplates(); // SQL-dialect
		return new SQLQuery(conn, dialect).from(o);
	}

	@Override
	public List<WalkerIdentifierType100> getAll(long offset, long maxResults)
			throws Exception {

		List<WalkerIdentifierType100> results;
		QWalkerIdentifierType100 t = new QWalkerIdentifierType100("t");
		Connection conn;
		conn = getConnection();
		SQLQuery sq = from(conn, t);
		results = sq.list(t);
		conn.close();
		if (results != null) {
			logger.debug("Returning " + results.size() + " entries");
		}
		return results;

	}

	@Override
	public IdType convertFrom(WalkerIdentifierType100 id) {
		IdType idType = new IdType();
		idType.setColumnSource(id.getColumnsource());
		idType.setFamily(id.getFamily());
		idType.setIdType_id(id.getIdtypeId());
		idType.setShortName(id.getShortName());
		idType.setTableSource(id.getTablesource());
		//G_CanonicalPropertyType.valueOf(id.getFamily().toUpperCase());
		
		// FIXME: idType does not have a setType() method anymore and it does not have setNodeType(), but it still has getNodeType()
		//idType.setType(G_CanonicalPropertyType.fromValue(id.getFamily()));
		if (idType.getNodeType() == null) {
			logger.warn("G_CanonicalPropertyType for " + idType.toString() + " was null.  This shouldn't happen");
		}
		return idType;
	}

}
