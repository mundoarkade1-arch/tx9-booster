# PROJECT_STATE.md — Estado atual do projeto

Última atualização: 2026-10-26

## Objetivo
Criar o TX9 Booster: app Android de manutenção e otimização para o Tanix TX9 (1 GB de RAM, root). Diagnóstico, limpeza segura, otimização rápida, tudo medido de verdade, com interface para controle remoto.

## Etapa atual
Etapa 3 (Diagnóstico) feita e testada no TX9 pelo dono, com pendências pequenas. Próxima: Etapa 4 — Limpeza segura.

## Última tarefa concluída
Tela de Diagnóstico e teste de root, instalados e abertos no TX9 pelo dono (versão 0.2.0).

## Funcionalidades implementadas
- Tela inicial com 5 botões grandes, navegação por controle remoto e foco amarelo visível.
- Botão DIAGNÓSTICO abre a tela de diagnóstico. Os outros 4 botões só mostram "Esta função ainda não foi criada."
- Diagnóstico mostra: fabricante, modelo, placa, Android e API, kernel, ABIs, RAM total, disponível e em uso, aviso de pouca memória, armazenamento total, livre e usado.
- Seção Root: mostra se existe arquivo su em locais comuns e tem o botão TESTAR ACESSO ROOT. O teste roda só o comando fixo "su -c id", com limite de 15 segundos, e só quando o usuário aperta o botão.
- Compilação automática do APK de depuração pelo GitHub Actions, com chave de depuração fixa (arquivo debug.keystore.b64 no repositório; chave pública, só para testes).

## Funcionalidades ainda não implementadas
Limpeza (simulação e real), otimização rápida, lista de aplicativos, histórico, ícone e banner do app.

## O que foi compilado
APK de depuração versão 0.2.0 (versionCode 2), pelo GitHub Actions. Resultado informado pelo dono: check verde. O agente não viu o repositório.

## Descobertas no aparelho real (informadas pelo dono)
- Android: a tela mostra "10.1" com API 25. A API 25 é a versão real (Android 7.1). "10.1" é só o texto do fabricante. Decisões de compatibilidade devem usar a API 25.
- Root: o botão TESTAR ACESSO ROOT retornou "ROOT FUNCIONANDO".

## Testes realizados
Ver TESTING.md.

## Erros conhecidos e pendências
- Ao instalar a versão 0.2.0, o dono precisou desinstalar a versão antiga antes. Não se sabe a causa (a chave fixa já estava em uso na versão anterior). Investigar: perguntar a mensagem exata que o Android mostrou.
- Os valores de RAM, armazenamento, kernel e ABIs aparecem na tela, mas o dono ainda não os comparou com o que o aparelho informa em outro lugar.
- O app ainda não tem ícone nem banner próprio para o launcher de TV.
- Se o repositório está Public ou Private: o dono não confirmou.

## Decisões técnicas
- Java, sem bibliotecas extras, sem AndroidX, sem banco de dados, sem serviço em segundo plano.
- minSdk 21, targetSdk 28, compileSdk 34. Pacote: br.com.tx9booster.
- Sem Gradle wrapper. O GitHub Actions usa Java 17 e Gradle 8.5.
- Root: sempre comandos fixos, com limite de tempo e checagem do código de saída. Nunca montar comando com texto do usuário.
- Sem root, o app continua funcionando só com as funções seguras.
- Chave de depuração fixa e pública para permitir atualização por cima. Assinatura privada fica para a Etapa 9.

## Arquivos do projeto
AGENTS.md, PROJECT_STATE.md, ROADMAP.md, CHANGELOG.md, TESTING.md, README.md, settings.gradle, build.gradle, gradle.properties, debug.keystore.b64, app/build.gradle, app/src/main/AndroidManifest.xml, app/src/main/res/values/strings.xml, app/src/main/res/layout/activity_main.xml, app/src/main/res/layout/activity_diagnostico.xml, app/src/main/res/drawable/botao_fundo.xml, app/src/main/java/br/com/tx9booster/MainActivity.java, app/src/main/java/br/com/tx9booster/DiagnosticoActivity.java, .github/workflows/build.yml.

## Próxima tarefa exata
Etapa 4 — Limpeza segura, só em modo de análise e simulação primeiro: lista de caminhos permitidos e proibidos (a proibida vence), análise do cache e dos arquivos temporários do próprio app, estimativa real de bytes, testes unitários da validação de caminhos. Nenhuma exclusão real antes da simulação ser testada no TX9.

## Instruções especiais para o próximo agente
- Dono iniciante: siga as regras de AGENTS.md (um passo, um arquivo, português simples).
- Conferir o repositório real antes de assumir qualquer coisa deste arquivo.
- Não recomeçar o projeto.
- Atualizar README.md quando houver instruções de instalação do APK.
