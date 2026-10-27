# PROJECT_STATE.md — Estado atual do projeto

Última atualização: 2026-10-27 (fim da sessão 5)

## Projeto

TX9 Booster: app Android (Java, androidx.core só para FileProvider) de manutenção e otimização para o Tanix TX9 (Amlogic S905W, 1 GB de RAM, Android 7.1 API 25, com root). Interface em português, para controle remoto. Pacote br.com.tx9booster.

## Versão atual

0.3.9 (versionCode 12). Release v0.3.9 publicada com o .apk (confirmado na página Releases).

## O que existe

Tela inicial, Diagnóstico, teste de root, tela LIMPEZA só em simulação (nunca apaga), atualização pelo próprio app (baixa o APK e abre o instalador do Android).

## Atualização do app

- Funciona. Testada no TX9 pelo dono: 0.3.7 para 0.3.8 (sessão 4) e 0.3.8 para 0.3.9 (sessão 5), pelo botão do app, por cima, sem desinstalar. A tela inicial mostrou "Versão 0.3.9".
- Assinatura com chave própria desde a 0.3.7 (tx9booster.jks, alias tx9booster; Secrets TX9_KEYSTORE_BASE64 e TX9_SENHA cadastrados pelo dono).
- Causa exata da recusa antiga NÃO provada. A 0.3.7 (chave própria) instalou por cima da 0.3.5 (chave de depuração), o que não bate com a teoria de assinaturas diferentes.
- A frase nova da mensagem de versão nova ("Aperte de novo o botão VERIFICAR ATUALIZAÇÃO para baixar e instalar. O Android vai pedir a sua confirmação.") está salva em strings_atualizacao.xml e foi publicada na 0.3.9, mas o dono NÃO leu a mensagem na tela. Não conferida.

## Feito na sessão 5

- README.md e ROADMAP.md corrigidos.
- Frase da mensagem de versão nova trocada (strings_atualizacao.xml).
- Versão subida para 0.3.9 (build.gradle e versao_texto em strings.xml).

## Pendências

- O dono precisa guardar uma cópia do tx9booster.jks fora do computador, junto com a senha. Se perder, precisa desinstalar o app e criar chave nova.
- debug.keystore.b64 continua na raiz, sem uso. strings_instalacao.xml tem textos sem uso.
- Tela LIMPEZA nunca foi testada no TX9.
- Conferir RAM, armazenamento, kernel e ABIs com outra fonte. Testar root com a permissão negada.
- App sem ícone e sem banner de TV.
- Ideia opcional: publicar um version.json com o SHA-256 do APK e conferir no download.

## Próxima tarefa exata

1. Pedir ao dono que teste a tela LIMPEZA no TX9: abrir LIMPEZA, apertar simular, anotar o que aparece (números e mensagens).
2. Só depois: exclusão real, apenas do cache do próprio app, com confirmação e relatório de bytes antes e depois, usando PoliticaCaminhos.
3. Ao subir versão: mudar versionCode e versionName em app/build.gradle E versao_texto em strings.xml. Salvar o strings.xml primeiro e o build.gradle por último (ele dispara a Release).

## Instruções para o próximo agente

- Seguir AGENTS.md. Dono muito iniciante, fala por voz: português simples, UM passo e UM arquivo por vez, arquivo INTEIRO para colar, nunca reescrever de memória (pedir o arquivo atual: "Copy raw file").
- As cópias do GitHub que o agente consegue abrir podem estar velhas (sessão 5 viu README e PROJECT_STATE antigos). Confie no que o dono colar.
- O dono responde "ok" ou número; às vezes escreve palavras trocadas. Se não bater, pergunte de novo com respostas numeradas.
- Se o texto colado falar de canais, filmes ou ExoPlayer, é do outro projeto (asc-tvbox): ignorar e avisar com calma.
- Verde no Actions não prova a Release: confirmar na página Releases.
- Não recomeçar o projeto.
