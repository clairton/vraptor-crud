# vraptor-crud
Boostrap para construir uma aplicação REST com VRaptor para Single Page Application.

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
