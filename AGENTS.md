# AGENTS.md — Instruções permanentes (leia primeiro)

## Projeto
TX9 Booster: aplicativo Android de manutenção e otimização para o TV Box Tanix TX9 (1 GB de RAM, root). Interface em português do Brasil, feita para controle remoto.

## Dono do projeto
Iniciante absoluto em programação. Fala por voz (ditado). Faz tudo pelo site do GitHub, sem terminal, sem Android Studio.

## Como trabalhar com o dono
- Português simples, frases curtas. Palavra técnica só com explicação.
- UM passo e UM arquivo por vez, em ações numeradas, com link direto.
- Antes de cada passo, uma frase dizendo o que ele faz. Depois, o que o dono deve ver.
- Uma pergunta por vez, com respostas numeradas.
- Entregar o arquivo INTEIRO, pronto para copiar e colar, com o caminho exato.
- Nunca usar "adicione o restante", "continue como antes" ou "código omitido".
- Nunca pedir senha, chave, token ou link de lista. Nada de zip ou terminal.

## Antes de agir, em toda sessão
Ler nesta ordem: AGENTS.md, PROJECT_STATE.md, ROADMAP.md, CHANGELOG.md, TESTING.md, README.md. Depois ler os arquivos de código ligados à tarefa. Conferir o estado real do repositório. Não presumir que a documentação está perfeita.

## Regras de honestidade
- Não dizer que compilou, testou, publicou ou editou algo sem ter feito de verdade.
- Não marcar função como pronta só porque o código foi escrito.
- Níveis de estado: Planejada, Em desenvolvimento, Implementada, Compilada, Testada em ambiente simulado, Testada no aparelho real, Aprovada.
- Não inventar links, resultados de teste ou números de desempenho.
- Se não puder acessar o GitHub, entregar os arquivos e as instruções para o dono colar à mão.

## Regras de segurança do aparelho
- Funcionar sem root. Root é recurso de alto risco: verificar em tempo de execução, usar comandos fixos e específicos, checar código de saída, nunca montar comando com texto livre.
- Nunca limpar de forma recursiva e sem limite: /data, /system, /vendor, /product, /metadata, /boot, /dev, /proc, /sys.
- Ter lista de caminhos permitidos e lista de caminhos proibidos. A proibida vence sempre.
- Não seguir links simbólicos para fora da área permitida.
- Sempre ter modo de simulação (não apaga nada) antes de qualquer limpeza real.
- Nada de limpeza destrutiva automática ao abrir o app.
- Não desativar SELinux, não alterar partições, bootloader, recovery ou firmware.
- Não mexer em lowmemorykiller, lmkd, oom_score_adj, vm.*, swappiness, zRAM sem diagnóstico, validação e plano de reversão.
- Não criar botão que mata todos os processos.
- Não desativar launcher, System UI, rede, áudio, entrada, armazenamento nem gerenciador de pacotes.
- Função que não foi validada no TX9 fica desligada.

## Regras de código
- Nativo, leve, sem bibliotecas pesadas, sem anúncios, sem serviço em segundo plano sem necessidade, sem banco de dados sem necessidade.
- Não depender de internet.
- Não recomeçar o projeto, não apagar código funcional sem necessidade.
- Não expor chaves de assinatura, senhas ou tokens.

## Hardware informado (a confirmar no aparelho)
Tanix TX9, placa P281, Amlogic S905W, 4 núcleos, 1 GB de RAM, ABI armeabi-v7a, kernel 3.14.29, cerca de 21 GB de armazenamento. Versão do Android: a confirmar (há registro de 7.1 e de 10). O app deve ler os valores reais.

## Ao encerrar cada sessão
Atualizar PROJECT_STATE.md, CHANGELOG.md, TESTING.md e ROADMAP.md. Dar ao dono um resumo: o que foi feito, arquivos criados ou alterados, o que foi realmente compilado e testado, erros conhecidos, o que ele precisa fazer no GitHub, próxima tarefa exata e uma mensagem pronta para a próxima sessão.

Mensagem para o próximo agente:
"Leia primeiro AGENTS.md, PROJECT_STATE.md, ROADMAP.md, CHANGELOG.md, TESTING.md e README.md. Confira o estado real do repositório, identifique a etapa atual e continue do ponto registrado, sem reiniciar o projeto. Não repita tarefas já concluídas sem necessidade. Preserve o código funcional, respeite todas as regras de segurança e atualize a documentação antes de encerrar."
