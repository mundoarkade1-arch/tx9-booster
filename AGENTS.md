# AGENTS.md — Instruções permanentes (leia primeiro)

## Projeto

TX9 Booster: aplicativo Android de manutenção e otimização para o TV Box Tanix TX9 (1 GB de RAM, root). Interface em português do Brasil, feita para controle remoto.

## Dono do projeto

Iniciante em programação. Trabalha no GitHub Codespaces; o agente pode editar os arquivos do workspace e usar o terminal para verificar o trabalho.

## Como trabalhar com o dono

- Português simples, frases curtas. Palavra técnica só com explicação.
- Edite diretamente os arquivos do workspace quando a tarefa autorizar mudanças. Não peça que o dono salve, cole ou copie arquivos.
- Não pergunte se o dono salvou nem se o Actions ficou verde. Verifique com o terminal: `git status`, os testes Gradle quando disponíveis e `gh run list` / `gh run watch` para conferir o Actions.
- Se o Android SDK não estiver disponível no Codespaces, informe isso uma vez, use os testes locais possíveis e confira o workflow do GitHub Actions.
- Uma pergunta por vez, com respostas numeradas.
- Uma pergunta por vez, com respostas numeradas.
- Antes de criar qualquer arquivo de código, conferir se ele já existe. A página principal do GitHub pode estar desatualizada; confira o workspace.
- Faça uma pergunta por vez, com respostas numeradas, somente quando precisar de uma decisão do dono ou de um teste no TX9.
- Nunca pedir senha, chave ou token. Não revele valores secretos.
- Antes de criar qualquer arquivo de código, conferir se ele já existe. A página principal do GitHub pode estar desatualizada; confira o workspace.

## Antes de agir, em toda sessão

Ler nesta ordem: AGENTS.md, PROJECT_STATE.md, ROADMAP.md, CHANGELOG.md, TESTING.md, README.md. Depois ler os arquivos de código ligados à tarefa. Conferir o estado real do repositório. Não presumir que a documentação está perfeita: ela já ficou atrasada uma vez e isso causou arquivos duplicados.

Antes de criar qualquer arquivo de código, conferir se ele já existe. Para ler arquivos dentro de pastas, pedir ao dono que use "Copy raw file" no link do arquivo e cole na conversa. A página principal do GitHub pode vir de cópia antiga.

## Regras de honestidade

- Não dizer que compilou, testou, publicou ou editou algo sem ter feito de verdade.
- Não marcar função como pronta só porque o código foi escrito.
- Níveis de estado: Planejada, Em desenvolvimento, Implementada, Compilada, Testada em ambiente simulado, Testada no aparelho real, Aprovada.
- Não inventar links, resultados de teste ou números de desempenho.
- Resultados que o dono informa (check verde, testes no TX9) são registrados como "informado pelo dono".
- Se não puder acessar o GitHub, entregar os arquivos e as instruções para o dono colar à mão.
- Não diga que compilou, testou, publicou, commitou ou enviou alterações sem confirmar isso no ambiente correspondente.

## Regras de segurança do aparelho

