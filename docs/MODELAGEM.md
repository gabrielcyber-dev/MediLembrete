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
- **Índice único em (`medicamento_id`, `data_hora_programada`)**: um medicamento não pode ter duas doses no mesmo instante. É isso que permite chamar a geração das doses do dia quantas vezes for preciso (ao abrir o app, por exemplo) sem duplicar nada nem perder o que já foi tomado.
- **Enquanto o app está em desenvolvimento**, `AppDatabase` usa `fallbackToDestructiveMigration()`: ao mudar o esquema, basta subir a `version` e o banco é recriado, sem cada integrante precisar desinstalar o app. Hoje a `version` é **3**.

## Consultas disponíveis em `RegistroDoseDao`

| método | para que serve |
|---|---|
| `listarPorPeriodo(inicio, fim)` | agenda do dia (Tela 1) e histórico por período (Tela 6) |
| `contarPorStatusNoPeriodo(status, inicio, fim)` | contadores "Tomados hoje" e "Pendentes" da Tela 1 |
| `proximaDosePendente(agora)` | card "Próximo lembrete" da Tela 1 e o alarme da Tela 7 |
| `pendentesAtrasadas(limite)` | marcar como PERDIDO as doses cujo horário já passou |
| `historicoPorMedicamento(id)` / `historicoCompleto()` | Tela 6 (Histórico) |
| `inserirSeNaoExistir(dose)` | geração idempotente das doses do dia |
| `marcarComoTomada(id, instante)` | Tela 5 - grava o status e a hora do registro |
| `marcarPendentesAtrasadasComoPerdidas(limite)` | passa para PERDIDO o que ficou para trás |

## Como as telas usam isso

As telas não falam com os DAOs diretamente; usam um repositório, que valida, roda fora da main thread e devolve `LiveData` para a tela se atualizar sozinha.

**`MedicamentoRepository`** — cadastro (Tela 2): `cadastrar(medicamento, callback)`.

**`RegistroDoseRepository`** — registro de doses (Telas 1, 5, 6 e 7):

| método | para que serve |
|---|---|
| `gerarDosesDoDia(dia, callback)` | cria as doses pendentes do dia a partir dos horários de cada medicamento em tratamento; pode ser chamado várias vezes |
| `marcarComoTomada(registroId, instante, callback)` | Tela 5; uma dose perdida pode ser tomada depois, mas uma já tomada não tem a hora regravada |
| `marcarAtrasadasComoPerdidas(limite, callback)` | evita mostrar como "pendente" uma dose de ontem |
| `dosesDoDia(dia)` | agenda do dia (Tela 1) |
| `tomadasNoDia(dia)` / `pendentesNoDia(dia)` / `perdidasNoDia(dia)` | contadores da Tela 1 |
| `proximaDose(agora)` | card "Próximo lembrete" (Tela 1) e o horário a agendar no lembrete (Tela 7) |
| `historico()` / `historicoDoMedicamento(id)` | Tela 6 |

Os cálculos de agenda ficam em **`GeradorDeDoses`** (sem Android, testado na JVM): `inicioDoDia`, `inicioDoDiaSeguinte`, `instanteDaDose(dia, "HH:mm")`, `instantesDoDia` e `tratamentoAtivoEm`.

## Próximos passos sugeridos

1. Rodar `Gradle Sync` no Android Studio para baixar as dependências do Room.
2. Criar as telas de cadastro/detalhes chamando os DAOs através do `executor` (nunca na main thread).
3. **Antes de entregar o app para uso real, trocar `fallbackToDestructiveMigration()` por `Migration`s** — senão os dados do usuário são apagados a cada atualização de esquema.
4. Chamar `gerarDosesDoDia(hoje)` e `marcarAtrasadasComoPerdidas(inicio de hoje)` quando o app abre, para a agenda do dia existir antes de a Tela 1 ser desenhada.
5. Validar o formato "HH:mm" na tela de horários (Tela 3): hoje um horário gravado fora do formato é apenas ignorado na geração das doses.
