# CHANGELOG.md — Histórico de alterações

Cada alteração relevante entra aqui, da mais nova para a mais antiga.

## 2026-10-26 — Sessão 2: conferência do GitHub Actions e documentação

- O dono conferiu a aba Actions: a execução mais recente está com check verde nas duas tarefas, Testes e Compilar APK. O PoliticaCaminhosTest passou no GitHub. Informado pelo dono, o agente não viu a tela.
- Atualizados PROJECT_STATE.md, TESTING.md e ROADMAP.md com esse resultado.
- Registrada a ideia adiada de atualização do app (botão VERIFICAR ATUALIZAÇÃO), por decisão do dono: primeiro terminar a Etapa 4. Detalhes no ROADMAP.md.
- Nenhum código foi alterado nesta sessão. Nenhuma função nova foi criada.

## 2026-10-26 — Etapa 4 (início): trava de segurança da limpeza

- Criada a classe PoliticaCaminhos: decide se um caminho pode ser apagado. Só permite o que está dentro de uma área autorizada, a lista proibida vence, resolve ".." e links simbólicos e descarta áreas perigosas (/, /data, /system). Só avalia, nunca apaga. Ainda não é usada por nenhuma tela.
- Criado o teste JUnit PoliticaCaminhosTest.
- app/build.gradle: adicionada a dependência de teste JUnit 4.13.2 (não entra no APK).
- Workflow do GitHub Actions agora tem duas tarefas: Testes (gradle testDebugUnitTest) e Compilar APK (gradle assembleDebug).
- Nenhuma função de exclusão real existe ainda.

## 2026-10-26 — Etapa 3: diagnóstico (versão 0.2.0)

- Nova tela de Diagnóstico: fabricante, modelo, placa, Android e API, kernel, ABIs, RAM (total, disponível, em uso), armazenamento (total, livre, usado).
- Nova seção Root: procura arquivo su em locais comuns e tem o botão TESTAR ACESSO ROOT, que roda só o comando fixo "su -c id", com limite de 15 segundos.
- O botão DIAGNÓSTICO da tela inicial abre a nova tela.
- Versão do app: versionCode 2, versionName 0.2.0.
- Testado no TX9 pelo dono: a tela abre com os dados, o Android aparece como "10.1" com API 25 (Android 7.1 real) e o teste de root retornou "ROOT FUNCIONANDO".
- Problema em aberto: o dono precisou desinstalar o app antigo antes de instalar a 0.2.0. Causa desconhecida.

## 2026-10-26 — Assinatura fixa de depuração

- Adicionado o arquivo debug.keystore.b64 (chave de depuração pública, só para testes) e um passo no GitHub Actions que a instala antes de compilar, para que o APK sempre saia com a mesma assinatura.

## 2026-10-26 — Etapa 2: estrutura mínima

- Criado o projeto Android mínimo em Java: tela inicial com 5 botões grandes, navegação por controle remoto e foco amarelo visível.
- Os botões ainda só mostravam o aviso "Esta função ainda não foi criada."
- Criada a compilação automática do APK de depuração pelo GitHub Actions (Java 17, Gradle 8.5, compileSdk 34).
- APK instalado e aberto no TX9 pelo dono, que informou que a navegação funcionou.

## 2026-10-26 — Etapa 1: planejamento

- Repositório tx9-booster criado pelo dono no GitHub.
- Criados AGENTS.md, PROJECT_STATE.md, ROADMAP.md, CHANGELOG.md, TESTING.md e README.md.
- Decisão: versão mínima do Android 5.0 (API 21), para servir ao Android 7.1 e ao 10.
