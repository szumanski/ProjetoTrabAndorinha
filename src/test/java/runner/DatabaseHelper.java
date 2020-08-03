package runner;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;

public class DatabaseHelper {

	Logger logger = LogManager.getRootLogger();

	private Connection connection;
	private DatabaseConnection dbUnit;

	private DatabaseHelper() {
	}

	/**
	 * Retorna uma instância do Helper para o banco de dados H2 em memória
	 * @return instância do Helper
	 */
	public static DatabaseHelper getH2Instance() {

		DatabaseHelper helper = new DatabaseHelper();

		try {
			Properties p = helper.loadProperties("conf/datasources.properties");

			//embeeded?
			String embeeded = null;
			Set<Object> keys = p.keySet();
			for ( Object o: keys ) {
				String key = o.toString();
				if ( key.endsWith("embeeded") ) {
					embeeded = p.getProperty(key);
					break;
				}
			}

			Class.forName("org.h2.Driver");

			if (embeeded != null) {
				String dir = System.getProperty("user.dir") + File.separator + "target/database" + File.separator + embeeded;
				helper.connection = DriverManager.getConnection("jdbc:h2:" + dir + ";DB_CLOSE_DELAY=-1;MODE=ORACLE", "sa", "");
			}
			else {
				helper.connection = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=ORACLE", "sa", "");
			}

			helper.dbUnit = new DatabaseConnection(helper.connection);

			DatabaseConfig config = helper.dbUnit.getConfig();

			config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new H2DataTypeFactory());
			config.setProperty(DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES, true);

			return helper;
		} catch (Exception e) {
			throw new RuntimeException("Erro inicializando DBUnit", e);
		}
	}

	public static DatabaseHelper getInstance( String jndi ) {
		DatabaseHelper helper = new DatabaseHelper();
		try {

			//busca os datasource.properties
			Properties p = helper.loadProperties("conf/datasources.properties");

			Class.forName( p.getProperty(jndi + ".JdbcDriver") );
			helper.connection = DriverManager.getConnection( p.getProperty(jndi + ".JdbcUrl"),
					p.getProperty(jndi + ".userName"),
					p.getProperty(jndi + ".password") );
			String schema = p.containsKey(jndi + ".schema") ? p.getProperty(jndi + ".schema") : p.getProperty(jndi + ".userName");
			helper.dbUnit = new DatabaseConnection(helper.connection, schema);

			DatabaseConfig config = helper.dbUnit.getConfig();

			config.setProperty( DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new PostgresqlDataTypeFactory() );
			config.setProperty(DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES, true);

			return helper;
		} catch (Exception e) {
			throw new RuntimeException("Erro inicializando DBUnit", e);
		}
	}

	/**
	 * Executa um dataset na base de dados, de acordo com a operação informada <br>
	 * O path do dataset deve ser relativo ao diretório 'src/test/resources'
	 *
	 * @param dataset dataset em XML
	 * @param operation operação a ser executada
	 * @return helper após execução do dataset
	 */
	public DatabaseHelper execute(String dataset, DatabaseOperation operation) {
		try {
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream( this.removeBarraInicio(dataset) );
			if ( is == null) {
				throw new FileNotFoundException("Arquivo dataset não encontrado: " + dataset);
			}

			FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
			builder.setColumnSensing(true);
			IDataSet dataSet = builder.build(is);

			operation.execute(dbUnit, dataSet);
		} catch (Exception e) {
			throw new RuntimeException("Erro executando DbUnit", e);
		}
		return this;
	}

	/**
	 * Executa um script SQL na base de dados <br>
	 * O path deste script deve ser relativo ao diretório 'src/test/resources'
	 *
	 * @param script caminho do script sql
	 * @return helper após execução do Script
	 */
	public DatabaseHelper executeSqlScript(String script) {
		try {
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream( this.removeBarraInicio(script) );
			if ( is == null) {
				throw new FileNotFoundException("Arquivo sql não encontrado: " + script);
			}

			List<String> comandos = this.loadCommandsFromSqlFile(is);

			for ( String s: comandos ) {
				if ( s != null ) {
					Statement st = connection.createStatement();
					logger.info( "Executando: "  + s );
					st.execute(s);
				}
			}

		} catch (Exception e) {
			throw new RuntimeException("Erro executando DbUnit", e);
		}
		return this;
	}

	public void close() {
		try {
			dbUnit.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private Properties loadProperties(String file) throws IOException {
		Properties p =  new Properties();
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream( this.removeBarraInicio(file) );
		if (in == null) {
			throw new IOException("Não foi possível carregar o arquivo " + file + " durante o DatabaseHelper: stream nulo" );
		}
		p.load(in);
		in.close();
		return p;
	}

	/**
	 * Este método remove uma barra "/" de um path inicial de resource
	 * @param path caminho do resource
	 * @return caminho do resource sem "/" no início
	 */
	private String removeBarraInicio(String path) {
		if( path != null && path.startsWith("/") ) {
			return path.substring(1);
		}
		return path;
	}

	/**
	 * Lê um arquivo SQL e retorna uma lista de comandos em String
	 * @param is stream do SQL
	 * @return lista de comandos em String
	 */
	private List<String> loadCommandsFromSqlFile(InputStream is) {
		List<String> linhas = new ArrayList<>();

		StringBuilder sb = new StringBuilder();
		StringBuilder sbFunction = new StringBuilder();

		InputStreamReader isr = null;
		BufferedReader br = null;
		String line;
		try {
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);

			boolean isFunction = false;

			while ((line = br.readLine()) != null) {
				// comentario
				if (line.startsWith("--")){
					continue;
				}

				//TODO: sintaxe de procedure Oracle
				if (line.startsWith("CREATE FUNCTION")){
					isFunction = true;
					sbFunction.append(line);
					sbFunction.append("\n");
					continue;
				}

				if (line.startsWith("END;;")){
					isFunction = false;
					sbFunction.append(line.replace(";;", ""));
					linhas.add(sbFunction.toString());
					sbFunction = new StringBuilder();
					continue;
				}

				if (isFunction){
					sb.append(" ");
					sbFunction.append(line);
					sbFunction.append("\n");
				}
				else{
					//fim do comando
					if (line.trim().endsWith(";")){
						sb.append(line);
						linhas.add(sb.toString());
						sb = new StringBuilder();
					}
					else{
						sb.append(line);
						sb.append(" ");
					}
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			//silent close resources
			if (isr != null) {
				try {isr.close();} catch (IOException e) {}
			}
			if (br != null) {
				try {br.close();} catch (IOException e) {}
			}
		}
		return linhas;
	}

}