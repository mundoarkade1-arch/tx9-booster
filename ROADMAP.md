# ROADMAP.md — Plano de desenvolvimento

Níveis de estado: Planejada, Em desenvolvimento, Implementada, Compilada, Testada em ambiente simulado, Testada no aparelho real, Aprovada.

Regra: não avançar para uma etapa que apaga coisas se a etapa anterior ainda tem falhas importantes.

## Etapa 1 — Planejamento e compatibilidade
Estado: Em desenvolvimento.
Fazer: criar repositório, arquivos de continuidade, escolher tecnologia, definir versão mínima do Android.
Pronta quando: AGENTS.md, PROJECT_STATE.md, ROADMAP.md, CHANGELOG.md, TESTING.md e README.md existem no repositório.

## Etapa 2 — Estrutura mínima funcional
Estado: Planejada.
Fazer: projeto Android com tela inicial, navegação por controle remoto, compilação automática do APK pelo GitHub Actions.
Pronta quando: o GitHub Actions gera um APK de depuração sem erro, o dono baixa, instala no TX9 e o app abre.

## Etapa 3 — Diagnóstico
Estado: Planejada.
Fazer: ler modelo, versão do Android e API, kernel, ABI, RAM total e livre, armazenamento total e livre, estado do root.
Pronta quando: a tela mostra os valores reais do TX9 e o dono confirma que batem com o aparelho.

## Etapa 4 — Limpeza segura
Estado: Planejada.
Fazer: análise de arquivos, estimativa real de espaço, modo de simulação, limpeza só dos arquivos do próprio app, lista de caminhos permitidos e proibidos, testes unitários.
Pronta quando: a simulação lista o que seria apagado sem apagar nada, os testes de caminhos passam e a limpeza real libera bytes medidos.

## Etapa 5 — Otimização rápida
Estado: Planejada.
Fazer: botão que junta as funções já testadas e mostra relatório antes e depois (RAM livre, espaço livre, arquivos removidos, falhas).
Pronta quando: o relatório mostra só números medidos, sem promessa de velocidade.

## Etapa 6 — Funções avançadas de root
Estado: Planejada.
Fazer: só recursos com compatibilidade, segurança e reversão verificadas, sempre com confirmação do usuário.
Pronta quando: cada função foi testada no TX9 e tem plano de reversão escrito.

## Etapa 7 — Gerenciamento de aplicativos
Estado: Planejada.
Fazer: lista de aplicativos, classificação (usuário, sistema, crítico, a analisar), ações reversíveis.
Pronta quando: nenhum componente crítico pode ser desativado pelo app e toda ação pode ser desfeita.

## Etapa 8 — Testes e estabilidade
Estado: Planejada.
Fazer: testar controle remoto, consumo de memória e comportamento no TX9, corrigir falhas.
Pronta quando: TESTING.md mostra os testes no aparelho real sem falhas importantes.

## Etapa 9 — Acabamento e lançamento
Estado: Planejada.
Fazer: melhorar interface, revisar documentos, preparar versão estável e instruções de assinatura.
Pronta quando: README.md explica como gerar e instalar o APK e a versão estável foi aprovada pelo dono.
