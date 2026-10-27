# PROJECT_STATE.md — Estado atual do projeto

Última atualização: 2026-10-26

## Objetivo
Criar o TX9 Booster: app Android de manutenção e otimização para o Tanix TX9 (1 GB de RAM, root). Diagnóstico, limpeza segura, otimização rápida, tudo medido de verdade, com interface para controle remoto.

## Etapa atual
Etapa 2 concluída. Próxima: Etapa 3 — Diagnóstico.

## Última tarefa concluída
App mínimo criado, compilado pelo GitHub Actions, instalado e aberto no TX9 pelo dono.

## Funcionalidades implementadas
- Tela inicial com título e 5 botões grandes (OTIMIZAÇÃO RÁPIDA, LIMPEZA AVANÇADA, APLICATIVOS, DIAGNÓSTICO, HISTÓRICO).
- Navegação por setas, OK e voltar, com foco visível (borda amarela).
- Cada botão só mostra o aviso "Esta função ainda não foi criada."
- Compilação automática do APK de depuração pelo GitHub Actions.

## Funcionalidades ainda não implementadas
Diagnóstico, verificação de root, limpeza (simulação e real), otimização rápida, lista de aplicativos, histórico, ícone do app.

## O que foi compilado
APK de depuração, pelo GitHub Actions (Java 17, Gradle 8.5, compileSdk 34). Resultado informado pelo dono: check verde. O agente não viu o repositório.

## Testes realizados
No TX9 real, pelo dono (informado por ele): instalou, abriu, setas movem o foco, OK mostra o aviso, voltar fecha o app. Detalhes em TESTING.md.

## Erros conhecidos
- Assinatura: cada APK de depuração sai assinado com uma chave nova. Atualizar por cima do app instalado não funciona. É preciso desinstalar o antigo antes de instalar o novo. Correção planejada: assinatura fixa.
- O app ainda não tem ícone nem banner próprio para o launcher de TV.

## Inconsistências a resolver
- Versão do Android do TX9: há registro de 7.1 e de 10. A Etapa 3 vai ler a versão real e mostrar na tela.
- Se o repositório está Public ou Private: o dono não confirmou.

## Decisões técnicas
- Java, sem bibliotecas extras, sem AndroidX, sem banco de dados, sem serviço em segundo plano.
- minSdk 21 (Android 5.0), targetSdk 28, compileSdk 34. Motivo de targetSdk 28: manter compatibilidade com o armazenamento do Android antigo.
- Pacote: br.com.tx9booster.
- Sem Gradle wrapper. O GitHub Actions instala o Gradle 8.5 direto.
- Sem root, o app continua funcionando só com as funções seguras.

## Arquivos do projeto
AGENTS.md, PROJECT_STATE.md, ROADMAP.md, CHANGELOG.md, TESTING.md, README.md, settings.gradle, build.gradle, gradle.properties, app/build.gradle, app/src/main/AndroidManifest.xml, app/src/main/res/values/strings.xml, app/src/main/res/layout/activity_main.xml, app/src/main/res/drawable/botao_fundo.xml, app/src/main/java/br/com/tx9booster/MainActivity.java, .github/workflows/build.yml.

## Próxima tarefa exata
Etapa 3 — Diagnóstico: criar a tela de diagnóstico (modelo, Android e API, kernel, ABI, RAM total e livre, armazenamento total e livre) e a verificação de root, sem executar comandos perigosos. Depois, testar no TX9.

## Instruções especiais para o próximo agente
- Dono iniciante: siga as regras de AGENTS.md (um passo, um arquivo, português simples).
- Conferir o repositório real antes de assumir qualquer coisa deste arquivo.
- Não recomeçar o projeto.
- Atualizar README.md quando houver instruções de instalação do APK.
