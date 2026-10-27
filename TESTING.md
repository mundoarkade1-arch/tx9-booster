# TESTING.md — Testes e resultados

Só entram aqui testes que foram realmente feitos. Cada teste diz onde foi feito: ambiente simulado ou aparelho real (TX9).

## Resultados até agora
Nenhum teste foi executado. Ainda não existe código do aplicativo.

## Testes necessários no aparelho real (TX9)
- O APK instala e o app abre.
- A navegação por setas, OK e voltar funciona e o botão em foco aparece bem.
- O diagnóstico mostra valores que batem com o aparelho (Android, RAM, armazenamento, ABI).
- O estado do root é detectado corretamente, com e sem permissão concedida.
- O app funciona sem root e explica o que fica indisponível.
- A simulação de limpeza não apaga nada.
- A limpeza real mexe só nos arquivos permitidos.
- O relatório antes e depois mostra números medidos.
- Consumo de memória do próprio app em uso.

## Testes automatizados planejados (ambiente simulado)
- Validação de caminhos permitidos e proibidos (a lista proibida vence).
- Bloqueio de caminhos fora da área permitida e de links simbólicos.
- Classificação das operações.
- Interpretação dos resultados e dos códigos de saída.

## Limitações
- Compilar não prova que a função é segura no firmware do TX9.
- Uma medição de RAM livre depois da limpeza não prova que o aparelho ficou mais rápido.
