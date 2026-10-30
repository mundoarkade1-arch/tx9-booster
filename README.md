# TX9 Booster

Aplicativo Android nativo em Java para manutenção do TV Box Tanix TX9. Interface em português, feita para controle remoto.

## Estado

Versão publicada: 0.7.1. A Etapa A foi testada no TX9, conforme informado pelo dono. A Etapa B, parte 1, exporta um inventário somente leitura; o inventário ainda aguarda teste no aparelho.

## O que existe

- Diagnóstico do aparelho e teste de root.
- Atualização pelo próprio app. A instalação sempre depende da confirmação do dono no Android.
- Limpeza do cache do próprio app com simulação e confirmação.
- Otimização atual e lista de aplicativos somente leitura.
- HISTÓRICO de ações root e tela LABORATÓRIO. O Modo Avançado exige root confirmado.

## Inventário de pacotes na versão 0.7.1

No Laboratório, o botão GERAR INVENTÁRIO executará apenas `pm list packages -s`, `-d`, `-3` e `-f`. O resultado será salvo como `.txt` na pasta pública `Download`, e a tela mostrará o caminho completo. No Android 7.1, autorize o acesso ao armazenamento quando o sistema pedir.
No Laboratório, use GERAR INVENTÁRIO. O app executa apenas `pm list packages -s`, `-d`, `-3` e `-f`, salva o resultado como `.txt` na pasta pública `Download` e mostra o caminho completo. No Android 7.1, autorize o acesso ao armazenamento quando o sistema pedir.

Esta etapa só lê pacotes. Não cria listas para desativar e não altera nem remove aplicativo algum. A função estará disponível depois da publicação da 0.7.1.
Esta etapa só lê pacotes. Não cria listas para desativar e não altera nem remove aplicativo algum.

## Instalar ou atualizar

Na página Releases do GitHub, baixe `tx9-booster-X.Y.Z.apk` e instale no TV Box. Para atualizar um app já instalado, use VERIFICAR ATUALIZAÇÃO. O Android sempre pede confirmação para instalar.

## Publicar versão

Atualize primeiro `versao_texto` em `app/src/main/res/values/strings.xml`. Depois que os testes passarem, atualize `versionName` e `versionCode` em `app/build.gradle` por último. Confira o Actions e a página Releases.

## Segurança

O app abre sem root, mas o Modo Avançado é bloqueado se o teste de root falhar. Recursos novos começam desligados e não são considerados validados no TX9 sem teste no aparelho.

O APK usa a chave própria do projeto por Secrets do GitHub. Nunca publique a chave privada ou sua senha no repositório. O app usa internet somente para verificar atualizações.
