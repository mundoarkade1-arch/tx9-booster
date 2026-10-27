# PROJECT_STATE.md — Estado atual do projeto

Última atualização: 2026-10-26

## Objetivo
Criar o TX9 Booster: app Android de manutenção e otimização para o Tanix TX9 (1 GB de RAM, root). Diagnóstico, limpeza segura, otimização rápida, tudo medido de verdade, com interface para controle remoto.

## Etapa atual
Etapa 1 — Planejamento e compatibilidade (em andamento).

## Última tarefa concluída
Repositório tx9-booster criado pelo dono no GitHub. Arquivo AGENTS.md criado pelo dono (informado por ele; o agente não conseguiu ver o repositório).

## Funcionalidades implementadas
Nenhuma. Ainda não existe código do aplicativo.

## Funcionalidades ainda não implementadas
Tudo: estrutura Android, interface de TV, diagnóstico, verificação de root, limpeza, otimização rápida, aplicativos, histórico.

## O que foi compilado
Nada.

## Testes realizados
Nenhum.

## Testes necessários
Ver TESTING.md.

## Erros conhecidos
Nenhum registrado.

## Inconsistências a resolver
- Versão do Android do TX9: há registro de 7.1 e de 10. O app deve ler a versão real e mostrar no diagnóstico. Até lá, o projeto usa o mínimo seguro.
- Se o repositório está Public ou Private: o dono não confirmou.

## Decisões técnicas
- Aplicativo nativo em Java ou Kotlin, a decidir na Etapa 2 pelo que compilar de forma mais simples e leve.
- minSdk 21 (Android 5.0), para rodar no Android 7.1 e no 10.
- Compilação do APK pelo GitHub Actions, APK de depuração no início.
- Sem bibliotecas extras, sem banco de dados, sem serviço em segundo plano por enquanto.
- Sem root, o app continua funcionando só com as funções seguras.

## Arquivos modificados recentemente
AGENTS.md, PROJECT_STATE.md (criados pelo dono no GitHub).

## Próxima tarefa exata
Criar ROADMAP.md, CHANGELOG.md, TESTING.md e README.md. Depois, a estrutura mínima do app (Etapa 2).

## Instruções especiais para o próximo agente
- Dono iniciante: siga as regras de AGENTS.md (um passo, um arquivo, português simples).
- Conferir o repositório real antes de assumir qualquer coisa deste arquivo.
- Não recomeçar o projeto.
