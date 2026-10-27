# PROJECT_STATE.md — Estado atual do projeto

Última atualização: 2026-10-27 (fim da sessão 3)

## Objetivo

Criar o TX9 Booster: app Android de manutenção e otimização para o Tanix TX9 (1 GB de RAM, root). Diagnóstico, limpeza segura, otimização rápida, tudo medido de verdade, com interface para controle remoto.

## Etapa atual

Etapa 4 — Limpeza segura (análise e simulação existem no código; falta testar a tela LIMPEZA no TX9; nenhuma exclusão real existe).

Em paralelo, a sessão 3 foi toda sobre a ATUALIZAÇÃO DO APP. Ela está PARADA por um problema de assinatura (ver "PROBLEMA ABERTO").

## PROBLEMA ABERTO (PRIMEIRA COISA DA PRÓXIMA SESSÃO)

O app baixa a atualização sozinho e abre a tela de instalação do Android (testado no TX9, funciona, sem abrir navegador). Mas o Android RECUSA instalar por cima, com a mensagem: "O app não foi instalado. Já existe um pacote com esse mesmo nome."

Aconteceu em dois testes:
1. App antigo já instalado no TX9 -> 0.3.3 (recusou; o dono desinstalou o antigo).
2. Depois de instalar a 0.3.3 pelo APK baixado da página Releases -> 0.3.4 pelo botão VERIFICAR ATUALIZAÇÃO (recusou de novo).

Como a 0.3.4 tem versionCode maior (7 > 6), não é "versão menor". A causa mais provável é ASSINATURA DIFERENTE entre os APKs. Isso NÃO está confirmado.

Hipóteses a conferir (nesta ordem):
1. O arquivo .github/workflows/build.yml (NÃO foi lido na sessão 3) pode usar a chave fixa (debug.keystore.b64) só na tarefa "Compilar APK" e NÃO na tarefa "Publicar Release", que então assinaria cada Release com uma chave de teste nova e diferente. app/build.gradle NÃO tem signingConfig.
2. O APK instalado no TX9 pode ter vindo de outra tarefa (artefato do Actions) que usa outra chave.
3. Outra causa de "pacote com o mesmo nome" que não seja assinatura.

O dono disse que no outro app dele (asc-tvbox) ele criou a PRÓPRIA chave de assinatura no PowerShell, com senha, e colocou no GitHub, e lá a atualização funciona. Aqui o projeto usa a chave de depuração pública (debug.keystore.b64). Caminho provável: usar uma chave PRÓPRIA do dono, guardada nos Secrets do GitHub (como no asc-tvbox). REGRA: o agente NUNCA pede nem vê senha, chave ou valores dos Secrets; o dono mesmo cadastra no GitHub, passo a passo. Trocar de chave exige UMA última desinstalação do app no TX9.

