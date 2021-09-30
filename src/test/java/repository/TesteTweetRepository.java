package repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import model.Usuario;
import model.exceptions.ErroAoConectarNaBaseExeption;
import model.exceptions.ErroAoConsultarBaseExeption;
import runner.DatabaseHelper;

public class TesteTweetRepository {
	
	private UsuarioRepository usuarioRepository;
	private static final int ID_TWEET_CONSULTA = 5;
	private static final int ID_USUARIO_SEM_TWEET = 5;
	
	
	@Before
	public void setUp() {
		DatabaseHelper.getInstance("andorinhaDS").executeSqlScript("sql/prepare-database.sql");
		this.usuarioRepository = new UsuarioRepository();
		
	}
	
	@Test
	public void testa_se_tweet_foi_inserido() throws ErroAoConectarNaBaseExeption, ErroAoConsultarBaseExeption {
		Usuario user = new Usuario();
		user.setNome("Tweet de Teste de Unidade");
		this.usuarioRepository.inserir(user);
		
		Usuario inserido = this.usuarioRepository.consultar(user.getId());
		
		assertThat(inserido).isNotNull();
		assertThat(inserido.getId()).isGreaterThan(0);
		assertThat(inserido.getNome()).isEqualTo(user.getNome());
		assertThat(inserido.getId()).isEqualTo(user.getId());
	}

	@Test
	public void testa_consultar_tweet() throws ErroAoConectarNaBaseExeption, ErroAoConsultarBaseExeption {
		Usuario user = this.usuarioRepository.consultar(ID_TWEET_CONSULTA);
		
		
		assertThat(user).isNotNull();
		assertThat(user.getNome()).isEqualTo("Usuario 1");
		assertThat(user.getId()).isEqualTo(ID_TWEET_CONSULTA);
		
	}
	
	@Test
	public void testa_alterar_tweet() throws ErroAoConectarNaBaseExeption, ErroAoConsultarBaseExeption {
		Usuario user = this.usuarioRepository.consultar(ID_TWEET_CONSULTA);
		user.setNome("Alterado!");
		
		this.usuarioRepository.atualizar(user);
		
		Usuario alterado = this.usuarioRepository.consultar(ID_TWEET_CONSULTA);
		
		assertThat(alterado).isEqualToComparingFieldByField(user);
		
	}
	
	@Test
	public void testa_remover_tweet() throws ErroAoConectarNaBaseExeption, ErroAoConsultarBaseExeption {
		Usuario user = this.usuarioRepository.consultar(ID_TWEET_CONSULTA);
		
		assertThat(user).isNotNull();
		
		this.usuarioRepository.remover(ID_TWEET_CONSULTA);
		
		Usuario removido = this.usuarioRepository.consultar(ID_USUARIO_SEM_TWEET);
		assertThat(removido).isNull();
		
		
	}
	
	
	@Test
	public void testa_listar_todos_os_tweet() throws ErroAoConectarNaBaseExeption, ErroAoConsultarBaseExeption {
		List<Usuario> usuarios = this.usuarioRepository.listarTodos();
		
		assertThat(usuarios).isNotNull()
							.isNotEmpty()
							.hasSize(5)
							.extracting("nome")
							.containsExactlyInAnyOrder("Usuario1", "Usuario2", "Usuario3", "Usuario4", "Usuario5");
		
		
	}

}
