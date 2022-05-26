package com.amazon.ata.kindlepublishingservice.activity;

import com.amazon.ata.kindlepublishingservice.converters.PublishingStatusConverter;
import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.PublishingStatusItem;
import com.amazon.ata.kindlepublishingservice.models.requests.GetPublishingStatusRequest;
import com.amazon.ata.kindlepublishingservice.models.response.GetPublishingStatusResponse;
import com.amazonaws.services.lambda.runtime.Context;

import javax.inject.Inject;
import java.util.List;

public class GetPublishingStatusActivity {

    PublishingStatusDao publishingStatusDao;

//    PublishingStatusConverter converter;


    @Inject
    public GetPublishingStatusActivity(PublishingStatusDao publishingStatusDao) {
        this.publishingStatusDao = publishingStatusDao;
//        this.converter = converter;
    }

    public GetPublishingStatusResponse execute(GetPublishingStatusRequest publishingStatusRequest) {
        //call the get publishingstatus on the passed in request using the id to find it
        //that returns a list status items that then pass through the converter and then

        //questioning whether returning a list and assigning that list to a list is going to go smoothly
        List<PublishingStatusItem> listOfItems = publishingStatusDao.getPublishingStatus
                (publishingStatusRequest.getPublishingRecordId());
//        PublishingStatusConverter converter = new PublishingStatusConverter();
        return GetPublishingStatusResponse.builder()
                .withPublishingStatusHistory(PublishingStatusConverter.toRecord(listOfItems))
                .build();
    }
}
