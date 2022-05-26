package com.amazon.ata.kindlepublishingservice.converters;

import com.amazon.ata.kindlepublishingservice.dynamodb.models.PublishingStatusItem;
import com.amazon.ata.kindlepublishingservice.enums.PublishingRecordStatus;
import com.amazon.ata.kindlepublishingservice.models.PublishingStatusRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PublishingStatusConverter {


    public PublishingStatusConverter() {
    }

    //a method that converts a list of PublishingStatusItems to a List of PublishingStatusRecords
    public static List<PublishingStatusRecord> toRecord(List<PublishingStatusItem> publishingStatusItems) {
        //create a new list to populate and return
        List<PublishingStatusRecord> toReturn = new ArrayList<>();
        //an enhanced for loop to loop through the list and convert the items to the record type before adding them back
        for (PublishingStatusItem publishingStatusItem : publishingStatusItems) {

            PublishingStatusRecord.Builder publishingStatusRecord = new PublishingStatusRecord.Builder()
                    .withStatus(publishingStatusItem.getStatus().toString())
                    .withStatusMessage(publishingStatusItem.getStatusMessage())
                    .withBookId(publishingStatusItem.getBookId());
                    //.build;
            //very confused why im getting an error for the .build function but it goes away when i build it in the
            //add statement so i will keep it like this unless it causes me a problem
            toReturn.add(publishingStatusRecord.build());
        }
        return toReturn;
    }



}
