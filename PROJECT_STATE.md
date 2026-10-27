# PROJECT_STATE.md — Estado atual do projeto

Última atualização: 2026-10-26 (fim da sessão 2)

## Objetivo

Criar o TX9 Booster: app Android de manutenção e otimização para o Tanix TX9 (1 GB de RAM, root). Diagnóstico, limpeza segura, otimização rápida, tudo medido de verdade, com interface para controle remoto.

## Etapa atual

Etapa 4 — Limpeza segura. A simulação existe e compilou, mas ainda NÃO foi testada no TX9. Nenhuma exclusão real foi implementada.

## Última tarefa concluída

Na sessão 2 o dono salvou no GitHub, um arquivo por vez: Analisador.java, AnalisadorTest.java, strings_simulacao.xml, activity_limpeza.xml, LimpezaActivity.java, AndroidManifest.xml (com a nova tela) e MainActivity.java (botão LIMPEZA AVANÇADA agora abre a tela nova). Depois de cada commit o dono informou check verde na aba Actions.

O que o agente conferiu com os próprios olhos: a execução #27 (antes do código novo) estava verde, com as tarefas "Testes" e "Compilar APK" bem-sucedidas. Das execuções seguintes, o agente só tem o relato do dono (a leitura do GitHub pelo agente mostrava uma lista antiga).

## Funcionalidades implementadas

- Tela inicial com 5 botões grandes, navegação por controle remoto e foco amarelo visível.
- Botão DIAGNÓSTICO abre a tela de diagnóstico (fabricante, modelo, placa, Android e API, kernel, ABIs, RAM, armazenamento) e o teste de root (comando fixo "su -c id", limite de 15 segundos, só ao apertar o botão).
- Botão LIMPEZA AVANÇADA abre a tela de simulação (LimpezaActivity). Ao apertar SIMULAR LIMPEZA, analisa em segundo plano o cache interno e o cache externo do próprio app e mostra quantos arquivos e quantos bytes seriam liberados, os primeiros 10 arquivos e quantos itens a trava pulou. NÃO apaga nada: o código não tem nenhum comando de apagar.
- Os botões OTIMIZAÇÃO, APLICATIVOS e HISTÓRICO só mostram "Esta função ainda não foi criada."
- PoliticaCaminhos: avalia se um caminho pode ser apagado. Só permite o que está dentro de área autorizada. Lista proibida vence. Resolve ".." e links simbólicos. Descarta áreas autorizadas perigosas (como /, /data, /system). Não apaga nada. Agora é usada pelo Analisador.
- Analisador: somente leitura. Só percorre áreas que a PoliticaCaminhos autoriza, soma bytes reais, não segue links simbólicos, tem limites (20000 arquivos, profundidade 20, 100 itens na lista).
- Compilação automática pelo GitHub Actions, com chave de depuração fixa e pública (debug.keystore.b64).

## Funcionalidades ainda não implementadas

Limpeza real (exclusão), otimização rápida, lista de aplicativos, histórico, ícone e banner do app.

## O que foi compilado e testado

- Compilado pelo GitHub Actions: versões 0.1.0 e 0.2.0 (check verde informado pelo dono) e a execução #27 (visto pelo agente). O código novo da sessão 2 compilou segundo o dono (check verde informado).
- PoliticaCaminhosTest: a tarefa "Testes" da execução #27 passou (visto pelo agente).
- AnalisadorTest (6 testes, JUnit): rodou na tarefa "Testes" do GitHub Actions e passou, segundo o dono (check verde informado). O agente escreveu os testes mas não os executou.
- Testado no TX9 real pelo dono (informado por ele): Etapa 2 e Etapa 3. Ver TESTING.md.
- A tela LIMPEZA AVANÇADA NÃO foi aberta no TX9.

## Descobertas no aparelho real (informadas pelo dono)

- Android: a tela mostra "10.1" com API 25. A API 25 é a real (Android 7.1). Usar a API 25 nas decisões.
- Root: o teste retornou "ROOT FUNCIONANDO".

## Erros conhecidos e pendências

- Ao instalar a versão 0.2.0, o dono precisou desinstalar a antiga antes. Causa desconhecida, apesar da chave fixa. Perguntar a mensagem exata do Android na próxima atualização.
- A versão do app continua 0.2.0 (versionCode 2), embora o APK novo tenha a tela de simulação. Considerar subir para 0.3.0 (versionCode 3) para o dono distinguir os APKs. Não foi feito porque o app/build.gradle não foi lido nesta sessão.
- Os valores de RAM, armazenamento, kernel e ABIs aparecem na tela, mas o dono ainda não os comparou com outra fonte.
- Teste de root com a permissão negada ainda não foi feito.
- O app ainda não tem ícone nem banner para o launcher de TV.
- O repositório está Public (confirmado pelo agente ao abrir a página sem login).
- A simulação só olha o cache do próprio app. Não sabemos ainda quanto cache o app tem de verdade; pode aparecer 0 bytes, o que é normal.
- Avisos do GitHub Actions (não são erros): Node.js 20 obsoleto e setup-java v4 obsoleto. Podem exigir ajuste no workflow no futuro.

