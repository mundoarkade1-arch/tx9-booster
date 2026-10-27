# CHANGELOG.md — Histórico de alterações

Cada alteração relevante entra aqui, da mais nova para a mais antiga.

## 2026-10-27 — Sessão 5: documentos corrigidos e versão 0.3.9

- README.md reescrito: versão 0.3.8, instalação pela página Releases, atualização pelo botão do próprio app, chave de assinatura própria. A seção antiga sobre instalar pelo zip do Actions saiu.
- ROADMAP.md reescrito: seção "Atualização do app" com o resultado do teste no TX9 e as pendências; Etapa 9 em desenvolvimento (chave própria já feita).
- strings_atualizacao.xml: a frase da mensagem de versão nova (atu_nova) agora diz "Aperte de novo o botão VERIFICAR ATUALIZAÇÃO para baixar e instalar. O Android vai pedir a sua confirmação." No texto antigo estava "Toque no botão VERIFICAR ATUALIZAÇÃO para baixar."
- 0.3.9: versionCode 12 e versionName 0.3.9 em app/build.gradle; versao_texto em strings.xml mudou para "Versão 0.3.9 (limpeza simulada)". O strings.xml foi salvo antes do build.gradle, para a Release sair já com o texto certo. Release v0.3.9 confirmada na página Releases, com o .apk.
- Atualização por cima da 0.3.8 para a 0.3.9, pelo botão do próprio app, FUNCIONOU no TX9 e a tela inicial mostrou "Versão 0.3.9". Foi a primeira prova com mudança real de código depois da chave própria.
- NÃO conferido: o dono não leu a frase nova da mensagem de versão nova na tela.
- Causa exata do erro antigo continua NÃO provada.
- Atualizados PROJECT_STATE.md, CHANGELOG.md e TESTING.md.
- As cópias do GitHub que o agente conseguiu abrir estavam velhas (README e PROJECT_STATE antigos). O agente usou o texto que o dono colou.

## 2026-10-27 — Sessão 4: chave própria de assinatura (versões 0.3.5 a 0.3.8)

- Lido o build.yml. Todas as tarefas usavam o mesmo APK, assinado com a chave pública de depuração. A hipótese "cada tarefa usa uma chave" não se confirmou.
- 0.3.5 e 0.3.6: só aumento de versão. O dono desinstalou o app, instalou a 0.3.5 pela página Releases e tentou atualizar para a 0.3.6 por cima. O Android recusou de novo: "O app não foi instalado. Já existe um pacote com esse mesmo nome."
- O dono criou a chave própria no PowerShell (arquivo tx9booster.jks, alias tx9booster) e cadastrou sozinho os Secrets TX9_KEYSTORE_BASE64 e TX9_SENHA no GitHub. O agente não viu os valores.
- 0.3.7: build.yml reescrito (a tarefa "Compilar APK" instala a chave própria a partir dos Secrets e falha se faltarem; não usa mais debug.keystore.b64). app/build.gradle ganhou signingConfigs "proprio", usado no build debug quando as variáveis TX9_KEYSTORE_ARQUIVO e TX9_KEYSTORE_SENHA existem. A 0.3.7 instalou por cima da 0.3.5 sem desinstalar.
- 0.3.8: strings.xml corrigido (versao_texto estava escrito à mão como 0.3.4). Atualização por cima pelo botão do próprio app, da 0.3.7 para a 0.3.8, FUNCIONOU no TX9 e a tela inicial mostrou "Versão 0.3.8".
- Causa exata do erro antigo NÃO provada. O problema sumiu com a chave própria.
- CHANGELOG.md: a entrada da sessão 3 estava no fim do arquivo, com um "- " na frente do título. Foi movida para o lugar certo.
- Atualizados PROJECT_STATE.md e CHANGELOG.md.

## 2026-10-27 — Sessão 3: atualização pelo próprio app (versões 0.3.1 a 0.3.4)

- 0.3.1: rodapé da tela inicial corrigido para a versão certa.
- 0.3.2: o botão VERIFICAR ATUALIZAÇÃO passa a baixar o APK sem abrir o navegador (primeira versão, pelo gerenciador de downloads do Android). Textos novos em strings_instalacao.xml e permissão REQUEST_INSTALL_PACKAGES.
- 0.3.3: download refeito no jeito do app asc-tvbox: classe InstaladorAtualizacao, FileProvider, file_paths.xml, androidx.core 1.12.0 e android.useAndroidX=true. Mostra a porcentagem do download.
- 0.3.4: só aumento de versão, para testar a atualização por cima.
- PROBLEMA ABERTO (resolvido na sessão 4): o Android recusava instalar por cima ("já existe um pacote com esse mesmo nome"). Ver PROJECT_STATE.md.
- Criados AGENTS.md, PROJECT_STATE.md, ROADMAP.md, CHANGELOG.md, TESTING.md e README.md.
- Decisão: versão mínima do Android 5.0 (API 21), para servir ao Android 7.1 e ao 10.

