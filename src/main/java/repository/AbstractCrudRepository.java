package repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.exceptions.ErroAoConectarNaBaseExeption;
import model.exceptions.ErroAoConsultarBaseExeption;

public abstract class AbstractCrudRepository {

	protected Connection abrirConexao() throws ErroAoConectarNaBaseExeption{
		try {
			
		
			return DriverManager.getConnection("jdbc:postgresql://localhost/andorinha_teste", "postgres", "postgres");

		} catch (SQLException e) {
			throw new ErroAoConectarNaBaseExeption("Ocoreu um erro ao acessar a base de dados", e);
		}
	}
	
	
	protected int RecuperarProximoValordaSequence(String NomeSequence) throws ErroAoConectarNaBaseExeption, ErroAoConsultarBaseExeption {
		
		try(Connection c = this.abrirConexao();) {
			
		
			PreparedStatement ps = c.prepareStatement("select nextval(?)");
			ps.setString(1, NomeSequence);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				return rs.getInt(1);
			}
			throw new ErroAoConsultarBaseExeption("Erro ao recuperar proximo valor da sequence" + NomeSequence, null);
		} catch (SQLException e) {
			throw new ErroAoConectarNaBaseExeption("Ocoreu um erro ao acessar a base de dados", e);
		}
		
	}
	
}
