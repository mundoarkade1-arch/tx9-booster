# TESTING.md — Testes e resultados

Só entram aqui testes que foram realmente feitos. Cada teste diz onde foi feito: ambiente simulado, GitHub Actions ou aparelho real (TX9).

## Resultados até agora

### 2026-10-26 — Sessão 2: testes automáticos no GitHub Actions

Resultado informado pelo dono, que olhou a aba Actions depois de cada mudança: check verde. O agente não viu as telas. O workflow fica vermelho quando um teste falha, então o verde indica que os testes passaram. Última informação do dono: três blocos verdes (Testes, Compilar APK, Publicar Release) depois do build.yml novo.

Testes que rodam na tarefa Testes (todos com pastas falsas temporárias ou respostas falsas do GitHub; nenhum usa internet nem toca o aparelho):

- PoliticaCaminhosTest (trava de caminhos).
- AnalisadorTest, 10 testes: soma de bytes; pasta vazia; área fora da política; área nula ou inexistente; analisar não apaga; link simbólico de arquivo ignorado; política nula; limite de 100 itens com totais completos; parada por profundidade; link simbólico de pasta ignorado.
- VerificadorAtualizacaoTest, 11 testes: comparação de versões como números (0.10.0 > 0.9.0; 1.0 igual a 1.0.0); versão igual ou menor não é nova; versão inválida nunca é nova; leitura de "v0.3.0" e de sufixos; nova versão com APK devolve o endereço do APK; sem APK devolve a página da Release; endereço de outro site é ignorado; mesma versão diz atualizado; resposta vazia ou sem versão dá erro; número de versão perigoso ("../x") dá erro.
- Testes de link simbólico são pulados, não falham, se o ambiente não permitir criar links.

NÃO coberto por teste automático: a parte de rede do VerificadorAtualizacao (HttpURLConnection), a tela LIMPEZA, o botão e a checagem ao abrir, a tarefa "Publicar Release".

NÃO confirmado: se a Release v0.3.0 foi criada (o dono não respondeu ao passo de conferir https://github.com/mundoarkade1-arch/tx9-booster/releases).

### 2026-10-26 — Trava PoliticaCaminhos (ambiente simulado do agente)

Linux, script próprio, fora do GitHub: 19 verificações, todas passaram. Casos: arquivo dentro da área, caminho inexistente dentro da área, raiz da área negada, pasta irmã com mesmo prefixo negada, fora da área negado, escape com "..", link simbólico para fora, /system, /data exato, /data/system, "/", /sdcard, caminho nulo, áreas perigosas descartadas, cache do próprio app permitido e de outro app negado.

### 2026-10-26 — Diagnóstico e root (Etapa 3, versão 0.2.0)

Feito no TX9 real, pelo dono. Informado por ele. O agente não viu o aparelho.

- Compilação pelo GitHub Actions: check verde.
- Instalação da 0.2.0: o dono precisou desinstalar a versão antiga antes. Causa desconhecida. A atualização por cima NÃO foi confirmada.
- Botão DIAGNÓSTICO abre a tela com dados: OK.
- Linha Android: mostra "10.1" com API 25. Conclusão: Android real 7.1 (API 25).
- Teste de root: "ROOT FUNCIONANDO": OK.

Não conferido: RAM total, RAM disponível, armazenamento, kernel e ABIs (aparecem na tela, mas não foram comparados com outra fonte); teste de root com permissão negada; consumo de memória do app.

### 2026-10-26 — Estrutura mínima (Etapa 2, versão 0.1.0)

Feito no TX9 real, pelo dono. Informado por ele.

- APK instalado: OK.
- App abre e mostra o título e os 5 botões: OK.
- Setas movem o foco entre os botões, com borda amarela visível: OK.
- OK no botão mostra o aviso "Esta função ainda não foi criada.": OK.
- Voltar fecha o app: OK.

## Testes necessários

- Conferir que a Release v0.3.0 existe, com o arquivo tx9-booster-0.3.0.apk.
- No TX9: instalar a 0.3.0 (anotar a mensagem do Android se pedir para desinstalar a antiga), abrir o app, apertar VERIFICAR ATUALIZAÇÃO. Esperado: "O app já está na versão mais nova". Anotar o que aparecer, inclusive mensagens de erro de conexão.
- No TX9: conferir se o aparelho tem navegador para abrir o download. Se não tiver, o endereço fica escrito na tela.
- No TX9: publicar uma 0.3.1 (subir versionName e versionCode), abrir o app 0.3.0, ver se avisa a versão nova, instalar por cima e anotar a mensagem exata do Android.
- No TX9: abrir a tela LIMPEZA, apertar simular e anotar o que aparece (arquivos, bytes, ignorados, aviso de limite, lista). Conferir que nada é apagado, que o app não trava e que os bytes são plausíveis (comparar com o cache do app nas configurações do Android).
- Conferir se o rodapé da tela inicial ainda diz 0.2.0 (texto versao_texto em strings.xml).
- Conferir RAM total, RAM disponível, armazenamento, kernel e ABIs da tela de diagnóstico com outra fonte.
- Teste de root com a permissão negada.
- O app funciona sem root e explica o que fica indisponível.
- O app funciona sem internet sem travar nem reclamar na abertura.
- A limpeza real mexe só nos arquivos permitidos.
- O relatório antes e depois mostra números medidos.
- Consumo de memória do próprio app em uso.

## Limitações

- Compilar não prova que a função é segura no firmware do TX9.
- Testes com pastas e respostas falsas não provam o comportamento no TX9 nem a conexão real com o GitHub.
- ## 2026-10-27 — Testes da atualização no TX9 (informados pelo dono)

- Release v0.3.0, v0.3.2, v0.3.3 existem com o .apk (conferido pelo dono na página Releases).
- Checagem de atualização no TX9: acha a versão nova pela internet. O https do TX9 com o GitHub funciona.
- Download direto (0.3.3): baixa sem abrir o navegador e abre a tela de instalação do Android. FUNCIONA.
- Instalar por cima: NÃO FUNCIONA. Mensagem do Android: "O app não foi instalado. Já existe um pacote com esse mesmo nome." Aconteceu com app antigo -> 0.3.3 e com 0.3.3 -> 0.3.4 (as duas vindas das Releases).
- Depois de desinstalar o app antigo, a 0.3.3 instalou normalmente pelo APK baixado e o rodapé mostrou 0.3.3.
- Ainda NÃO testado no TX9: tela LIMPEZA.
- Uma medição de RAM livre depois da limpeza não prova que o aparelho ficou mais rápido.
- As Releases usam a chave de depuração pública: não são adequadas para publicação em loja.
