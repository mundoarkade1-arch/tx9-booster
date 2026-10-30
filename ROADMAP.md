# ROADMAP.md — Plano de desenvolvimento

Estados possíveis: Planejada, Em desenvolvimento, Implementada, Compilada, Testada em ambiente simulado, Testada no aparelho real, Aprovada.

## Base existente

O app já tem diagnóstico, teste de root, atualização pelo próprio app, limpeza simulada e exclusão do cache do próprio app, otimização atual e lista de aplicativos somente leitura. Resultados de versões antigas estão em `TESTING.md`.

## Etapa A — Núcleo root (0.7.0)

Estado: Implementada, compilada, publicada e testada no TX9. O dono informou em 2026-10-30 que testou e que tudo deu certo.

- RootShell, ComandosRoot, Histórico e Reversor com comandos fixos e ações reversíveis.
- HISTÓRICO ligado à tela inicial; LABORATÓRIO com recursos desligados por padrão e bloqueio sem root.
- A política padrão nega alterações de pacote até haver inventário real e proteção.

## Etapa B — Inventário e debloat (0.8.0)

Parte 1: inventário somente leitura. O botão do Laboratório executa `pm list packages -s`, `-d`, `-3` e `-f` via RootShell, salva um `.txt` em `Download` e mostra o caminho completo.

Estado: Implementada, compilada e publicada em 0.7.1. O workflow do commit `f00dad1` (execução 41070382583) passou em Testes, Compilar APK e Publicar Release. 20 testes JUnit JVM manuais passaram. O inventário ainda não foi testado no TX9.

Não criar listas de pacotes nem desativar/remover nada nesta parte. Depois que o dono trouxer o inventário, classificar somente nomes reais observados. Qualquer ação de desativação pertence a uma etapa posterior, com proteção e reversão.

## Etapa C — Memória e processos (0.9.0)

Planejada. Exibir somente recursos existentes. Cada ajuste exige diagnóstico, original salvo no Histórico, justificativa, medição e reversão. Aplicar no boot depende do Modo Seguro de Boot.

## Etapa D — Limpeza profunda com root (1.0.0)

Planejada. Política explícita de caminhos permitidos e proibidos, simulação obrigatória, limites, proteção contra links simbólicos, confirmação e relatório.

## Etapa E — Desempenho do sistema (1.1.0)

Planejada. Ajustes opcionais e medidos, com original no Histórico e reversão; só oferecer opções confirmadas no aparelho.

## Etapa F — Perfis e otimização v2 (1.2.0)

Planejada. Combinar somente funções validadas no TX9 e manter REVERTER TUDO acessível.

## Etapa G — Opcionais de alto risco

Planejada, somente se o dono pedir. Cada opção começa desligada e exige confirmação, testes e reversão.

## Travas permanentes

Não desativar SELinux, alterar partições, bootloader, recovery ou firmware; executar `dd`, `mkfs` ou `flash`; apagar `/system`, `/vendor`, `/boot`, `/data/system`, `/data/dalvik-cache`, `/data/app`, `/efs` ou `/persist`; nem desativar componentes essenciais. Funções sem validação no TX9 ficam desligadas.
