# TX9 Booster

Aplicativo Android de manutenção e otimização para o TV Box Tanix TX9 (Amlogic S905W, 1 GB de RAM, com root).

## Estado

Em desenvolvimento. Versão 0.4.0. Já existe: tela inicial para controle remoto, tela de Diagnóstico, teste de root, atualização pelo próprio app (botão VERIFICAR ATUALIZAÇÃO e checagem ao abrir o app, com download do APK e abertura do instalador do Android) e a tela LIMPEZA AVANÇADA, com dois botões: SIMULAR (só mede o cache do próprio app e não apaga nada) e APAGAR CACHE DO APP (apaga só o cache do próprio app, depois de uma simulação e de uma confirmação, e mostra um relatório com Antes, Apagados e Depois). A atualização por cima do app instalado funcionou no TX9. A exclusão real foi testada no TX9 uma vez e o cache ficou em zero; faltam conferir os números do relatório. Cache de outros aplicativos não é apagado. Veja PROJECT_STATE.md.

## Objetivo

- Diagnóstico do aparelho: Android, RAM, armazenamento, ABI, root.
- Limpeza segura, com modo de simulação.
- Otimização rápida com relatório antes e depois, só com números medidos.
- Interface em português, feita para controle remoto.
- Funciona sem root. As funções de root são opcionais e ficam atrás de confirmação.
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

Suba versionName e versionCode em app/build.gradle e também o texto da versão (versao_texto) em app/src/main/res/values/strings.xml. Salve o strings.xml primeiro e o build.gradle por último, porque é o build.gradle que dispara a Release. Ao salvar no main, se os testes e a compilação passarem, o GitHub cria sozinho a Release vX.Y.Z com o APK. Sem subir a versão, nenhuma Release nova é criada. O check verde no Actions não basta: confirme na página Releases que a Release apareceu.

## Atualização do app

O botão VERIFICAR ATUALIZAÇÃO e a checagem ao abrir o app consultam a Release mais recente deste repositório. Se houver versão nova, o app baixa o APK e abre o instalador do Android. O app não instala nada sozinho: a instalação é sempre confirmada por você no Android.

## Limpeza

A tela LIMPEZA AVANÇADA só mexe no cache do próprio TX9 Booster (a pasta de cache interna e a externa do app). Toda exclusão passa por uma trava de segurança (PoliticaCaminhos) que proíbe caminhos do sistema e nunca segue links simbólicos. O botão APAGAR só liga depois de SIMULAR e sempre pede confirmação, com o foco em CANCELAR.

## Assinatura do APK

Desde a versão 0.3.7, o APK é assinado com uma chave própria do dono do projeto. A chave e a senha ficam guardadas nos Secrets do GitHub e fora do repositório. As versões antigas usavam uma chave pública de testes. O arquivo debug.keystore.b64 continua no repositório, mas não é mais usado na compilação. Este APK ainda não é feito para loja de aplicativos.

## Aviso

Este projeto mexe em arquivos do aparelho. A exclusão real apaga só o cache do próprio app, depois de uma confirmação. Nenhuma função é considerada segura no TX9 até ser testada nele.
