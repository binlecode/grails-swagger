package swagger

import grails.test.mixin.integration.Integration
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import org.springframework.beans.factory.annotation.Value
import spock.lang.Specification

/**
 * This functional testing shows how to call the apidoc endpoint to generate swagger api doc JSON via Gradle
 * functional testing stage. The test spec also saves the generated JSON file to project root folder by default.
 */
@Integration
class SwaggerApidocFunctionalSpec extends Specification {

    /**
     * Server port configuration for Grails test environment is set to server.port = 0 by default, which
     * means a random available port is used each time the application starts.
     *
     * The value for the port number is accessible via ${local.server.port} in our integration test.
     * It is lazy-initialized as system property by embedded tomcat at runtime. And is only set by
     * SpringBoot test running with @WebIntegrationTests, which is configured by Grails integration tests.
     */
    @Value('${local.server.port}')
    Integer serverPort  // alternatively = System.property('local.server.port')

    String serverHostIp = InetAddress.getLocalHost().getHostAddress()

    HTTPBuilder http

    def setup() {
    }

    def cleanup() {
    }

    void "test apidoc json"() {
        given:
        String jsonText
        def json

        when: "The apidoc endpoint is called"
        http = new HTTPBuilder("http://${serverHostIp}:${serverPort}/apidoc/")
        /*
         * Normally we would use the method "request(GET, JSON)" to return a json result, but with the release
         * of groovy 2.3.0+, which included extensive changes to JSON parsing, the result returned from the
         * response could not be parsed by HTTPBuilder's default JSON parser automatically.
         *
         * Specifying the content type as TEXT removes the parsing from the call. Code was added to parse the returned
         * text into JSON using the more lenient 'LAX' parser.
         *
         * @see https://gist.github.com/zedar/abbb3c01635c0d6a977f for related bug thread
         */
        http.request(Method.GET) { req ->
            uri.path = 'getDocuments'
            headers.Accept = 'application/json'

            response.success = { resp ->
                jsonText = resp.entity.content.text  // get text content and parse manually
                String jsonTextPretty = JsonOutput.prettyPrint(jsonText)
                println "json pretty print: \n${jsonTextPretty}"
                new File('swagger-api-test.json').withWriter('UTF-8') { writer ->
                    writer.write(jsonTextPretty)
                }

//                def parser = new JsonSlurper().setType(JsonParserType.LAX)
                json = new JsonSlurper().parseText(jsonText)
                json.host = "${serverHostIp}:8080"
                new File('swagger-api.json').withWriter('UTF-8') { writer ->
                    writer.write(JsonOutput.prettyPrint(JsonOutput.toJson(json)))
                }
            }
            response.failure = { resp ->
                println "fail: $resp"
            }
        }

        then: "The json is correct"
        jsonText
        json.swagger == '2.0'  // verify JSON swagger version
        json.paths.size() > 0
    }

    void "test apidoc yaml"() {
        given:
        String yamlText
        def yaml

        when: "The apidoc endpoint is called with yaml mime type"
        http = new HTTPBuilder("http://${serverHostIp}:${serverPort}/apidoc/")
        http.request(Method.GET) { req ->
            uri.path = 'getDocuments'
            headers.Accept = 'application/x-yaml'

            response.success = { resp ->
                yamlText = resp.entity.content.text  // get text content for manual parse later
                println "yaml  print: \n${yamlText}"
                new File('swagger-api-test.yaml').withWriter('UTF-8') { writer ->
                    writer.write(yamlText)
                }
            }
            response.failure = { resp ->
                println "fail: $resp"
            }
        }

        yaml = new org.yaml.snakeyaml.Yaml().load(yamlText)

        then: "The yaml is correct"
        yaml.swagger == '2.0'
        yaml.paths.size() > 0
    }

}
