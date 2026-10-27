# PROJECT_STATE.md — Estado atual do projeto

Última atualização: 2026-10-26 (sessão 2)

## Objetivo

Criar o TX9 Booster: app Android de manutenção e otimização para o Tanix TX9 (1 GB de RAM, root). Diagnóstico, limpeza segura, otimização rápida, tudo medido de verdade, com interface para controle remoto.

## Etapa atual

Etapa 4 — Limpeza segura. A análise e a tela de simulação JÁ EXISTEM no código. Falta confirmar a simulação no TX9 real. Nenhuma exclusão real foi implementada.

## Última tarefa concluída

Na sessão 2 o agente conferiu o código real e descobriu que a documentação estava atrasada: o Analisador (Analisador.java) e a tela de simulação (LimpezaActivity.java, ligada ao botão LIMPEZA da tela inicial) já existiam. O AnalisadorTest.java foi ampliado de 6 para 10 testes (novos: política nula, limite de 100 itens na lista, parada por profundidade, link simbólico de pasta). O dono informou que a execução do GitHub Actions ficou com check verde (Testes e Compilar APK) depois dessa mudança.

## Funcionalidades implementadas

- Tela inicial com 5 botões grandes, navegação por controle remoto e foco amarelo visível.
- Botão DIAGNÓSTICO abre a tela de diagnóstico (fabricante, modelo, placa, Android e API, kernel, ABIs, RAM, armazenamento) e o teste de root (comando fixo "su -c id", limite de 15 segundos, só ao apertar o botão).
- Botão LIMPEZA abre a LimpezaActivity (tela LIMPEZA AVANÇADA, modo de simulação): botão simular, mede o cache interno e o cache externo do próprio app com o Analisador, mostra arquivos, bytes, ignorados, aviso de limite e os 10 maiores itens. Só lê, nunca apaga. Roda numa thread separada.
- Os botões Otimização, Aplicativos e Histórico só mostram "Esta função ainda não foi criada."
- PoliticaCaminhos: avalia se um caminho pode ser apagado. Só permite o que está dentro de área autorizada. Lista proibida vence. Resolve ".." e links simbólicos. Descarta áreas autorizadas perigosas (como /, /data, /system). Não apaga nada. É usada pelo Analisador.
- Analisador: somente leitura. Só analisa área autorizada pela PoliticaCaminhos. Ignora links simbólicos. Limites: 20000 arquivos, 20 níveis de pasta, 100 itens na lista.
- Compilação automática pelo GitHub Actions, com chave de depuração fixa e pública (debug.keystore.b64).

## Funcionalidades ainda não implementadas

Limpeza real (exclusão), otimização rápida, lista de aplicativos, histórico, ícone e banner do app, verificação de atualização do app.

## O que foi compilado e testado

- Compilado pelo GitHub Actions: versões 0.1.0 e 0.2.0 (check verde informado pelo dono). A execução mais recente, com a tarefa Testes e a tarefa Compilar APK, está verde (informado pelo dono na sessão 2).
- Testado no TX9 real pelo dono (informado por ele): Etapa 2 e Etapa 3. Ver TESTING.md.
- Testes automáticos no GitHub Actions (verde, informado pelo dono): PoliticaCaminhosTest e AnalisadorTest (10 testes, com pastas falsas temporárias).
- PoliticaCaminhos: o agente também rodou 19 verificações num ambiente simulado (Linux, script próprio).
- NÃO CONFIRMADO: a tela LIMPEZA (simulação) rodando no TX9 real. Não se sabe se o dono já a abriu.

## Descobertas no aparelho real (informadas pelo dono)

- Android: a tela mostra "10.1" com API 25. A API 25 é a real (Android 7.1). Usar a API 25 nas decisões.
- Root: o teste retornou "ROOT FUNCIONANDO".

## Erros conhecidos e pendências

- Ao instalar a versão 0.2.0, o dono precisou desinstalar a antiga antes. Causa desconhecida, apesar da chave fixa. Perguntar a mensagem exata do Android na próxima atualização.
- Os valores de RAM, armazenamento, kernel e ABIs aparecem na tela, mas o dono ainda não os comparou com outra fonte.
- Teste de root com a permissão negada ainda não foi feito.
- O app ainda não tem ícone nem banner para o launcher de TV.
- Falta confirmar no TX9 que o botão LIMPEZA abre a tela e que a simulação mostra números plausíveis. O app novo ainda não foi instalado no TX9 com essa tela, pelo que se sabe.
- A versão do app continua 0.2.0 (versionCode 2), mesmo com a tela de limpeza no código. Decidir a versão antes do próximo APK para o TX9.
- O agente da sessão 2 consegue ler os arquivos na raiz do repositório pelo site, mas não abre arquivos dentro de pastas. Para ler esses arquivos, pedir ao dono que cole o conteúdo.
- Erro do agente na sessão 2, já corrigido: confiou neste arquivo desatualizado, criou AnalisadorArquivos.java e AnalisadorArquivosTest.java (duplicados do Analisador e do AnalisadorTest) e depois apagou os dois. Lição: conferir o código real antes de criar qualquer arquivo.

