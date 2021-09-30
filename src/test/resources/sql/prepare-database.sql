delete drom comentario;
delete from tweet;
delete usuario;

INSERT INTO usuario(id, nome) VALUES (1, 'Usuario 1'); 
INSERT INTO usuario(id, nome) VALUES (1, 'Usuario 2');
INSERT INTO usuario(id, nome) VALUES (1, 'Usuario 3');
INSERT INTO usuario(id, nome) VALUES (1, 'Usuario 4');
INSERT INTO usuario(id, nome) VALUES (1, 'Usuario 5');


INSERT INTO TWEET(id, id_usuario, conteudo, data_postagem) VALUES (1,1 'Minha postagem de teste', '2020-04-08 15:45:20');
INSERT INTO TWEET(id, id_usuario, conteudo, data_postagem) VALUES (2,2 'Minha postagem de teste2', '2020-04-08 15:45:20');
INSERT INTO TWEET(id, id_usuario, conteudo, data_postagem) VALUES (3,3 'Minha postagem de teste3', '2020-04-08 15:45:20');

INSERT INTO comentario(id, id_usuario, id_tweet, conteudo, data_postagem) VALUES (1, 4, 1, 'Minha postagem de teste1', '2020-04-08 15:45:20');
INSERT INTO comentario(id, id_usuario, id_tweet, conteudo, data_postagem) VALUES (2, 4, 1, 'Minha postagem de teste2', '2020-04-08 15:45:20');
INSERT INTO comentario(id, id_usuario, id_tweet, conteudo, data_postagem) VALUES (3, 3, 1, 'Minha postagem de teste3', '2020-04-08 15:45:20');
INSERT INTO comentario(id, id_usuario, id_tweet, conteudo, data_postagem) VALUES (4, 2, 1, 'Minha postagem de teste4', '2020-04-08 15:45:20');
INSERT INTO comentario(id, id_usuario, id_tweet, conteudo, data_postagem) VALUES (5, 1, 2, 'Minha postagem de teste5', '2020-04-08 15:45:20');
INSERT INTO comentario(id, id_usuario, id_tweet, conteudo, data_postagem) VALUES (6, 4, 2, 'Minha postagem de teste6', '2020-04-08 15:45:20');
INSERT INTO comentario(id, id_usuario, id_tweet, conteudo, data_postagem) VALUES (7, 4, 3, 'Minha postagem de teste7', '2020-04-08 15:45:20');
INSERT INTO comentario(id, id_usuario, id_tweet, conteudo, data_postagem) VALUES (8, 3, 3, 'Minha postagem de teste8', '2020-04-08 15:45:20');
INSERT INTO comentario(id, id_usuario, id_tweet, conteudo, data_postagem) VALUES (9, 2, 3, 'Minha postagem de teste9', '2020-04-08 15:45:20');
INSERT INTO comentario(id, id_usuario, id_tweet, conteudo, data_postagem) VALUES (10, 1, 3, 'Minha postagem de teste10', '2020-04-08 15:45:20');

select setval('seq_usuario', (select max(id) from usuario));
select setval('seq_tweet', (select max(id) from tweet));
select setval('seq_comentario', (select max(id) from comentario));