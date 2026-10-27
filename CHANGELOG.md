# CHANGELOG.md — Histórico de alterações

Cada alteração relevante entra aqui, da mais nova para a mais antiga.

## 2026-10-26 — Etapa 2: estrutura mínima
- Criado o projeto Android mínimo em Java: tela inicial com 5 botões grandes, navegação por controle remoto e foco amarelo visível.
- Os botões ainda só mostram o aviso "Esta função ainda não foi criada."
- Criada a compilação automática do APK de depuração pelo GitHub Actions (Java 17, Gradle 8.5, compileSdk 34).
- APK instalado e aberto no TX9 pelo dono, que informou que a navegação funcionou.
- Problema conhecido: o APK de depuração sai com assinatura nova a cada compilação, então não atualiza por cima do app instalado.

## 2026-10-26 — Etapa 1: planejamento
- Repositório tx9-booster criado pelo dono no GitHub.
- Criados AGENTS.md, PROJECT_STATE.md, ROADMAP.md, CHANGELOG.md, TESTING.md e README.md.
- Decisão: versão mínima do Android 5.0 (API 21), para servir ao Android 7.1 e ao 10.
