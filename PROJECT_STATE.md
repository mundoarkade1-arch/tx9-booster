# PROJECT_STATE.md — Estado atual do projeto

Última atualização: 2026-10-27 (sessão 6, depois da versão 0.4.1)

## Projeto

TX9 Booster: app Android (Java, androidx.core só para FileProvider) de manutenção e otimização para o Tanix TX9 (Amlogic S905W, 1 GB de RAM, Android 7.1 API 25, com root). Interface em português, para controle remoto. Pacote br.com.tx9booster.

## Versão atual

0.4.1 (versionCode 14). O dono informou Actions verde e Release 0.4.1 na página Releases. NÃO conferido no TX9: instalação da 0.4.1 e os textos novos na tela. A 0.4.1 só mudou textos (strings_simulacao.xml e versao_texto); nenhum código Java mudou desde a 0.4.0.

## O que existe

Tela inicial, Diagnóstico, teste de root, atualização pelo próprio app, tela LIMPEZA com SIMULAR (nunca apaga) e APAGAR CACHE DO APP (exclusão real, só do cache do próprio app, com confirmação e relatório Antes/Apagados/Depois).

## Exclusão real (0.4.0)

- ExclusorCache.java: apaga só dentro das áreas autorizadas pela PoliticaCaminhos, cada arquivo passa pela trava, nunca apaga a pasta do cache, ignora links simbólicos, soma como "liberado" só o que foi apagado.
- Testada no TX9 pelo dono: cache ficou em ZERO.
- NÃO conferido: números de Antes, Apagados, Depois e Falhas (o dono adiou); que CANCELAR não apaga (ainda não perguntado); texto exato do relatório; a frase da mensagem de versão nova (ainda não perguntado).
- ExclusorCacheTest (JUnit, 7 testes, pastas falsas) criado na sessão 6. O dono informou verde no Actions. Não é teste no TX9. Não se sabe se o teste do link simbólico rodou ou foi pulado.

## Textos da tela LIMPEZA (0.4.1)

simul_subtitulo: "Simular e apagar o cache do app". simul_aviso: "O botão SIMULAR só mede e mostra. Ele não apaga nada." Nada mais mudou em strings_simulacao.xml. strings_exclusao.xml foi lido e está certo.

## Atualização do app

- Funciona. Testada no TX9: 0.3.7 para 0.3.8, 0.3.8 para 0.3.9 e 0.3.9 para 0.4.0, pelo botão, por cima. A 0.4.1 ainda não foi instalada no TX9 (o dono testa depois).
- Assinatura com chave própria desde a 0.3.7 (tx9booster.jks, alias tx9booster; Secrets TX9_KEYSTORE_BASE64 e TX9_SENHA).
- Causa exata da recusa antiga NÃO provada.

## Chave de assinatura (RISCO ABERTO)

O dono colou o arquivo e a senha só nos Secrets do GitHub (que não podem ser lidos de volta) e disse que NÃO tem a senha em mãos. Cópia fora do GitHub: não confirmada. Dicas já dadas: não guardar a chave no GitHub (repositório público); procurar a senha na conversa da sessão 4, no histórico do PowerShell, em anotações. Se não achar: criar chave nova, recadastrar os dois Secrets e desinstalar/instalar o app uma vez. O dono adiou o assunto. Tocar no assunto de novo, com calma, em algum momento.

## Pendências

- README.md e ROADMAP.md: o PROJECT_STATE antigo dizia que ainda falavam em versão 0.3.8 e sem exclusão real, mas o CHANGELOG da sessão 5 diz que foram reescritos. NÃO lidos na sessão 6: pedir os textos e conferir.
- Perguntar ao dono, uma por vez: números do relatório, CANCELAR, frase da mensagem de versão nova, e o resultado da 0.4.1 no TX9.
- Chave de assinatura (acima).
- debug.keystore.b64 na raiz, sem uso. strings_instalacao.xml tem textos sem uso.
- TESTING.md não tem os testes das Etapas 2 e 3 no TX9. Não inventar.
- Conferir RAM, armazenamento, kernel e ABIs com outra fonte. Testar root com a permissão negada. App sem ícone e sem banner de TV.
- Ideia opcional: version.json com SHA-256 do APK.

## Próxima tarefa exata

1. Pedir README.md e ROADMAP.md e conferir se estão atuais (0.4.1, exclusão real existe).
2. Perguntas ao dono (uma por vez) sobre o que ele viu no TX9.
3. Só depois pensar em limpar outras áreas. Cache de outros aplicativos só na Etapa 6, depois de validar o método no TX9. Nada de limpeza automática ao abrir o app.
4. Ao subir versão: salvar strings.xml (versao_texto) primeiro e app/build.gradle (versionCode e versionName) por último (ele dispara a Release).

## Instruções para o próximo agente

- Seguir AGENTS.md. Dono muito iniciante, fala por voz: português simples, UM passo por vez, arquivo INTEIRO para colar, nunca reescrever de memória (pedir o arquivo atual). Se pedir rapidez: entregue os arquivos da tarefa juntos, na ordem certa de salvar, sem pular a conferência.
- As cópias do GitHub que o agente abre podem estar velhas. Confie no que o dono colar.
- O dono responde "ok" ou número; às vezes escreve palavras trocadas. Se não bater, pergunte de novo com respostas numeradas. "Deu certo" sozinho não basta.
- Se o texto falar de canais, filmes ou ExoPlayer, é do outro projeto (asc-tvbox): ignorar e avisar com calma.
- Verde no Actions não prova a Release: confirmar na página Releases.
- Não recomeçar o projeto.
