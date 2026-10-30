# PROJECT_STATE.md — Estado atual do projeto

Última atualização: 2026-10-30 — Etapa B, parte 1

## Projeto

TX9 Booster: app Android nativo em Java para manutenção do Tanix TX9. Interface em português, feita para controle remoto. Pacote `br.com.tx9booster`.

## Versão

Release publicada: 0.7.0 (versionCode 17). No workspace, `app/build.gradle` e `strings.xml` estão em 0.7.1 (versionCode 18), ainda aguardando o workflow final e a publicação da Release.

## Base do aplicativo

O app tem tela inicial, Diagnóstico, teste de root, atualização pelo próprio app, limpeza do cache do próprio app, otimização atual e lista de aplicativos somente leitura.

## Etapa A — Núcleo root (0.7.0)

Estado: Implementada, compilada, publicada e testada no TX9. O dono informou em 2026-10-30 que testou a Etapa A no aparelho e que tudo deu certo.

- `RootShell` executa comandos fixos da `ComandosRoot`, verifica timeout e código de saída, captura stdout/stderr e confirma `uid=0`.
- `Historico` grava ações em arquivo privado. `Reversor` confere o estado antes de reverter.
- A tela HISTÓRICO lista as ações. A tela LABORATÓRIO mantém recursos avançados desligados por padrão e bloqueados sem root.
- A política padrão não permite alterar pacotes enquanto não houver inventário real e proteção.

## Etapa B — Inventário e debloat

Parte 1 em implementação para 0.7.1: o Laboratório oferece exportação somente leitura de `pm list packages -s`, `-d`, `-3` e `-f` pelo RootShell. O resultado é gravado em `.txt` na pasta pública `Download`, e a tela mostra o caminho completo. Nenhuma lista de desativação foi criada e nenhum pacote foi alterado.

O código passou 20 testes JUnit JVM manuais e o Actions do commit `2860d88` (execução 41069125960) passou testes e compilou o APK na versão-base 0.7.0. O Gradle local não iniciou por falta de Android SDK. O inventário ainda não foi testado no TX9.

## Pendências

- Aguardar o workflow da versão 0.7.1 e confirmar a Release.
- O dono deve gerar o inventário no TX9 e trazer o conteúdo do arquivo `.txt` para análise. Classificar somente os nomes presentes no arquivo.
- Não desativar nem remover pacotes nesta parte.
- Modo Seguro de Boot ainda não foi implementado. Nenhum ajuste root persistente no boot está disponível.
- Os registros anteriores dizem que a cópia da chave de assinatura fora do GitHub e a senha não foram confirmadas. Nunca pedir a senha.
- Conferir `debug.keystore.b64` e textos sem uso em `strings_instalacao.xml` antes de qualquer remoção.

## Próxima tarefa exata

1. Conferir o Actions e a Release `v0.7.1`.
2. No TX9, gerar o inventário e trazer o texto do arquivo para análise.
3. Só depois classificar os pacotes observados. Não desativar nada sem etapa e autorização próprias.

## Instruções para o próximo agente

Leia `AGENTS.md`, `PROJECT_STATE.md`, `ROADMAP.md`, `CHANGELOG.md`, `TESTING.md` e `README.md`. Confira o estado real do repositório, continue desta etapa e atualize a documentação antes de encerrar.
