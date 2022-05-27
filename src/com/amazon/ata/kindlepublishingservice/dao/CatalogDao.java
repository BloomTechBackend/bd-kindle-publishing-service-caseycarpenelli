package com.amazon.ata.kindlepublishingservice.dao;

import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.enums.PublishingRecordStatus;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;
import com.amazon.ata.kindlepublishingservice.publishing.KindleFormattedBook;
import com.amazon.ata.kindlepublishingservice.utils.KindlePublishingUtils;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import javax.inject.Inject;

public class CatalogDao {

    private final DynamoDBMapper dynamoDbMapper;

    /**
     * Instantiates a new CatalogDao object.
     *
     * @param dynamoDbMapper The {@link DynamoDBMapper} used to interact with the catalog table.
     */
    @Inject
    public CatalogDao(DynamoDBMapper dynamoDbMapper) {
        this.dynamoDbMapper = dynamoDbMapper;
    }

    /**
     * Returns the latest version of the book from the catalog corresponding to the specified book id.
     * Throws a BookNotFoundException if the latest version is not active or no version is found.
     * @param bookId Id associated with the book.
     * @return The corresponding CatalogItem from the catalog table.
     */
    public CatalogItemVersion getBookFromCatalog(String bookId) {
        CatalogItemVersion book = getLatestVersionOfBook(bookId);

        if (book == null || book.isInactive()) {
            throw new BookNotFoundException(String.format("No book found for id: %s", bookId));
        }

        return book;
    }

    public CatalogItemVersion removeBookFromCatalog(String bookId) {
        CatalogItemVersion book = getLatestVersionOfBook(bookId);
        if (book == null || book.isInactive()) {
            throw new BookNotFoundException(String.format("No book found for id: %s", bookId));
        }
        book.setInactive(true);
        book.setBookId(bookId);
        dynamoDbMapper.save(book);
        return book;

    }
        //validateBookExists says it needs to be void but im confused on how that would be used so for now
        //im going to make it return a boolean so i can use the value it returns in my other logic
    public boolean validateBookExists(String bookId) {
        //creates new CatalogItemVersion object and assigns it the value that was passed in
        CatalogItemVersion catalogItemVersion = new CatalogItemVersion();
        catalogItemVersion.setBookId(bookId);

        //this queries the table with the book id as the hash
        DynamoDBQueryExpression<CatalogItemVersion> queryExpression = new DynamoDBQueryExpression()
                .withHashKeyValues(catalogItemVersion)
                .withScanIndexForward(false)
                .withLimit(10);

        List<CatalogItemVersion> results = dynamoDbMapper.query(CatalogItemVersion.class, queryExpression);
        //this weeds out the bookid queries that dont exists
        if (results.isEmpty()) {
            return false;
        }
        return true;


    }

    //not sure if i need to make a new DyanmoDB table or not but for now i will just use the CatalogItemVersions
    public CatalogItemVersion createOrUpdateBook(KindleFormattedBook kindleFormattedBook) {
        //if the book does exist in the table
        if (validateBookExists(kindleFormattedBook.getBookId())) {
            CatalogItemVersion newBook = new CatalogItemVersion();
            newBook.setBookId(kindleFormattedBook.getBookId());
            newBook.setAuthor(kindleFormattedBook.getAuthor());
            newBook.setGenre(kindleFormattedBook.getGenre());
            newBook.setText(kindleFormattedBook.getText());
            newBook.setTitle(kindleFormattedBook.getTitle());
            newBook.setInactive(false);
            int newVersion = dynamoDbMapper.load(newBook.getVersion());
            newBook.setVersion(newVersion + 1);
            dynamoDbMapper.save(newBook);

            if (!validateBookExists(newBook.getBookId())) {
                PublishingStatusDao publishingStatusDao = new PublishingStatusDao(dynamoDbMapper);
                //supposed to set the status to failed but can't figure it out
                //because i would need a publishingRecordId and those are only in requests which i converted it out of
                throw new BookNotFoundException("Book id " + newBook.getBookId() + " was not in table");
            }

            return newBook;
        }

        //if the book doesn't exist
        //it was an idea
//                  KindleFormattedBook newBook = KindleFormattedBook.builder()
//                    .withBookId(KindlePublishingUtils.generateBookId())
//                    .withAuthor(kindleFormattedBook.getAuthor())
//                    .withGenre(kindleFormattedBook.getGenre())
//                    .withText(kindleFormattedBook.getText())
//                    .withTitle(kindleFormattedBook.getTitle())
//                    .build();

        CatalogItemVersion newBook = new CatalogItemVersion();
        newBook.setBookId(KindlePublishingUtils.generateBookId());
        newBook.setAuthor(kindleFormattedBook.getAuthor());
        newBook.setGenre(kindleFormattedBook.getGenre());
        newBook.setText(kindleFormattedBook.getText());
        newBook.setTitle(kindleFormattedBook.getTitle());
        newBook.setInactive(false);
        newBook.setVersion(1);

        dynamoDbMapper.save(newBook);
        return newBook;

    }




    // Returns null if no version exists for the provided bookId
    private CatalogItemVersion getLatestVersionOfBook(String bookId) {
        CatalogItemVersion book = new CatalogItemVersion();
        book.setBookId(bookId);

        DynamoDBQueryExpression<CatalogItemVersion> queryExpression = new DynamoDBQueryExpression()
            .withHashKeyValues(book)
            .withScanIndexForward(false)
            .withLimit(1);

        List<CatalogItemVersion> results = dynamoDbMapper.query(CatalogItemVersion.class, queryExpression);
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }
}
