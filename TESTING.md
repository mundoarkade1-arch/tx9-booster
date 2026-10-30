# TESTING.md — Testes e resultados

Só entram aqui testes que foram realmente feitos. Cada teste diz onde foi feito: ambiente simulado, GitHub Actions ou aparelho real (TX9).

## Resultados até agora

### 2026-10-30 — Núcleo root, Histórico e Laboratório (Codespaces e Actions)

- JUnit executado manualmente com JDK 17 e JUnit 4.13.2 para `ComandosRoot`, `RootShell`, `Historico` e `Reversor`: 16 testes passaram, cobrindo validação contra injeção, lista de chaves de leitura, confirmação de root, política padrão que nega pacotes, registro antes da ação, persistência do Histórico, reversão individual e total e recusa quando o estado diverge.
- `gradle testDebugUnitTest` foi tentado no Codespaces e não iniciou: falta Android SDK (`ANDROID_HOME` ou `sdk.dir`). Isso não é falha de teste. A compilação/execução Gradle completa depende do Actions.
- `gh run watch 41062735314`: workflow do commit `786cee33dd4f23044c0abd32342f9ce1bf477f1b` terminou com sucesso. Esse commit antecedeu a adição do 16º teste e a última alteração no texto do Histórico; o push da versão 0.7.0 deve validar a árvore final.
- Nenhuma tela ou função da Etapa A foi testada no TX9. Nenhum resultado de aparelho foi informado pelo dono nesta etapa.

### 2026-10-27 — Sessão 6: textos da limpeza corrigidos, versão 0.4.1 (informado pelo dono)

O agente não viu o aparelho nem o GitHub; o resultado foi informado pelo dono, que respondeu a perguntas numeradas.

- strings_simulacao.xml salvo com dois textos novos: simul_subtitulo "Simular e apagar o cache do app" e simul_aviso "O botão SIMULAR só mede e mostra. Ele não apaga nada." O dono informou Actions verde depois de salvar.
- Versão 0.4.1 publicada: strings.xml (versao_texto "Versão 0.4.1 (textos da limpeza corrigidos)") salvo primeiro e build.gradle (versionCode 14, versionName 0.4.1) por último. O dono informou Actions verde e que a Release 0.4.1 apareceu na página Releases.
- NÃO conferido no TX9: instalar a 0.4.1 pelo botão do app; que a tela inicial mostra "Versão 0.4.1 (textos da limpeza corrigidos)"; que a tela LIMPEZA mostra o subtítulo e o aviso novos. O dono disse que testa depois.
- NÃO conferido: os números de Antes, Apagados, Depois e Falhas do relatório. O dono adiou e vai testar depois, com calma.
- NÃO perguntado ainda: se CANCELAR não apaga; o que a frase da mensagem de versão nova diz na tela.
- Chave de assinatura: o dono disse que colou o arquivo e a senha só nos Secrets do GitHub e que NÃO tem a senha em mãos. Os Secrets não podem ser lidos de volta. Cópia fora do GitHub: NÃO confirmada. O dono adiou o assunto. Se perder o arquivo ou a senha, precisa desinstalar o app e criar chave nova.

### 2026-10-27 — Sessão 6: ExclusorCacheTest (GitHub Actions, informado pelo dono)

- ExclusorCacheTest.java (JUnit 4.13.2, em app/src/test/java/br/com/tx9booster/) salvo pelo dono. O agente leu o teste e o ExclusorCache.java colados pelo dono e conferiu que os nomes batem (areaValida, arquivosApagados, bytesLiberados, falhas, ignorados). O agente não rodou o teste.
- Resultado: o dono informou Actions VERDE na execução do arquivo. Isso é pasta falsa em ambiente do Actions, não é o TX9.
- 7 testes: apaga arquivos e subpastas e mantém a pasta do cache; soma só os bytes apagados (35); pasta vazia continua válida e não apaga nada; área fora da política não apaga nada; link simbólico para fora é ignorado; área nula ou inexistente não apaga nada; política nula não apaga nada.
- NÃO conferido: se o teste do link simbólico rodou de verdade ou foi pulado (ele pula, não falha, se o ambiente não deixar criar link). O PoliticaCaminhosTest cria link sem pular e passa, o que sugere que o ambiente aceita, mas isso não prova.
- Esse teste não cria Release (a versão não muda).

### 2026-10-27 — Sessão 5: exclusão real do cache do app, versão 0.4.0 (TX9 real, informado pelo dono)

O agente não viu o aparelho; o resultado foi informado pelo dono, que respondeu a perguntas numeradas.

