package graphene.walker.model;

import graphene.dao.sql.DBConnectionPoolService;
import graphene.dao.sql.util.JDBCUtil;
import graphene.model.idl.G_SymbolConstants;
import graphene.util.db.MainDB;
import graphene.util.db.SecondaryDB;

import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.internal.services.ClasspathResourceSymbolProvider;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.apache.tapestry5.ioc.services.SymbolSource;
import org.slf4j.Logger;

/**
 * A module used specifically for the generation of DTO Objects. This may need
 * closer integration with the ingest modules. Note that right now this class
 * uses the same database.properties as the application does for running.
 * Potentially these could be different if the user you log in with can't read
 * the metadata needed by QueryDSL.
 * 
 * @author djue
 * 
 */
public class DTOGenerationModule {
	@Contribute(SymbolSource.class)
	public void contributePropertiesFileAsSymbols(
			OrderedConfiguration<SymbolProvider> configuration) {
		configuration.add("DatabaseConfiguration",
				new ClasspathResourceSymbolProvider("database.properties"),
				"after:SystemProperties", "before:ApplicationDefaults");
	}
	
	public static void bind(ServiceBinder binder) {
		// Make bind() calls on the binder object to define most IoC services.
		// Use service builder methods (example below) when the implementation
		// is provided inline, or requires more initialization than simply
		// invoking the constructor.
		// binder.bind(JavaDiskCache.class);
		binder.bind(JDBCUtil.class).eagerLoad();
	}

	/**
	 * Note that when injecting a String symbol you must also add the @Inject
	 * annotation in addition to the @Symbol annotation.
	 * 
	 * 
	 * @param serverUrl
	 * @param username
	 * @param userPassword
	 * @param logger
	 * @param util
	 * @return
	 */
	@Marker(MainDB.class)
	public static DBConnectionPoolService buildMainDBConnectionPool(
			@Inject @Symbol(G_SymbolConstants.MIDTIER_SERVER_URL) String serverUrl,
			@Inject @Symbol(G_SymbolConstants.MIDTIER_SERVER_USERNAME) String username,
			@Inject @Symbol(G_SymbolConstants.MIDTIER_SERVER_PASSWORD) String userPassword,
			final Logger logger, JDBCUtil util) {
		try {
			return new DBConnectionPoolService(logger, util, serverUrl,
					username, userPassword, true);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	/**
	 * Builder for a second database, if needed. Note that when injecting a
	 * String symbol you must also add the @Inject annotation in addition to the @Symbol
	 * annotation.
	 * 
	 * 
	 * @param serverUrl
	 * @param username
	 * @param userPassword
	 * @param logger
	 * @param util
	 * @return
	 */
	@Marker(SecondaryDB.class)
	public static DBConnectionPoolService buildSecondaryConnectionPool(
			@Inject @Symbol(G_SymbolConstants.MIDTIER_SERVER_URL) String serverUrl,
			@Inject @Symbol(G_SymbolConstants.MIDTIER_SERVER_USERNAME) String username,
			@Inject @Symbol(G_SymbolConstants.MIDTIER_SERVER_PASSWORD) String userPassword,
			final Logger logger, JDBCUtil util) {
		try {
			return new DBConnectionPoolService(logger, util, serverUrl,
					username, userPassword, true);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}
}
