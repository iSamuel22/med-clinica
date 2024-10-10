# Sistema de Gerenciamento de Histórico de Atendimentos

Este projeto é um sistema web para gerenciamento de atendimentos de pacientes e médicos em uma clínica, desenvolvido utilizando Java, JSF, PrimeFaces, JPA e MySQL. Ele permite o cadastro de médicos, o lançamento de atendimentos e a geração de relatórios com base em filtros de data e médicos.

## Funcionalidades

#### 1. Cadastro de Médicos
* Permite cadastrar novos médicos, listar, editar e excluir médicos existentes.
* Os médicos são listados em ordem alfabética pelo nome.
* Utiliza Column Filtering do componente dataTable para filtrar médicos pelo nome.

#### 2. Lançar Atendimento

* Tela onde o CPF do paciente é solicitado para verificar se ele já foi atendido.
  * Caso tenha sido atendido, os dados são preenchidos automaticamente.
  * Caso contrário, o paciente deve ser cadastrado manualmente.
* Após o preenchimento dos dados, o usuário pode selecionar os médicos e a data/hora do atendimento.
* Ao gravar, um número de atendimento único é gerado e o status do atendimento fica como "Em Aberto".
* Mensagens de sucesso ou erro são exibidas após a operação.

#### 3. Relatório de Atendimentos

* Filtros disponíveis:
  * Por data de início e término.
  * Por médico específico.
  * Combinação de data e médico.
* Exibe uma listagem com: Número do Atendimento, CPF, Nome do Paciente, Data do Atendimento, Situação, e um botão para exibir os médicos envolvidos.
* Permite excluir atendimentos ou finalizar um atendimento com um parecer.

#### 4. Validações

* Todas as telas possuem validações, incluindo campos obrigatórios e mensagens de confirmação para inclusão, edição e exclusão.

## Tecnologias Utilizadas

* Java
* JSF (JavaServer Faces)
* PrimeFaces
* JPA (Java Persistence API)
* MySQL

## Pré-requisitos

Para executar este projeto, você precisará de:
  * JDK 11+
  * Servidor de aplicação (ex: WildFly ou Apache Tom Cat)
  * MySQL
  * Biblioteca do PrimeFaces
  * IDE com suporte para Maven ou Gradle

## Instalação

1. Criar um JavaServer Faces (JSF) v2.3 Project no Eclipse:

  * No Eclipse, selecione **File** > **New** > **Dynamic Web Project**.
  * Defina o nome do projeto e configure o servidor de aplicação.
  * Certifique-se de selecionar **JavaServer Faces (JSF) v2.3** e adicionar `*.xhtml` como a extensão de página padrão.

2. Clone o repositório:
```bash
git clone https://github.com/smuelp/med-clinica.git
```

3. Adicione as bibliotecas:

  * Inclua as dependências do PrimeFaces, JPA, e MySQL no arquivo pom.xml (ou build.gradle se estiver usando Gradle).

4. Configurar o banco de dados no arquivo persistence.xml:

  * Defina a conexão com o banco de dados MySQL no arquivo persistence.xml.

5. Executar o servidor de aplicação:

  * Inicie o servidor de aplicação e acesse o sistema via navegador.

## Estrutura do Projeto

`modelo/`: Contém as classes de domínio.
`controle/`: Controladores das interações do usuário.
`service/`: Lógica de negócio e interações com o banco de dados.
`views/`: Páginas JSF (XHTML) para interface com o usuário.