Primeira tarefa: pedir ao dono o build.yml INTEIRO (https://github.com/mundoarkade1-arch/tx9-booster/blob/main/.github/workflows/build.yml), achar qual chave cada tarefa usa, e só então decidir.

## Última tarefa concluída

Sessão 3, nesta ordem (todas com check verde informado pelo dono; o agente NÃO viu as telas):
1. Conferido: Release v0.3.0 existe com o .apk. strings.xml corrigido (rodapé).
2. Versão 0.3.1 e 0.3.2 publicadas (Releases confirmadas pelo dono até a v0.3.2; v0.3.3 também confirmada).
3. Pedido do dono: a atualização não deve abrir o navegador; deve baixar e instalar.
4. Primeira versão (0.3.2): download pelo DownloadManager do Android. Funcionou o download no TX9, mas o app antigo instalado ainda abria o navegador.
5. Segunda versão (0.3.3), copiada do jeito do app asc-tvbox (Updater.java dele): classe nova InstaladorAtualizacao.java, FileProvider, file_paths.xml, androidx.core 1.12.0, android.useAndroidX=true. Testada no TX9: baixa, abre a tela de instalação, mas o Android recusa (PROBLEMA ABERTO).
6. Versão 0.3.4 (versionCode 7) publicada só para testar a atualização por cima.

## Funcionalidades implementadas

- Tela inicial com botões grandes, navegação por controle remoto e foco amarelo visível. Botões Otimização, Aplicativos e Histórico só mostram "Esta função ainda não foi criada."
- DIAGNÓSTICO: fabricante, modelo, placa, Android e API, kernel, ABIs, RAM, armazenamento; teste de root com comando fixo "su -c id" e limite de 15 segundos, só ao apertar o botão.
- LIMPEZA (LimpezaActivity): modo de simulação. Mede o cache interno e externo do próprio app com o Analisador. Nunca apaga.
- PoliticaCaminhos e Analisador: trava de segurança e leitura segura (ver sessão 2; sem mudança na sessão 3).
- ATUALIZAÇÃO: VerificadorAtualizacao consulta https://api.github.com/repos/mundoarkade1-arch/tx9-booster/releases/latest (tempo limite 8 s, resposta até 256 KB, sem redirecionamento, só aceita endereço que comece com https://github.com/mundoarkade1-arch/tx9-booster/releases/download/ e termine em .apk). Ao abrir o app: checagem silenciosa (linha amarela só se houver versão nova). Botão VERIFICAR ATUALIZAÇÃO: sempre responde; se houver versão nova, mostra o endereço e agora CHAMA InstaladorAtualizacao.baixarEInstalar (mostra a porcentagem do download).
- InstaladorAtualizacao.java (NOVO): baixa o APK para cache/updates/TX9-Booster.apk, só https e só do repositório do dono, até 150 MB, confere que o arquivo começa com "PK" e abre o instalador do Android por FileProvider (authority = applicationId + ".fileprovider"). No Android 8+ abre a tela "fontes desconhecidas" se faltar permissão. NÃO confere SHA-256 (o asc-tvbox confere, via version.json; o tx9-booster ainda não publica esse arquivo).
- Compilação automática pelo GitHub Actions com três tarefas: Testes, Compilar APK e Publicar Release (só em push no main; lê o versionName de app/build.gradle; cria a Release vX.Y.Z com tx9-booster-X.Y.Z.apk se ela não existir; se existir, não faz nada).

## Funcionalidades ainda não implementadas

Limpeza real (exclusão), otimização rápida, lista de aplicativos, histórico, ícone e banner do app.

## O que foi compilado e testado

- GitHub Actions verde (informado pelo dono) após cada mudança da sessão 3, incluindo a Release v0.3.4.
- Testes automáticos (sem internet): PoliticaCaminhosTest, AnalisadorTest (10), VerificadorAtualizacaoTest (11). Não testam o download nem a instalação.
- Testado no TX9 pelo dono: Etapas 2 e 3; botão e checagem de atualização (acham a versão nova pela internet: o https do TX9 com o GitHub FUNCIONA); download direto e tela de instalação (funcionam).
- NÃO funciona ainda: instalar por cima (PROBLEMA ABERTO).
- NÃO testado no TX9: tela LIMPEZA.

## Descobertas no aparelho real (informadas pelo dono)

- Android: a tela mostra "10.1" com API 25. A API 25 é a real (Android 7.1).
- Root: o teste retornou "ROOT FUNCIONANDO".
- O TX9 tem navegador (o app antigo abriu o Google ao tentar baixar).
- O https do TX9 com o GitHub funciona.

## Erros conhecidos e pendências

- PROBLEMA ABERTO da assinatura (acima).
- README.md ainda diz "Versão 0.2.0" e descreve como instalar só pelo Actions. Atualizar para 0.3.4 e para o novo jeito (ROADMAP.md também não foi revisado na sessão 3).
- strings_instalacao.xml tem textos inst_sem_permissao e inst_recusada que a MainActivity nova não usa mais (limpeza opcional; não é erro).
- A mensagem "versão nova disponível" ainda diz "Toque no botão para baixar", mas o botão já baixa e instala.
- O APK das Releases é assinado com a chave de depuração pública (se for mesmo o caso). Qualquer pessoa pode fabricar um APK com essa chave. Assinatura privada estava prevista só na Etapa 9, mas o PROBLEMA ABERTO pode antecipar isso.
- Uma Release só é criada quando o versionName sobe. Mudar o código sem subir a versão NÃO gera Release nova.
- Os valores de RAM, armazenamento, kernel e ABIs ainda não foram comparados com outra fonte. Teste de root com permissão negada ainda não foi feito. O app ainda não tem ícone nem banner.
- O agente consegue abrir pelo site só arquivos da raiz do repositório e a página principal pode vir de cópia antiga. Arquivos dentro de pastas precisam ser colados pelo dono. Tentativa de abrir PROJECT_STATE.md direto falhou na sessão 3.

## Decisões técnicas

- Java, sem banco de dados, sem serviço em segundo plano. AGORA usa androidx.core 1.12.0 (só para o FileProvider), com android.useAndroidX=true. JUnit 4.13.2 só para testes.
- minSdk 21, targetSdk 28, compileSdk 34. Pacote br.com.tx9booster. versionCode 7, versionName 0.3.4.
- Sem Gradle wrapper. GitHub Actions: Java 17, Gradle 8.5.
- Rede: única exceção à regra "sem internet" é a verificação e o download da atualização, aprovada pelo dono (2026-10-26). Só https, só o repositório do dono, com tempo limite, sem serviço em segundo plano. Quem instala é o Android, com confirmação do dono (nenhum app pula essa tela).
- Root: sempre comandos fixos, com limite de tempo e checagem do código de saída.
- A limpeza só pode apagar o que PoliticaCaminhos permitir. Área inicial: cache do próprio app.

## Arquivos do projeto

Raiz: AGENTS.md, PROJECT_STATE.md, ROADMAP.md, CHANGELOG.md, TESTING.md, README.md, settings.gradle, build.gradle, gradle.properties (agora com android.useAndroidX=true), debug.keystore.b64, .github/workflows/build.yml. Em app/: build.gradle; src/main/AndroidManifest.xml (agora com REQUEST_INSTALL_PACKAGES e o provider FileProvider); res/values/strings.xml, strings_atualizacao.xml e strings_instalacao.xml (novo); res/xml/file_paths.xml (novo, cache-path "updates/"); res/layout/activity_main.xml, activity_diagnostico.xml, activity_limpeza.xml; res/drawable/botao_fundo.xml. Em app/src/main/java/br/com/tx9booster/: MainActivity, DiagnosticoActivity, LimpezaActivity, PoliticaCaminhos, Analisador, VerificadorAtualizacao, InstaladorAtualizacao (novo). Em app/src/test/java/br/com/tx9booster/: PoliticaCaminhosTest, AnalisadorTest, VerificadorAtualizacaoTest.

## Próxima tarefa exata

1. Resolver o PROBLEMA ABERTO: pedir o build.yml inteiro, achar qual chave cada tarefa usa. Corrigir para TODOS os APKs saírem com a MESMA chave. Se for usar a chave própria do dono (como no asc-tvbox), guiar o dono a cadastrar os Secrets no GitHub sozinho (o agente nunca pede nem vê os valores).
2. Depois da correção: subir a versão (ex.: 0.3.5), pedir ao dono para desinstalar o app UMA vez e instalar a nova pela página Releases; depois publicar mais uma versão (0.3.6) e testar a atualização por cima pelo botão. Anotar a mensagem exata do Android se falhar.
3. Corrigir README.md e ROADMAP.md (pedir os arquivos inteiros antes), e a frase "Toque no botão para baixar".
4. Pedir ao dono o teste da tela LIMPEZA no TX9 (apertar simular e dizer o que aparece).
5. Só depois: planejar a exclusão real, só do cache do próprio app, com confirmação e relatório antes e depois.

## Instruções especiais para o próximo agente

- Dono muito iniciante, fala por voz, usa só o site do GitHub, sem terminal. Siga AGENTS.md: português simples, UM passo e UM arquivo por vez, uma pergunta por vez com respostas numeradas, arquivo INTEIRO para colar, nunca reescrever código de memória, nunca pedir senha, chave de assinatura nem link M3U.
- Combinado: o dono responde "1" para "salvei e a execução ficou verde". Respostas como "11" são ambíguas: confirmar. Às vezes ele manda só o número sem colar o arquivo: peça de novo para COLAR o texto.
- O dono já colou por engano textos do OUTRO projeto (asc-tvbox, app de IPTV). Se o texto falar de canais, filmes ou ExoPlayer, é do outro projeto: ignore e avise com calma.
- Antes de criar QUALQUER arquivo de código, conferir se já existe (o GitHub avisa "A file with the same name already exists").
- Para ler arquivos dentro de pastas, pedir "Copy raw file" e colar na conversa. O agente não consegue abrir esses arquivos sozinho.
- Não recomeçar o projeto.
