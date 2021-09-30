package repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.xml.registry.infomodel.User;

import model.Usuario;
import model.exceptions.ErroAoConectarNaBaseExeption;
import model.exceptions.ErroAoConsultarBaseExeption;

public class UsuarioRepository extends AbstractCrudRepository{
	
	
	
	public void inserir(Usuario usuario) throws ErroAoConectarNaBaseExeption, ErroAoConsultarBaseExeption {
		try(Connection c = this.abrirConexao();) {
			
			int id = this.RecuperarProximoValordaSequence("seq_usuario");
			usuario.setId(id);
			
			PreparedStatement ps = c.prepareStatement("insert into usuario (id, nome) values(?, ?)");
			ps.setInt(1, usuario.getId());
			ps.setString(2, usuario.getNome());
			ps.execute();
			ps.close();
			
		} catch (SQLException e) {
			throw new ErroAoConsultarBaseExeption("Ocoreu um erro ao inserir usuario", e);
		}
			
		
	}
	
	public void atualizar(Usuario usuario) throws ErroAoConectarNaBaseExeption, ErroAoConsultarBaseExeption {
		try(Connection c = this.abrirConexao();) {
		
		
			
			PreparedStatement ps = c.prepareStatement("update usuario set nome = ? where id = ?");
			
			ps.setString(1, usuario.getNome());
			ps.setInt(2, usuario.getId());
			ps.execute();
			ps.close();
			
		} catch (SQLException e) {
			throw new ErroAoConsultarBaseExeption("Ocoreu um erro ao atualizar usuario", e);
		}
	}
	
	public void remover(int id) throws ErroAoConectarNaBaseExeption, ErroAoConsultarBaseExeption {
		try(Connection c = this.abrirConexao();) {
		
		
			
			PreparedStatement ps = c.prepareStatement("delete usuario where id = ?");
			
			ps.setInt(1, id);
			ps.execute();
			ps.close();
			
		} catch (SQLException e) {
			throw new ErroAoConsultarBaseExeption("Ocoreu um erro ao deletar usuario", e);
		}
	}
	
	public Usuario consultar(int id) throws ErroAoConectarNaBaseExeption, ErroAoConsultarBaseExeption {
		try(Connection c = this.abrirConexao();) {
			
			Usuario user = null;
			PreparedStatement ps = c.prepareStatement("select id, nome from usuario where id = ?");
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				user = new Usuario();
				user.setId(rs.getInt("id"));
				user.setNome(rs.getString("nome"));
				
			}
			rs.close();
			ps.close();		
	
			return user;
			
		} catch (SQLException e) {
			throw new ErroAoConsultarBaseExeption("Ocoreu um erro ao consultar usuario", e);
		}
	}
	
	public List<Usuario> listarTodos() {
		return null;
	}

}
