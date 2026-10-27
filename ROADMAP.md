# ROADMAP.md — Plano de desenvolvimento

Níveis de estado: Planejada, Em desenvolvimento, Implementada, Compilada, Testada em ambiente simulado, Testada no aparelho real, Aprovada.

Regra: não avançar para uma etapa que apaga coisas se a etapa anterior ainda tem falhas importantes.

## Etapa 1 — Planejamento e compatibilidade

Estado: Aprovada (2026-10-26). Fez: repositório, arquivos de continuidade, escolha de tecnologia (Java), versão mínima do Android (5.0, API 21).

## Etapa 2 — Estrutura mínima funcional

Estado: Testada no aparelho real (2026-10-26), resultado informado pelo dono. Fez: projeto Android com tela inicial, navegação por controle remoto, compilação automática do APK pelo GitHub Actions. Pendências: ícone e banner do app.

## Etapa 3 — Diagnóstico

Estado: Testada no aparelho real, com pendências (2026-10-26), resultado informado pelo dono. Fez: tela de diagnóstico (modelo, Android e API, kernel, ABIs, RAM, armazenamento) e teste de root com comando fixo e limite de tempo. Confirmado pelo dono: Android real é a API 25 (7.1); root funcionando. Pendências: conferir RAM, armazenamento, kernel e ABIs com outra fonte; testar o root com a permissão negada; entender por que a atualização por cima do app não funcionou.

## Etapa 4 — Limpeza segura

Estado: Em desenvolvimento. A simulação está Compilada e testada em ambiente automático do GitHub (testes JUnit), mas NÃO foi testada no TX9.

Feito:
- PoliticaCaminhos (trava de segurança dos caminhos): testada em ambiente simulado pelo agente e pelo JUnit no GitHub (PoliticaCaminhosTest passou na execução #27, vista pelo agente).
- Analisador somente leitura, com AnalisadorTest (6 testes). Check verde informado pelo dono.
- Tela LIMPEZA AVANÇADA em modo de simulação: mostra arquivos e bytes do cache do próprio app (interno e externo) sem apagar nada. Compilada, check verde informado pelo dono.

Falta:
- Testar a simulação no TX9 (o dono instala o APK novo, aperta SIMULAR LIMPEZA e conta o que apareceu).
- Só depois disso, a exclusão real, apenas do que é do próprio app, com confirmação do dono, e relatório de bytes realmente liberados.

Pronta quando: a simulação lista o que seria apagado sem apagar nada (testado no TX9), os testes de caminhos passam e a limpeza real libera bytes medidos.

## Etapa 5 — Otimização rápida

Estado: Planejada. Fazer: botão que junta as funções já testadas e mostra relatório antes e depois (RAM livre, espaço livre, arquivos removidos, falhas). Pronta quando: o relatório mostra só números medidos, sem promessa de velocidade.

## Etapa 6 — Funções avançadas de root

Estado: Planejada. Fazer: só recursos com compatibilidade, segurança e reversão verificadas, sempre com confirmação do usuário. Inclui validar se existe método seguro para limpar cache de outros aplicativos no Android 7.1. Pronta quando: cada função foi testada no TX9 e tem plano de reversão escrito.

## Etapa 7 — Gerenciamento de aplicativos

Estado: Planejada. Fazer: lista de aplicativos, classificação (usuário, sistema, crítico, a analisar), ações reversíveis. Pronta quando: nenhum componente crítico pode ser desativado pelo app e toda ação pode ser desfeita.

## Etapa 8 — Testes e estabilidade

Estado: Planejada. Fazer: testar controle remoto, consumo de memória e comportamento no TX9, corrigir falhas. Pronta quando: TESTING.md mostra os testes no aparelho real sem falhas importantes.

## Etapa 9 — Acabamento e lançamento

Estado: Planejada. Fazer: melhorar interface, revisar documentos, preparar versão estável, assinatura privada e instruções de instalação. Pronta quando: README.md explica como gerar e instalar o APK e a versão estável foi aprovada pelo dono.
