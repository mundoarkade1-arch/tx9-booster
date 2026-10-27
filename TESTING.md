# TESTING.md — Testes e resultados

Só entram aqui testes que foram realmente feitos. Cada teste diz onde foi feito: ambiente simulado ou aparelho real (TX9).

## Resultados até agora

### 2026-10-26 — Analisador e tela de simulação (Etapa 4, código da sessão 2)

GitHub Actions (compilação e testes automáticos do JUnit):
- Execução #27 (antes do código novo): vista pelo agente. Tarefas "Testes" e "Compilar APK" bem-sucedidas. Isso confirma que o PoliticaCaminhosTest passou no GitHub.
- Depois de cada arquivo novo (Analisador.java, AnalisadorTest.java, strings_simulacao.xml, activity_limpeza.xml, LimpezaActivity.java, AndroidManifest.xml, MainActivity.java e as atualizações de documentação): check verde, informado pelo dono. O agente NÃO viu essas execuções, porque a leitura do GitHub pelo agente mostrava uma lista antiga.
- AnalisadorTest (6 testes): soma de bytes de arquivos e subpastas, pasta vazia, área fora da política, área nula ou inexistente, "analisar não apaga nada", link simbólico ignorado. Rodou na tarefa "Testes" e passou, segundo o dono. O agente escreveu os testes e não os executou.

Tela LIMPEZA AVANÇADA: compilou. NÃO foi aberta no TX9. Nenhum teste no aparelho real ainda.

### 2026-10-26 — Trava de segurança PoliticaCaminhos (Etapa 4)

Ambiente simulado, feito pelo agente (Linux, script próprio, fora do GitHub): 19 verificações, todas passaram. Casos: arquivo dentro da área, caminho inexistente dentro da área, raiz da área negada, pasta irmã com mesmo prefixo negada, fora da área negado, escape com "..", link simbólico para fora, /system, /data exato, /data/system, "/", /sdcard, caminho nulo, áreas perigosas descartadas, cache do próprio app permitido e de outro app negado.

GitHub Actions: o teste JUnit equivalente (PoliticaCaminhosTest) passou na tarefa "Testes" da execução #27, vista pelo agente. Não testado no TX9.

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

No TX9, para a tela LIMPEZA AVANÇADA (próximo teste do dono):
- Instalar o APK novo. Se a atualização por cima falhar, anotar a mensagem exata do Android.
- Na tela inicial, LIMPEZA AVANÇADA abre a tela nova e o foco amarelo aparece no botão SIMULAR LIMPEZA.
- Apertar SIMULAR LIMPEZA: a tela mostra a área do cache interno e a do cache externo, quantidade de arquivos e bytes. Anotar o que apareceu. Zero arquivos é um resultado possível e normal.
- Apertar o botão uma segunda vez: os números não podem diminuir (nada foi apagado).
- Botão Voltar sai da tela de simulação e volta para a tela inicial.
- O app não trava nem fecha durante a simulação.

Outros:
- Conferir RAM total, RAM disponível, armazenamento, kernel e ABIs da tela de diagnóstico com outra fonte, no TX9.
- Atualizar o app por cima, sem desinstalar, e anotar a mensagem exata se falhar.
- Teste de root com a permissão negada.
- O app funciona sem root e explica o que fica indisponível.
- A limpeza real mexe só nos arquivos permitidos.
- O relatório antes e depois mostra números medidos.
- Consumo de memória do próprio app em uso.

## Limitações

- Compilar não prova que a função é segura no firmware do TX9.
- Os testes JUnit rodam em um computador do GitHub, não no TX9. Eles não provam o comportamento no Android 7.1.
- Uma medição de RAM livre depois da limpeza não prova que o aparelho ficou mais rápido.
