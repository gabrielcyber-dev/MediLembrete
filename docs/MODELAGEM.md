# Modelagem inicial do banco de dados — MediLembrete

Banco local em SQLite via **Room**, com 3 tabelas.

## Entidades

**medicamentos** (`Medicamento`) — Tela 2 (Cadastro)
| coluna | tipo | descrição |
|---|---|---|
| id | long (PK, auto) | identificador |
| nome | String | nome do medicamento |
| dosagem | String | ex: "50 mg", "10 ml" |
| quantidade_por_dose | int | ex: 1 comprimido, 2 gotas |
| forma | String | forma do medicamento (padrão: Comprimido) |
| cor_identificacao | String | cor de identificação (padrão: #FF3E00) |
| horario_inicial | String "HH:mm" | primeiro horário (padrão: 08:00) |
| intervalo_horas | int | intervalo entre doses em horas (padrão: 24) |
| duracao_dias | Integer, opcional | duração do tratamento em dias |
| data_inicio | long (epoch millis) | início do tratamento |
| data_fim | long, opcional | fim do tratamento; nulo = uso contínuo |
| observacoes | String, opcional | anotações livres |

**horarios** (`Horario`) — Tela 3 (Configuração de Horários)
| coluna | tipo | descrição |
|---|---|---|
| id | long (PK, auto) | identificador |
| medicamento_id | long (FK → medicamentos.id, CASCADE) | a qual medicamento pertence |
| horario | String "HH:mm" | ex: "08:00" |

Um medicamento tem N horários (ex: 08:00, 14:00, 20:00).

**registros_dose** (`RegistroDose`) — Telas 5 e 6 (Registro de Dose / Histórico)
| coluna | tipo | descrição |
|---|---|---|
| id | long (PK, auto) | identificador |
| medicamento_id | long (FK → medicamentos.id, CASCADE) | a qual medicamento pertence |
| data_hora_programada | long (epoch millis) | dia e hora em que a dose deveria ser tomada |
| data_hora_registro | long (epoch millis), opcional | quando foi marcado como tomado; nulo = ainda pendente |
| status | enum: PENDENTE / TOMADO / PERDIDO | estado da dose |

## Decisões de projeto

- **Horários de `horarios` como string "HH:mm"** em vez de `java.time`, para evitar desugaring (o `minSdk` do projeto é 23, abaixo do 26 exigido pelo `java.time` nativo). É o horário recorrente do medicamento, que não tem dia.
- **`registros_dose.data_hora_programada` guarda dia + hora** (epoch millis), e não "HH:mm": um registro de dose é de um dia específico. Sem a data, a dose das 08:00 de hoje seria indistinguível da de ontem, e não daria para contar "tomados hoje", montar o histórico por data nem achar a próxima dose.
- **Datas como `long` (epoch millis)** por simplicidade e para permitir ordenação/filtro direto em SQL.
- **`status` como enum** (`StatusDose`), convertido para texto no banco via `Converters` — assim o Room não precisa modelar `status` como inteiro mágico.
- **`ForeignKey` com `onDelete = CASCADE`**: excluir um medicamento remove automaticamente seus horários e histórico de doses.
- Acesso ao banco sempre fora da main thread — `AppDatabase` expõe um `ExecutorService` (`AppDatabase.executor`) para isso.
- **Índice em `data_hora_programada`**: todas as consultas de dose filtram ou ordenam por essa coluna.
- **Migração do banco de dados:** `AppDatabase` utiliza a versão **3** e a migration `MIGRATION_2_3` para adicionar os novos campos à tabela `medicamentos`, preservando os dados existentes. A migration é registrada por meio de `addMigrations(MIGRATION_2_3)`.
- **Índices:** `RegistroDose` possui índice em `data_hora_programada` e índice único composto por `medicamento_id` e `data_hora_programada`, evitando registros duplicados para o mesmo medicamento no mesmo instante.

## Consultas disponíveis em `RegistroDoseDao`

| método | para que serve |
|---|---|
| `listarPorPeriodo(inicio, fim)` | agenda do dia (Tela 1) e histórico por período (Tela 6) |
| `contarPorStatusNoPeriodo(status, inicio, fim)` | contadores "Tomados hoje" e "Pendentes" da Tela 1 |
| `proximaDosePendente(agora)` | card "Próximo lembrete" da Tela 1 e o alarme da Tela 7 |
| `pendentesAtrasadas(limite)` | marcar como PERDIDO as doses cujo horário já passou |
| `inserirSeNaoExistir(registro)` | insere uma dose somente se ela ainda não existir |
| `buscarPorIdSync(id)` | busca um registro de dose pelo identificador |
| `marcarComoTomada(id, dataHoraRegistro)` | marca uma dose como tomada e registra quando foi tomada |
| `marcarPendentesAtrasadasComoPerdidas(limite)` | marca como perdidas as doses pendentes cujo horário já passou |
| `historicoPorMedicamento(id)` / `historicoCompleto()` | Tela 6 (Histórico) |

## Repositórios

**MedicamentoRepository** — responsável pelo cadastro e edição de medicamentos junto com seus horários.
- `cadastrar(medicamento, horarios, callback)`: valida os dados e cadastra o medicamento e seus horários em uma única transação.
- `atualizar(medicamento, horarios, callback)`: atualiza o medicamento, substitui seus horários e executa as operações em uma única transação.
- As operações de escrita são executadas em background.
- O callback retorna o ID do medicamento cadastrado ou atualizado.

**RegistroDoseRepository** — responsável pela geração e movimentação dos registros de doses.

Leituras disponíveis por LiveData:
- `dosesDoDia(dia)`: doses programadas para o dia.
- `tomadasNoDia(dia)`: quantidade de doses tomadas.
- `pendentesNoDia(dia)`: quantidade de doses pendentes.
- `perdidasNoDia(dia)`: quantidade de doses perdidas.
- `proximaDose(agora)`: próxima dose pendente.
- `historico()`: histórico completo.
- `historicoDoMedicamento(medicamentoId)`: histórico de um medicamento.

Operações de escrita:
- `gerarDosesDoDia(dia, callback)`: gera os registros de doses dos medicamentos ativos no período, ignorando duplicatas existentes.
- `marcarComoTomada(registroId, dataHoraRegistro, callback)`: registra uma dose como tomada.
- `marcarAtrasadasComoPerdidas(limite, callback)`: altera para perdida as doses pendentes cujo horário já passou do limite.

As escritas são executadas em background e os callbacks recebem os resultados das operações.

## GeradorDeDoses

Classe auxiliar responsável pelos cálculos de datas e horários da agenda, sem dependência direta do Android ou do banco de dados.

- `inicioDoDia(instante)`: retorna o início do dia correspondente.
- `inicioDoDiaSeguinte(instante)`: retorna o início do dia seguinte.
- `instanteDaDose(dia, horarioHHmm)`: combina uma data com um horário no formato HH:mm.
- `instantesDoDia(dia, horariosHHmm)`: gera os instantes das doses em ordem crescente, sem repetições.
- `tratamentoAtivoEm(dataInicio, dataFim, instante)`: verifica se o tratamento está ativo no instante informado. Data final nula representa uso contínuo.

## Próximos passos sugeridos

1. Rodar `Gradle Sync` no Android Studio para baixar as dependências do Room.
2. Validar as migrations do banco de dados para garantir que os dados existentes sejam preservados nas próximas atualizações.
3. Integrar a geração automática de doses utilizando `GeradorDeDoses` e o `RegistroDoseRepository`.
4. Garantir que as doses duplicadas sejam evitadas pelo índice único composto de `RegistroDose`.