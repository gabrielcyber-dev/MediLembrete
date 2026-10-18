# MedLembrete — plano de entrega

**Prazo-alvo:** primeira semana de dezembro de 2026  
**Equipe:** Gabriel, Nickolas e Guilherme  
**Objetivo:** entregar um aplicativo Android funcional para organizar medicamentos e acompanhar doses programadas.

Este plano é um ponto de partida. Atualize os status após validar cada item no código e nos testes; não marque uma tarefa como concluída apenas porque ela foi implementada em uma branch.

## Prioridade 0 — Consolidar a integração

- [ ] Confirmar o estado do PR #3 (camada de dados com Room) e sua branch de destino.
- [ ] Comparar as branches `master`, `guilherme`, `nickolas` e `gabriel`; não fazer merge ou rebase antes de entender a divergência.
- [ ] Fazer uma compilação limpa da versão escolhida como base.
- [ ] Escolher uma única tela de cadastro e um único modelo persistido.
- [ ] Registrar no PR as decisões de integração e os testes executados.
- [ ] Evitar force-push na branch principal.

## Prioridade 1 — Fluxo principal do aplicativo

- [ ] Cadastrar medicamento e persistir os dados com Room.
- [ ] Configurar um ou mais horários para cada medicamento.
- [ ] Mostrar os medicamentos e as próximas doses na tela principal.
- [ ] Registrar uma dose como tomada sem duplicar ou sobrescrever registros incorretamente.
- [ ] Consultar o histórico de doses.

## Prioridade 2 — Lembretes e qualidade

- [ ] Implementar e testar notificações/lembretes no Android.
- [ ] Tratar permissões e comportamento de notificações conforme a versão do Android.
- [ ] Corrigir problemas de layout, incluindo conteúdo sob as barras do sistema.
- [ ] Executar testes unitários, testes instrumentados e um roteiro manual no emulador/dispositivo.
- [ ] Verificar mensagens de erro, campos inválidos e estados vazios.

## Cronograma de referência

| Período | Meta |
| --- | --- |
| 18–25 out. | Entender branches, revisar o PR #3 e definir a base integrada |
| 26 out.–8 nov. | Cadastro único, validação e persistência |
| 9–22 nov. | Agenda diária, registro de doses e histórico |
| 23–29 nov. | Lembretes, correções e testes |
| 30 nov.–6 dez. | Regressão final, documentação e apresentação |

## Regras de trabalho em equipe

1. Cada mudança funcional começa em uma branch curta e com objetivo claro.
2. Abrir Pull Request para a branch principal; revisar o diff antes de integrar.
3. Rodar os testes relevantes e anotar os resultados no PR.
4. Não resolver divergências com force-push ou reset sem entender o impacto.
5. Dividir o trabalho por responsabilidade e combinar previamente alterações em arquivos compartilhados.
6. Atualizar este plano com status reais e bloqueios, não com estimativas otimistas.

## Critério mínimo de entrega

Uma pessoa deve conseguir cadastrar um medicamento, configurar seus horários, ver as doses do dia, marcar uma dose como tomada e consultar o histórico sem perder dados durante o uso normal. Os lembretes devem ser testados no ambiente-alvo. O app organiza informações e lembretes; não prescreve medicamentos nem recomenda mudanças de tratamento.
