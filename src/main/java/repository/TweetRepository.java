package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import model.Tweet;
import model.exceptions.ErroAoConectarNaBaseExeption;
import model.exceptions.ErroAoConsultarBaseExeption;

public class TweetRepository extends AbstractCrudRepository {

	public void inserir(Tweet tweet) throws ErroAoConectarNaBaseExeption, ErroAoConsultarBaseExeption {
		try(Connection c = this.abrirConexao();) {
			
			int id = this.RecuperarProximoValordaSequence("seq_tweet");
			tweet.setId(id);
			
			PreparedStatement ps = c.prepareStatement("insert into tweet (id, conteudo) values(?, ?)");
			ps.setInt(1, tweet.getId());
			ps.setString(2, tweet.getConteudo());
			ps.execute();
			ps.close();
			
		} catch (SQLException e) {
			throw new ErroAoConsultarBaseExeption("Ocoreu um erro ao inserir tweet", e);
		}
			
		
	}
	
	public void atualizar(Tweet tweet) throws ErroAoConectarNaBaseExeption, ErroAoConsultarBaseExeption {
		try(Connection c = this.abrirConexao();) {
		
		
			
			PreparedStatement ps = c.prepareStatement("update tweet set conteudo = ? where id = ?");
			
			ps.setString(1, tweet.getConteudo());
			ps.setInt(2, tweet.getId());
			ps.execute();
			ps.close();
			
		} catch (SQLException e) {
			throw new ErroAoConsultarBaseExeption("Ocoreu um erro ao atualizar tweet", e);
		}
	}
	
	public void remover(int id) throws ErroAoConectarNaBaseExeption, ErroAoConsultarBaseExeption {
		try(Connection c = this.abrirConexao();) {
		
		
			
			PreparedStatement ps = c.prepareStatement("delete tweet where id = ?");
			
			ps.setInt(1, id);
			ps.execute();
			ps.close();
			
		} catch (SQLException e) {
			throw new ErroAoConsultarBaseExeption("Ocoreu um erro ao deletar tweet", e);
		}
	}
	
	public Tweet consultar(int id) throws ErroAoConectarNaBaseExeption, ErroAoConsultarBaseExeption {
		try(Connection c = this.abrirConexao();) {
			
			Tweet tweet = null;
			PreparedStatement ps = c.prepareStatement("select id, conteudo from tweet where id = ?");
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				tweet = new Tweet();
				tweet.setId(rs.getInt("id"));
				tweet.setConteudo(rs.getString("conteudo"));
				
			}
			rs.close();
			ps.close();		
	
			return tweet;
			
		} catch (SQLException e) {
			throw new ErroAoConsultarBaseExeption("Ocoreu um erro ao consultar tweet", e);
		}
	}
	
	public List<Tweet> listarTodos() {
		return null;
	}
	
}
