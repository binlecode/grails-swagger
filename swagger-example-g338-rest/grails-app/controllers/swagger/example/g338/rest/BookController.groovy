package swagger.example.g338.rest

import grails.validation.ValidationException
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses

import static org.springframework.http.HttpStatus.*

@Api(value = 'book', description = 'book resource')
class BookController {

    BookService bookService

    static responseFormats = ['json', 'xml']
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    @ApiOperation(
            value = "List Books",
            nickname = "/",
            produces = "application/json",
            consumes = "application/json",
            httpMethod = "GET",
            response = Book,
            responseContainer = 'array'
    )
    @ApiResponses([
            @ApiResponse(code = 405, message = "Method Not Allowed. Only GET is allowed"),
    ])
    @ApiImplicitParams([
            @ApiImplicitParam(name = 'offset',
                    value = 'Records to skip',
                    defaultValue = '0',
                    paramType = 'query',
                    dataType = 'int'),
            @ApiImplicitParam(name = 'max',
                    value = 'Max records to return',
                    defaultValue = '10',
                    paramType = 'query',
                    dataType = 'int'),
            @ApiImplicitParam(name = 'sort',
                    value = 'Field to sort by',
                    defaultValue = 'id',
                    paramType = 'query',
                    dataType = 'string'),
            @ApiImplicitParam(name = 'order',
                    value = 'Order to sort by',
                    defaultValue = 'asc',
                    paramType = 'query',
                    dataType = 'string')
    ])
    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond bookService.list(params), model:[bookCount: bookService.count()]
    }

    @ApiOperation(
            value = "Show Book",
            nickname = "/{id}",
            produces = "application/json",
            consumes = "application/json",
            httpMethod = "GET",
            response = Book
    )
    @ApiResponses([
            @ApiResponse(code = 405, message = "Method Not Allowed. Only GET is allowed"),
            @ApiResponse(code = 404, message = "Resource Not Found")
    ])
    @ApiImplicitParams([
            @ApiImplicitParam(name = 'id',
                    value = 'Resource id',
                    paramType = 'query',
                    dataType = 'string',
                    required = true)
    ])
    def show(Long id) {
        respond bookService.get(id)
    }

    def save(Book book) {
        if (book == null) {
            render status: NOT_FOUND
            return
        }

        try {
            bookService.save(book)
        } catch (ValidationException e) {
            respond book.errors, view:'create'
            return
        }

        respond book, [status: CREATED, view:"show"]
    }

    def update(Book book) {
        if (book == null) {
            render status: NOT_FOUND
            return
        }

        try {
            bookService.save(book)
        } catch (ValidationException e) {
            respond book.errors, view:'edit'
            return
        }

        respond book, [status: OK, view:"show"]
    }

    def delete(Long id) {
        if (id == null) {
            render status: NOT_FOUND
            return
        }

        bookService.delete(id)

        render status: NO_CONTENT
    }
}
