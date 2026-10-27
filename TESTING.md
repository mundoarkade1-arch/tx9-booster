# TESTING.md — Testes e resultados

Só entram aqui testes que foram realmente feitos. Cada teste diz onde foi feito: ambiente simulado ou aparelho real (TX9).

## Resultados até agora

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

## Testes necessários no aparelho real (TX9)
- Conferir RAM total, RAM disponível, armazenamento, kernel e ABIs da tela de diagnóstico com outra fonte.
- Atualizar o app por cima, sem desinstalar, e anotar a mensagem exata se falhar.
- Teste de root com a permissão negada.
- O app funciona sem root e explica o que fica indisponível.
- A simulação de limpeza não apaga nada.
- A limpeza real mexe só nos arquivos permitidos.
- O relatório antes e depois mostra números medidos.
- Consumo de memória do próprio app em uso.

## Testes automatizados planejados (ambiente simulado)
- Validação de caminhos permitidos e proibidos (a lista proibida vence).
- Bloqueio de caminhos fora da área permitida e de links simbólicos.
- Classificação das operações.
- Interpretação dos resultados e dos códigos de saída.

## Limitações
- Compilar não prova que a função é segura no firmware do TX9.
- Uma medição de RAM livre depois da limpeza não prova que o aparelho ficou mais rápido.
