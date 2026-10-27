# TESTING.md — Testes e resultados

Só entram aqui testes que foram realmente feitos. Cada teste diz onde foi feito: ambiente simulado, GitHub Actions ou aparelho real (TX9).

## Resultados até agora

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
- Próxima prova: publicar uma versão nova (0.3.9 ou a seguinte) com mudança real e atualizar por cima de novo pelo botão do app.

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

NÃO coberto por teste automático: a parte de rede do VerificadorAtualizacao (HttpURLConnection), o download e a instalação
