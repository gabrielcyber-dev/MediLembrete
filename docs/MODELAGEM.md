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
| horario_programado | String "HH:mm" | horário a que este registro se refere |
| data_hora_registro | long (epoch millis), opcional | quando foi marcado como tomado; nulo = ainda pendente |
| status | enum: PENDENTE / TOMADO / PERDIDO | estado da dose |

## Decisões de projeto

- **Horários como string "HH:mm"** em vez de `java.time`, para evitar desugaring (o `minSdk` do projeto é 23, abaixo do 26 exigido pelo `java.time` nativo).
- **Datas como `long` (epoch millis)** por simplicidade e para permitir ordenação/filtro direto em SQL.
- **`status` como enum** (`StatusDose`), convertido para texto no banco via `Converters` — assim o Room não precisa modelar `status` como inteiro mágico.
- **`ForeignKey` com `onDelete = CASCADE`**: excluir um medicamento remove automaticamente seus horários e histórico de doses.
- Acesso ao banco sempre fora da main thread — `AppDatabase` expõe um `ExecutorService` (`AppDatabase.executor`) para isso.

## Próximos passos sugeridos

1. Rodar `Gradle Sync` no Android Studio para baixar as dependências do Room.
2. Criar as telas de cadastro/detalhes chamando os DAOs através do `executor` (nunca na main thread).
3. Se o esquema mudar depois que o app já estiver em uso por alguém, criar uma `Migration` em vez de só subir a `version`.
