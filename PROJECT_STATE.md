# PROJECT_STATE.md — Estado atual do projeto

Última atualização: 2026-10-30 (Etapa A, versão 0.7.0 publicada)

## Projeto

TX9 Booster: app Android (Java, androidx.core só para FileProvider) de manutenção e otimização para o Tanix TX9 (Amlogic S905W, 1 GB de RAM, Android 7.1 API 25, com root). Interface em português, para controle remoto. Pacote br.com.tx9booster.

## Versão atual

0.7.0 (versionCode 17). Release `v0.7.0` publicada com APK. O workflow do commit `19d66ab3237228bba6631a7317c3f6ad80bb773f` terminou com sucesso nos jobs Testes, Compilar APK e Publicar Release (execução 41064795547). Ainda não testada no TX9.

## O que existe

O app tem tela inicial, Diagnóstico, teste de root, atualização pelo próprio app, limpeza do cache do próprio app, otimização atual e lista somente leitura de aplicativos. Desde a Etapa A, HISTÓRICO lê ações de um arquivo privado e oferece reversão de ações compatíveis; LABORATÓRIO mostra interruptores avançados desligados por padrão e bloqueados até confirmar root.

## Etapa A — Núcleo root

- `RootShell` executa apenas comandos produzidos por `ComandosRoot`, com timeout, captura separada de stdout/stderr e código de saída tipado. O teste de root exige `uid=0`.
- Mutação de pacote exige registro prévio no Histórico, root confirmado, leitura do estado real antes e confirmação depois. A política padrão nega todos os pacotes até existir inventário real e lista de proteção da Etapa B.
- `Historico` usa arquivo texto append-only em `getFilesDir()` e guarda data/hora, ação, alvo, valores original/novo, resultado e inverso.
- `Reversor` confere o estado antes de reverter uma ação, grupo ou todas as ações elegíveis; para no primeiro estado divergente.
- HISTÓRICO e LABORATÓRIO foram ligados à tela inicial. Nenhuma função avançada foi validada no TX9. Os interruptores não executam ações por si mesmos.
- 16 testes JUnit novos passaram em execução JVM manual no Codespaces. O Gradle local não iniciou a tarefa por falta de Android SDK. O Actions da versão 0.7.0 passou testes e compilação no commit `19d66ab`; a Release foi publicada.

## Funções anteriores

- Limpeza real continua restrita ao cache do próprio app, com simulação, confirmação e política de caminhos.
- A otimização existente atua no cache do app e solicita ao Android encerrar processos de apps de usuário; não é uma função root.
- A lista de aplicativos continua somente leitura. Nenhum pacote do TX9 foi inventariado ou classificado nesta etapa.
- Resultados anteriores no aparelho são os descritos no histórico abaixo e foram informados pelo dono; a Etapa A ainda não foi testada no TX9.

## Atualização do app

- O fluxo de atualização foi testado em versões anteriores no TX9, conforme os registros históricos. A instalação da versão 0.7.0 ainda não foi testada no TX9.
- Assinatura com chave própria desde a 0.3.7 (tx9booster.jks, alias tx9booster; Secrets TX9_KEYSTORE_BASE64 e TX9_SENHA).
- Causa exata da recusa antiga NÃO provada.

## Chave de assinatura (RISCO ABERTO)

O dono colou o arquivo e a senha só nos Secrets do GitHub (que não podem ser lidos de volta) e disse que NÃO tem a senha em mãos. Cópia fora do GitHub: não confirmada. Dicas já dadas: não guardar a chave no GitHub (repositório público); procurar a senha na conversa da sessão 4, no histórico do PowerShell, em anotações. Se não achar: criar chave nova, recadastrar os dois Secrets e desinstalar/instalar o app uma vez. O dono adiou o assunto. Tocar no assunto de novo, com calma, em algum momento.

## Pendências

- Pedir ao dono que teste no TX9 o acesso root permitido/negado, abertura do HISTÓRICO e interruptores desligados/bloqueio sem root. Registrar o resultado como informado pelo dono.
- Etapa B depende do inventário real de pacotes do TX9; não inserir nomes presumidos nem permitir desativação até haver classificação e proteção.
- Modo Seguro de Boot ainda não foi implementado. Nenhum ajuste root persistente no boot está disponível.
- A cópia da chave de assinatura fora do GitHub e o acesso à senha continuam não confirmados nos registros anteriores.
- `debug.keystore.b64` e textos sem uso em `strings_instalacao.xml` ainda precisam ser conferidos antes de qualquer remoção.

## Próxima tarefa exata

1. Aguardar o dono testar a Etapa A no TX9 antes de iniciar a Etapa B.
2. Na Etapa B, coletar o inventário real antes de classificar ou desativar pacotes.

## Instruções para o próximo agente

- Seguir `AGENTS.md`: editar diretamente no Codespaces, verificar com terminal e usar Actions quando o SDK local não estiver disponível.
- Não marcar a Etapa A como testada no TX9 sem o dono informar o resultado.
- Confirmar a Release separadamente do check verde do Actions.
- Não recomeçar o projeto.
