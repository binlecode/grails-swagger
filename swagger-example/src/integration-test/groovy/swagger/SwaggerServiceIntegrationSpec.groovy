package swagger

import grails.test.mixin.integration.Integration
import groovy.json.JsonOutput
import io.swagger.models.Swagger
import io.swagger.parser.SwaggerParser
import spock.lang.Shared
import spock.lang.Specification

@Integration
class SwaggerServiceIntegrationSpec extends Specification {

    @Shared
    File fileJson
    @Shared
    Swagger swaggerModel

    def setupSpec() {
        fileJson = new File("src/test/resources/swagger-api-test.json")
        swaggerModel = new SwaggerParser().read(fileJson.path)
    }

    def setup() {
    }

    def cleanup() {
    }

    void 'test convert json text from swagger model'() {
        when:
        String jsonText = SwaggerService.getJsonDocument(swaggerModel)
        Swagger swg = new SwaggerParser().parse(jsonText)

        then:
        println JsonOutput.prettyPrint(jsonText)
        swg.swagger == swaggerModel.swagger
        swg.paths.size() == swaggerModel.paths.size()
    }

    void "test convert yaml text from swagger model"() {
        when:
        String ymlText = SwaggerService.getYamlDocument(swaggerModel)
        Swagger swg = new SwaggerParser().parse(ymlText)   // SwaggerParser can parse both json and yaml text

        then:
        println ymlText
        swg.swagger == swaggerModel.swagger
        swg.paths.size() == swaggerModel.paths.size()
    }
}
