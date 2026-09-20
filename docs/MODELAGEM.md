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
- **Enquanto o app está em desenvolvimento**, `AppDatabase` usa `fallbackToDestructiveMigration()`: ao mudar o esquema, basta subir a `version` e o banco é recriado, sem cada integrante precisar desinstalar o app. Hoje a `version` é **2**.

## Consultas disponíveis em `RegistroDoseDao`

| método | para que serve |
|---|---|
| `listarPorPeriodo(inicio, fim)` | agenda do dia (Tela 1) e histórico por período (Tela 6) |
| `contarPorStatusNoPeriodo(status, inicio, fim)` | contadores "Tomados hoje" e "Pendentes" da Tela 1 |
| `proximaDosePendente(agora)` | card "Próximo lembrete" da Tela 1 e o alarme da Tela 7 |
| `pendentesAtrasadas(limite)` | marcar como PERDIDO as doses cujo horário já passou |
| `historicoPorMedicamento(id)` / `historicoCompleto()` | Tela 6 (Histórico) |

## Próximos passos sugeridos

1. Rodar `Gradle Sync` no Android Studio para baixar as dependências do Room.
2. Criar as telas de cadastro/detalhes chamando os DAOs através do `executor` (nunca na main thread).
3. **Antes de entregar o app para uso real, trocar `fallbackToDestructiveMigration()` por `Migration`s** — senão os dados do usuário são apagados a cada atualização de esquema.
4. Gerar os registros de dose a partir dos `horarios` de cada medicamento ativo, combinando o "HH:mm" com a data do dia para formar `data_hora_programada`.