## 2026-10-26 — Sessão 2: atualização do app (versão 0.3.0) e documentação

- Conferido o repositório real: já existiam Analisador (somente leitura) e a tela LIMPEZA em modo de simulação (LimpezaActivity). A documentação estava atrasada e foi corrigida.
- AnalisadorTest ampliado de 6 para 10 testes (política nula, limite de 100 itens com totais completos, parada por profundidade, link simbólico de pasta). Os 6 antigos não foram alterados.
- Erro do agente, corrigido: criou AnalisadorArquivos.java e AnalisadorArquivosTest.java, duplicados de arquivos que já existiam; os dois foram apagados depois de o agente ler o código real. Nenhum código funcional foi perdido.
- Nova função, aprovada pelo dono: atualização do app. AndroidManifest.xml ganhou a permissão INTERNET. Novo VerificadorAtualizacao.java (consulta a Release mais recente do repositório, só https e só endereços do repositório, tempo limite de 8 segundos, resposta limitada, sem redirecionamentos). Novo VerificadorAtualizacaoTest.java (11 testes, sem internet). Novo strings_atualizacao.xml. activity_main.xml ganhou o botão VERIFICAR ATUALIZAÇÃO (btnAtualizar) e uma linha de aviso escondida (txtAtualizacao). MainActivity.java ganhou a checagem silenciosa ao abrir o app e o botão. O app não instala nada sozinho.
- app/build.gradle: versionCode 3, versionName 0.3.0.
- .github/workflows/build.yml: terceira tarefa "Publicar Release" (depende de Testes e Compilar APK, só em push no main; cria a Release vX.Y.Z com o APK se ela ainda não existir). As duas tarefas antigas não foram alteradas.
- O dono informou check verde depois de cada mudança, inclusive nos três blocos após o build.yml. O agente não viu as telas.
- NÃO confirmado: a Release v0.3.0 existe de fato; a função de atualização no TX9; a tela LIMPEZA no TX9.
- AGENTS.md: a regra "não depender de internet" ganhou a exceção aprovada da verificação de atualização.
- Atualizados PROJECT_STATE.md, TESTING.md, ROADMAP.md, README.md e AGENTS.md.
- Nenhuma função de exclusão real foi criada.

## 2026-10-26 — Etapa 4 (início): trava de segurança da limpeza

- Criada a classe PoliticaCaminhos: decide se um caminho pode ser apagado. Só permite o que está dentro de uma área autorizada, a lista proibida vence, resolve ".." e links simbólicos e descarta áreas perigosas (/, /data, /system). Só avalia, nunca apaga.
- Criado o teste JUnit PoliticaCaminhosTest.
- app/build.gradle: adicionada a dependência de teste JUnit 4.13.2 (não entra no APK).
- Workflow do GitHub Actions agora tem duas tarefas: Testes (gradle testDebugUnitTest) e Compilar APK (gradle assembleDebug).
- Nenhuma função de exclusão real existe ainda.

## 2026-10-26 — Etapa 3: diagnóstico (versão 0.2.0)

- Nova tela de Diagnóstico: fabricante, modelo, placa, Android e API, kernel, ABIs, RAM (total, disponível, em uso), armazenamento (total, livre, usado).
- Nova seção Root: procura arquivo su em locais comuns e tem o botão TESTAR ACESSO ROOT, que roda só o comando fixo "su -c id", com limite de 15 segundos.
- O botão DIAGNÓSTICO da tela inicial abre a nova tela.
- Versão do app: versionCode 2, versionName 0.2.0.
- Testado no TX9 pelo dono: a tela abre com os dados, o Android aparece como "10.1" com API 25 (Android 7.1 real) e o teste de root retornou "ROOT FUNCIONANDO".
- Problema em aberto: o dono precisou desinstalar o app antigo antes de instalar a 0.2.0. Causa desconhecida (suspeita: a 0.1.0 foi compilada antes da chave fixa).

## 2026-10-26 — Assinatura fixa de depuração

- Adicionado o arquivo debug.keystore.b64 (chave de depuração pública, só para testes) e um passo no GitHub Actions que a instala antes de compilar, para que o APK sempre saia com a mesma assinatura.

## 2026-10-26 — Etapa 2: estrutura mínima

- Criado o projeto Android mínimo em Java: tela inicial com 5 botões grandes, navegação por controle remoto e foco amarelo visível.
- Os botões ainda só mostravam o aviso "Esta função ainda não foi criada."
- Criada a compilação automática do APK de depuração pelo GitHub Actions (Java 17, Gradle 8.5, compileSdk 34).
- APK instalado e aberto no TX9 pelo dono, que informou que a navegação funcionou.

## 2026-10-26 — Etapa 1: planejamento

- Repositório tx9-booster criado pelo dono no GitHub.