## Decisões técnicas

- Java, sem AndroidX, sem banco de dados, sem serviço em segundo plano. JUnit 4.13.2 só para testes (não entra no APK).
- minSdk 21, targetSdk 28, compileSdk 34. Pacote: br.com.tx9booster. versionCode 2, versionName 0.2.0.
- Sem Gradle wrapper. O GitHub Actions usa Java 17 e Gradle 8.5. Duas tarefas no workflow: "Testes" (gradle testDebugUnitTest) e "Compilar APK" (gradle assembleDebug). Se os testes falham, o APK ainda é gerado, mas a execução fica vermelha.
- Root: sempre comandos fixos, com limite de tempo e checagem do código de saída. Nunca montar comando com texto do usuário.
- A limpeza só pode apagar o que PoliticaCaminhos permitir. Área inicial: apenas o cache e os arquivos temporários do próprio app (getCacheDir e getExternalCacheDir). Cache de outros aplicativos só depois de validar o método no TX9 (Etapa 6).
- Os textos da tela de simulação ficam em strings_simulacao.xml (nomes começam com simul_), separado do strings.xml.
- Chave de depuração fixa e pública. Assinatura privada fica para a Etapa 9.

## Arquivos do projeto

AGENTS.md, PROJECT_STATE.md, ROADMAP.md, CHANGELOG.md, TESTING.md, README.md, settings.gradle, build.gradle, gradle.properties, debug.keystore.b64, app/build.gradle, app/src/main/AndroidManifest.xml, app/src/main/res/values/strings.xml, app/src/main/res/values/strings_simulacao.xml, app/src/main/res/layout/activity_main.xml, app/src/main/res/layout/activity_diagnostico.xml, app/src/main/res/layout/activity_limpeza.xml, app/src/main/res/drawable/botao_fundo.xml, app/src/main/java/br/com/tx9booster/MainActivity.java, app/src/main/java/br/com/tx9booster/DiagnosticoActivity.java, app/src/main/java/br/com/tx9booster/LimpezaActivity.java, app/src/main/java/br/com/tx9booster/PoliticaCaminhos.java, app/src/main/java/br/com/tx9booster/Analisador.java, app/src/test/java/br/com/tx9booster/PoliticaCaminhosTest.java, app/src/test/java/br/com/tx9booster/AnalisadorTest.java, .github/workflows/build.yml.

## Próxima tarefa exata

1. Pedir ao dono que abra a aba Actions e diga se a execução mais recente está verde ou vermelha. Se houver X, pedir o texto da linha vermelha.
2. Ler app/build.gradle e subir a versão para 0.3.0 (versionCode 3), para o dono saber qual APK está instalado.
3. O dono baixa o APK da execução mais recente (README.md explica), instala no TX9, abre LIMPEZA AVANÇADA, aperta SIMULAR LIMPEZA e conta o que a tela mostrou. Se a atualização por cima falhar, anotar a mensagem exata do Android. Apertar o botão uma segunda vez e conferir que os números não mudam (nada foi apagado).
4. Só depois de a simulação ser testada no TX9: planejar a exclusão real, apenas do cache do próprio app, com confirmação do dono, usando PoliticaCaminhos, com relatório de bytes realmente liberados.

## Instruções especiais para o próximo agente

- Dono muito iniciante, fala por voz (ditado), usa só o site do GitHub, sem terminal. Siga AGENTS.md: português simples, UM passo e UM arquivo por vez, uma pergunta por vez com respostas numeradas, arquivo INTEIRO para colar, nunca reescrever código de memória, nunca pedir senha, chave ou link M3U.
- O dono costuma responder "ok" ou "1". Confirme o resultado antes de seguir quando a resposta for ambígua.
- Na sessão 2 o agente conseguiu ler páginas públicas do GitHub (documentos .md, aba Actions e páginas de commit), mas pastas e alguns arquivos de código não abriram. O dono colou à mão MainActivity.java, activity_diagnostico.xml e AndroidManifest.xml. Tente ler o código pelo link do arquivo; se falhar, peça para colar. Confira o repositório real antes de assumir qualquer coisa deste arquivo.
- Não recomeçar o projeto.
