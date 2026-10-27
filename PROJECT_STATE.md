# PROJECT_STATE.md — Estado atual do projeto

Última atualização: 2026-10-27 (sessão 5, depois da primeira exclusão real)

## Projeto

TX9 Booster: app Android (Java, androidx.core só para FileProvider) de manutenção e otimização para o Tanix TX9 (Amlogic S905W, 1 GB de RAM, Android 7.1 API 25, com root). Interface em português, para controle remoto. Pacote br.com.tx9booster.

## Versão atual

0.4.0 (versionCode 13). O dono atualizou pelo botão do próprio app, o que só funciona se a Release v0.4.0 existe. A conferência direta na página Releases não foi respondida em separado.

## O que existe

Tela inicial, Diagnóstico, teste de root, atualização pelo próprio app, tela LIMPEZA com SIMULAR (nunca apaga) e APAGAR CACHE DO APP (exclusão real, só do cache do próprio app).

## Exclusão real (nova na 0.4.0)

- ExclusorCache.java (novo): apaga só dentro das áreas autorizadas pela PoliticaCaminhos. Cada arquivo passa pela trava. Nunca apaga a própria pasta do cache, ignora links simbólicos, soma como "liberado" só o que foi apagado de verdade.
- LimpezaActivity.java: o botão APAGAR CACHE DO APP só liga depois de um SIMULAR com algo para apagar. Abre uma pergunta com o foco em CANCELAR. Depois mostra o relatório: Antes, Apagados, Depois, Falhas, Ignorados (números medidos).
- Textos novos em strings_exclusao.xml. activity_limpeza.xml ganhou o botão btnApagar e o aviso novo (exc_aviso).
- Testada no TX9 pelo dono: ele informou ter feito todos os passos e que o cache ficou em ZERO depois de apagar.
- NÃO conferido: os números de Antes, Apagados e Falhas; que CANCELAR realmente não apaga; que a tela inicial mostrou "Versão 0.4.0" (não foi lida em separado); o texto exato do relatório.
- Verificação do agente (ambiente simulado, não é o TX9): o ExclusorCache foi testado com um script próprio (11 verificações, todas passaram) e o LimpezaActivity foi compilado com classes falsas do Android. A compilação de verdade foi a do GitHub Actions (verde, informado pelo dono).
- NÃO existe ainda teste JUnit do ExclusorCache no GitHub Actions.

## Atualização do app

- Funciona. Testada no TX9 pelo dono: 0.3.7 para 0.3.8, 0.3.8 para 0.3.9 e 0.3.9 para 0.4.0, pelo botão do app, por cima, sem desinstalar.
- Assinatura com chave própria desde a 0.3.7 (tx9booster.jks, alias tx9booster; Secrets TX9_KEYSTORE_BASE64 e TX9_SENHA cadastrados pelo dono).
- Causa exata da recusa antiga NÃO provada. A 0.3.7 (chave própria) instalou por cima da 0.3.5 (chave de depuração), o que não bate com a teoria de assinaturas diferentes.
- A frase nova da mensagem de versão nova ("Aperte de novo o botão VERIFICAR ATUALIZAÇÃO para baixar e instalar. O Android vai pedir a sua confirmação.") está no app desde a 0.3.9, mas o dono ainda NÃO contou o que leu na tela. Não conferida.

## Feito na sessão 5

- README.md e ROADMAP.md corrigidos (antes da exclusão real).
- Versão 0.3.9 e depois 0.4.0 publicadas.
- Teste da tela LIMPEZA em simulação no TX9 (1,4 MB, com aviso de que nada foi apagado).
- Exclusão real do cache do próprio app criada e testada no TX9.

## Pendências

- Atualizar README.md e ROADMAP.md: ainda dizem que a exclusão real não existe e que a versão é 0.3.8.
- Escrever o teste JUnit do ExclusorCache (pasta falsa, link simbólico, área fora da política).
- Pedir ao dono, uma pergunta por vez, os números do relatório, o teste do CANCELAR e a leitura da frase nova.
- O dono precisa guardar uma cópia do tx9booster.jks fora do computador, junto com a senha. Ainda não confirmou que guardou. Se perder, precisa desinstalar o app e criar chave nova.
- debug.keystore.b64 continua na raiz, sem uso. strings_instalacao.xml tem textos sem uso. Os textos simul_titulo e simul_subtitulo podem ainda dizer "simulação": conferir.
- TESTING.md não tem os testes das Etapas 2 e 3 no TX9 (Diagnóstico e root), citados no CHANGELOG. Não inventar.
- Conferir RAM, armazenamento, kernel e ABIs com outra fonte. Testar root com a permissão negada. App sem ícone e sem banner de TV.
- Ideia opcional: publicar um version.json com o SHA-256 do APK e conferir no download.

## Próxima tarefa exata

1. Fechar a documentação da 0.4.0 (CHANGELOG, TESTING, README, ROADMAP), um arquivo por vez.
2. Escrever o ExclusorCacheTest (JUnit) e conferir o verde no Actions.
3. Só depois pensar em mais limpeza (outras áreas). Cache de outros aplicativos só na Etapa 6, depois de validar o método no TX9.
4. Ao subir versão: mudar versionCode e versionName em app/build.gradle E versao_texto em strings.xml. Salvar o strings.xml primeiro e o build.gradle por último (ele dispara a Release).

## Instruções para o próximo agente

- Seguir AGENTS.md. Dono muito iniciante, fala por voz: português simples, UM passo e UM arquivo por vez, arquivo INTEIRO para colar, nunca reescrever de memória (pedir o arquivo atual: "Copy raw file"). Ele pode pedir para ir mais rápido: nesse caso entregue os arquivos de uma tarefa juntos, na ordem certa de salvar, mas sem pular a conferência do resultado.
- As cópias do GitHub que o agente consegue abrir podem estar velhas. Confie no que o dono colar.
- O dono responde "ok" ou número; às vezes escreve palavras trocadas ("geito", "certi"). Se não bater, pergunte de novo com respostas numeradas. "Deu certo" ou "já foi" sozinhos não bastam: peça o que está na tela.
- Se o texto colado falar de canais, filmes ou ExoPlayer, é do outro projeto (asc-tvbox): ignorar e avisar com calma.
- Verde no Actions não prova a Release: confirmar na página Releases.
- Não recomeçar o projeto.
