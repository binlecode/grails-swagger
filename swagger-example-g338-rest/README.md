

```bash
grails create-app swagger-example-g338-rest --profile rest-api
```

Create domain:
```bash
grails create-domain-class swagger.example.g338.rest.Book
```

Book model is defined as below:
```groovy
@ApiModel
@JsonIgnoreProperties(['dirtyPropertyNames', 'dirty', 'attached', 'properties'])  // this affects global json marshalling
class Book {

    @ApiModelProperty(position = 1, required = true, value = "title of the book, must be provided")
    String title

    @ApiModelProperty(position = 2, required = false)
    String isbn

    String author

    static constraints = {
        title blank: false
        isbn nullable: true
        author nullable: true
    }

}
```


Then create MVC scaffold:
```bash
generate-all swagger.example.g338.rest.Book
```

Enable CORS to allow swagger API page to fire ajax to webservice API.
In ```grails-app/conf/application.yml```:
```yaml
grails:
    cors:
        enabled: true
```

Also optional global swagger API configuration info can be added to ```application.yml``` file.


Now the example application can be started and running at port 8080 (by default). The swagger API url is:
```http://localhost:8080/apidoc```
