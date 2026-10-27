# PROJECT_STATE.md — Estado atual do projeto

Última atualização: 2026-10-26 (fim da sessão 2)

## Objetivo

Criar o TX9 Booster: app Android de manutenção e otimização para o Tanix TX9 (1 GB de RAM, root). Diagnóstico, limpeza segura, otimização rápida, tudo medido de verdade, com interface para controle remoto.

## Etapa atual

Etapa 4 — Limpeza segura (análise e simulação existem no código; falta testar no TX9; nenhuma exclusão real existe). Em paralelo, a função de atualização do app foi implementada na sessão 2 (versão 0.3.0), mas NÃO foi testada no TX9 e a Release v0.3.0 NÃO foi confirmada.

## Última tarefa concluída

Sessão 2, nesta ordem: (1) conferido o repositório real e corrigida a documentação; (2) AnalisadorTest ampliado de 6 para 10 testes; (3) implementada a atualização do app: permissão de internet, VerificadorAtualizacao.java, VerificadorAtualizacaoTest.java (11 testes), textos em strings_atualizacao.xml, botão VERIFICAR ATUALIZAÇÃO e linha de aviso no activity_main.xml, ligação no MainActivity.java (checagem silenciosa ao abrir e botão); (4) versão subida para 0.3.0 (versionCode 3); (5) build.yml ganhou a terceira tarefa "Publicar Release". Depois de cada passo o dono informou check verde. Depois do build.yml o dono informou verde nos três blocos (Testes, Compilar APK, Publicar Release). O agente NÃO viu as telas: é informação do dono.

## PRIMEIRA COISA A FAZER NA PRÓXIMA SESSÃO

Perguntar ao dono (resposta numerada): abrir https://github.com/mundoarkade1-arch/tx9-booster/releases e dizer: 1) a Release v0.3.0 existe com o arquivo tx9-booster-0.3.0.apk; 2) existe sem o .apk; 3) não há Release; 4) outro erro. A tarefa "Publicar Release" pode ter ficado verde sem criar nada (por exemplo, se a Release já existisse), então o verde não prova a Release. Se for 2 ou 3, pedir a linha do passo "Criar a Release se ainda nao existir" na aba Actions. Se o erro for "Resource not accessible by integration", o dono precisa liberar em Settings > Actions > General > Workflow permissions > "Read and write permissions".

## Funcionalidades implementadas

- Tela inicial com botões grandes, navegação por controle remoto e foco amarelo visível. Botões Otimização, Aplicativos e Histórico só mostram "Esta função ainda não foi criada."
- DIAGNÓSTICO: fabricante, modelo, placa, Android e API, kernel, ABIs, RAM, armazenamento; teste de root com comando fixo "su -c id" e limite de 15 segundos, só ao apertar o botão.
- LIMPEZA (LimpezaActivity): modo de simulação. Mede o cache interno e o cache externo do próprio app com o Analisador e mostra arquivos, bytes, ignorados, aviso de limite e os 10 maiores itens. Nunca apaga.
- PoliticaCaminhos: trava de segurança (só permite dentro de área autorizada; lista proibida vence; resolve ".." e links; descarta áreas perigosas).
- Analisador: somente leitura, só em área autorizada, ignora links simbólicos, limites de 20000 arquivos, 20 níveis e 100 itens na lista.
- ATUALIZAÇÃO (nova, versão 0.3.0): VerificadorAtualizacao consulta https://api.github.com/repos/mundoarkade1-arch/tx9-booster/releases/latest, tempo limite de 8 segundos, resposta limitada a 256 KB, sem seguir redirecionamentos, compara versões como números (0.10.0 > 0.9.0), só aceita endereços de download que comecem com https://github.com/mundoarkade1-arch/tx9-booster/releases/download/ e terminem em .apk (senão usa a página da Release), valida o número da versão. Ao abrir o app: checagem silenciosa, só mostra uma linha amarela se houver versão nova (uma vez por abertura, não a cada giro de tela). Botão VERIFICAR ATUALIZAÇÃO: sempre responde (atualizado, versão nova ou erro); se houver versão nova, mostra o endereço escrito na tela e tenta abrir o download. O app NÃO instala sozinho: o dono confirma a instalação no Android.
- Compilação automática pelo GitHub Actions com chave de depuração fixa e pública. Três tarefas: Testes (gradle testDebugUnitTest), Compilar APK (gradle assembleDebug) e Publicar Release (só em push no main, depois das duas primeiras; lê o versionName de app/build.gradle; se a Release vX.Y.Z não existir, cria e anexa tx9-booster-X.Y.Z.apk; se existir, não faz nada).

