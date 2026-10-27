# TESTING.md — Testes e resultados

Só entram aqui testes que foram realmente feitos. Cada teste diz onde foi feito: ambiente simulado, GitHub Actions ou aparelho real (TX9).

## Resultados até agora

### 2026-10-27 — Sessão 5: atualização por cima com mudança real de código, 0.3.8 para 0.3.9 (TX9 real, informado pelo dono)

O agente não viu o aparelho; o resultado foi informado pelo dono, que respondeu a perguntas numeradas.

- Versão 0.3.9 publicada: o dono informou verde no Actions e a Release v0.3.9 existe na página Releases, com o .apk. O strings.xml (versao_texto) foi salvo antes do build.gradle, para a Release sair já com o texto certo.
- 0.3.8 -> 0.3.9 pelo botão VERIFICAR ATUALIZAÇÃO do próprio app, por cima, sem desinstalar: OK. O dono disse "deu certo" e confirmou que a tela inicial mostra "Versão 0.3.9".
- Esta foi a "próxima prova" pedida na sessão 4: atualização por cima com mudança real de código, já com a chave própria. Passou.
- NÃO conferido: a frase nova da mensagem de versão nova ("Aperte de novo o botão VERIFICAR ATUALIZAÇÃO para baixar e instalar. O Android vai pedir a sua confirmação."). O dono respondeu que não leu a mensagem. Só vale como conferida quando ele ler na tela, numa próxima atualização.
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
- A tela LIMPEZA nunca foi testada no TX9.
