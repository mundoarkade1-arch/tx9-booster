# TX9 Booster

Aplicativo Android de manutenção e otimização para o TV Box Tanix TX9 (Amlogic S905W, 1 GB de RAM, com root).

## Estado

Em desenvolvimento. Versão 0.3.0. Já existe: tela inicial para controle remoto, tela de Diagnóstico, teste de root, tela LIMPEZA em modo de simulação (só mede o cache do próprio app e não apaga nada) e a função de atualização (botão VERIFICAR ATUALIZAÇÃO e checagem ao abrir o app). A limpeza e a atualização ainda não foram testadas no TX9. A exclusão real ainda não existe. Veja PROJECT_STATE.md.

## Objetivo

- Diagnóstico do aparelho: Android, RAM, armazenamento, ABI, root.
- Limpeza segura, com modo de simulação.
- Otimização rápida com relatório antes e depois, só com números medidos.
- Interface em português, feita para controle remoto.
- Funciona sem root. As funções de root são opcionais e ficam atrás de confirmação.
- Funciona sem internet. A internet só é usada para verificar se há versão nova do app.

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

Pela aba Releases (a partir da versão 0.3.0, se a Release existir):

1. Abra a página Releases do repositório no GitHub.
2. Abra a versão mais nova e, em Assets, baixe o arquivo tx9-booster-X.Y.Z.apk.
3. Instale o arquivo no TV Box.

Pela aba Actions (alternativa):

1. Abra a aba Actions do repositório no GitHub.
2. Clique na execução mais recente, chamada Compilar APK, que esteja com o check verde.
3. Role até a área Artifacts e clique em tx9-booster-debug. O GitHub baixa um arquivo zip.
4. Extraia o zip. Dentro dele está o arquivo app-debug.apk. Instale no TV Box.

Atualizar por cima do app instalado ainda não foi confirmado. Se o Android recusar, desinstale o app antigo antes de instalar o novo e anote a mensagem exata.

## Como publicar uma versão nova

Suba versionName e versionCode em app/build.gradle. Ao salvar no main, se os testes e a compilação passarem, o GitHub cria sozinho a Release vX.Y.Z com o APK. Sem subir a versão, nenhuma Release nova é criada.

## Atualização do app

O botão VERIFICAR ATUALIZAÇÃO e a checagem ao abrir o app consultam a Release mais recente deste repositório. Se houver versão nova, o app mostra o endereço do download e tenta abrir. O app não instala nada sozinho: a instalação é confirmada por você no Android.

O APK é de depuração, assinado com uma chave pública de testes (debug.keystore.b64). Ele não serve para publicar em loja.

## Aviso

Este projeto mexe em arquivos do aparelho. Nenhuma função é considerada segura no TX9 até ser testada nele.
