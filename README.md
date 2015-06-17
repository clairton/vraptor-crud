# vraptor-crud[![Build Status](https://drone.io/github.com/clairton/vraptor-crud/status.png)](https://drone.io/github.com/clairton/vraptor-crud/latest)
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
O controle de erros e mensagens é efetuado através de um interceptor do CDI que avaliar as exceções lançadas,
e promulgando-as caso não as conheça. Para habilita-lo, deve adicionar a classe no beans.xml da sua aplicação:

```xml
<beans>
	<interceptors>
		<class>br.eti.clairton.vraptor.crud.interceptor.ExceptionVerifierInterceptor</class>
	</interceptors>
</beans>
```


O vraptor-crud possui um controller abstrato que quando extendido pouco esforço disponibiliza
em REST as operações básicas do CRUD por exemplo:
```java
@Controller
public class AplicacaoController extends CrudController<Aplicacao> {
	@Deprecated
	protected AplicacaoController() {
		this(null, null, null, null, null);
	}

	@Inject
	public AplicacaoController(@Default final Repository repository,
			final Result result, @Language final Inflector inflector,
			final ServletRequest request,
			final QueryParser queryParser) {
		super(Aplicacao.class, repository, result, inflector, request,
				queryParser);
	}
}
```
O nome do controller deve ser referente ao recurso no singular, a rota esta no plural, se em algum caso
especificos a pluralização não der certo, verifique o manual do https://github.com/clairton/inflector
que é a lib usada para fazer isso.
De qualquer forma ao criar um controller extendendo de CrudController, terá as seguintes URL's:
```http
URL                    HTTP Method   Java Method
/aplicacoes/new        [GET]         CrudController#new
/aplicacoes/{id}/edit  [GET]         CrudController#edit(id)
/aplicacoes/{id}       [GET]         CrudController#show(id)
/aplicacoes            [GET]         CrudController#index
/aplicacoes/{model.id} [PUT]         CrudController#update(model)
/aplicacoes            [POST]        CrudController#create(model)
/aplicacoes/{id}       [DELETE]      CrudController#destroy(id)

``` 

O formato aceito é JSON, sendo necessário setar o "Content-type" "como application/json".

Se desejar que seu recurso seja multi-tenancy injete um Repository com o qualifier @Tenant, veja mais em
https://github.com/clairton/tenant e https://github.com/clairton/repository.

As URL padrao do recurso(/path-da-aplicacao/recurso com o metodo HTTP GET), possui um mecanismo de query params, 
que aplicaca filtros na consultado do banco de dados, para tornar ela mais poderosa algumas opções podem ser usadas,
para isso verifique https://github.com/clairton/repository-vraptor:

A implementação de segurança é opcional, para mais detalhes veja https://github.com/clairton/security.

#Geração Relatórios

O projeto também conta com um mixin para geração de relatórios. Implementando a interface ExportControllerMixin:
```http
URL                    HTTP Method   Java Method
/aplicacoes.{format}   [POST]        ExportControllerMixin#export(format)
```
```java
http://meudominio.com/app/aplicacoes.{format}
```
O método gerara um arquivo e devolverá o nome desse arquivo em JSON na propriedade file_path. Posteriormente, o download
do arquivo pode ser feito:
```http
URL                    HTTP Method   Java Method
/downloads/{path}     [GET]          DownloadController#get(path)
```
```java
http://meudominio.com/app/downloads/{path}
```
Será necessário uma implementação da interface FileService, recomendamos o uso do projeto https://github.com/clairton/exporter.


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
