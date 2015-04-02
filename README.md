# vraptor-crud
Bootstrap para construir uma aplicação REST com VRaptor para Single Page Application.

É necessário usar a versão do java 1.8+ e usar o parametro --parameters na compilação.
No caso de usar maven, segue um exemplo:
```xml
<build>
	...
	<plugins>
	...
		<plugin>
			<groupId>org.apache.maven.plugins</groupId>
			<artifactId>maven-compiler-plugin</artifactId>
			<version>3.3</version>
			<configuration>
				<encoding>UTF-8</encoding>
				<source>1.8</source>
				<target>1.8</target>
				<compilerArgs>
					<arg>-parameters</arg>
				</compilerArgs>
			</configuration>
		</plugin>
	...
	</plugins>
</build>
```

O vraptor-crud possui um controller abstrato que pode ser extendido e com pouco esforço disponibiliza
em REST as operações básicas do CRUD por exemplo:
```java
@Controller
public class AplicacaoController extends CrudController<Aplicacao> {
	@Deprecated
	protected AplicacaoController() {
		this(null, null, null, null, null, null);
	}

	@Inject
	public AplicacaoController(@Default final Repository repository,
			final Result result, @Language final Inflector inflector,
			final Mirror mirror, final ServletRequest request,
			final QueryParser queryParser) {
		super(Aplicacao.class, repository, result, inflector, mirror, request,
				queryParser);
	}
}
```
Se desejar que seu recurso seja multi-tenancy injete um Repository com o qualifier @Tenant, veja mais em
https://github.com/clairton/tenant e https://github.com/clairton/repository.

As URL padrao do recurso(/path-da-aplicacao/recurso com o metodo HTTP GET), possui um mecanismo de query params, 
que aplicaca filtros na consultado do banco de dados, para tornar ela mais poderosa algumas opções podem ser usadas,
segue exemplos:
```java
http://meudominio.com/app/recurso?nome=abc //retornara o recurso com o nome igual a "abc"

http://meudominio.com/app/recurso?operacao.nome=abc //retornara o recurso com o nome da operacao igual a "abc"

http://meudominio.com/app/recurso?operacao[nome]=abc //retornara o recurso com o nome da operacao igual a "abc"

http://meudominio.com/app/recurso?id=>=1&id=<=11 //retornara o recurso com o id entre 1 e 11
```
Como pode notar a formato é o seguinte "nomeDoCampo=[operacaoLogica]valorDoFiltro", a operação lógica
não é obrigatório, sendo que se não for informada é assumida como "igual".
As operações lógicas disponíveis são:
* == Igual
* =* Igual ignorando maisculas e minusculas
* <> Diferente
* ∃  Existe
* ∅  Nulo
* !∅ Não Nulo
* >  Maior
* >= Maior ou Igual
* <  Menor
* <= Menor ou Igual

A implementação de segurança é opcional, para mais detalhes veja https://github.com/clairton/security.


Se usar o maven, será necessário adicionar os repositórios:
```xml
<repository>
	<id>mvn-repo-releases</id>
	<url>https://raw.github.com/clairton/mvn-repo/releases</url>
</repository>
<repository>
	<id>mvn-repo-snapshot</id>
	<url>https://raw.github.com/clairton/mvn-repo/snapshots</url>
</repository>
```
 Também adicionar as depêndencias:
```xml
<dependency>
    <groupId>br.eti.clairton</groupId>
    <artifactId>vraptor-crud</artifactId>
    <version>0.1.0-SNAPSHOT</version><!--Ou versão mais recente-->
</dependency>
```
