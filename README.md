# MediLembrete

Aplicativo Android desenvolvido como projeto acadêmico para ajudar pessoas a organizar medicamentos e acompanhar as doses programadas.

> **Status:** em desenvolvimento. A implementação da camada de dados com Room está no [Pull Request #3](https://github.com/gabrielcyber-dev/MediLembrete/pull/3) e precisa ser revisada e integrada antes de ser considerada parte da versão principal.

## Índice

- [Objetivo](#objetivo)
- [Funcionalidades e andamento](#funcionalidades-e-andamento)
- [Tecnologias](#tecnologias)
- [Como executar](#como-executar)
- [Como o aplicativo é organizado](#como-o-aplicativo-é-organizado)
- [Conceitos importantes](#conceitos-importantes)
- [Fluxo de funcionamento](#fluxo-de-funcionamento)
- [Banco de dados planejado](#banco-de-dados-planejado)
- [Como testar](#como-testar)
- [Como contribuir com o projeto](#como-contribuir-com-o-projeto)
- [Próximos passos](#próximos-passos)
- [Equipe](#equipe)

## Objetivo

O MediLembrete tem como objetivo permitir que a pessoa cadastre medicamentos, organize os horários de uso e consulte o que precisa tomar. A proposta inclui acompanhar doses pendentes, doses tomadas e histórico.

O aplicativo é um projeto acadêmico. Ele **não substitui orientação médica** e não deve ser usado para decidir por conta própria doses ou alterações de tratamento.

## Funcionalidades e andamento

O projeto está sendo desenvolvido por etapas. A situação abaixo diferencia o que existe na branch principal (`master`) do que está proposto para integração.

| Parte | Situação |
| --- | --- |
| Tela inicial e navegação entre telas | Implementação inicial na `master` |
| Cadastro de medicamento | Existem duas implementações que precisam ser unificadas |
| Banco de dados local com Room | Proposto no [PR #3](https://github.com/gabrielcyber-dev/MediLembrete/pull/3); conferir o estado do PR antes de trabalhar nessa parte |
| Horários recorrentes e registros de doses | Modelados na proposta do PR #3 |
| Lembretes/notificações do Android | Trabalho a concluir e testar |
| Testes e ajustes finais | Devem acompanhar a integração das funcionalidades |

**Atenção:** a documentação acompanha o desenvolvimento e pode mudar. Antes de assumir que uma funcionalidade já está disponível, confira a branch e o código que você está executando.

## Tecnologias

- **Java:** linguagem usada para a lógica do aplicativo.
- **XML:** arquivos que descrevem a aparência das telas Android.
- **Android SDK:** ferramentas e bibliotecas para criar o aplicativo.
- **Gradle:** sistema que configura, compila e testa o projeto.
- **Git:** controle de versão do código.
- **GitHub:** hospedagem do repositório, branches e Pull Requests.
- **Room / SQLite:** camada de persistência local proposta no PR #3. Room é uma biblioteca que facilita trabalhar com o banco SQLite.

## Como executar

### Requisitos

- Git;
- JDK 17;
- Android SDK instalado e configurado;
- Um dispositivo Android com depuração habilitada ou um emulador;
- Gradle Wrapper incluído no projeto.

O projeto pode ser editado em uma IDE compatível com Android/Java. O Android Studio facilita a configuração do SDK e a execução em emulador, mas o código também pode ser editado em outros editores se o ambiente Android estiver configurado.

### 1. Baixe o projeto

```bash
git clone https://github.com/gabrielcyber-dev/MediLembrete.git
cd MediLembrete
```

### 2. Confira o Java

```bash
java -version
```

Use o JDK 17 para manter o ambiente alinhado ao utilizado no desenvolvimento.

### 3. Compile o aplicativo

No Linux ou macOS:

```bash
chmod +x gradlew
./gradlew assembleDebug
```

No Windows, use:

```bat
gradlew.bat assembleDebug
```

O arquivo APK de depuração normalmente é gerado em:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Se a compilação falhar, leia a primeira mensagem de erro relevante no terminal, confira a versão do Java e verifique se o Android SDK está instalado. Se estiver usando uma branch que ainda não inclui as dependências do Room, os comandos de teste e a configuração podem ser diferentes.

## Como o aplicativo é organizado

Os nomes e a estrutura podem variar entre branches. Estes são os principais elementos que você encontrará no projeto:

| Elemento | Responsabilidade |
| --- | --- |
| `MainActivity.java` | Controla a tela inicial e suas interações |
| `NovoMedicamentoActivity.java` | Implementação de tela de cadastro presente na versão inicial |
| `CadastroMedicamentoActivity.java` | Outra tela de cadastro, usada na proposta do PR #3; precisa ser conciliada com a tela anterior |
| `model/Medicamento.java` | Modelo Java simples usado pela implementação inicial |
| `data/entity/` | Entidades que representam dados persistidos na proposta com Room |
| `data/dao/` | Interfaces com operações de leitura e escrita no banco, na proposta com Room |
| `data/AppDatabase.java` | Configuração do banco Room, na proposta com Room |
| `data/*Repository.java` | Camada que organiza o acesso aos dados, na proposta com Room |
| `res/layout/*.xml` | Estrutura visual das telas |
| `res/drawable/` | Recursos visuais, como fundos e formas |
| `app/build.gradle` | Configurações do módulo Android e dependências |
| `gradlew` / `gradlew.bat` | Scripts do Gradle Wrapper para Linux/macOS e Windows |

Para conferir os arquivos exatos da branch atual, navegue pela pasta [app/src/main](https://github.com/gabrielcyber-dev/MediLembrete/tree/master/app/src/main).

## Conceitos importantes

Esta seção serve como um guia rápido para quem está aprendendo Android e Java.

### Activity

Uma `Activity` representa uma tela ou ponto de interação do aplicativo. Por exemplo, a tela inicial e a tela de cadastro podem ser implementadas em Activities.

### Intent

Um `Intent` é uma mensagem usada para solicitar uma ação ao Android. No MediLembrete, ele pode ser usado para abrir a tela de cadastro a partir da tela inicial.

### Modelo (`Medicamento`)

Um modelo representa os dados de um medicamento, como nome, dosagem e quantidade. A implementação inicial usa um objeto Java simples. A proposta com Room possui uma entidade própria para representar os dados armazenados. Essas classes têm objetivos relacionados, mas não são automaticamente a mesma classe — por isso as duas implementações de cadastro precisam ser integradas com cuidado.

### DAO

DAO significa *Data Access Object*. É a camada que declara operações para consultar, inserir, atualizar ou excluir dados. Com Room, essas operações são descritas por interfaces e anotações, e a biblioteca gera parte da implementação.

### Repository

O repositório funciona como intermediário entre a interface e as fontes de dados. Assim, a tela solicita uma operação ao repositório em vez de concentrar regras de banco de dados dentro da Activity.

### Room e SQLite

SQLite é o banco de dados local do Android. Room é uma biblioteca que adiciona uma camada de abstração sobre SQLite, ajudando a definir entidades, consultas e DAOs e a detectar alguns erros durante a compilação.

### XML

Os arquivos XML em `res/layout` descrevem elementos visuais, como textos, campos de entrada e botões. O Java controla o comportamento desses elementos.

### Gradle

Gradle automatiza tarefas como baixar dependências, compilar o código e executar testes. O arquivo `app/build.gradle` configura o módulo do aplicativo; o Gradle Wrapper permite executar uma versão definida do Gradle sem precisar instalá-la globalmente.

## Fluxo de funcionamento

O fluxo conceitual esperado é:

```text
Pessoa usuária
    |
    v
Tela (Activity + XML)
    |
    | lê os campos e valida as informações
    v
Modelo / entidade Medicamento
    |
    v
Repository
    |
    v
DAO
    |
    v
Banco local (Room / SQLite)
```

A tela não deve precisar conhecer todos os detalhes do banco. Separar essas responsabilidades deixa o código mais fácil de testar e manter.

**Importante:** na versão atual, esse fluxo ainda depende da integração entre as duas telas de cadastro e a camada de dados. Consulte o [PR #3](https://github.com/gabrielcyber-dev/MediLembrete/pull/3) para entender a implementação proposta, sem presumir que ela já foi incorporada à `master`.

## Banco de dados planejado

A proposta do PR #3 utiliza três grupos de dados:

| Entidade/tabela | Para que serve |
| --- | --- |
| `medicamentos` | Guarda os dados principais de cada medicamento e o período de tratamento |
| `horarios` | Guarda um ou mais horários recorrentes associados a um medicamento |
| `registros_dose` | Guarda cada dose programada e seu estado, como pendente, tomada ou perdida |

Um medicamento pode ter vários horários e vários registros de dose. Os registros permitem diferenciar, por exemplo, a dose das 08:00 de hoje da dose das 08:00 de ontem.

A modelagem detalhada proposta está em [docs/MODELAGEM.md no PR #3](https://github.com/gabrielcyber-dev/MediLembrete/blob/guilherme/docs/MODELAGEM.md). O link aponta para a branch do Pull Request; confirme o conteúdo antes de usar como referência para a branch principal.

## Como testar

Depois que a implementação com Room estiver integrada, o PR #3 documenta estes comandos:

```bash
./gradlew testDebugUnitTest
./gradlew connectedDebugAndroidTest
```

- `testDebugUnitTest`: executa testes unitários que rodam na JVM.
- `connectedDebugAndroidTest`: executa testes instrumentados em um dispositivo ou emulador conectado.

O segundo comando exige um dispositivo/emulador configurado e acessível. Confira os resultados atuais do PR e execute os testes novamente após alterações; resultados anteriores não garantem que a versão atual continua passando.

## Como contribuir com o projeto

1. **Atualize sua cópia local** antes de começar.
2. **Confira a branch de destino** e trabalhe em uma branch própria para a tarefa.
3. Faça alterações pequenas, com um objetivo claro.
4. Compile e teste o que mudou.
5. Revise o diff para confirmar que não incluiu arquivos ou mudanças não relacionadas.
6. Envie as alterações e abra um Pull Request para revisão antes de integrar à branch principal.
7. Não use *force-push* na branch principal. Se houver divergência entre sua cópia local e o GitHub, investigue antes de tentar sincronizar.

Exemplo de atualização da branch principal:

```bash
git switch master
git pull origin master
```

Exemplo de criação de uma branch de trabalho:

```bash
git switch -c docs/melhora-readme
```

Se você já tem uma branch com esse nome, escolha outro nome ou mude para a branch existente. Não faça alterações diretamente em uma branch compartilhada sem combinar com a equipe.

### Padrão de mensagens de commit

Use mensagens curtas que expliquem a intenção da alteração:

```text
tipo: descrição objetiva
```

Exemplos:

```text
feat: adiciona validação do cadastro
fix: corrige abertura da tela de medicamentos
refactor: separa acesso ao banco da Activity
test: adiciona testes para validação de medicamento
docs: explica a arquitetura no README
build: configura dependências do Room
```

Os prefixos são uma convenção da equipe, não comandos especiais do Git.

## Próximos passos

1. Revisar e decidir sobre o [Pull Request #3](https://github.com/gabrielcyber-dev/MediLembrete/pull/3).
2. Escolher uma única tela de cadastro e conectá-la à camada de dados.
3. Garantir que a tela inicial liste os medicamentos realmente salvos.
4. Implementar e testar a geração das doses e a atualização de seus estados.
5. Desenvolver os lembretes/notificações e validar o comportamento no Android.
6. Corrigir os problemas de layout e acessibilidade identificados nos testes.
7. Atualizar esta documentação sempre que a arquitetura ou as funcionalidades mudarem.

## Equipe

Projeto acadêmico desenvolvido em equipe. As responsabilidades são organizadas entre interface, modelagem de dados, persistência e integração. Confirme a distribuição atual das tarefas com o grupo e registre decisões importantes nos Pull Requests.

## Licença

Projeto desenvolvido para fins acadêmicos. Nenhuma licença de código aberto específica está indicada neste README; consulte o repositório ou a equipe antes de reutilizar o código fora do projeto.