- Versão 0.4.0 publicada: o dono informou verde no Actions depois de salvar os arquivos da exclusão e de subir a versão. A conferência direta da Release v0.4.0 na página Releases não foi respondida em separado; o dono atualizou o app pelo botão, o que só funciona com a Release existente.
- Teste no TX9: o dono respondeu que fez todos os passos (atualizar pelo botão, abrir LIMPEZA AVANÇADA, SIMULAR, APAGAR CACHE DO APP, confirmar). Resultado informado: o cache do app ficou em ZERO depois de apagar. Esta é a primeira exclusão real do projeto.
- NÃO conferido: os números de Antes, Apagados e Falhas do relatório; que apertar CANCELAR na pergunta realmente não apaga nada; que a tela inicial mostrou "Versão 0.4.0" (não foi lida em separado); o texto exato do relatório na tela.
- NÃO conferido: a frase nova da mensagem de versão nova ("Aperte de novo o botão VERIFICAR ATUALIZAÇÃO para baixar e instalar. O Android vai pedir a sua confirmação."). O dono não contou o que leu.
- NÃO conferido: que o app continua normal depois da limpeza em outras telas (Diagnóstico, atualização).

### 2026-10-27 — Sessão 5: verificação do agente da exclusão real (ambiente simulado, NÃO é o TX9)

- ExclusorCache testado com um script próprio do agente (Java, pastas falsas temporárias, fora do GitHub). 11 verificações, todas passaram: área válida; 3 arquivos apagados; 35 bytes liberados (soma certa); sem falhas; a própria pasta do cache continua existindo; arquivo apagado some; arquivo de fora da área fica intacto; link simbólico para fora é ignorado; área não autorizada: nada é apagado; área nula; política nula.
- LimpezaActivity compilado pelo agente com classes falsas do Android (confere erros de escrita do código, não prova que funciona no Android).
- Estes testes NÃO estão no repositório. (Na sessão 6 foi criado o ExclusorCacheTest JUnit no GitHub Actions, ver acima.)

### 2026-10-27 — Sessão 5: tela LIMPEZA em simulação no TX9, versão 0.3.9 (TX9 real, informado pelo dono)

O agente não viu o aparelho; o resultado foi informado pelo dono, que respondeu a perguntas numeradas.

- O dono abriu LIMPEZA AVANÇADA, apertou simular e a tela mostrou números. O valor que ele informou: "1,4 MB". Ele também informou que a tela diz que nada foi apagado.
- Pelo código do LimpezaActivity (lido pelo agente), o 1,4 MB é o total geral: cache interno mais cache externo do próprio app.
- NÃO conferido: se a tela mostrou lista de arquivos; que o valor bate com o tamanho real do cache (Configurações do Android, Aplicativos, TX9 Booster).
- Nada foi apagado: nessa versão a tela era só simulação.

### 2026-10-27 — Sessão 5: atualização por cima com mudança real de código, 0.3.8 para 0.3.9 (TX9 real, informado pelo dono)

O agente não viu o aparelho; o resultado foi informado pelo dono, que respondeu a perguntas numeradas.

- Versão 0.3.9 publicada: o dono informou verde no Actions e a Release v0.3.9 existe na página Releases, com o .apk. O strings.xml (versao_texto) foi salvo antes do build.gradle, para a Release sair já com o texto certo.
- 0.3.8 -> 0.3.9 pelo botão VERIFICAR ATUALIZAÇÃO do próprio app, por cima, sem desinstalar: OK. O dono disse "deu certo" e confirmou que a tela inicial mostra "Versão 0.3.9".
- Esta foi a "próxima prova" pedida na sessão 4: atualização por cima com mudança real de código, já com a chave própria. Passou.
- A causa exata do erro antigo continua NÃO provada. Duas atualizações por cima seguidas funcionaram com a chave própria (0.3.7 para 0.3.8 e 0.3.8 para 0.3.9), mas isso não explica por que a 0.3.7 entrou por cima da 0.3.5.
- Sobre a pendência anterior ("guardar o tx9booster.jks"): ainda não foi confirmado pelo dono que ele guardou a cópia da chave.

### 2026-10-27 — Sessão 4: atualização por cima com chave própria (TX9 real, informado pelo dono)

O agente não viu o aparelho; as telas foram descritas pelo dono (ou mostradas em foto).

