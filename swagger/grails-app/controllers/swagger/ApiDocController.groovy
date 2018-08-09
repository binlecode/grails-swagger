package swagger

import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

class ApiDocController {
    static responseFormats = ['json']
    static namespace = 'v1'
    static allowedMethods = [getDocuments: "GET"]
    static final MIME_YAML = 'application/x-yaml'

    SwaggerService swaggerService

    @Value("classpath*:**/webjars/swagger-ui/**/index.html")
    Resource[] swaggerUiResources

    def getDocuments() {
        if (request.getHeader('accept')?.contains(MediaType.APPLICATION_JSON_VALUE)) {
            try {
                String swaggerJson
                if (params.groupName) {
                    swaggerJson = swaggerService.generateSwaggerGroupDocument(params.groupName)
                } else {
                    swaggerJson = swaggerService.generateSwaggerDocument()
                }
                render contentType: MediaType.APPLICATION_JSON_UTF8_VALUE,
                        text: swaggerJson

            } catch (Exception e) {
                e.printStackTrace()
                render status: HttpStatus.INTERNAL_SERVER_ERROR,
                        text: 'Some error occurred'  //todo: better error msg
            }
        } else if (request.getHeader('accept')?.contains(MIME_YAML)) {
            try {
                String swaggerYaml
                if (params.groupName) {
                    swaggerYaml = swaggerService.generateSwaggerGroupDocument(params.groupName, [yaml: true])
                } else {
                    swaggerYaml = swaggerService.generateSwaggerDocument(yaml: true)
                }
                render contentType: MIME_YAML + ';charset=UTF-8',
                        text: swaggerYaml

            } catch (Exception e) {
                e.printStackTrace()
                render status: HttpStatus.INTERNAL_SERVER_ERROR,
                        text: 'Some error occurred'  //todo: better error msg
            }
        } else {
            redirect uri: "/webjars/swagger-ui${getSwaggerUiFile()}?url=${request.getRequestURI()}"
        }
    }

    protected String getSwaggerUiFile() {
        try {
            (swaggerUiResources.getAt(0) as Resource).getURI().toString().split("/webjars/swagger-ui")[1]
        } catch (Exception e) {
            throw new Exception("Unable to find swagger ui.. Please make sure that you have added swagger ui dependency eg:-\n compile 'org.webjars:swagger-ui:2.2.8' \nin your build.gradle file", e)
        }
    }
}