## Funcionalidades ainda não implementadas

Limpeza real (exclusão), otimização rápida, lista de aplicativos, histórico, ícone e banner do app.

## O que foi compilado e testado

- GitHub Actions verde (informado pelo dono) após cada mudança da sessão 2, incluindo os três blocos após o build.yml novo.
- Testes automáticos com pastas falsas e respostas falsas do GitHub (verde, informado pelo dono): PoliticaCaminhosTest, AnalisadorTest (10 testes), VerificadorAtualizacaoTest (11 testes). Esses testes NÃO usam internet.
- Testado no TX9 real pelo dono: só Etapa 2 e Etapa 3 (ver TESTING.md).
- NÃO testado no TX9: tela LIMPEZA, botão e checagem de atualização, instalação por cima, conexão https do TX9 com o GitHub.
- A parte de rede de VerificadorAtualizacao.verificar() (HttpURLConnection) não tem teste automático. Só a decisão (interpretar, ehMaisNova, partes) tem.

## Descobertas no aparelho real (informadas pelo dono)

- Android: a tela mostra "10.1" com API 25. A API 25 é a real (Android 7.1).
- Root: o teste retornou "ROOT FUNCIONANDO".

## Erros conhecidos e pendências

- Release v0.3.0: existência NÃO confirmada (ver PRIMEIRA COISA A FAZER).
- strings.xml (não foi aberto pelo agente) tem o texto versao_texto, mostrado no rodapé da tela inicial. Provavelmente ainda diz 0.2.0. Pedir ao dono que cole app/src/main/res/values/strings.xml e corrigir para 0.3.0.
- Ao instalar a 0.2.0 o dono precisou desinstalar a antiga. Suspeita (não confirmada): a 0.1.0 foi compilada antes da chave fixa e a 0.2.0 depois, então as assinaturas diferiam. Teste: instalar a 0.3.0 e depois publicar uma 0.3.1 e instalar por cima. Anotar a mensagem exata do Android se falhar.
- O TX9 pode não ter navegador para abrir o download. Por isso o endereço aparece escrito na tela. Descobrir no aparelho.
- O TX9 pode recusar a conexão https com o GitHub (aparelho antigo, relógio errado). O app mostrará "Sem conexão com o GitHub." Descobrir no aparelho.
- O APK das Releases é assinado com a chave de depuração pública. Qualquer pessoa pode fabricar um APK com essa chave. Mitigação atual: o app só abre endereços do repositório do dono e o Android pede confirmação. Assinatura privada fica para a Etapa 9.
- Uma Release só é criada quando o versionName sobe. Mudar o código sem subir a versão NÃO gera Release nova (a Release antiga fica velha). Para publicar, subir versionName e versionCode em app/build.gradle.
- A mensagem "versão nova disponível" diz "Toque no botão para baixar", mas o botão já tenta abrir o download.
- Os valores de RAM, armazenamento, kernel e ABIs ainda não foram comparados com outra fonte. Teste de root com a permissão negada ainda não foi feito. O app ainda não tem ícone nem banner.
- O agente consegue abrir pelo site só arquivos da raiz do repositório e a página principal pode vir de cópia antiga. Arquivos dentro de pastas precisam ser colados pelo dono.
- Erro do agente na sessão 2, já corrigido: confiou em documentação desatualizada e criou AnalisadorArquivos.java e AnalisadorArquivosTest.java duplicados; os dois foram apagados.

