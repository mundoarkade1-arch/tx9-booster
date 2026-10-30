# TX9 Booster

Aplicativo Android de manutenção e otimização para o TV Box Tanix TX9 (Amlogic S905W, 1 GB de RAM, com root).

## Estado

Em desenvolvimento. Versão 0.7.0 em preparação, após a base 0.6.0 (versionCode 16). Já existem diagnóstico, atualização pelo próprio app, limpeza do cache do próprio app, otimização atual, lista somente leitura de aplicativos e, na Etapa A, núcleo root, HISTÓRICO e LABORATÓRIO. A Etapa A passou 16 testes JUnit em execução JVM manual; o Actions compilou a base da etapa. Ainda não foi testada no TX9 e a Release 0.7.0 depende do próximo workflow. Veja `PROJECT_STATE.md`.

## Objetivo

- Diagnóstico do aparelho: Android, RAM, armazenamento, ABI, root.
- Limpeza segura, com modo de simulação.
- Otimização rápida com relatório antes e depois, só com números medidos.
- Interface em português, feita para controle remoto.
- O app abre sem root. O Modo Avançado exige root confirmado e fica bloqueado se o teste falhar.
- Toda ação root deve ser registrada no Histórico e oferecer reversão. Recursos sem validação no TX9 ficam desligados.
- Funciona sem internet. A internet só é usada para verificar e baixar uma versão nova do app.

## Compatibilidade

- Alvo principal: Tanix TX9. O aparelho do dono mostra "Android 10.1" com API 25. A API 25 (Android 7.1) é a versão real.
- Versão mínima do app: Android 5.0 (API 21).

## Documentos do projeto

- AGENTS.md: regras permanentes.
- PROJECT_STATE.md: estado atual e próxima tarefa.
- ROADMAP.md: etapas do projeto.
- CHANGELOG.md: histórico de mudanças.
- TESTING.md: testes e resultados.

## Como instalar o APK

Pela página Releases:

1. Abra a página Releases do repositório no GitHub.
2. Abra a versão mais nova e, em Assets, baixe o arquivo tx9-booster-X.Y.Z.apk.
3. Instale o arquivo no TV Box. O Android sempre pede a sua confirmação para instalar.

Para atualizar um app que já está instalado, use o botão VERIFICAR ATUALIZAÇÃO dentro do próprio app (veja abaixo). Não precisa desinstalar o app antigo.

## Como publicar uma versão nova
Atualize o texto da versão (`versao_texto`) em `app/src/main/res/values/strings.xml` primeiro. Depois dos testes passarem, atualize `versionName` e `versionCode` em `app/build.gradle` por último, pois esse arquivo dispara a Release. O Actions precisa terminar verde; depois confirme também a página Releases.


## Atualização do app

O botão VERIFICAR ATUALIZAÇÃO e a checagem ao abrir o app consultam a Release mais recente deste repositório. Se houver versão nova, o app baixa o APK e abre o instalador do Android. O app não instala nada sozinho: a instalação é sempre confirmada por você no Android.

## Limpeza

A tela LIMPEZA AVANÇADA ainda só mexe no cache do próprio TX9 Booster. Toda exclusão passa por `PoliticaCaminhos`; o botão APAGAR só liga depois de SIMULAR e pede confirmação com foco em CANCELAR. A limpeza de outros apps ainda não foi implementada.

## Root, Histórico e Laboratório

O Histórico fica em arquivo privado do app e guarda o valor original, o novo valor, o resultado e o comando inverso. O Reversor confere o estado atual antes de agir. Na Etapa A, a política root nega alterações de pacote até haver inventário real e lista de proteção. Os interruptores do Laboratório começam desligados; ativá-los não executa funções ainda não implementadas. Nenhuma função avançada foi testada no TX9.

## Assinatura do APK
Desde a versão 0.3.7, o APK é assinado com a chave própria do projeto por Secrets do GitHub. Nunca publique a chave privada ou sua senha no repositório. O arquivo `debug.keystore.b64` permanece no repositório e não é usado na compilação. Este APK ainda não é feito para loja de aplicativos.


## Aviso

Este projeto mexe em arquivos do aparelho. A exclusão real apaga só o cache do próprio app, depois de uma confirmação. Nenhuma função é considerada segura no TX9 até ser testada nele.
