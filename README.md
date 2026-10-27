# TX9 Booster

Aplicativo Android de manutenção e otimização para o TV Box Tanix TX9 (Amlogic S905W, 1 GB de RAM, com root).

## Estado

Em desenvolvimento. Versão 0.2.0 (o número da versão ainda não foi atualizado). Já existe: tela inicial para controle remoto, tela de Diagnóstico, teste de root e a tela LIMPEZA em modo de simulação, que só mede o cache do próprio app e não apaga nada. A tela de limpeza ainda não foi testada no TX9. A exclusão real ainda não existe. Veja PROJECT_STATE.md.

## Objetivo

- Diagnóstico do aparelho: Android, RAM, armazenamento, ABI, root.
- Limpeza segura, com modo de simulação.
- Otimização rápida com relatório antes e depois, só com números medidos.
- Interface em português, feita para controle remoto.
- Funciona sem root. As funções de root são opcionais e ficam atrás de confirmação.

## Compatibilidade

- Alvo principal: Tanix TX9. O aparelho do dono mostra "Android 10.1" com API 25. A API 25 (Android 7.1) é a versão real.
- Versão mínima do app: Android 5.0 (API 21).

## Documentos do projeto

- AGENTS.md: regras permanentes.
- PROJECT_STATE.md: estado atual e próxima tarefa.
- ROADMAP.md: etapas do projeto.
- CHANGELOG.md: histórico de mudanças.
- TESTING.md: testes e resultados.

## Como gerar e instalar o APK

1. Abra a aba Actions do repositório no GitHub.
2. Clique na execução mais recente, chamada Compilar APK, que esteja com o check verde.
3. Role até a área Artifacts e clique em tx9-booster-debug. O GitHub baixa um arquivo zip.
4. Extraia o zip. Dentro dele está o arquivo app-debug.apk.
5. Instale o app-debug.apk no TV Box.
6. Atualizar por cima do app instalado ainda não foi confirmado. Se o Android recusar, desinstale o app antigo antes de instalar o novo.

O APK é de depuração, assinado com uma chave pública de testes (debug.keystore.b64). Ele não serve para publicar em loja.

## Aviso

Este projeto mexe em arquivos do aparelho. Nenhuma função é considerada segura no TX9 até ser testada nele.
