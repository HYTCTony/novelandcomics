package com.nnmedia.comics.parser;

import com.nnmedia.comics.model.Comic;

 

public interface SearchIterator {

    boolean empty();

    boolean hasNext();

    Comic next();

}
