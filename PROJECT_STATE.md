# PROJECT_STATE.md — Estado atual do projeto

Última atualização: 2026-10-27 (sessão 4)

## Objetivo

Criar o TX9 Booster: app Android de manutenção e otimização para o Tanix TX9 (1 GB de RAM, root). Diagnóstico, limpeza segura, otimização rápida, tudo medido de verdade, com interface para controle remoto.

## Etapa atual

Etapa 4 — Limpeza segura (análise e simulação existem no código; falta testar a tela LIMPEZA no TX9; nenhuma exclusão real existe).

## Problema da atualização: RESOLVIDO

Antes, o Android recusava instalar por cima ("Já existe um pacote com esse mesmo nome"). Na sessão 4:
- O build.yml foi lido. Todas as tarefas usavam o MESMO APK, feito uma vez na tarefa "Compilar APK" com a chave pública de depuração (debug.keystore.b64). A hipótese "cada tarefa usa uma chave" NÃO se confirmou.
- A 0.3.6 ainda foi recusada ao atualizar por cima da 0.3.5 (mensagem: "O app não foi instalado. Já existe um pacote com esse mesmo nome").
- Causa exata NÃO provada, mas o problema sumiu com a chave própria do dono.
- O dono criou a chave própria no PowerShell (arquivo tx9booster.jks, alias tx9booster) e cadastrou sozinho dois Secrets no GitHub: TX9_KEYSTORE_BASE64 e TX9_SENHA. O agente nunca viu os valores.
- build.yml e app/build.gradle agora assinam o APK com essa chave (a tarefa "Compilar APK" falha se os Secrets faltarem).
- Resultado no TX9: a 0.3.7 instalou por cima, sem desinstalar. A 0.3.8 foi instalada pelo botão do próprio app, por cima da 0.3.7, e a tela inicial mostrou "Versão 0.3.8". A atualização por cima FUNCIONA.
- O dono precisa guardar o arquivo tx9booster.jks (cópia fora do computador) e a senha. Se perder, precisa desinstalar o app de novo.

## Última tarefa concluída

Sessão 4:
1. Lido o build.yml. Chave própria criada e cadastrada nos Secrets (feito pelo dono).
2. Versões 0.3.5 e 0.3.6 (chave de teste) e 0.3.7 e 0.3.8 (chave própria) publicadas; Releases conferidas pelo dono.
3. Corrigido o texto de versão da tela inicial (estava escrito à mão como 0.3.4 em app/src/main/res/values/strings.xml, linha versao_texto). Ela PRECISA ser mudada a mão a cada versão nova.

## Funcionalidades implementadas

- Tela inicial com botões grandes, navegação por controle remoto e foco amarelo visível. Botões Otimização, Aplicativos e Histórico só mostram "Esta função ainda não foi criada."
- DIAGNÓSTICO: fabricante, modelo, placa, Android e API, kernel, ABIs, RAM, armazenamento; teste de root com comando fixo "su -c id" e limite de 15 segundos, só ao apertar o botão.
- LIMPEZA (LimpezaActivity): modo de simulação. Mede o cache interno e externo do próprio app com o Analisador. Nunca apaga.
- PoliticaCaminhos e Analisador: trava de segurança e leitura segura.
- ATUALIZAÇÃO: VerificadorAtualizacao consulta a última Release do repositório (https, tempo limite 8 s, só aceita endereço do repositório do dono terminado em .apk). InstaladorAtualizacao baixa o APK, confere que começa com "PK", e abre o instalador do Android por FileProvider. NÃO confere SHA-256.
- Compilação automática pelo GitHub Actions com três tarefas: Testes, Compilar APK (assinada com a chave própria) e Publicar Release (só em push no main; cria a Release vX.Y.Z se ela não existir).

## Funcionalidades ainda não implementadas

Limpeza real (exclusão), otimização rápida, lista de aplicativos, histórico, ícone e banner do app.

## O que foi compilado e testado

