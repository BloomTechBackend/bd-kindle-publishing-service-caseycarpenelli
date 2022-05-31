package com.amazon.ata.kindlepublishingservice.publishing;

import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.PublishingStatusItem;
import com.amazon.ata.kindlepublishingservice.enums.PublishingRecordStatus;
import com.amazon.ata.kindlepublishingservice.utils.KindlePublishingUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

public class BookPublishTask implements Runnable {

    BookPublishRequestManager bookPublishRequestManager;

    PublishingStatusDao publishingStatusDao;

    CatalogDao catalogDao;

    private static final Logger LOGGER = LogManager.getLogger(BookPublisher.class);

    //currently leaving this void and without args
    //not sure what to put here as it being kinda the start to this.


    public BookPublishTask() {
    }

    @Inject
    public BookPublishTask(BookPublishRequestManager bookPublishRequestManager, PublishingStatusDao publishingStatusDao, CatalogDao catalogDao) {
        this.bookPublishRequestManager = bookPublishRequestManager;
        this.publishingStatusDao = publishingStatusDao;
        this.catalogDao = catalogDao;
    }

//    @Override
    public void run() {

        CatalogItemVersion test = new CatalogItemVersion();

        //retrieves the next bookPublishRequests and sets it to a new bookPublishRequest

        BookPublishRequest bookPublishRequest = bookPublishRequestManager.getBookPublishRequestToProcess();
            try {
        if (bookPublishRequest == null) {
            return;
        }
    
        //sets the status to "IN_PROGRESS"
        PublishingStatusItem publishingStatusItem = publishingStatusDao.setPublishingStatus(bookPublishRequest.getPublishingRecordId(),
                PublishingRecordStatus.IN_PROGRESS, bookPublishRequest.getBookId());

        //formats the book into type KindleFormattedBook
        KindleFormattedBook kindleBook = KindleFormatConverter.format(bookPublishRequest);

        //calls the CatalogDao method with the Kindle Book
        test = catalogDao.createOrUpdateBook(kindleBook);

                publishingStatusDao.setPublishingStatus(bookPublishRequest.getPublishingRecordId(),
                        PublishingRecordStatus.SUCCESSFUL, test.getBookId());

                 } catch (Exception e) {
                publishingStatusDao.setPublishingStatus(bookPublishRequest.getPublishingRecordId(),
                        PublishingRecordStatus.FAILED, bookPublishRequest.getBookId(),
                        "an exception occured during proccessing" + e);
            }
    }



    //sets to "IN PROGRESS" after accepting the request that was checked through the manager

    //calls the kindle converter and changes it to type of kindle

    //then calls catalogDao with new method "CreateOrUpdateBook

    //if the book is new then use the kindlepublishingutils to generate stuff then add it to dynamodb

    //if not new you are going to check dynamodb if it exists and then continue if it does

    // if it doesn't then you are going to throw an exception and you are going to set it to "FAILED"

    //if it does exist you are going to add it and make the old one inactive

    //once it makes it this far you will return and update the status to "SUCCEEDED" or you will update it to "FAILED"
}
