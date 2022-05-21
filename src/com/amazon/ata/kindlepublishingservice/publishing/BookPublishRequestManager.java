package com.amazon.ata.kindlepublishingservice.publishing;

import com.amazon.ata.kindlepublishingservice.publishing.BookPublishRequest;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Iterator;

public class BookPublishRequestManager {

    Collection<BookPublishRequest> requests = new Collection<BookPublishRequest>() {
        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }

        @Override
        public Iterator<BookPublishRequest> iterator() {
            return null;
        }

        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return null;
        }

        @Override
        public boolean add(BookPublishRequest bookPublishRequest) {
            return false;
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean addAll(Collection<? extends BookPublishRequest> c) {
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return false;
        }

        @Override
        public void clear() {

        }
    };
    @Inject
    //test inject not sure how they work
    public BookPublishRequestManager() {
    }

    public void addBookPublishRequest(BookPublishRequest bookPublishRequest) {
        requests.add(bookPublishRequest);
        //simply adds the converted request to a collection for now
    }

    public BookPublishRequest getBookPublishRequestToProcess() {
        //gets the next publish request. returns null if there is none.
        //my first idea is to iterate through the Collection in order to retrieve and return all the requests.
        //for now i will just return the closest one and then remove it from the collection so the next time
        //it is called it will have the next request ready
        if (requests.isEmpty()) {
            return null;
        }
        BookPublishRequest publishRequest = requests.iterator().next();
        requests.remove(publishRequest);
        return publishRequest;
    }
}