- GitHub Actions verde (informado pelo dono) após cada mudança, incluindo a Release v0.3.8.
- Testes automáticos (sem internet): PoliticaCaminhosTest, AnalisadorTest, VerificadorAtualizacaoTest. Não testam download nem instalação.
- Testado no TX9 pelo dono: Etapas 2 e 3; verificação e download da atualização; instalação por cima (0.3.7 -> 0.3.8) com a chave própria.
- NÃO testado no TX9: tela LIMPEZA.

## Descobertas no aparelho real (informadas pelo dono)

- Android: a tela mostra "10.1" com API 25. A API 25 é a real (Android 7.1).
- Root: o teste retornou "ROOT FUNCIONANDO".
- O https do TX9 com o GitHub funciona.

## Erros conhecidos e pendências

- README.md ainda diz "Versão 0.2.0" e descreve o jeito antigo de instalar. ROADMAP.md não foi revisado. Pedir os arquivos inteiros antes de mudar.
- A mensagem "versão nova disponível" ainda diz "Toque no botão para baixar", mas o botão já baixa e instala.
- strings_instalacao.xml tem textos inst_sem_permissao e inst_recusada que não são mais usados (limpeza opcional).
- O arquivo debug.keystore.b64 ainda está na raiz, mas o build.yml não o usa mais.
- Uma Release só é criada quando o versionName sobe em app/build.gradle (o versionCode também deve subir).
- Valores de RAM, armazenamento, kernel e ABIs ainda não foram comparados com outra fonte. Teste de root com permissão negada ainda não foi feito. O app ainda não tem ícone nem banner.
- O agente abre pelo site só arquivos da raiz do repositório, e a página principal pode vir de cópia antiga. Arquivos dentro de pastas precisam ser colados pelo dono.

## Decisões técnicas

- Java, sem banco de dados, sem serviço em segundo plano. androidx.core 1.12.0 (só para o FileProvider), android.useAndroidX=true. JUnit 4.13.2 só para testes.
- minSdk 21, targetSdk 28, compileSdk 34. Pacote br.com.tx9booster. versionCode 11, versionName 0.3.8.
- Sem Gradle wrapper. GitHub Actions: Java 17, Gradle 8.5.
- Assinatura: chave própria do dono, nos Secrets TX9_KEYSTORE_BASE64 e TX9_SENHA. O app/build.gradle lê as variáveis TX9_KEYSTORE_ARQUIVO e TX9_KEYSTORE_SENHA.
- Rede: única exceção à regra "sem internet" é a verificação e o download da atualização, aprovada pelo dono. Quem instala é o Android, com confirmação do dono.
- Root: sempre comandos fixos, com limite de tempo e checagem do código de saída.
- A limpeza só pode apagar o que PoliticaCaminhos permitir. Área inicial: cache do próprio app.

## Próxima tarefa exata

1. Corrigir README.md e ROADMAP.md (pedir os arquivos inteiros antes), e a frase "Toque no botão para baixar" (em strings_atualizacao.xml; pedir o arquivo inteiro antes).
2. Pedir ao dono o teste da tela LIMPEZA no TX9 (apertar simular e dizer o que aparece).
3. Só depois: planejar a exclusão real, só do cache do próprio app, com confirmação e relatório antes e depois.
4. Ideia opcional: publicar um version.json com o SHA-256 do APK e conferir no download.

## Instruções especiais para o próximo agente

- Dono muito iniciante, fala por voz, usa só o site do GitHub, sem terminal (a chave foi a única exceção: PowerShell, guiado). Siga AGENTS.md: português simples, UM passo e UM arquivo por vez, uma pergunta por vez com respostas numeradas, arquivo INTEIRO para colar, nunca reescrever código de memória, nunca pedir senha, chave de assinatura nem link M3U.
- O dono costuma responder "ok" para "feito". Se a resposta não bater com as opções numeradas, confirme.
- O dono já colou por engano textos do OUTRO projeto (asc-tvbox, app de IPTV). Se o texto falar de canais, filmes ou ExoPlayer, ignore e avise com calma.
- Antes de criar QUALQUER arquivo de código, conferir se já existe.
- Para ler arquivos dentro de pastas, pedir "Copy raw file" e colar na conversa.
- A cada versão nova: mudar versionCode e versionName em app/build.gradle E o texto versao_texto em strings.xml.
- Não recomeçar o projeto.
