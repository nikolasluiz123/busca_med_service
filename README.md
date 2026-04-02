# BuscaMed - Buscador Inteligente de Medicamentos

Bem-vindo ao repositório do BuscaMed (Backend). Este serviço é construído em Kotlin com Ktor e utiliza a API do Google Gemini para extração de dados médicos (receitas e cartelas de comprimidos), além de integrar com a base de dados abertos da ANVISA.

## 🛠️ Configuração do Ambiente Local (Windows)

Para rodar o projeto localmente e autenticar com os serviços do Google Cloud Platform (GCP), você precisa instalar o Google Cloud CLI (`gcloud`).

1. Baixe o instalador do Google Cloud CLI para Windows no site oficial: [Instalador gcloud](https://cloud.google.com/sdk/docs/install?hl=pt-br#windows)

2. Execute o instalador e siga as instruções (deixe marcado para iniciar o terminal ao final).

3. No terminal do gcloud, autentique sua conta do Google:
```bash
   gcloud init
```

4. Configure as credenciais padrão de aplicação (ADC), que o código Ktor usará localmente para acessar o Firestore, Storage e Gemini:
```bash
gcloud auth application-default login
```
Uma janela do navegador se abrirá para você confirmar o acesso.

## 🔐 Autenticação e Uso dos Endpoints (Swagger)

A aplicação possui dois tipos de proteção nos endpoints. Você pode identificar qual token usar verificando as anotações no código `Routing.kt` ou no Swagger UI disponível em `/swagger`.

1. Token OIDC (Google Service-to-Service)

Usado para rotas administrativas ou integrações entre serviços.

**Como identificar:** No código, a rota está dentro de um bloco `authenticate("auth-google-oidc")`. No Swagger, o endpoint indicará a necessidade de um Bearer Token administrativo. Exemplo: `/v1/anvisa/import` ou `/v1/prescription/history`.

Como gerar para testes locais:

Abra seu terminal com o gcloud configurado e execute:
```bash
gcloud auth print-identity-token
```

2. Token JWT Firebase (Usuários Finais)

Usado para rotas expostas aos clientes da aplicação.

Como identificar: No código, a rota usa `authenticate("auth-jwt-firebase")`. Exemplo: `/v1/prescription/process/text`.

Como gerar para testes locais:

Como o Firebase não possui um gerador nativo no CLI para tokens de usuário final, você precisa fazer uma requisição POST usando a chave de API pública do projeto (solicite ao administrador):

```bash
curl -X POST "[https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=SUA_WEB_API_KEY_AQUI](https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=SUA_WEB_API_KEY_AQUI)" \
-H "Content-Type: application/json" \
-d '{"email":"usuario_teste@buscamed.com", "password":"senha_segura", "returnSecureToken":true}'
```

O token que você deve usar estará no campo idToken da resposta.

## 🏗️ Arquitetura do Projeto

O projeto segue os princípios da Clean Architecture (Arquitetura Limpa), dividindo o código em camadas para separar regras de negócio de detalhes de infraestrutura.

### api (Camada de Apresentação)

Responsável por receber requisições HTTP e devolver respostas. Não contém regras de negócio.

**Exemplo Prático:** PrescriptionController. Ele recebe um JSON com o texto da receita, passa para o ProcessTextUseCase e devolve o DTO ao usuário.

**Regra:** Os DTOs (Data Transfer Objects) em sua enorme maioria pertencem a essa camada, aqui que são definidas as rotas e os controllers que apenas cuidam da passagem de dados para outras camadas.

### domain (Camada de Domínio)

O coração da aplicação. Contém as entidades principais e os Casos de Uso (Use Cases). Não conhece banco de dados ou frameworks web.

**Exemplo Prático:** ProcessImageUseCase. Ele define os passos: receber a imagem, chamar o serviço do LLM, salvar o histórico de execução no repositório e despachar o salvamento da imagem no Storage.

**Regra:** Se precisar acessar dados externos, crie uma interface aqui (ex: AnvisaMedicationRepository) que será implementada na camada data.

### data (Camada de Dados / Infraestrutura)

Implementa as interfaces definidas no domain. É aqui que lidamos com o mundo externo (Firestore, Google Cloud Storage, APIs REST, SDK do Gemini).

**Exemplo Prático:** FirestoreAnvisaMedicationDataSource (lida com as queries no Firestore) ou GeminiMedicalPrescriptionImageProcessClient (lida com o SDK do Google GenAI).

**Regra:** Converte as respostas externas (como AnvisaMedicationDocument) para as entidades limpas do domain (como AnvisaMedication) antes de devolver para o Use Case.

### core (Camada Transversal)

Configurações globais, injeção de dependência (Koin), segurança, exceções base e utilitários.

## 🧪 Estrutura de Testes

Os testes são focados em garantir o comportamento sem depender de bancos de dados reais.

**Testes de Roteamento (RoutingTest)**: Utilizamos o testApplication do Ktor. Mockamos os Use Cases usando o MockK para verificar se os endpoints respondem com o status HTTP correto (ex: validando o comportamento sem autenticação em PrescriptionRoutingTest.kt).

**Testes de Integração Simulados (KtorClientTest)**: Para clientes HTTP externos (como a ANVISA), usamos o MockEngine do Ktor para simular respostas (ex: HttpStatusCode.BadGateway) e validar se o sistema lança as exceções corretas.

**Testes Unitários: **Focados em regras de negócio puras ou parsers (ex: ApacheCommonsAnvisaCsvParserTest), validando transformações de dados.

**Como continuar com os testes:**

Ao criar um novo Use Case, injete suas dependências via construtor, instancie a classe no teste passando mockk<NomeDoRepositorio>() e valide os fluxos com coEvery { ... } returns

1. Acesse o **Console do Firebase**.
2. Vá em Configurações do Projeto > Usuários e Permissões.
3. Adicione o e-mail do desenvolvedor e atribua o papel: **Firebase Viewer** (Visualizador do Firebase) ou, se quiser ser mais específico, vá no IAM do GCP e dê **Firebase Authentication Viewer**.

Com a Web API Key do projeto (que eles verão na tela de configurações do Firebase), eles poderão usar a requisição `curl` ensinada no README para criar usuários de teste ou logar, gerando o token necessário para bater no endpoint Ktor com `authenticate("auth-jwt-firebase")`.
