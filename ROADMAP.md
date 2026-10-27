# ROADMAP.md — Plano de desenvolvimento

Níveis de estado: Planejada, Em desenvolvimento, Implementada, Compilada, Testada em ambiente simulado, Testada no aparelho real, Aprovada.

Regra: não avançar para uma etapa que apaga coisas se a etapa anterior ainda tem falhas importantes.

## Etapa 1 — Planejamento e compatibilidade

Estado: Aprovada (2026-10-26). Fez: repositório, arquivos de continuidade, escolha de tecnologia (Java), versão mínima do Android (5.0, API 21).

## Etapa 2 — Estrutura mínima funcional

Estado: Testada no aparelho real (2026-10-26), resultado informado pelo dono. Fez: projeto Android com tela inicial, navegação por controle remoto, compilação automática do APK pelo GitHub Actions. Pendências: ícone e banner do app.

## Etapa 3 — Diagnóstico

Estado: Testada no aparelho real, com pendências (2026-10-26), resultado informado pelo dono. Fez: tela de diagnóstico (modelo, Android e API, kernel, ABIs, RAM, armazenamento) e teste de root com comando fixo e limite de tempo. Confirmado pelo dono: Android real é a API 25 (7.1); root funcionando. Pendências: conferir RAM, armazenamento, kernel e ABIs com outra fonte; testar o root com a permissão negada.

## Etapa 4 — Limpeza segura

Estado: Compilada e testada em ambiente simulado (GitHub Actions verde, informado pelo dono). Não testada no aparelho real. Feito: PoliticaCaminhos (trava de caminhos, com teste); Analisador somente leitura (limites de 20000 arquivos, 20 níveis e 100 itens, ignora links simbólicos, 10 testes); tela LIMPEZA em modo de simulação (LimpezaActivity), que mede o cache interno e externo do próprio app e nunca apaga. Falta: testar a simulação no TX9 (abrir LIMPEZA, apertar simular, anotar o que aparece); e só depois a exclusão real, apenas do cache do próprio app, com confirmação do usuário e relatório de bytes antes e depois. Pronta quando: a simulação lista o que seria apagado sem apagar nada, os testes de caminhos passam e a limpeza real libera bytes medidos.

## Atualização do app (pedido do dono, fora da numeração das etapas)

Estado: Testada no aparelho real, com pendência (2026-10-27), resultado informado pelo dono. Feito: permissão de internet; VerificadorAtualizacao (consulta a Release mais recente do repositório, só https e só endereços do repositório, tempo limite, compara versões como números); botão VERIFICAR ATUALIZAÇÃO e checagem silenciosa ao abrir; InstaladorAtualizacao (baixa o APK dentro do app, sem navegador, e abre o instalador do Android); tarefa "Publicar Release" no workflow (cria a Release vX.Y.Z quando o versionName sobe). O app não instala sozinho: o dono sempre confirma no Android. Chave própria de assinatura desde a versão 0.3.7. Confirmado pelo dono: a 0.3.8 foi instalada pelo botão do app, por cima da 0.3.7, e a tela inicial mostrou "Versão 0.3.8". O texto de versão da tela inicial (versao_texto em strings.xml) estava escrito à mão como 0.3.4 e foi corrigido. Pendências: a causa exata da recusa antiga não foi provada (a 0.3.7, com chave própria, instalou por cima da 0.3.5, de chave de depuração, o que não bate com a teoria de assinaturas diferentes); provar de novo com uma versão que tenha mudança real de código; corrigir a frase "Toque no botão para baixar" da mensagem de versão nova; o dono guardar uma cópia do tx9booster.jks fora do computador, junto com a senha (se perder, precisa desinstalar o app e criar chave nova). Ideia opcional: publicar um version.json com o SHA-256 do APK e conferir no download. Pronta quando: a instalação por cima funcionar de novo com uma versão com mudança real de código e a mensagem de versão nova estiver correta. Instalação silenciosa com root: não implementada, só considerar depois, como recurso de alto risco da Etapa 6.

## Etapa 5 — Otimização rápida

Estado: Planejada. Fazer: botão que junta as funções já testadas e mostra relatório antes e depois (RAM livre, espaço livre, arquivos removidos, falhas). Pronta quando: o relatório mostra só números medidos, sem promessa de velocidade.

## Etapa 6 — Funções avançadas de root

Estado: Planejada. Fazer: só recursos com compatibilidade, segurança e reversão verificadas, sempre com confirmação do usuário. Inclui validar se existe método seguro para limpar cache de outros aplicativos no Android 7.1. Pronta quando: cada função foi testada no TX9 e tem plano de reversão escrito.

## Etapa 7 — Gerenciamento de aplicativos

Estado: Planejada. Fazer: lista de aplicativos, classificação (usuário, sistema, crítico, a analisar), ações reversíveis. Pronta quando: nenhum componente crítico pode ser desativado pelo app e toda ação pode ser desfeita.

## Etapa 8 — Testes e estabilidade

Estado: Planejada. Fazer: testar controle remoto, consumo de memória e comportamento no TX9, corrigir falhas. Pronta quando: TESTING.md mostra os testes no aparelho real sem falhas importantes.

## Etapa 9 — Acabamento e lançamento

Estado: Em desenvolvimento. Já feito: assinatura com chave própria do dono (desde a 0.3.7, com Secrets no GitHub) e instruções de instalação pela página Releases no README. Fazer: melhorar interface, ícone e banner, revisar documentos, limpar arquivos sem uso (debug.keystore.b64 na raiz e textos sem uso em strings_instalacao.xml), preparar versão estável. Pronta quando: README.md explica como gerar e instalar o APK, o dono tem cópia segura da chave e a versão estável foi aprovada pelo dono.