- O dono autorizou em 2026-10-30 o modo avançado com root. O app continua abrindo sem root, mas o Modo Avançado fica bloqueado se o teste de root falhar.
- Comandos root devem sair de modelos fixos em uma fábrica única, validar os parâmetros, ter limite de tempo, capturar stdout/stderr e verificar o código de saída. Nunca monte comandos com texto livre.
- Toda ação root deve ser registrada no Histórico antes de ser aplicada, com data/hora, ação, alvo, valor original, valor novo, resultado e comando inverso. Toda ação deve oferecer REVERTER; antes da reversão, confira o estado atual.
- Para alterações de sistema, leia o original, aplique, meça e ofereça reversão. Ajustes persistentes no boot exigem proteção de inicialização segura que desfaça os ajustes após reinicializações sem confirmação.
- Nunca limpar de forma recursiva e sem limite: /data, /system, /vendor, /product, /metadata, /boot, /dev, /proc, /sys.
- Ter lista de caminhos permitidos e lista de caminhos proibidos. A proibida vence sempre.
- Não seguir links simbólicos para fora da área permitida.
- Sempre ter modo de simulação (não apaga nada) antes de qualquer limpeza real.
- Nada de limpeza destrutiva automática ao abrir o app.
- Não desativar SELinux, não alterar partições, bootloader, recovery ou firmware.
- É proibido desativar SELinux, alterar partições, bootloader, recovery ou firmware; executar `dd`, `mkfs` ou `flash`; ou apagar `/system`, `/vendor`, `/boot`, `/data/system`, `/data/dalvik-cache`, `/data/app`, `/efs` ou `/persist`.
- Ajustes em lowmemorykiller, lmkd, oom_score_adj, vm.*, swappiness ou zRAM só podem ser considerados após diagnóstico do recurso existente, leitura e registro do original, justificativa, medição e reversão disponível.
- Não criar botão que mata todos os processos.
- Não desativar launcher, System UI, rede, áudio, entrada, armazenamento nem gerenciador de pacotes.
- Não desativar launcher, System UI, rede, áudio, entrada/controle remoto, armazenamento, gerenciador ou instalador de pacotes, `android`, provedores essenciais nem serviços Amlogic/Droidlogic.
- Função que não foi validada no TX9 fica desligada.
- Toda função nova fica desligada por padrão no Laboratório até o dono confirmar que funcionou no TX9.
- Debloat e limpeza root exigem inventário real, listas de proteção, política explícita, simulação quando houver exclusão, confirmação e registro reversível. Apps protegidos sempre vencem listas permissivas. `pm uninstall -k --user 0` só pode ser considerado após testar em runtime a disponibilidade do comando inverso `pm install-existing`.
- Para atualizar a versão, salve primeiro `strings.xml` (versao_texto) e `app/build.gradle` por último, depois que os testes passarem (ele dispara a Release).

## Regras de código

- Nativo, leve, sem bibliotecas pesadas, sem anúncios, sem serviço em segundo plano sem necessidade, sem banco de dados sem necessidade.
- O app funciona sem internet. A ÚNICA função que usa a internet é a verificação de atualização, aprovada pelo dono em 2026-10-26 (botão VERIFICAR ATUALIZAÇÃO e checagem silenciosa ao abrir o app). Regras dessa função: só https; só o repositório mundoarkade1-arch/tx9-booster; tempo limite curto; sem seguir redirecionamentos; resposta com tamanho limitado; falha de rede nunca trava nem incomoda o dono; o app NUNCA instala nada sozinho, só mostra o endereço e abre o download, e quem confirma a instalação é o dono no Android. Qualquer outro uso da internet precisa de nova aprovação do dono.
- Não recomeçar o projeto, não apagar código funcional sem necessidade.
- Não expor chaves de assinatura, senhas ou tokens. A chave de depuração fixa é pública e só serve para testes.

## Hardware informado (a confirmar no aparelho)

Tanix TX9, placa P281, Amlogic S905W, 4 núcleos, 1 GB de RAM, ABI armeabi-v7a, kernel 3.14.29, cerca de 21 GB de armazenamento. Versão do Android: o dono vê "10.1" com API 25; a API 25 (Android 7.1) é a real. O app deve ler os valores reais.

## Ao encerrar cada sessão

Atualizar PROJECT_STATE.md, CHANGELOG.md, TESTING.md e ROADMAP.md (e README.md se mudou algo que o dono vê). Dar ao dono um resumo: o que foi feito, arquivos criados ou alterados, o que foi realmente compilado e testado, erros conhecidos, o que ele precisa fazer no GitHub, próxima tarefa exata e uma mensagem pronta para a próxima sessão.

Mensagem para o próximo agente: "Leia primeiro AGENTS.md, PROJECT_STATE.md, ROADMAP.md, CHANGELOG.md, TESTING.md e README.md. Confira o estado real do repositório, identifique a etapa atual e continue do ponto registrado, sem reiniciar o projeto. Não repita tarefas já concluídas sem necessidade. Preserve o código funcional, respeite todas as regras de segurança e atualize a documentação antes de encerrar."
