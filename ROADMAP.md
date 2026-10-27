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

Estado: Compilada e testada em ambiente simulado (GitHub Actions verde, informado pelo dono em 2026-10-26). Não testada no aparelho real. Feito: PoliticaCaminhos (trava de segurança dos caminhos, com teste); Analisador somente leitura (limites de 20000 arquivos, 20 níveis de pasta e 100 itens na lista, ignora links simbólicos, com 10 testes); tela LIMPEZA AVANÇADA em modo de simulação (LimpezaActivity, ligada ao botão LIMPEZA), que mede o cache interno e o cache externo do próprio app e nunca apaga nada. Falta: testar a simulação no TX9 (abrir a tela, apertar simular, anotar o que aparece); preparar a versão 0.3.0 para esse teste; e só depois a exclusão real, apenas do cache do próprio app, com confirmação do usuário e relatório de bytes antes e depois. Pronta quando: a simulação lista o que seria apagado sem apagar nada, os testes de caminhos passam e a limpeza real libera bytes medidos.

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

## Ideia adiada — Atualização do app

Estado: Planejada, adiada por decisão do dono (2026-10-26): primeiro terminar a Etapa 4. Pedido do dono: o app se atualizar ao abrir ou ter um botão VERIFICAR ATUALIZAÇÃO. Recomendação: só o botão, que consulta a internet quando o dono apertar. Checar sozinho ao abrir exige o dono aprovar a mudança da regra "não depender de internet e não agir sozinho" do AGENTS.md. Antes de começar: (1) publicar cada versão em Releases do GitHub (ainda não existe nenhuma); (2) resolver a atualização por cima do app sem desinstalar (assinatura); (3) o app não instala sozinho, só avisa e abre o download, com confirmação do Android. Pronta quando: o botão avisa corretamente se há versão nova e a atualização por cima funciona no TX9.
