package org.filho.liquibase.test.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.postgresql.Driver;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

public class LiquibaseStartup {
	
	private ClassLoader classLoader;

	public LiquibaseStartup(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public void run() throws DatabaseException {
		new Driver();
		
	    Liquibase liquibase = null;
	    Connection c = null;
	    try {
	    	c = DriverManager.getConnection("jdbc:postgresql:test?user=postgres&password=quilts");
	        
	    	Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(c));
	        
	    	ClassLoaderResourceAccessor resourceAcessor = new ClassLoaderResourceAccessor(classLoader);
	    	
	        liquibase = new Liquibase("db/v1.xml", resourceAcessor, database);
	        
	        liquibase.update((String)null);
	        
//	        liquibase.rollback(1, (String)null);
	        
	        liquibase = new Liquibase("db/v2.xml", resourceAcessor, database);
	        
	        liquibase.update((String)null);
	        
	    } catch (SQLException | LiquibaseException e) {
	        throw new DatabaseException(e);
	    } finally {
	        if (c != null) {
	            try {
	                c.rollback();
	                c.close();
	            } catch (SQLException e) {
	                //nothing to do
	            }
	        }
	    }
	}
	
}
