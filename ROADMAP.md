# ROADMAP.md — Plano de desenvolvimento

Estados possíveis: Planejada, Em desenvolvimento, Implementada, Compilada, Testada em ambiente simulado, Testada no aparelho real, Aprovada.

## Base existente

O projeto já tem diagnóstico, teste de root, atualização pelo próprio app, limpeza simulada e exclusão do cache do próprio app, otimização atual e inventário de aplicativos somente leitura. Os resultados de testes anteriores no TX9 estão registrados como informados pelo dono em `TESTING.md`; não foram repetidos nesta etapa.

## Etapa A — Núcleo root (0.7.0)

Estado: Implementada, compilada e publicada. Workflow da versão 0.7.0 no commit `19d66ab3237228bba6631a7317c3f6ad80bb773f` (execução 41064795547) passou em Testes, Compilar APK e Publicar Release. Os 16 testes novos também passaram em execução JVM manual no Codespaces. Ainda não testada no TX9.

- RootShell com comandos de fábrica fixa, timeout, leitura separada de stdout/stderr, código de saída e teste de `uid=0`.
- ComandosRoot com validação de pacote e lista fechada de chaves de leitura.
- Mutação de pacote negada por padrão; só uma política baseada no inventário real poderá liberar pacotes nas próximas etapas.
- Histórico texto append-only em `getFilesDir()`, com original, valor novo, resultado e inverso.
- Reversor de ação, grupo ou tudo; compara o estado atual e para na primeira divergência.
- Tela HISTÓRICO ligada à lista inicial, com confirmação e foco em CANCELAR.
- Tela LABORATÓRIO com interruptores desligados por padrão e modo avançado bloqueado quando root não é confirmado.
- Próxima verificação: teste do dono no TX9 antes de iniciar a Etapa B.

## Etapa B — Inventário e debloat (0.8.0)

Estado: Planejada. Primeiro exportar o inventário real do TX9 e receber a lista do dono. Só então classificar os pacotes observados em PROTEGIDO, SEGURO, AGRESSIVO ou ANALISAR. Começar por `pm disable-user --user 0`; remoção do usuário 0 só será considerada após confirmar no aparelho que `pm install-existing` funciona. Nunca apagar APKs do sistema nem alterar `/system`. Registrar estado antes/depois no Histórico e conferir memória medida.

## Etapa C — Memória e processos (0.9.0)

Estado: Planejada. Mostrar apenas valores existentes no kernel/aparelho. Cada ajuste exige diagnóstico, original salvo, justificativa, medição e reversão. Aplicação no boot depende do Modo Seguro de Boot.

## Etapa D — Limpeza profunda com root (1.0.0)

Estado: Planejada. Criar política root de caminhos permitidos/proibidos. Simulação obrigatória, limites de profundidade e quantidade, exclusão dos apps protegidos, confirmação e relatório. Nunca seguir links simbólicos nem usar caminho livre em comando destrutivo.

## Etapa E — Desempenho do sistema (1.1.0)

Estado: Planejada. Ajustes opcionais e medidos, com valor original no Histórico e reversão. Só oferecer controles para arquivos e opções confirmados no aparelho.

## Etapa F — Perfis e otimização v2 (1.2.0)

Estado: Planejada. Combinar apenas funções que já tenham sido validadas no TX9. Mostrar números medidos e manter REVERTER TUDO acessível.

## Etapa G — Opcionais de alto risco

Estado: Planejada, somente se o dono pedir. Cada opção começa desligada e exige confirmação, testes e reversão. Inclui instalação silenciosa, exportação/importação do Histórico e tarefas agendadas explícitas.

## Travas permanentes

Não desativar SELinux, alterar partições, bootloader, recovery ou firmware, executar `dd`, `mkfs` ou `flash`, nem apagar `/system`, `/vendor`, `/boot`, `/data/system`, `/data/dalvik-cache`, `/data/app`, `/efs` ou `/persist`. Componentes essenciais e pacotes protegidos nunca podem ser desativados. Função sem validação no TX9 permanece desligada.
