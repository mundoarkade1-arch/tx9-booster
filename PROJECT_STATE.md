# PROJECT_STATE.md — Estado atual do projeto

Última atualização: 2026-10-26 (fim da sessão 1)

## Objetivo
Criar o TX9 Booster: app Android de manutenção e otimização para o Tanix TX9 (1 GB de RAM, root). Diagnóstico, limpeza segura, otimização rápida, tudo medido de verdade, com interface para controle remoto.

## Etapa atual
Etapa 4 — Limpeza segura, no começo. Só análise e simulação. Nenhuma exclusão real foi implementada.

## Última tarefa concluída
O dono salvou no GitHub: PoliticaCaminhos.java (trava de segurança dos caminhos), PoliticaCaminhosTest.java (teste JUnit), app/build.gradle com JUnit e o workflow com duas tarefas (Testes e Compilar APK). O resultado dessa última compilação NÃO foi conferido.

## Funcionalidades implementadas
- Tela inicial com 5 botões grandes, navegação por controle remoto e foco amarelo visível.
- Botão DIAGNÓSTICO abre a tela de diagnóstico (fabricante, modelo, placa, Android e API, kernel, ABIs, RAM, armazenamento) e o teste de root (comando fixo "su -c id", limite de 15 segundos, só ao apertar o botão). Os outros 4 botões só mostram "Esta função ainda não foi criada."
- PoliticaCaminhos: avalia se um caminho pode ser apagado. Só permite o que está dentro de área autorizada. Lista proibida vence. Resolve ".." e links simbólicos. Descarta áreas autorizadas perigosas (como /, /data, /system). Não apaga nada. Ainda não é usada por nenhuma tela.
- Compilação automática pelo GitHub Actions, com chave de depuração fixa e pública (debug.keystore.b64).

## Funcionalidades ainda não implementadas
Analisador de arquivos (somente leitura), tela de limpeza com simulação, limpeza real, otimização rápida, lista de aplicativos, histórico, ícone e banner do app.

## O que foi compilado e testado
- Compilado pelo GitHub Actions: versões 0.1.0 e 0.2.0 (check verde informado pelo dono). A compilação mais recente (com testes e JUnit) não foi conferida.
- Testado no TX9 real pelo dono (informado por ele): Etapa 2 e Etapa 3. Ver TESTING.md.
- PoliticaCaminhos: o agente rodou 19 verificações num ambiente simulado (Linux, script próprio, fora do GitHub). Todas passaram. O arquivo JUnit equivalente foi escrito mas NÃO foi executado pelo agente, porque o ambiente dele não baixa o JUnit. Nada disso foi testado no TX9.

## Descobertas no aparelho real (informadas pelo dono)
- Android: a tela mostra "10.1" com API 25. A API 25 é a real (Android 7.1). Usar a API 25 nas decisões.
- Root: o teste retornou "ROOT FUNCIONANDO".

## Erros conhecidos e pendências
- Ao instalar a versão 0.2.0, o dono precisou desinstalar a antiga antes. Causa desconhecida, apesar da chave fixa. Perguntar a mensagem exata do Android na próxima atualização.
- Os valores de RAM, armazenamento, kernel e ABIs aparecem na tela, mas o dono ainda não os comparou com outra fonte.
- Teste de root com a permissão negada ainda não foi feito.
- O app ainda não tem ícone nem banner para o launcher de TV.
- Se o repositório está Public ou Private: o dono não confirmou.
- README.md está desatualizado: ainda diz que não existe aplicativo nem APK.
- Documentação atrasada: CHANGELOG.md, TESTING.md e ROADMAP.md ainda não registram a Etapa 4 (PoliticaCaminhos, JUnit, workflow com duas tarefas, verificações do agente).

## Decisões técnicas
- Java, sem AndroidX, sem banco de dados, sem serviço em segundo plano. JUnit 4.13.2 só para testes (não entra no APK).
- minSdk 21, targetSdk 28, compileSdk 34. Pacote: br.com.tx9booster. versionCode 2, versionName 0.2.0.
- Sem Gradle wrapper. O GitHub Actions usa Java 17 e Gradle 8.5. Duas tarefas no workflow: "Testes" (gradle testDebugUnitTest) e "Compilar APK" (gradle assembleDebug). Se os testes falham, o APK ainda é gerado, mas a execução fica vermelha.
- Root: sempre comandos fixos, com limite de tempo e checagem do código de saída. Nunca montar comando com texto do usuário.
- A limpeza só pode apagar o que PoliticaCaminhos permitir. Área inicial prevista: apenas o cache e os arquivos temporários do próprio app. Cache de outros aplicativos só depois de validar o método no TX9 (Etapa 6).
- Chave de depuração fixa e pública. Assinatura privada fica para a Etapa 9.

## Arquivos do projeto
AGENTS.md, PROJECT_STATE.md, ROADMAP.md, CHANGELOG.md, TESTING.md, README.md, settings.gradle, build.gradle, gradle.properties, debug.keystore.b64, app/build.gradle, app/src/main/AndroidManifest.xml, app/src/main/res/values/strings.xml, app/src/main/res/layout/activity_main.xml, app/src/main/res/layout/activity_diagnostico.xml, app/src/main/res/drawable/botao_fundo.xml, app/src/main/java/br/com/tx9booster/MainActivity.java, app/src/main/java/br/com/tx9booster/DiagnosticoActivity.java, app/src/main/java/br/com/tx9booster/PoliticaCaminhos.java, app/src/test/java/br/com/tx9booster/PoliticaCaminhosTest.java, .github/workflows/build.yml.

## Próxima tarefa exata
1. Pedir ao dono que abra a aba Actions do repositório e diga se a execução mais recente está com check verde ou X vermelho. Se houver X, pedir o texto da linha vermelha (tarefa "Testes" ou "Compilar APK") e corrigir.
2. Atualizar CHANGELOG.md, TESTING.md, ROADMAP.md e README.md com o que está em "Documentação atrasada".
3. Continuar a Etapa 4: criar um Analisador somente leitura que usa PoliticaCaminhos e lista o cache e os arquivos temporários do próprio app com bytes reais. Criar a tela LIMPEZA AVANÇADA com modo de simulação (mostra o que seria apagado, não apaga). Só depois de a simulação ser testada no TX9 pelo dono, planejar a exclusão real.

## Instruções especiais para o próximo agente
- Dono muito iniciante, fala por voz (ditado), usa só o site do GitHub, sem terminal. Siga AGENTS.md: português simples, UM passo e UM arquivo por vez, uma pergunta por vez com respostas numeradas, arquivo INTEIRO para colar, nunca reescrever código de memória, nunca pedir senha, chave ou link M3U.
- O dono costuma responder "ok" ou "1". Confirme o resultado antes de seguir quando a resposta for ambígua.
- Na sessão 1 o agente não teve acesso ao GitHub do dono. Tudo foi colado à mão pelo dono. Confira o repositório real antes de assumir qualquer coisa deste arquivo.
- Não recomeçar o projeto.