## Ideia adiada: atualização do app

O dono quer que o app se atualize ao abrir ou tenha um botão VERIFICAR ATUALIZAÇÃO. Decisão do dono na sessão 2: deixar para depois e continuar a Etapa 4 primeiro. Pontos já levantados: (1) AGENTS.md diz que o app não depende de internet e não faz nada automático, então o recomendado é só um botão que consulta ao apertar; checar ao abrir exige o dono aprovar a mudança dessa regra. (2) O APK hoje fica só na aba Actions; seria preciso publicar cada versão em Releases do GitHub (ainda não existe nenhuma). (3) Antes, resolver o problema de atualizar por cima sem desinstalar. O app não deve instalar sozinho: só avisar e abrir o download, com confirmação do Android.

## Decisões técnicas

- Java, sem AndroidX, sem banco de dados, sem serviço em segundo plano. JUnit 4.13.2 só para testes (não entra no APK).
- minSdk 21, targetSdk 28, compileSdk 34. Pacote: br.com.tx9booster. versionCode 2, versionName 0.2.0.
- Sem Gradle wrapper. O GitHub Actions usa Java 17 e Gradle 8.5. Duas tarefas no workflow: "Testes" (gradle testDebugUnitTest) e "Compilar APK" (gradle assembleDebug). Se os testes falham, o APK ainda é gerado, mas a execução fica vermelha.
- Root: sempre comandos fixos, com limite de tempo e checagem do código de saída. Nunca montar comando com texto do usuário.
- A limpeza só pode apagar o que PoliticaCaminhos permitir. Área inicial: apenas o cache e os arquivos temporários do próprio app (getCacheDir e getExternalCacheDir). Cache de outros aplicativos só depois de validar o método no TX9 (Etapa 6).
- Chave de depuração fixa e pública. Assinatura privada fica para a Etapa 9.

## Arquivos do projeto

AGENTS.md, PROJECT_STATE.md, ROADMAP.md, CHANGELOG.md, TESTING.md, README.md, settings.gradle, build.gradle, gradle.properties, debug.keystore.b64, app/build.gradle, app/src/main/AndroidManifest.xml, app/src/main/res/values/strings.xml, app/src/main/res/layout/activity_main.xml, app/src/main/res/layout/activity_diagnostico.xml, app/src/main/res/layout/activity_limpeza.xml (existência deduzida do código, não aberto), app/src/main/res/drawable/botao_fundo.xml, app/src/main/java/br/com/tx9booster/MainActivity.java, DiagnosticoActivity.java, LimpezaActivity.java, PoliticaCaminhos.java, Analisador.java, app/src/test/java/br/com/tx9booster/PoliticaCaminhosTest.java, AnalisadorTest.java, .github/workflows/build.yml.

## Próxima tarefa exata

1. Atualizar CHANGELOG.md, TESTING.md e ROADMAP.md com o mesmo conteúdo corrigido (um arquivo por vez). README.md: conferir se menciona a tela de limpeza.
2. Perguntar ao dono se já testou a tela LIMPEZA no TX9. Se não, preparar a versão 0.3.0 (versionCode 3) e pedir o teste: instalar o APK novo, abrir LIMPEZA, apertar simular e contar o que aparece (números, mensagens, travamentos). Anotar a mensagem exata do Android se a atualização por cima falhar.
3. Só depois de a simulação ser confirmada no TX9: planejar a exclusão real, apenas do cache do próprio app, com confirmação do usuário e relatório de bytes antes e depois.

## Instruções especiais para o próximo agente

- Dono muito iniciante, fala por voz (ditado), usa só o site do GitHub, sem terminal. Siga AGENTS.md: português simples, UM passo e UM arquivo por vez, uma pergunta por vez com respostas numeradas, arquivo INTEIRO para colar, nunca reescrever código de memória, nunca pedir senha, chave ou link M3U.
- Combinado com o dono: responder "1" significa "salvei e a execução ficou verde". Só aceitar depois de a execução parar de ficar amarela. Se vier "11" ou algo ambíguo, confirmar.
- Antes de criar QUALQUER arquivo de código, conferir se ele já existe (o GitHub avisa "A file with the same name already exists"). A documentação já ficou atrasada uma vez e causou arquivos duplicados.
- Confira o repositório real antes de assumir qualquer coisa deste arquivo. Arquivos dentro de pastas precisam ser colados pelo dono.
- Não recomeçar o projeto.
