# MedLembrete

Aplicativo Android desenvolvido como Projeto Integrador acadêmico. O objetivo é ajudar pessoas a organizar medicamentos, acompanhar horários e registrar doses.

> **Status:** em desenvolvimento. A camada Room e a interface XML foram reunidas na branch de integração e estão em revisão no [Pull Request #7](https://github.com/gabrielcyber-dev/MediLembrete/pull/7). A compilação e os testes Android ainda precisam ser executados após a integração.

## Objetivo

O MedLembrete busca centralizar informações de medicamentos e apoiar a rotina de tratamento com cadastro, horários programados e acompanhamento das doses.

**Aviso:** o aplicativo é uma ferramenta de organização e não substitui orientação de médicos, farmacêuticos ou outros profissionais de saúde. Não utilize o aplicativo para alterar doses ou tratamentos por conta própria.

## Funcionalidades e andamento

| Funcionalidade | Situação |
| --- | --- |
| Estrutura Android e navegação inicial | Implementação existente na branch principal |
| Persistência local com Room/SQLite | Integrada à `master` |
| Cadastro e validação de medicamentos | Camada de dados integrada; interface em evolução |
| Horários associados aos medicamentos | Modelados na camada Room |
| Registro de doses e estados (pendente, tomado, perdido) | Estrutura e consultas disponíveis na camada de dados |
| Listagem, detalhes, edição e exclusão com a interface refinada | Em integração no [PR #7](https://github.com/gabrielcyber-dev/MediLembrete/pull/7) |
| Notificações e lembretes Android | Ainda precisam de implementação/validação integrada |
| Testes em dispositivo físico | Necessários após a integração da interface |

A tabela descreve o estado conhecido do desenvolvimento. Um recurso modelado no banco não significa, por si só, que já esteja disponível em uma tela ou funcionando como notificação no aparelho.

## Tecnologias

- **Java** — lógica da aplicação.
- **XML** — layouts e recursos visuais Android.
- **Android SDK** — plataforma de desenvolvimento.
- **Gradle** — compilação, dependências e execução de testes.
- **Room** — camada de persistência sobre SQLite.
- **SQLite** — banco de dados local.
- **LiveData** — observação de dados e atualização das telas.
- **JUnit / AndroidX Test** — testes unitários e instrumentados.
- **Git e GitHub** — versionamento, branches e revisão por Pull Requests.

## Requisitos para executar

- JDK 17;
- Android SDK configurado;
- Git;
- Android Studio ou outro ambiente com as ferramentas Android configuradas;
- Emulador Android ou dispositivo físico com depuração USB habilitada para testes instrumentados.

O projeto pode ser editado no VS Code, desde que o JDK, o Android SDK e as ferramentas de build estejam configurados corretamente.

## Como baixar e compilar

Clone o repositório:

```bash
git clone https://github.com/gabrielcyber-dev/MediLembrete.git
cd MediLembrete
```

Confira a versão do Java:

```bash
java -version
```

Compile no Linux/macOS:

```bash
chmod +x gradlew
./gradlew clean assembleDebug
```

No Windows:

```bat
gradlew.bat clean assembleDebug
```

Se a compilação terminar com sucesso, o APK de depuração normalmente estará em:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Testes

Testes unitários:

```bash
./gradlew testDebugUnitTest
```

Testes instrumentados (exigem emulador ou dispositivo conectado):

```bash
./gradlew connectedDebugAndroidTest
```

No Windows, substitua `./gradlew` por `gradlew.bat`.

Execute novamente os testes depois de integrar alterações. Resultados de uma branch ou execução anterior não garantem que o código atual esteja compilando.

## Organização da camada de dados

A arquitetura separa a interface, as regras de acesso aos dados e o armazenamento:

```text
Activity / interface XML
        |
        v
   Repository
        |
        v
       DAO
        |
        v
   Room / SQLite
```

Principais componentes da camada de dados:

| Componente | Responsabilidade |
| --- | --- |
| `data/AppDatabase.java` | Configuração do banco Room |
| `data/Converters.java` | Conversões de tipos usadas pelo Room |
| `data/MedicamentoRepository.java` | Operações de acesso e atualização de medicamentos |
| `data/entity/` | Entidades persistidas: medicamento, horário e registro de dose |
| `data/dao/` | Consultas e operações do banco |
| `data/MedicamentoValidator.java` | Validações dos dados de medicamento |
| `docs/MODELAGEM.md` | Documentação da modelagem de dados |

O banco organiza os dados em três entidades principais:

- **Medicamento:** informações do medicamento e do tratamento.
- **Horario:** horários associados a cada medicamento.
- **RegistroDose:** ocorrência programada de uma dose e seu estado, como pendente, tomada ou perdida.

Os acessos ao banco devem ocorrer fora da thread principal para evitar travamentos na interface.

## Estrutura geral do projeto

```text
MediLembrete/
├── app/
│   └── src/
│       ├── main/
│       │   ├── java/com/example/medilembrete/
│       │   │   └── data/
│       │   │       ├── dao/
│       │   │       ├── entity/
│       │   │       ├── AppDatabase.java
│       │   │       ├── Converters.java
│       │   │       └── MedicamentoRepository.java
│       │   └── res/
│       │       ├── drawable/
│       │       ├── layout/
│       │       └── values/
│       ├── test/
│       └── androidTest/
├── docs/
│   └── MODELAGEM.md
├── gradle/
├── build.gradle
├── settings.gradle
├── gradlew
└── README.md
```

A estrutura pode evoluir conforme a integração das telas e das funcionalidades.

## Fluxo esperado de cadastro

1. A pessoa informa os dados do medicamento na tela.
2. A interface valida os campos obrigatórios.
3. A Activity encaminha a operação ao repositório.
4. O repositório utiliza os DAOs para persistir os dados no Room.
5. As telas observam os dados e apresentam a lista atualizada.

## Cuidados com o banco durante o desenvolvimento

A configuração atual do Room utiliza uma estratégia de migração destrutiva enquanto o esquema está em desenvolvimento. Isso facilita testes quando a estrutura muda, mas pode apagar dados locais ao atualizar o banco. Antes de distribuir o aplicativo para uso real, essa estratégia deve ser substituída por migrações versionadas e testadas.

## Git e fluxo de contribuição

O desenvolvimento é organizado em branches. Faça alterações em uma branch específica da tarefa, compile e teste antes de abrir um Pull Request para revisão.

Exemplos de mensagens de commit:

```text
feat: adiciona cadastro de medicamento
fix: corrige validação da duração
refactor: separa acesso ao banco da Activity
test: adiciona testes para o repositório
docs: atualiza documentação do projeto
```

Evite alterações diretas na `master` e nunca utilize force-push nela. Em caso de divergência entre branches, compare os históricos e resolva os conflitos antes de integrar.

## Próximas etapas

1. Resolver os conflitos entre a interface refinada e a camada Room no [PR #7](https://github.com/gabrielcyber-dev/MediLembrete/pull/7).
2. Compilar e executar os testes unitários e instrumentados após a resolução.
3. Validar cadastro, edição, exclusão e listagem em dispositivo Android.
4. Implementar e testar os lembretes/notificações.
5. Corrigir ajustes de acessibilidade e comportamento com as barras do sistema.
6. Substituir a migração destrutiva do Room por migrações versionadas antes de uma distribuição real.

## Equipe

Projeto acadêmico desenvolvido como Projeto Integrador. As contribuições são organizadas por branches e Pull Requests no GitHub.

## Licença

Repositório destinado ao desenvolvimento acadêmico. Consulte a equipe responsável antes de reutilizar ou distribuir o código.