- build.yml lido: as três tarefas usavam o mesmo APK, assinado com a chave pública de depuração. A hipótese "cada tarefa usa uma chave" não se confirmou.
- 0.3.5 instalada pela página Releases, depois de desinstalar o app antigo: OK.
- 0.3.5 -> 0.3.6 por cima (APK da página Releases, chave de depuração): FALHOU. Mensagem exata do Android: "O app não foi instalado. Já existe um pacote com esse mesmo nome." (tela com o botão CONCLUÍDO).
- Chave própria criada pelo dono (tx9booster.jks, alias tx9booster) e Secrets TX9_KEYSTORE_BASE64 e TX9_SENHA cadastrados por ele. GitHub Actions verde com a 0.3.7; Release 0.3.7 foi publicada (o dono informou a versão 0.3.7 nas configurações do Android).
- 0.3.7 instalada por cima da 0.3.5 pela página Releases, sem desinstalar: OK (a Configurações do Android mostrou a versão 0.3.7).
- Tela inicial mostrava "Versão 0.3.4" mesmo na 0.3.7: o texto versao_texto estava escrito à mão em strings.xml. Corrigido na 0.3.8.
- 0.3.7 -> 0.3.8 pelo botão VERIFICAR ATUALIZAÇÃO do próprio app (download, tela de instalação do Android, confirmação): OK. A tela inicial mostrou "Versão 0.3.8". Release v0.3.8 conferida pelo dono na página Releases.
- Conclusão: a atualização por cima FUNCIONA com a chave própria. A causa exata do erro antigo NÃO foi provada.
- Não confirmado: que o APK da 0.3.5 (chave de depuração) e o da 0.3.7 (chave própria) tenham assinaturas diferentes. O Android aceitou a 0.3.7 por cima da 0.3.5, o que não bate com a teoria de assinaturas diferentes entre elas. Não se sabe explicar isso ainda.
- Próxima prova: publicar uma versão nova (0.3.9 ou a seguinte) com mudança real e atualizar por cima de novo pelo botão do app. (Feita na sessão 5, com a 0.3.9.)

### 2026-10-27 — Sessão 3: testes da atualização no TX9 (informados pelo dono)

- Release v0.3.0, v0.3.2 e v0.3.3 existem com o .apk (conferido pelo dono na página Releases).
- Checagem de atualização no TX9: acha a versão nova pela internet. O https do TX9 com o GitHub funciona.
- Download direto (0.3.3): baixa sem abrir o navegador e abre a tela de instalação do Android. FUNCIONA.
- Instalar por cima (com a chave de depuração): NÃO FUNCIONAVA. Mensagem do Android: "O app não foi instalado. Já existe um pacote com esse mesmo nome." Aconteceu com app antigo -> 0.3.3 e com 0.3.3 -> 0.3.4 (as duas vindas das Releases).
- Depois de desinstalar o app antigo, a 0.3.3 instalou normalmente pelo APK baixado e o rodapé mostrou 0.3.3.

### 2026-10-26 — Sessão 2: testes automáticos no GitHub Actions

Resultado informado pelo dono, que olhou a aba Actions depois de cada mudança: check verde. O agente não viu as telas. O workflow fica vermelho quando um teste falha, então o verde indica que os testes passaram.

Testes que rodam na tarefa Testes (todos com pastas falsas temporárias ou respostas falsas do GitHub; nenhum usa internet nem toca o aparelho):

- PoliticaCaminhosTest (trava de caminhos).
- AnalisadorTest, 10 testes: soma de bytes; pasta vazia; área fora da política; área nula ou inexistente; analisar não apaga; link simbólico de arquivo ignorado; política nula; limite de 100 itens com totais completos; parada por profundidade; link simbólico de pasta ignorado.
- VerificadorAtualizacaoTest, 11 testes: comparação de versões como números (0.10.0 > 0.9.0; 1.0 igual a 1.0.0); versão igual ou menor não é nova; versão inválida nunca é nova; leitura de "v0.3.0" e de sufixos; nova versão com APK devolve o endereço do APK; sem APK devolve a página da Release; endereço de outro site é ignorado; mesma versão diz atualizado; resposta vazia ou sem versão dá erro; número de versão perigoso ("../x") dá erro.
- Testes de link simbólico são pulados, não falham, se o ambiente não permitir criar links.

NÃO coberto por teste automático: a parte de rede do VerificadorAtualizacao (HttpURLConnection), o download e a instalação.

## Pendências de registro

- Este arquivo não tem os testes das Etapas 2 e 3 no TX9 (tela de Diagnóstico e teste de root), que o CHANGELOG cita. Eles não foram escritos aqui para não inventar. Se o dono tiver o texto antigo, ele pode ser acrescentado.
- Permanecem pendências antigas da limpeza 0.4.0/0.4.1: conferir o relatório no TX9, o comportamento de CANCELAR e os textos. Elas não são testes da Etapa A.
- O teste do link simbólico do ExclusorCacheTest: não se sabe se rodou ou foi pulado.
