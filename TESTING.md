# TESTING.md — Testes e resultados

Só entram aqui testes que foram realmente feitos. Cada teste diz onde foi feito: ambiente simulado, GitHub Actions ou aparelho real (TX9).

## Resultados até agora

### 2026-10-26 — Analisador e trava PoliticaCaminhos (Etapa 4)

GitHub Actions: a execução mais recente está com check verde nas duas tarefas, Testes (gradle testDebugUnitTest) e Compilar APK. Resultado informado pelo dono, que olhou a aba Actions. O agente não viu a tela. Como o workflow fica vermelho quando um teste falha, o verde indica que os testes passaram.

Testes automáticos que rodam nessa tarefa, todos com pastas falsas temporárias, sem tocar no aparelho:

- PoliticaCaminhosTest (trava de caminhos).
- AnalisadorTest, 10 testes: soma de bytes de arquivos e subpastas; pasta vazia; área fora da política não é analisada; área nula ou inexistente; analisar não apaga nada; link simbólico de arquivo para fora é ignorado; política nula não analisa nada; a lista de itens para em 100, mas os totais contam todos os arquivos; para quando as pastas são fundas demais (marca como truncado); link simbólico de pasta é ignorado. Os 6 primeiros já existiam; os 4 últimos foram acrescentados na sessão 2.
- Limite dos testes de link simbólico: se o ambiente do GitHub não permitir criar links, esses testes são pulados, não falham.

Ambiente simulado, feito pelo agente (Linux, script próprio, fora do GitHub): 19 verificações da PoliticaCaminhos, todas passaram. Casos: arquivo dentro da área, caminho inexistente dentro da área, raiz da área negada, pasta irmã com mesmo prefixo negada, fora da área negado, escape com "..", link simbólico para fora, /system, /data exato, /data/system, "/", /sdcard, caminho nulo, áreas perigosas descartadas, cache do próprio app permitido e de outro app negado.

NÃO testado no TX9: a tela LIMPEZA (simulação) e o Analisador rodando no aparelho real. Não se sabe se o dono já abriu essa tela.

### 2026-10-26 — Diagnóstico e root (Etapa 3, versão 0.2.0)

Feito no TX9 real, pelo dono. Os resultados abaixo foram informados por ele. O agente não viu o aparelho.

- Compilação pelo GitHub Actions: check verde, informado pelo dono.
- Instalação da 0.2.0: o dono precisou desinstalar a versão antiga antes. Causa desconhecida. A atualização por cima NÃO foi confirmada.
- Botão DIAGNÓSTICO abre a tela de diagnóstico com dados: OK.
- Linha Android: mostra "10.1" com API 25. Conclusão: Android real 7.1 (API 25), com texto de versão do fabricante.
- Teste de root (botão TESTAR ACESSO ROOT): resultado "ROOT FUNCIONANDO": OK.

Não conferido nesta rodada: RAM total, RAM disponível, armazenamento, kernel e ABIs (aparecem na tela, mas o dono não comparou com outra fonte); comportamento do teste de root quando a permissão é negada; consumo de memória do app.

### 2026-10-26 — Estrutura mínima (Etapa 2, versão 0.1.0)

Feito no TX9 real, pelo dono. Informado por ele.

- APK instalado: OK.
- App abre e mostra o título e os 5 botões: OK.
- Setas movem o foco entre os botões, com borda amarela visível: OK.
- OK no botão mostra o aviso "Esta função ainda não foi criada.": OK.
- Voltar fecha o app: OK.

## Testes necessários

- No TX9: abrir a tela LIMPEZA, apertar o botão de simular e anotar o que aparece (arquivos, bytes, ignorados, aviso de limite, lista dos maiores itens). Conferir que nada é apagado e que o app não trava.
- No TX9: conferir se os bytes mostrados são plausíveis (por exemplo, comparando com o tamanho do cache do app nas configurações do Android).
- Conferir RAM total, RAM disponível, armazenamento, kernel e ABIs da tela de diagnóstico com outra fonte, no TX9.
- Atualizar o app por cima, sem desinstalar, e anotar a mensagem exata se falhar. (Necessário também antes de criar a função de atualização do app.)
- Teste de root com a permissão negada.
- O app funciona sem root e explica o que fica indisponível.
- A limpeza real mexe só nos arquivos permitidos.
- O relatório antes e depois mostra números medidos.
- Consumo de memória do próprio app em uso.

## Limitações

- Compilar não prova que a função é segura no firmware do TX9.
- Testes com pastas falsas não provam o comportamento no armazenamento do TX9.
- Uma medição de RAM livre depois da limpeza não prova que o aparelho ficou mais rápido.
