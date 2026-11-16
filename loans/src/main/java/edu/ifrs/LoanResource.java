package edu.ifrs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import edu.ifrs.client.IBookCatalog;
import edu.ifrs.model.Book;
import edu.ifrs.model.Loan;
import edu.ifrs.model.LoanRequest;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/loans")
@RolesAllowed("User")
public class LoanResource {

    @Inject
    @RestClient
    IBookCatalog bookCatalog;

    private Map<Long, Loan> loans = new ConcurrentHashMap<>();
    private AtomicLong idGenerator = new AtomicLong(1);

    @GET
    @Path("/books")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Book> listBooks() {
        return bookCatalog.listBooks();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Loan> listLoans(@QueryParam("borrower") String borrower) {
        List<Loan> result = new ArrayList<>();
        for (Loan loan : loans.values()) {
            if (borrower == null || loan.borrower().equalsIgnoreCase(borrower)) {
                result.add(loan);
            }
        }
        return result;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response newLoan(LoanRequest request) {
        Book book = bookCatalog.getBook(request.bookId());
        if (book.loaned()) {
            return Response.status(Response.Status.CONFLICT).build();
        }

        bookCatalog.markAsLoaned(request.bookId());

        Long id = idGenerator.getAndIncrement();
        Loan loan = new Loan(id, request.bookId(), request.borrower(), false);
        loans.put(id, loan);

        return Response.status(Response.Status.CREATED).entity(loan).build();
    }

    @PUT
    @Path("/{id}/return")
    @Produces(MediaType.APPLICATION_JSON)
    public Response returnLoan(@PathParam("id") Long id) {
        Loan loan = loans.get(id);
        if (loan == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (loan.returned()) {
            return Response.status(Response.Status.CONFLICT).build();
        }

        bookCatalog.markAsReturned(loan.bookId());

        Loan updated = new Loan(loan.id(), loan.bookId(), loan.borrower(), true);
        loans.put(id, updated);

        return Response.ok(updated).build();
    }
}