## Decisões técnicas

- Java, sem AndroidX, sem banco de dados, sem serviço em segundo plano. JUnit 4.13.2 só para testes.
- minSdk 21, targetSdk 28, compileSdk 34. Pacote br.com.tx9booster. versionCode 3, versionName 0.3.0.
- Sem Gradle wrapper. GitHub Actions: Java 17, Gradle 8.5.
- Rede: única exceção à regra "sem internet" é a verificação de atualização, aprovada pelo dono em 2026-10-26 (botão e checagem ao abrir). Só https, só o repositório do dono, com tempo limite, sem instalar nada sozinho, sem serviço em segundo plano.
- Root: sempre comandos fixos, com limite de tempo e checagem do código de saída.
- A limpeza só pode apagar o que PoliticaCaminhos permitir. Área inicial: cache do próprio app (getCacheDir e getExternalCacheDir).
- Chave de depuração fixa e pública. Assinatura privada na Etapa 9.

## Arquivos do projeto

Raiz: AGENTS.md, PROJECT_STATE.md, ROADMAP.md, CHANGELOG.md, TESTING.md, README.md, settings.gradle, build.gradle, gradle.properties, debug.keystore.b64, .github/workflows/build.yml. Em app/: build.gradle; src/main/AndroidManifest.xml; src/main/res/values/strings.xml (não aberto) e strings_atualizacao.xml; res/layout/activity_main.xml, activity_diagnostico.xml, activity_limpeza.xml (não aberto); res/drawable/botao_fundo.xml. Em app/src/main/java/br/com/tx9booster/: MainActivity, DiagnosticoActivity, LimpezaActivity, PoliticaCaminhos, Analisador, VerificadorAtualizacao. Em app/src/test/java/br/com/tx9booster/: PoliticaCaminhosTest, AnalisadorTest, VerificadorAtualizacaoTest.

## Próxima tarefa exata

1. Confirmar a Release v0.3.0 (ver PRIMEIRA COISA A FAZER). Corrigir o que for preciso.
2. Pedir o strings.xml e corrigir o versao_texto para 0.3.0 (isso muda o app: subir para 0.3.1 se for publicar).
3. Pedir ao dono o teste no TX9: baixar o APK da Release v0.3.0 (aba Releases, Assets), instalar (se pedir, desinstalar o antigo primeiro e anotar a mensagem do Android), abrir o app, apertar VERIFICAR ATUALIZAÇÃO e dizer o que aparece (esperado: "O app já está na versão mais nova"). Depois abrir LIMPEZA, apertar simular e dizer o que aparece.
4. Testar a atualização de verdade: publicar uma 0.3.1 (subir versionName e versionCode), abrir o app 0.3.0 no TX9 e ver se avisa; seguir o download; instalar por cima; anotar a mensagem do Android.
5. Só depois de a simulação ser confirmada no TX9: planejar a exclusão real, só do cache do próprio app, com confirmação e relatório antes e depois.

## Instruções especiais para o próximo agente

- Dono muito iniciante, fala por voz, usa só o site do GitHub, sem terminal. Siga AGENTS.md: português simples, UM passo e UM arquivo por vez, uma pergunta por vez com respostas numeradas, arquivo INTEIRO para colar, nunca reescrever código de memória, nunca pedir senha, chave ou link M3U.
- Combinado: o dono responde "1" para "salvei e a execução ficou verde". Só aceitar depois de a execução parar de ficar amarela. Respostas como "11" são ambíguas: confirmar.
- Antes de criar QUALQUER arquivo de código, conferir se já existe. O GitHub avisa "A file with the same name already exists". A documentação já ficou atrasada uma vez e gerou duplicados.
- Para ler arquivos dentro de pastas, pedir que o dono use "Copy raw file" no link do arquivo e cole na conversa. A página principal pode vir de cópia antiga: não confiar nela para saber o estado.
- Não recomeçar o projeto.
