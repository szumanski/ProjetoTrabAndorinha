package repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import model.Usuario;
import model.exceptions.ErroAoConectarNaBaseExeption;
import model.exceptions.ErroAoConsultarBaseExeption;
import runner.AndorinhaTestRunner;
import runner.DatabaseHelper;


@RunWith(AndorinhaTestRunner.class)
public class TesteUsuarioRepository {
	
	private UsuarioRepository usuarioRepository;
	private static final int ID_USUARIO_CONSULTA = 5;
	private static final int ID_USUARIO_SEM_TWEET = 5;
	
	
	@Before
	public void setUp() {
		DatabaseHelper.getInstance("andorinhaDS").executeSqlScript("sql/prepare-database.sql");
		this.usuarioRepository = new UsuarioRepository();
		
	}
	
	@Test
	public void testa_se_usuario_foi_inserido() throws ErroAoConectarNaBaseExeption, ErroAoConsultarBaseExeption {
		Usuario user = new Usuario();
		user.setNome("Usuario de Teste de Unidade");
		this.usuarioRepository.inserir(user);
		
		Usuario inserido = this.usuarioRepository.consultar(user.getId());
		
		assertThat(inserido).isNotNull();
		assertThat(inserido.getId()).isGreaterThan(0);
		assertThat(inserido.getNome()).isEqualTo(user.getNome());
		assertThat(inserido.getId()).isEqualTo(user.getId());
	}

	@Test
	public void testa_consultar_usuario() throws ErroAoConectarNaBaseExeption, ErroAoConsultarBaseExeption {
		Usuario user = this.usuarioRepository.consultar(ID_USUARIO_CONSULTA);
		
		
		assertThat(user).isNotNull();
		assertThat(user.getNome()).isEqualTo("Usuario 1");
		assertThat(user.getId()).isEqualTo(ID_USUARIO_CONSULTA);
		
	}
	
	@Test
	public void testa_alterar_usuario() throws ErroAoConectarNaBaseExeption, ErroAoConsultarBaseExeption {
		Usuario user = this.usuarioRepository.consultar(ID_USUARIO_CONSULTA);
		user.setNome("Alterado!");
		
		this.usuarioRepository.atualizar(user);
		
		Usuario alterado = this.usuarioRepository.consultar(ID_USUARIO_CONSULTA);
		
		assertThat(alterado).isEqualToComparingFieldByField(user);
		
	}
	
	@Test
	public void testa_remover_usuario() throws ErroAoConectarNaBaseExeption, ErroAoConsultarBaseExeption {
		Usuario user = this.usuarioRepository.consultar(ID_USUARIO_CONSULTA);
		
		assertThat(user).isNotNull();
		
		this.usuarioRepository.remover(ID_USUARIO_CONSULTA);
		
		Usuario removido = this.usuarioRepository.consultar(ID_USUARIO_SEM_TWEET);
		assertThat(removido).isNull();
		
		
	}
	
	
	@Test
	public void testa_remover_usuario_com_tweet() throws ErroAoConectarNaBaseExeption, ErroAoConsultarBaseExeption {
		assertThatThrownBy(()-> {this.usuarioRepository.remover(ID_USUARIO_CONSULTA);})
		.isInstanceOf(ErroAoConsultarBaseExeption.class)
		.hasMessageContaining("Ocorreu um erro ao remover o usuario");		
		
	}
	
	@Test
	public void testa_listar_todos_os_usuarios() throws ErroAoConectarNaBaseExeption, ErroAoConsultarBaseExeption {
		List<Usuario> usuarios = this.usuarioRepository.listarTodos();
		
		assertThat(usuarios).isNotNull()
							.isNotEmpty()
							.hasSize(5)
							.extracting("nome")
							.containsExactlyInAnyOrder("Usuario1", "Usuario2", "Usuario3", "Usuario4", "Usuario5");
		
		
	}
}
