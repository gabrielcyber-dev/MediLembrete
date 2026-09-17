# MediLembrete

Aplicativo Android desenvolvido como projeto acadêmico com o objetivo de auxiliar usuários no controle e acompanhamento de medicamentos por meio de lembretes.

## Status do projeto

Em desenvolvimento.

Atualmente, o projeto possui:

* Tela inicial do aplicativo;
* Navegação entre a tela inicial e a tela de cadastro;
* Tela de cadastro de medicamento;
* Modelo `Medicamento`;
* Seleção de intervalo entre doses;
* Definição de duração do tratamento;
* Opção de duração indefinida;
* Estrutura inicial preparada para persistência dos dados.

## Tecnologias

* Java
* XML
* Android SDK
* Gradle
* Git
* GitHub

## Estrutura principal

```text
MediLembrete/
├── app/
│   └── src/
│       └── main/
│           ├── java/
│           │   └── com/example/medilembrete/
│           │       ├── MainActivity.java
│           │       ├── NovoMedicamentoActivity.java
│           │       └── model/
│           │           └── Medicamento.java
│           │
│           └── res/
│               ├── layout/
│               │   ├── activity_main.xml
│               │   └── activity_novo_medicamento.xml
│               └── drawable/
│
├── gradle/
├── build.gradle
├── settings.gradle
├── gradlew
├── gradlew.bat
└── README.md
```

## Funcionamento atual

A aplicação inicia pela `MainActivity`.

Na tela inicial, o usuário pode selecionar a opção de adicionar um medicamento. A aplicação então utiliza um `Intent` para abrir a `NovoMedicamentoActivity`.

Na tela de cadastro, são coletadas informações como:

* Nome do medicamento;
* Dosagem;
* Quantidade;
* Horário inicial;
* Intervalo entre doses;
* Duração do tratamento;
* Duração indefinida.

Os dados são utilizados para criar um objeto da classe `Medicamento`.

## Modelo de dados

A classe `Medicamento` representa uma entidade do domínio da aplicação.

Atualmente, possui os seguintes atributos:

| Atributo            | Tipo      | Descrição                                          |
| ------------------- | --------- | -------------------------------------------------- |
| `id`                | `int`     | Identificador do medicamento                       |
| `nome`              | `String`  | Nome do medicamento                                |
| `dosagem`           | `String`  | Dosagem informada                                  |
| `quantidade`        | `String`  | Quantidade do medicamento                          |
| `horarioInicial`    | `String`  | Horário inicial do tratamento                      |
| `intervaloHoras`    | `int`     | Intervalo entre as doses                           |
| `duracaoDias`       | `int`     | Duração do tratamento                              |
| `duracaoIndefinida` | `boolean` | Indica se o tratamento não possui duração definida |

## Fluxo atual

```text
MainActivity
     │
     │ usuário seleciona
     │ "Adicionar remédio"
     ▼
NovoMedicamentoActivity
     │
     │ usuário informa os dados
     ▼
Medicamento
     │
     │ objeto criado
     ▼
Próxima etapa:
Persistência dos dados
```

## Próximas etapas

O desenvolvimento seguirá aproximadamente esta ordem:

1. Finalizar a interface de cadastro;
2. Validar os dados informados pelo usuário;
3. Implementar persistência dos medicamentos;
4. Implementar DAO e acesso ao banco de dados;
5. Exibir medicamentos cadastrados na tela inicial;
6. Implementar edição e exclusão de medicamentos;
7. Implementar sistema de lembretes/notificações;
8. Integrar completamente interface, modelo e banco de dados;
9. Realizar testes e correções;
10. Refinar a interface e preparar a versão final.

## Organização do Git

O projeto utiliza branches para separar o trabalho dos integrantes.

Branches atuais:

* `master` — branch principal do projeto;
* `gabriel` — desenvolvimento relacionado ao trabalho do Gabriel;
* `guilherme` — branch destinada ao trabalho do Guilherme;
* `nickolas` — branch destinada ao trabalho do Nickolas.

As alterações devem ser desenvolvidas em branches próprias e posteriormente integradas à `master` por meio de Pull Requests quando apropriado.

## Padrão de commits

Os commits devem utilizar mensagens curtas e objetivas, preferencialmente seguindo o padrão:

```text
tipo: descrição da alteração
```

Exemplos:

```text
feat: adiciona tela de cadastro de medicamento
fix: corrige navegação entre atividades
refactor: reorganiza modelo de medicamento
docs: atualiza documentação do projeto
style: ajusta layout da tela inicial
build: adiciona Gradle Wrapper
```

O objetivo é permitir que qualquer integrante consiga compreender o histórico do desenvolvimento sem precisar abrir cada alteração individualmente.

## Equipe

Projeto acadêmico desenvolvido em equipe.

Cada integrante trabalha em sua respectiva branch e as alterações são posteriormente integradas ao projeto principal.

## Licença

Projeto desenvolvido para fins acadêmicos.
